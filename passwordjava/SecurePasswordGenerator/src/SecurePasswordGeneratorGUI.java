import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class SecurePasswordGeneratorGUI {

    private JFrame frame;
    private JButton generateButton;
    private JTextField passwordField;
    private JSpinner lengthSpinner;
    private JComboBox<String> complexityCombo;

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                SecurePasswordGeneratorGUI window = new SecurePasswordGeneratorGUI();
                window.frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public SecurePasswordGeneratorGUI() {
        
        frame = new JFrame();
        frame.setBounds(100, 100, 450, 300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(new FlowLayout());

        
        JLabel passwordLabel = new JLabel("Generated Password:");
        passwordField = new JTextField(20);
        passwordField.setEditable(false);

        JLabel lengthLabel = new JLabel("Password Length:");
        lengthSpinner = new JSpinner(new SpinnerNumberModel(12, 6, 20, 1));

        JLabel complexityLabel = new JLabel("Complexity:");
        complexityCombo = new JComboBox<>(new String[] { "Low", "Medium", "High" });

        generateButton = new JButton("Generate Password");
        generateButton.addActionListener(e -> generatePassword());

        
        frame.getContentPane().add(passwordLabel);
        frame.getContentPane().add(passwordField);
        frame.getContentPane().add(lengthLabel);
        frame.getContentPane().add(lengthSpinner);
        frame.getContentPane().add(complexityLabel);
        frame.getContentPane().add(complexityCombo);
        frame.getContentPane().add(generateButton);
    }

    private void generatePassword() {
        int length = (int) lengthSpinner.getValue();
        String complexity = (String) complexityCombo.getSelectedItem();
        String password = generateSecurePassword(length, complexity);
        passwordField.setText(password);
    }

    private String generateSecurePassword(int length, String complexity) {
        StringBuilder password = new StringBuilder();
        String characters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        
        if ("High".equals(complexity)) {
            characters += "!@#$%^&*()-_+=<>?";
        } else if ("Medium".equals(complexity)) {
            characters += "!@#$%^&*";
        }

        for (int i = 0; i < length; i++) {
            int randomIndex = (int) (Math.random() * characters.length());
            password.append(characters.charAt(randomIndex));
        }

        return password.toString();
    }
}
