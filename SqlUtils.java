public class SqlUtils {

    public static String getSaldoQuery(String user){
        return ("Select saldo from usuarios where user = " + user);
    }
}
    