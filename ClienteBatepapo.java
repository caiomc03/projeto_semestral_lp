import java.awt.*;
import java.awt.event.*;
import java.io.File;
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
    JButton button_Sair;


    private String usr_login;
    private boolean login_sucesso = false;
    private double balance = 0.0;
    private String logstring;

    CryptoDummy cdummy = new CryptoDummy();
    String dummyPath = "chave.dummy";

    byte[]   bMsgClara = null;
    byte[]   bMsgCifrada = null;



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
        button_Sair = new JButton("Sair");
        panel2.add(button_Sair);

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

                if(login.getCadStart()){
                    Cadastro cadastro = new Cadastro();
                    //while loop dentro do cadastro
                    String cadstring = cadastro.wait_for_cadastro_input();
                    clientSocket.sendMsg(cadstring);
                    System.out.println(cadstring);
                    login.setCadStartFALSE();

                }
                
                if(!login.getCadStart()){  
                    logstring = login.getLogmsg();
                    if (logstring != "||empty||"){
                        break;
                    }
                }
            }


            logstring = login.getLogmsg();
            String[] parts = logstring.split("---");
            usr_login = parts[1];
            clientSocket.sendMsg(logstring); //login---


    
            try {
                Thread.sleep(1000);
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

            else if(msg.split("---")[0].equals("balance") || msg.split("---")[0].equals("newbalance")  ){
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

        cdummy = new CryptoDummy();

       
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String msg = textField.getText();

                if (msg.equals("sair")) {
                    try {
                        clientSocket.close();
                        frame.dispose();
                        Thread.interrupted();

                    } finally {
                        System.exit(0);
                    }
                }

                try{
                    bMsgClara = msg.getBytes("ISO-8859-1");
                    cdummy.geraCifra(bMsgClara, new File (dummyPath));
                    bMsgCifrada = cdummy.getTextoCifrado();
                    msg = (new String (bMsgCifrada, "ISO-8859-1"));
                }
                catch(Exception er){
                    System.out.println(er);
                }

                clientSocket.sendMsg(msg);
                textField.setText("");
  
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
            button_Sair.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String msg = "sair";
                clientSocket.sendMsg(msg);
            }
        });
        button_Sacar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                double value;
                String text = textField.getText();
                if (text.equals("")) {
                    value = 0;
                } else {
                    value = Double.parseDouble(text);
                }
                String msg = SqlUtils.getUpdateSaldoQuery(usr_login, 0, value); //pegar valores de uma caixa de texto
                clientSocket.sendMsg(msg);
            }
        });
        button_Depositar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                double value;
                String text = textField.getText();
                if (text.equals("")) {
                    value = 0;
                } else {
                    value = Double.parseDouble(text);
                }
                String msg = SqlUtils.getUpdateSaldoQuery(usr_login,value,0); //pegar valores de uma caixa de texto
                clientSocket.sendMsg(msg);
            }
        });
        
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


