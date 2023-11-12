import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class ClienteBatepapo implements Runnable {
    private SocketCliente clientSocket;
    private Scanner scanner;
    private Frame frame;
    private TextField textField;
    private Button button;

    public ClienteBatepapo(){
        scanner = new Scanner (System.in);
        frame = new Frame("Cliente Batepapo");
        textField = new TextField(50); // aumentando o tamanho da caixa de texto
        button = new Button("Enviar");
    }

    public void start() throws IOException
    {
        try{
            clientSocket = new SocketCliente(new Socket(ServidorBatepapo.ADDRESS, ServidorBatepapo.PORT));
            new Thread(this).start();
            Login login = new Login();
            try {
                Thread.sleep(1000000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("Login bem-sucedido: " + login.isLoginSuccessful());
            if(login.isLoginSuccessful()){
            messageLoop();
            }
        }
        finally{
            clientSocket.close();
        }
    }

    @Override
    public void run()
    {
        String msg;
        while((msg = clientSocket.getMessage()) != null){
            System.out.printf("\n-> %s\n", msg);
            System.out.print("Digite uma mensagem (ou <sair> para finalizar): \n<-");
        }
    }

    private void messageLoop() throws IOException{

        frame.setLayout(new FlowLayout());
        frame.add(textField);
        frame.add(button);
        frame.setSize(200, 100); // aumentando o tamanho da janela
        frame.setVisible(true);

        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String msg = textField.getText();
                clientSocket.sendMsg(msg);
                textField.setText("");
            }
        });

        System.out.println("Digite uma mensagem (ou <sair> para finalizar):");
        do
        {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } while(true);
    }

    public static void main(String args[]){
        System.out.println("*v*v*v* CONSOLE DO CLIENTE *v*v*v*");
        try{
            ClienteBatepapo client = new ClienteBatepapo();
            client.start();
        }
        catch(IOException ex){
            System.out.println("Erro ao iniciar o cliente: " + ex.getMessage());
        }
        System.out.println("Cliente finalizado! ");
    }
    
}
