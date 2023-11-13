
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class SqlUtils {

    public static Connection connect() {
    ConexaoDB bd = new ConexaoDB();
    Connection conn = null;
    try {
        conn = bd.conectar();
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return conn;
    }

    public static void createUserTableIfNotExists(Connection conn) {
        String sqlCreate = "CREATE TABLE IF NOT EXISTS users (" +
                            "user VARCHAR(255) , " +
                            "password VARCHAR(255) , " +
                            "fullname VARCHAR(255) , " +
                            "email VARCHAR(255) , " +
                            "cpf VARCHAR(255) , " +
                            "contact VARCHAR(255))";
        try (Statement stmt = conn.createStatement()) {
            stmt.execute(sqlCreate);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void createBankTableIfNotExists(Connection conn) {
    String sqlCreate = "CREATE TABLE IF NOT EXISTS bank (" +
                        "user VARCHAR(255) , " +
                        "balance DOUBLE " + ")";
    try (Statement stmt = conn.createStatement()) {
        stmt.execute(sqlCreate);
    } catch (SQLException e) {
        e.printStackTrace();
    }
}

    public static boolean verifyPassword(Connection conn, String user, String password) {
        String password_sql = null;
        String stmt = "SELECT password FROM users WHERE user = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(stmt)) {
            pstmt.setString(1, user);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                password_sql = rs.getString("password");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if(password.equals(password_sql)){
            System.out.println("Login efetuado com sucesso!");
            return true;
        }
        else{
            System.out.println("Login ou senha incorretos!");
            return false;
        }
    }

    public static String getSaldoQuery(String user){
        return ("sql---Select balance from bank where user = " +"'"+ user+"'");
    }

    public static double getSaldo(String query, Connection conn) {
        double saldo = 0.0;
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                saldo = rs.getDouble("balance") ;
                System.out.println("Saldo: " + saldo);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return saldo;
    }

    public static boolean updateSaldoQuery(String user, double deposit, double withdraw, Connection conn){
        double balance = getSaldo(getSaldoQuery(user),conn);
       
        double balance_update = balance + deposit - withdraw;
        String new_balance_query = "UPDATE bank SET balance = "+ balance_update + " WHERE user = '" + user + "'";
        if(balance_update < 0){
            System.out.println("Saldo insuficiente!");
            return false;
        }
        else{
            System.out.println("Saldo atualizado!");
            try (PreparedStatement pstmt = conn.prepareStatement(new_balance_query)) {
                pstmt.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return true;
        }
   
        
    }




}
    