
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.DriverManager;

public class ConexaoDB {
    static{
        try{

            Class.forName("com.mysql.cj.jdbc.Driver");
            
            }
        catch(ClassNotFoundException e){
            throw new RuntimeException(e);
        }
        }

    public Connection conectar() throws SQLException{
        String servidor = "localhost";
        String porta = "3306";
        String database = "bank";
        String usuario = "root";
        String senha = "Maua23!";
        
        return DriverManager.getConnection("jdbc:mysql://"+servidor+
                                                ":"+porta+"/"+database+
                                                "?user="+usuario+"&password="+senha);
    }

    public static void desconectar(Connection conn) throws SQLException{
        conn.close();
    }
}