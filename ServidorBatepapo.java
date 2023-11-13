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
        try
        {
            while((msg = clientSocket.getMessage()) != null){
                if("sair".equalsIgnoreCase(msg)) return;

                if(msg.startsWith("login---")){
                    String[] login = msg.split("---");
                    if(SqlUtils.verifyPassword(conn, login[1],login[2])){
                        clientSocket.sendMsg("Login efetuado com sucesso!");
                    }
                    else{
                        clientSocket.sendMsg("Login ou senha incorretos!");
                    }
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
