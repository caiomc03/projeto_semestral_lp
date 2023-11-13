import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.net.Socket;


import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class ClienteBatepapo implements Runnable {
    private SocketCliente clientSocket;
 
    private JFrame frame;

    JPanel panel;
    JPanel panel1;
    JPanel panel2;


    //for panel1
    private JLabel numberLabel;
    private JTextField textField;
    private JButton addButton;


    Boolean mostrarSaldo = false;
    JButton button_VerifSaldo;
    JButton button_Sacar;
    JButton button_Depositar;
    JButton button_Deletar;


    private String usr_login;
    private boolean login_sucesso = false;
    private double balance = 0.0;
    String logstring;


    public void setBalance(double _balance) {
        balance = _balance;
    }
   

    public ClienteBatepapo(){
       
        frame = new JFrame("Cliente Batepapo");
        panel = new JPanel();


        panel1 = new JPanel();
        panel1.setLayout(new FlowLayout());

        // PANEL 1
        numberLabel = new JLabel("Saldo Conta: ---");
        panel1.add(numberLabel);
        textField = new JTextField(5); // 15 columns
        panel1.add(textField);
        addButton = new JButton("CONFIRMAR");
        panel1.add(addButton);

        panel2 = new JPanel();
        panel2.setLayout(new FlowLayout());

        button_VerifSaldo = new JButton("Verificar Saldo");
        panel2.add(button_VerifSaldo);
        button_Sacar = new JButton("Sacar Dinheiro");
        panel2.add(button_Sacar);
        button_Depositar = new JButton("Depositar");
        panel2.add(button_Depositar);
        button_Deletar = new JButton("Deletar Conta");
        panel2.add(button_Deletar);

        panel.add(panel1);
        panel.add(panel2);


        // adicionando WindowListener para fechar o socket e a janela
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                try {
                    clientSocket.close();
                } finally {
                    frame.dispose();
                }
            }
        });
    }


    public void start() throws IOException {
        try {
            clientSocket = new SocketCliente(new Socket(ServidorBatepapo.ADDRESS, ServidorBatepapo.PORT));
            new Thread(this).start();

            Login login = new Login();

            try {
                Thread.sleep(4000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            System.out.println("ready");

            while(true){
                // System.out.println("looping");
                logstring = login.getLogmsg();
                if (logstring != "||empty||"){
                    break;
                }
            }


            logstring = login.getLogmsg();
            String[] parts = logstring.split("---");
            usr_login = parts[1];
            clientSocket.sendMsg(logstring); //login---
    
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if(login_sucesso){
                messageLoop();
            }
            else{
                System.out.println("Login ou senha incorretos!");
            }


            }
      
         finally {
            clientSocket.close();
        }
    }

    @Override
    public void run()
    {
        String msg;
        while((msg = clientSocket.getMessage()) != null){

            if(msg.equals("Login efetuado com sucesso!")){
                login_sucesso = true; //trocar por um setter

            }

            else if(msg.split("---")[0].equals("balance")){
                setBalance(Double.parseDouble(msg.split("---")[1]));
                
                if(mostrarSaldo == false){
                    numberLabel.setText("Saldo Conta: ---");
                }
                else{
                    numberLabel.setText("Saldo Conta: " + balance);
                }
                
                System.out.println("Seu saldo é: " + balance); 
            }

            System.out.printf("\n-> %s\n", msg);
            System.out.print("Digite uma mensagem (ou <sair> para finalizar): \n<-");
        }
    }

    private void messageLoop() throws IOException{

        frame.setLayout(new FlowLayout());

        frame.setSize(490, 130); // aumentando o tamanho da janela
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);


        frame.add(panel);

        
       
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String msg = textField.getText();
                clientSocket.sendMsg(msg);
                textField.setText("");
                if (msg.equals("sair")) {
                    try {
                        clientSocket.close();

                    } finally {
                        System.exit(0);
                    }
                }
            }
        });
        button_VerifSaldo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String msg = SqlUtils.getSaldoQuery(usr_login);
                clientSocket.sendMsg(msg);
                if(mostrarSaldo == false){
                    mostrarSaldo = true;
                }
                else{
                    mostrarSaldo = false;
                }


            }
        });
        button_Sacar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String msg = SqlUtils.getUpdateSaldoQuery(usr_login,0,20); //pegar valores de uma caixa de texto
                clientSocket.sendMsg(msg);
            }
        });
        button_Depositar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String msg = SqlUtils.getUpdateSaldoQuery(usr_login,20,0); //pegar valores de uma caixa de texto
                clientSocket.sendMsg(msg);
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


