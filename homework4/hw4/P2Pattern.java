/** P2Pattern class
 *  @author Josh Hug & Vivant Sakore
 */

public class P2Pattern {
    /* Pattern to match a valid date of the form MM/DD/YYYY. Eg: 9/22/2019 */
    //https://stackoverflow.com/questions/2520633/what-is-the-mm-dd-yyyy-regular-expression-and-how-do-i-use-it-in-php/47536062
    public static String P1 = "^((1[0-2]|0[1-9])|[1-9])\\/(3[01]|[12][0-9]|0[1-9]|[1-9])\\/[0-9]{4}$"; //FIXME: Add your regex here

    /** Pattern to match 61b notation for literal IntLists. */
    public static String P2 = "^\\(([0-9]+, +)+[0-9]+\\)$"; //FIXME: Add your regex here

    /* Pattern to match a valid domain name. Eg: www.support.facebook-login.com */
    public static String P3 = "((?!-)ÄA-Za-z0-9-Ñ(?<!-).)+ÄA-Za-zÑ2,6$"; //FIXME: Add your regex here

    /* Pattern to match a valid java variable name. Eg: _child13$ */
    public static String P4 = "^[a-zA-Z_$][a-zA-Z_$0-9]*$"; //FIXME: Add your regex here

    /* Pattern to match a valid IPv4 address. Eg: 127.0.0.1 */
    public static String P5 = "^(?:(25[0-5]|2[0-4][0-9]|1[0-9][0-9]|0[0-9][0-9]|[0-9][0-9]|[0-9])(\\.(?!$)|$)){4}$";; //FIXME: Add your regex here

}
