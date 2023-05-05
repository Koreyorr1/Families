import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SignInScreen {
    private JFrame frame;
    private JTextField emailField;
    private JPasswordField passwordField;

    public SignInScreen() {
        initialize();
    }
    
    public void show() {
        frame.setVisible(true);
    }

    private void initialize() {
        frame = new JFrame("Login Screen");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(300, 200);
        frame.setLocationRelativeTo(null);
        frame.setLayout(new GridLayout(4, 2));

        frame.add(new JLabel("Email:"));
        emailField = new JTextField();
        frame.add(emailField);

        frame.add(new JLabel("Password:"));
        passwordField = new JPasswordField();
        frame.add(passwordField);

        JButton loginButton = new JButton("Login");
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String email = emailField.getText();
                String password = new String(passwordField.getPassword());                   

                    if (FirebaseAuthenticator.loginUser(email, password)) {
                        System.out.println("Login successful!");
                    } else {
                        System.out.println("Login failed!");
                    }
            }
        });
        frame.add(loginButton);

        JButton createAccountButton = new JButton("Create Account");
        createAccountButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String email = emailField.getText();
                String password = new String(passwordField.getPassword());

                if (FirebaseAuthenticator.registerUser(email, password)) {
                    System.out.println("Registration successful!");
                } else {
                    System.out.println("Registration failed!");
                }    
            }
        });
        
        frame.add(createAccountButton);
    }
  }



/*import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Properties;
import java.io.FileInputStream;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import com.google.firebase.auth.UserRecord;
import com.google.firebase.auth.UserRecord.CreateRequest;

public class SignInScreen {
    private JFrame frame;
    private JTextField emailField;
    private JPasswordField passwordField;
    private JButton signInButton;
    private JLabel statusLabel;
    private JButton registerButton;

    public SignInScreen() {
        createUIComponents();
        

        signInButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                signInWithEmailAndPassword(emailField.getText(), new String(passwordField.getPassword()));
            }
        });
        
        registerButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Handle registration process
                registerNewUser();
            }
        });


        frame.pack();
        frame.setVisible(true);
    }

    private void createUIComponents() {
        frame = new JFrame("Sign In");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(new GridLayout(4, 2));

        frame.getContentPane().add(new JLabel("Email:"));
        emailField = new JTextField(20);
        frame.getContentPane().add(emailField);

        frame.getContentPane().add(new JLabel("Password:"));
        passwordField = new JPasswordField(20);
        frame.getContentPane().add(passwordField);

        signInButton = new JButton("Sign In");
        frame.getContentPane().add(signInButton);
        
        registerButton = new JButton("Create New Account");
        frame.getContentPane().add(registerButton);

        statusLabel = new JLabel("");
        frame.getContentPane().add(statusLabel);
    }

    private void signInWithEmailAndPassword(String email, String password) {
        String authUrl = "https://identitytoolkit.googleapis.com/v1/accounts:signInWithPassword?key=" + Families.getFirebaseApiKey();
        
        try {
            URL url = new URL(authUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);

            String jsonInputString = "{\"email\":\"" + email + "\", \"password\":\"" + password + "\", \"returnSecureToken\":true}";
            connection.getOutputStream().write(jsonInputString.getBytes("UTF-8"));

            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder content = new StringBuilder();
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    content.append(inputLine);
                }
                in.close();

                JsonElement jsonElement = JsonParser.parseString(content.toString());
                JsonObject jsonObject = jsonElement.getAsJsonObject();
                String idToken = jsonObject.get("idToken").getAsString();

                statusLabel.setText("Signed in as: " + email);
            } else {
                statusLabel.setText("Error: Invalid email or password");
            }
        } catch (IOException e) {
            statusLabel.setText("Error: " + e.getMessage());
        }
        
    }
    
    private void registerNewUser() {
        JPanel registrationPanel = new JPanel();
        registrationPanel.setLayout(new GridLayout(3, 2));

        JLabel emailLabel = new JLabel("Email: ");
        JTextField emailField = new JTextField(20);
        registrationPanel.add(emailLabel);
        registrationPanel.add(emailField);

        JLabel passwordLabel = new JLabel("Password: ");
        JPasswordField passwordField = new JPasswordField(20);
        registrationPanel.add(passwordLabel);
        registrationPanel.add(passwordField);

        JLabel confirmPasswordLabel = new JLabel("Confirm Password: ");
        JPasswordField confirmPasswordField = new JPasswordField(20);
        registrationPanel.add(confirmPasswordLabel);
        registrationPanel.add(confirmPasswordField);

        int result = JOptionPane.showConfirmDialog(null, registrationPanel, "Register", JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            String email = emailField.getText();
            String password = new String(passwordField.getPassword());
            String confirmPassword = new String(confirmPasswordField.getPassword());

            if (password.equals(confirmPassword)) {
            	 // Register the new user with the provided email and password
                CreateRequest request = new CreateRequest()
                        .setEmail(email)
                        .setEmailVerified(false)
                        .setPassword(password)
                        .setDisabled(false);

                try {
                	
                    UserRecord userRecord = FirebaseAuth.getInstance().createUser(request);
                    JOptionPane.showMessageDialog(null, "User registered successfully. User ID: " + userRecord.getUid());
                } catch (FirebaseAuthException e) {
                    JOptionPane.showMessageDialog(null, "Error registering user: " + e.getMessage());
                    e.printStackTrace();
                }
            } else {
                JOptionPane.showMessageDialog(null, "Passwords do not match. Please try again.");
            }
        }
    }

}*/


