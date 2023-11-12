public class SqlUtils {

    public String getSaldoQuery(String user){
        return ("Select saldo from usuarios where user = " + user);
    }
}
    