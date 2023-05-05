import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.people.v1.PeopleService;
import com.google.api.services.people.v1.model.Person;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.Arrays;

public class GoogleSignIn {

    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private static final String CLIENT_SECRET_FILE = "/client_secret.json";
    private static final String TOKENS_DIRECTORY_PATH = "tokens";
    private static final String APPLICATION_NAME = "Your Application Name";

    private static final String[] SCOPES = {
            "https://www.googleapis.com/auth/userinfo.email",
            "https://www.googleapis.com/auth/userinfo.profile"
    };

    private static Credential getCredentials(NetHttpTransport httpTransport) throws IOException {
        InputStream in = GoogleSignIn.class.getResourceAsStream(CLIENT_SECRET_FILE);
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                httpTransport, JSON_FACTORY, clientSecrets, Arrays.asList(SCOPES))
                .setDataStoreFactory(new FileDataStoreFactory(new File(TOKENS_DIRECTORY_PATH)))
                .setAccessType("offline")
                .build();

        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
        return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
    }

    public static void signIn() {
        try {
            final NetHttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
            Credential credential = getCredentials(httpTransport);

            PeopleService peopleService = new PeopleService.Builder(httpTransport, JSON_FACTORY, credential)
                    .setApplicationName(APPLICATION_NAME)
                    .build();

            Person me = peopleService.people().get("people/me")
                    .setPersonFields("emailAddresses,names,photos")
                    .execute();

            System.out.println("User Information:");
            System.out.println("Name: " + me.getNames().get(0).getDisplayName());
            System.out.println("Email: " + me.getEmailAddresses().get(0).getValue());
            System.out.println("Profile Photo URL: " + me.getPhotos().get(0).getUrl());

            // Use this authenticated user to interact with your Firebase Realtime Database

        } catch (GeneralSecurityException | IOException e) {
            e.printStackTrace();
        }
    }

}

