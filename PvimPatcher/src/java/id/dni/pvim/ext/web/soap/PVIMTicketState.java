/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.dni.pvim.ext.web.soap;

/**
 *
 * @author darryl.sulistyan
 */
public class PVIMTicketState {
    public static final String
            NEW = "1",
            ASSIGNED = "11",
            SUSPENDED = "15",
            WORKING = "2",
            FIXED = "4",
            CLOSED = "6",
            DELETED = "7",
            IN_GROUP = "16";

    private PVIMTicketState() {

    }

    public static String getDisplayText(String state) {
        switch (state) {
            case NEW:
                return "NEW";

            case ASSIGNED:
                return "ASSIGNED";

            case SUSPENDED:
                return "SUSPENDED";

            case WORKING:
                return "WORKING";

            case FIXED:
                return "FIXED";

            case CLOSED:
                return "CLOSED";

            case DELETED:
                return "DELETED";

            case IN_GROUP:
                return "IN GROUP";

            default:
                return "UNKNOWN STATE";
        }
    }
}
