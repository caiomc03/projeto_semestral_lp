import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.sql.Connection;
import java.util.List;
import java.util.LinkedList;
import java.util.Iterator;


public class ServidorBatepapo {

    public static Connection conn;
    public static final String ADDRESS = "127.0.0.1";
    public static final int PORT = 4000;
    private ServerSocket serverSocket;
    private final List<SocketCliente> clients = new LinkedList<>();

    // private String usr_password = "whindersonnunes";//Recebidas pelo cliente em algum momento, ainda nao definido
    // private String usr_login = "caio";

    public void start() throws IOException{
        serverSocket = new ServerSocket(PORT);
        System.out.println("Servidor iniciado na porta: " + PORT);
        conn = SqlUtils.connect();
        SqlUtils.createBankTableIfNotExists(conn);
        SqlUtils.createUserTableIfNotExists(conn);
        // SqlUtils.verifyPassword(conn, usr_login,usr_password);
        clientConnectionLoop();
    }

    private void clientConnectionLoop() throws IOException{
        System.out.println("Aguardando conexao de um cliente!");
        while(true){
            SocketCliente clientSocket = new SocketCliente(serverSocket.accept());
            clients.add(clientSocket);
            new Thread(() -> clientMessageLoop(clientSocket)).start();
        }
    }

    private void clientMessageLoop(SocketCliente clientSocket){
        String msg;
        CryptoDummy cdummy = new CryptoDummy();
        String dummyPath = "chave.dummy";
        try
        {
            while((msg = clientSocket.getMessage()) != null){
                if("sair".equalsIgnoreCase(msg)) return;

                else if(msg.startsWith("login---")){
                    String[] login = msg.split("---");

                    try{
                    login[1] = cdummy.autoDecifra(login[1], new File(dummyPath));
                    login[2] = cdummy.autoDecifra(login[2], new File(dummyPath));
                    }catch(Exception e){}


                    if(SqlUtils.verifyPassword(conn, login[1],login[2])){
                        clientSocket.sendMsg("Login efetuado com sucesso!");
                    }
                    else{
                        clientSocket.sendMsg("Login ou senha incorretos!");
                    }
                }

                else if(msg.startsWith("cadastro---")){
                    String cad_usr = msg.split("---")[1];
                    String cad_password = msg.split("---")[2];
                    String cad_fullname = msg.split("---")[3];  
                    String cad_email = msg.split("---")[4];
                    String cad_cpf = msg.split("---")[5];
                    String cad_contact = msg.split("---")[6];
                    String cad_gender = msg.split("---")[7];
                    String query = SqlUtils.createUserQuery(cad_usr,cad_password,cad_fullname,cad_email,cad_cpf,cad_contact,cad_gender);


                    SqlUtils.createUser(query,conn);
                    clientSocket.sendMsg("Cadastro realizado com sucesso!");
                }

                else if(msg.startsWith("sqlgetbalance---")){
                    String query = msg.split("---")[1];
                    double saldo = SqlUtils.getSaldo(query,conn);
                    clientSocket.sendMsg("balance---"+saldo);
                }
                
                else if(msg.startsWith("sqldeleteuser---")){
                    String user_del = msg.split("---")[1];
                    SqlUtils.deleteUser(user_del,conn);     
                    clientSocket.sendMsg("---delete---");
                       
                }

                else if(msg.startsWith("sqlgetall---")){
                    String user = msg.split("---")[1];
                    String all = SqlUtils.getProfile(user,conn);
                    clientSocket.sendMsg("profile---"+all);
                }

                else if(msg.startsWith("sqlupdatebalance---")){
                    String query = msg.split("---")[1];
                    double saldo_update = SqlUtils.updateSaldo(query, conn, clientSocket);
                    clientSocket.sendMsg("newbalance---"+saldo_update);
                }

                else{
                    System.out.printf("<- Client %s: %s\n", 
                                    clientSocket.getRemoteSocketAddress(), msg);
                    sendMsgToAll(clientSocket, msg);
                }
            }
        }
        finally
        {
            clientSocket.close();
        }

    }

    private void sendMsgToAll(SocketCliente sender, String msg){
        Iterator<SocketCliente> iterator = clients.iterator();
        while(iterator.hasNext()){
            SocketCliente clientSocket = iterator.next();
            if(!sender.equals(clientSocket)){
                if(!clientSocket.sendMsg("Cliente " + sender.getRemoteSocketAddress() + ": " + msg)){
                    iterator.remove();
                }
            }
        }
    }

    public static void main(String agrs[]){
        System.out.println("*v*v*v* CONSOLE DO SERVIDOR *v*v*v*");
        try{
            ServidorBatepapo server = new ServidorBatepapo();
            server.start();
        }
        catch(IOException ex){
            System.out.println("Erro ao iniciar o servidor: " + ex.getMessage());
        }
        System.out.println("Servidor finalizado!");
    }

}
