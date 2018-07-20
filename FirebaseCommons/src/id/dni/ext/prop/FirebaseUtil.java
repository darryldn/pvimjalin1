/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.dni.ext.prop;

/**
 *
 * @author darryl.sulistyan
 */
public class FirebaseUtil {
    
    private static final String[] FORBIDDEN = new String[]{
        ".", "$", "[", "]", "#", "/"
    };
    
    /**
     * Firebase key may not have the following entries
     * . (period)
        $ (dollar sign)
        [ (left square bracket)
        ] (right square bracket)
        # (hash or pound sign)
        / (forward slash)
     * @param key
     * @return 
     */
    public static boolean isValidKey(String key) {
        if (key == null) {
            return true;
        }
        for (String illegal : FORBIDDEN) {
            if (key.contains(illegal)) {
                return false;
            }
        }
        return true;
    }
    
}
