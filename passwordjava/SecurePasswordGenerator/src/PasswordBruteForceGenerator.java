import javax.swing.*;
import java.io.*;
import java.util.concurrent.*;
import java.util.*;

public class PasswordBruteForceGenerator {

    private ExecutorService executorService;
    private JTextArea passwordArea;
    private JTextField lengthField;
    private String chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789!@#$%^&*()_+";
    private BufferedWriter writer; 

    public PasswordBruteForceGenerator(JTextArea passwordArea, JTextField lengthField) {
        this.passwordArea = passwordArea;
        this.lengthField = lengthField;
        this.executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors()); 
        try {
            writer = new BufferedWriter(new FileWriter("generated_passwords.txt")); 
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    
    public void generateAllPasswords() {
        int length = Integer.parseInt(lengthField.getText());  
        if (length <= 0) {
            JOptionPane.showMessageDialog(null, "Please enter a valid length.");
            return;
        }
        passwordArea.setText("");  
        PasswordGenerationWorker worker = new PasswordGenerationWorker(length);
        worker.execute();  
    }

   
    private class PasswordGenerationWorker extends SwingWorker<Void, String> {
        private int length;

        public PasswordGenerationWorker(int length) {
            this.length = length;
        }

        @Override
        protected Void doInBackground() throws Exception {
            generatePasswordsWithTypingEffect(length); 
            return null;
        }

        @Override
        protected void process(java.util.List<String> chunks) {
            for (String password : chunks) {
                passwordArea.append(password + "\n");  
            }
        }

        @Override
        protected void done() {
            JOptionPane.showMessageDialog(null, "Password generation complete.");
            try {
                writer.close(); 
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

       
        private void generatePasswordsWithTypingEffect(int length) {
            int totalCombinations = (int) Math.pow(chars.length(), length);
            CountDownLatch latch = new CountDownLatch(totalCombinations);

            for (int i = 0; i < totalCombinations; i++) {
                final int index = i;
                executorService.submit(() -> {
                    String password = generatePassword(index, length);
                    try {
                        publish(password);  
                        writer.write(password);  
                        writer.newLine();  
                        Thread.sleep(10);  
                    } catch (InterruptedException | IOException e) {
                        e.printStackTrace();
                    } finally {
                        latch.countDown();
                    }
                });
            }

            try {
                latch.await();  
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        
        private String generatePassword(int index, int length) {
            StringBuilder password = new StringBuilder();
            for (int i = 0; i < length; i++) {
                password.append(chars.charAt((index / (int) Math.pow(chars.length(), i)) % chars.length()));
            }
            return password.toString();
        }
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Password Generator");
        JTextArea passwordArea = new JTextArea(20, 40);
        passwordArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(passwordArea);

        JTextField lengthField = new JTextField(5);  
        JLabel lengthLabel = new JLabel("Enter Password Length:");

        JButton generateButton = new JButton("Generate Passwords");
        generateButton.addActionListener(e -> {
            PasswordBruteForceGenerator generator = new PasswordBruteForceGenerator(passwordArea, lengthField);
            generator.generateAllPasswords();  
        });

        JPanel panel = new JPanel();
        panel.add(lengthLabel);
        panel.add(lengthField);
        panel.add(generateButton);

        frame.setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));
        frame.add(panel);
        frame.add(scrollPane);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}
