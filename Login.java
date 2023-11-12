import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.ExecutionException;

public class Login {
    private JFrame frame;
    private JTextField loginField;
    private JPasswordField passwordField;
    private boolean loginSuccessful;
    private String logmsg;

    public Login() {
        frame = new JFrame("Login");
        frame.setSize(300, 200);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel();
        frame.add(panel);
        placeComponents(panel);

        frame.setVisible(true);
    }

    private void placeComponents(JPanel panel) {
        panel.setLayout(null);

        JLabel userLabel = new JLabel("User");
        userLabel.setBounds(10, 20, 80, 25);
        panel.add(userLabel);

        loginField = new JTextField(20);
        loginField.setBounds(100, 20, 165, 25);
        panel.add(loginField);

        JLabel passwordLabel = new JLabel("Password");
        passwordLabel.setBounds(10, 50, 80, 25);
        panel.add(passwordLabel);

        passwordField = new JPasswordField(20);
        passwordField.setBounds(100, 50, 165, 25);
        panel.add(passwordField);

        JButton loginButton = new JButton("login");
        loginButton.setBounds(10, 80, 80, 25);
        panel.add(loginButton);
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String user = loginField.getText();
                String password = new String(passwordField.getPassword());
                logmsg = "login" + "---" + user + "---" + password; //Enviar essa mensagem ao servidor
                frame.dispose();
            }
            
        });
    }

    public String getLogmsg() {
        return logmsg;
    }
}
