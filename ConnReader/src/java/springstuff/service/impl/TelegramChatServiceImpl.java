/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package springstuff.service.impl;

import id.dni.pvim.ext.repo.exceptions.PvExtPersistenceException;
import id.dni.pvim.ext.telegram.pojo.TelegramMessageChatPOJO;
import id.dni.pvim.ext.telegram.pojo.TelegramMessageContentPOJO;
import id.dni.pvim.ext.telegram.pojo.TelegramUpdateObjPOJO;
import id.dni.pvim.ext.repo.db.ISlmUserRepository;
import id.dni.pvim.ext.telegram.repo.ITelegramSuscribersRepository;
import id.dni.pvim.ext.telegram.repo.db.vo.TelegramSubscriberVo;
import id.dni.pvim.ext.telegram.repo.spec.TelegramSubscribersByChatIDSpec;
import id.dni.pvim.ext.telegram.repo.spec.TelegramSubscribersPhoneNumSpecification;
import id.dni.pvim.ext.web.in.Commons;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import springstuff.service.TelegramChatService;
import springstuff.service.TelegramMessageSenderService;

/**
 *
 * @author darryl.sulistyan
 */
@Service
public class TelegramChatServiceImpl implements TelegramChatService {

    private TelegramMessageSenderService senderService;
    private ITelegramSuscribersRepository subscriberRepo;
    private ISlmUserRepository userRepo;

    @Autowired
    public void setTelegramSubscriberRepo(ITelegramSuscribersRepository subsriberRepo) {
        this.subscriberRepo = subsriberRepo;
    }

    @Autowired
    public void setSlmUserRepo(ISlmUserRepository userRepo) {
        this.userRepo = userRepo;
    }

    @Autowired
    public void setSenderService(TelegramMessageSenderService senderService) {
        this.senderService = senderService;
    }

    private String getUsage() {
        StringBuilder sb = new StringBuilder();
        sb.append("Commands:");
        sb.append("/help: Print this text");
        sb.append("/reg mobile-number: Register your phone number for notifications");
        sb.append("/unreg: Unregister phone number");
        return sb.toString();
    }

    private String genPassKey() {
        StringBuilder sb = new StringBuilder();
        Random r = new Random();
        for (int i = 0; i < 6; ++i) {
            sb.append(r.nextInt(10));
        }
        return sb.toString();
    }

    private static boolean eq(Object a, Object b) {
        if (a == null && b == null) {
            return true;
        }

        if (a == null || b == null) {
            return false;
        }

        return a.equals(b);
    }

    private void sendTelegramMessage(long chatID, String message) {
        senderService.asyncSendTelegramReply(chatID, message);
    }

    private static class TelMsg {

        private final long id;
        private final String text;
        private final boolean isSuccess;

        public TelMsg(boolean isSuccess, long id, String text) {
            this.id = id;
            this.text = text;
            this.isSuccess = isSuccess;
        }

        public long getId() {
            return id;
        }

        public String getText() {
            return text;
        }

        public boolean isIsSuccess() {
            return isSuccess;
        }

    }

    private TelMsg handleException(long telegramChatId, Exception ex) {
        Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
        TelMsg telegramMessage = new TelMsg(false, telegramChatId, "Internal server error, please contact system administrator");
        return telegramMessage;
    }

    @Override
    @Transactional(transactionManager = "pvimTransactionManager", rollbackFor = PvExtPersistenceException.class)
    public void consume(TelegramUpdateObjPOJO updateObj) throws PvExtPersistenceException {
        if (updateObj == null) {
            return;
        }
        TelegramMessageContentPOJO content = updateObj.getMessage();
        if (content == null) {
            return;
        }
        TelegramMessageChatPOJO user = content.getChat();

        long telegramChatId = user.getId();

//        ITelegramSuscribersRepository subscriberRepo = 
//                new TelegramSubscribersJdbcTemplateRepository(pvimJdbcTemplate);
//        ISlmUserRepository userRepo = new SlmUserJdbcTemplateRepository(pvimJdbcTemplate);
        TelMsg message = null;
        try {
            message = _consume(updateObj, subscriberRepo, userRepo);
//            // START FAIL TEST
//            if (telegramChatId == 250 || telegramChatId == 251) {
//                throw new RuntimeException("TEST FAIL TEST using RuntimeException. Expect: rollback");
//            }
//            if (telegramChatId == 350 || telegramChatId == 351) {
//                throw new PvExtPersistenceException("TEST FAIL TEST using PvExtPersistenceException. Expect: rollback");
//            }
//            // END FAIL TEST
        } catch (PvExtPersistenceException ex) {
            message = handleException(telegramChatId, ex);
            throw ex;
        } catch (Exception ex) {
            message = handleException(telegramChatId, ex);
            throw new PvExtPersistenceException(ex);
        } finally {
            if (message != null) {
                sendTelegramMessage(message.getId(), message.getText());
            }
        }

    }

    private TelMsg _consume(TelegramUpdateObjPOJO updateObj,
            ITelegramSuscribersRepository subscriberRepo,
            ISlmUserRepository slmUserRepo) throws PvExtPersistenceException {

        TelegramMessageContentPOJO content = updateObj.getMessage();
        TelegramMessageChatPOJO user = content.getChat();

        long id = user.getId();
        String text = content.getText();

        // Don't link against name here.
        // it is possible that the user already installed Telegram with different
        // name registered from ProView.
        if (Commons.isEmptyStrIgnoreSpaces(text)) {
            return new TelMsg(false, id, "No input given");
        }

        long chatDate = content.getDate();
        chatDate *= 1000; // transform to ms. Telegram only up to seconds precision.

        // commands:
        // /help --> print usage
        // /reg (phone number)
        // else, ignore
        String[] commands = text.split(" +"); // split for one or more spaces
        if (commands.length >= 2) {
            String cmd = commands[0].toLowerCase();
            if ("/reg".equals(cmd)) {

                String probablePhoneNum = commands[1];
                if (Commons.isEmptyStrIgnoreSpaces(probablePhoneNum)) {
                    return new TelMsg(false, id, "No phone number given");

                } else {

                    try {
                        // Find the phone number in ProViewIM.
                        // If not registered, send error updateObj saying no phone number found
                        boolean isMobileExist;
                        ISlmUserRepository userRepo = slmUserRepo;
                        isMobileExist = userRepo.isMobileExist(probablePhoneNum);

                        if (isMobileExist) {

                            String phoneNum = probablePhoneNum;
                            long chatID = id;
                            ITelegramSuscribersRepository repos = subscriberRepo;

                            // modify the code to enforce 1-1 relationship between phone num and chat telegramChatId
                            TelegramSubscriberVo existingPhone = repos.querySingleResult(
                                    new TelegramSubscribersPhoneNumSpecification(phoneNum));
                            if (existingPhone != null) {
                                if (chatID != existingPhone.getChat_id()/* && chatDate > existingPhone.getLastupdate()*/) {
                                    // this shouldn't happen because Telegram also
                                    // associates mobile number with chat telegramChatId
                                    // Different machine with same mobile with obtain same chat telegramChatId as well
                                    // from Telegram.

                                    // new ChatID is found with same phone number. Update entry in DB
                                    existingPhone.setChat_id(chatID);
                                    existingPhone.setLastupdate(chatDate);
                                    boolean ok = repos.update(existingPhone);
                                    if (ok) {
                                        return new TelMsg(true, id, "Update chatID successful");
                                    } else {
                                        return new TelMsg(false, id, "Update chatID failed, try again later");
                                    }

                                } else {
                                    return new TelMsg(true, id, "Number is already registered");
                                }
                                // entry exists in DB, no need to do anything

                            } else {
                                existingPhone = repos.querySingleResult(
                                        new TelegramSubscribersByChatIDSpec(chatID));
                                if (existingPhone != null) {
                                    if (!eq(existingPhone.getPhone_num(), phoneNum)/* && chatDate > existingPhone.getLastupdate()*/) {
                                        // new phone Number for existing chatID. Update entry in DB
                                        existingPhone.setPhone_num(phoneNum);
                                        existingPhone.setLastupdate(chatDate);
                                        boolean ok = repos.update(existingPhone);
                                        if (ok) {
                                            return new TelMsg(true, id, "Update mobile number successful");
                                        } else {
                                            return new TelMsg(false, id, "Update mobile number failed, try again later");
                                        }

                                    } else {
                                        return new TelMsg(true, id, "Number is already registered");
                                    }
                                } else {

                                    // completely new entry
                                    TelegramSubscriberVo newSub = new TelegramSubscriberVo();
                                    newSub.setChat_id(chatID);
                                    newSub.setLastupdate(chatDate);
                                    newSub.setPhone_num(phoneNum);
                                    newSub.setSubs_id(Commons.randomUUID());
                                    newSub.setPasskey(genPassKey());
                                    boolean ok = repos.insert(newSub);
                                    if (ok) {
                                        return new TelMsg(true, id, String.format(
                                                "Registration successful with number %s!",
                                                probablePhoneNum));
                                    } else {
                                        return new TelMsg(false, id, "Registration failed. Please try again later");
                                    }
                                }
                            }

                        } else {
                            return new TelMsg(false, id, "Registration failed. The mobile number is not found in PVIM database");

                        }

                    } catch (PvExtPersistenceException ex) {
                        throw ex;

                    }
                }

            } else {
                return new TelMsg(false, id, "Unrecognized command " + cmd);
            }

        } else {

            if (commands.length == 1) {
                String cmd = commands[0].toLowerCase();
                if ("/help".equals(cmd)) {
                    return new TelMsg(true, id, getUsage());

                } else if ("/unreg".equals(cmd)) {

                    try {
                        ITelegramSuscribersRepository repo = subscriberRepo;
                        TelegramSubscriberVo subs = repo.querySingleResult(new TelegramSubscribersByChatIDSpec(id));
                        if (subs != null) {
                            boolean isSuccess = repo.delete(subs);
                            if (isSuccess) {
                                return new TelMsg(true, id, "Unregistration successful!");
                            } else {
                                return new TelMsg(false, id, "Unregistration failed!");
                            }
                        } else {
                            return new TelMsg(false, id, "Number not found in PVIM database");
                        }

                    } catch (PvExtPersistenceException ex) {
                        throw ex;
                    }

                } else {
                    return new TelMsg(false, id, "Unrecognized command " + cmd);
                }
            } else {
                return new TelMsg(false, id, "Unknown input");
            }
        }

//        return null;
    }

}
