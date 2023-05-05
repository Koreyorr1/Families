import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import javax.swing.SwingUtilities;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;


public class Main {
	
private static String firebaseApiKey;
private static String DATABASE_URL;
private static String adminSDK;


public static void main(String[] args) {
	Logger.getLogger("").setLevel(Level.FINEST);
	loadConfig();
	initializeFirebase();

	 SwingUtilities.invokeLater(new Runnable() {
         public void run() {
            SignInScreen signInScreen = new SignInScreen();
             signInScreen.show();
         }
     });
}

static void loadConfig() {
    Properties prop = new Properties();

    try (FileInputStream fis = new FileInputStream("config.properties")) {
        prop.load(fis);
        setFirebaseApiKey(prop.getProperty("apiKey"));
        setDatabaseURL(prop.getProperty("DATABASE_URL"));
        setAdminSDK(prop.getProperty("adminSDK"));
        
    } catch (IOException e) {
        System.err.println("Error loading config.properties file: " + e.getMessage());
    }
}

static void initializeFirebase() {

    try {
        // Set the path to the service account key JSON file
        FileInputStream serviceAccount = new FileInputStream(getAdminSDK());
        // Initialize the Firebase Admin SDK
        FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .setDatabaseUrl(getDatabaseURL())
                .build();
        FirebaseApp.initializeApp(options);
        
    } catch (IOException e) {
        e.printStackTrace();
    }
}
protected void writeToDatabase(String data) {
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference ref = database.getReference("families");

    // Write data to the database
    ref.setValue(data, new DatabaseReference.CompletionListener() {
        @Override
        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
            if (databaseError != null) {
                System.out.println("Data could not be saved. " + databaseError.getMessage());
            } else {
                System.out.println("Data saved successfully.");
            }
        }
    });
}

protected void readFromDatabase() {
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference ref = database.getReference("families");

    ref.addValueEventListener(new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            for (DataSnapshot familySnapshot : dataSnapshot.getChildren()) {
                String familyId = familySnapshot.getKey();
                // Process each family data here
                // For example, retrieve the list of items
                for (DataSnapshot itemSnapshot : familySnapshot.child("items").getChildren()) {
                    String itemId = itemSnapshot.getKey();
                    String itemName = itemSnapshot.child("name").getValue(String.class);
                    boolean itemChecked = itemSnapshot.child("checked").getValue(Boolean.class);
                    // Process each item data here
                   
                
                }
            }
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            // Handle database errors here
        }
    });
}

//Getters and Setters

public static String getFirebaseApiKey() {
	return firebaseApiKey;
}
public static void setFirebaseApiKey(String firebaseApiKey) {
	Main.firebaseApiKey = firebaseApiKey;
}
public static String getDatabaseURL() {
	return DATABASE_URL;
}
public static void setDatabaseURL(String databaseURL) {
	Main.DATABASE_URL = databaseURL;
}
public static String getAdminSDK() {
	return adminSDK;
}
public static void setAdminSDK(String adminSDK) {
	Main.adminSDK = adminSDK;
}
}


