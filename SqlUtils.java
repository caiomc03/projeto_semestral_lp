
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.mysql.cj.jdbc.ClientPreparedStatement;
import com.mysql.cj.jdbc.exceptions.SQLError;

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
        return ("sqlgetbalance---Select balance from bank where user = " +"'"+ user+"'");
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

    public static String getUpdateSaldoQuery(String user, double deposit, double withdraw){
        return ("sqlupdatebalance---UPDATE bank SET balance ="+ " -newbalance- " +" WHERE user = '" + user + "'" + "@"+deposit+ "@"+withdraw);
    }

    public static double updateSaldo(String query, Connection conn){
        String user = query.split("=")[2].split("@")[0].replace(" ", "").replace("'","");
        String query1 = "SELECT balance FROM bank WHERE user = '"+user+"'";
        double balance = getSaldo(query1,conn);
        double deposit = Double.parseDouble(query.split("@")[1]);
        double withdraw = Double.parseDouble(query.split("@")[2]);
        double balance_update = balance + deposit - withdraw;

        if (balance_update < 0) {
            System.out.println("Saldo insuficiente!");
            return 0.0;
        } else {
            String new_balance_query = query.replace("-newbalance-", Double.toString(balance_update)).split("@")[0];
            System.out.println("Saldo atualizado!");
            System.out.println(query.split("=")[2].split("@")[0].replace(" ", ""));
            try (PreparedStatement pstmt = conn.prepareStatement(new_balance_query)) {
                int rowsAffected = pstmt.executeUpdate();
                System.out.println("Linhas afetadas: " + rowsAffected);
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return balance_update;
        }

  
   
        
    }

    public static String createUserQuery(String user, String password, String fullname, String email, String cpf, String contact, Connection conn){
        return("INSERT INTO users VALUES('"+user+"','"+password+"','"+fullname+"','"+email+"','"+cpf+"','"+contact+"')");
    }

    public static void createUser(String query,Connection conn){
        String user = query.split("'")[1];
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
        int rowsAffected = pstmt.executeUpdate();
        System.out.println("Linhas afetadas: " + rowsAffected);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        String querybank = "INSERT INTO bank VALUES('"+user+"',0.0)";
        try (PreparedStatement pstmt = conn.prepareStatement(querybank)) {
        int rowsAffected = pstmt.executeUpdate();
        System.out.println("Linhas afetadas: " + rowsAffected);
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    



}
    