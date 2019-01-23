package mtc.fernanda.moyatransportes;

import android.content.Context;
import android.util.Log;

import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.android.gms.common.util.IOUtils;
import com.google.api.client.googleapis.auth.oauth2.*;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.Base64;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.Label;
import com.google.api.services.gmail.model.ListLabelsResponse;
import com.google.api.services.gmail.model.Message;


public class EmailService {
    private static final String SCOPE = "https://www.googleapis.com/auth/gmail.modify";
    private static final String APP_NAME = "mtc-email-notification";
    // Path to the client_secret.json file deloper Console
    private static final String CLIENT_SECRET_PATH = "/credentials.json";
    private static GoogleClientSecrets clientSecrets;
    private static GoogleAuthorizationCodeFlow flow;
    private static HttpTransport httpTransport;
    private static JsonFactory jsonFactory;
    private static Gmail service;

    public static void test(Context context) {
        Thread thread = new Thread(new Runnable(){
            @Override
            public void run() {
                try {
                    String requestUrl = getRequestUrl(context);
                    Reader inputString = new StringReader(requestUrl);

                    BufferedReader br = new BufferedReader(inputString);

                    emailCredentialSetup(br.readLine());

                    try {
                        sendMessage("moya.mtc@gmail.com" ,
                                createEmail("feerzinha@gmail.com", "moya.mtc@gmail.com", "Testão", "Oiee"));
                    } catch (MessagingException e) {
                        e.printStackTrace();
                    }

                } catch (IOException e) {

                    System.out.println("erro "+e);

                    e.printStackTrace();
                }
            }
        });

        thread.start();
    }

    public static String getRequestUrl(Context context) throws FileNotFoundException, IOException{
        httpTransport = new NetHttpTransport();
        jsonFactory = new JacksonFactory();

        BufferedReader reader = new BufferedReader(new InputStreamReader(context.getAssets().open("credentials.json"), "UTF-8"));

        clientSecrets = GoogleClientSecrets.load(jsonFactory, reader);

        Log.i("Fer", "clientSecrets: "+ clientSecrets);

        // Allow user to authorize via url.
        flow = new GoogleAuthorizationCodeFlow.Builder(
                httpTransport, jsonFactory, clientSecrets, Arrays.asList(SCOPE))
                .setAccessType("offline").setApprovalPrompt("auto").build();

        String url = flow.newAuthorizationUrl()
                .setRedirectUri(GoogleOAuthConstants.OOB_REDIRECT_URI).build();

        return url;
    }

    static void emailCredentialSetup(String code) throws IOException {
        // Generate Credential using retrieved code.

        Log.i("Fer", "code: "+ code);

//        GoogleTokenResponse response = flow.newTokenRequest(code)
//                .setRedirectUri(GoogleOAuthConstants.OOB_REDIRECT_URI)
//                .execute();

        GoogleTokenResponse response = flow.newTokenRequest("4/3ABd3tZtAi7VknRsFMq_JC6teCqieq5TK14YlOi8WLqxZPcuP6ZjBn4")
                .setRedirectUri(clientSecrets.getDetails().getRedirectUris().get(0)).execute();

        Log.i("Fer", "Resposta: "+ response);

        GoogleCredential credential = new GoogleCredential()
                .setFromTokenResponse(response);



        Log.i("Fer", "Credential: "+ credential);

        // Create a new authorized Gmail API client
        service = new Gmail.Builder(httpTransport, jsonFactory,
                credential).setApplicationName(APP_NAME).build();
    }

    public static MimeMessage createEmail(String to,
                                          String from,
                                          String subject,
                                          String bodyText)
            throws MessagingException {
        Properties props = new Properties();
        Session session = Session.getDefaultInstance(props, null);

        MimeMessage email = new MimeMessage(session);

        email.setFrom(new InternetAddress(from));
        email.addRecipient(javax.mail.Message.RecipientType.TO,
                new InternetAddress(to));
        email.setSubject(subject);
        email.setText(bodyText);
        return email;
    }

    public static Message createMessageWithEmail(MimeMessage emailContent)
            throws MessagingException, IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        emailContent.writeTo(buffer);
        byte[] bytes = buffer.toByteArray();
        String encodedEmail = Base64.encodeBase64URLSafeString(bytes);
        Message message = new Message();
        message.setRaw(encodedEmail);
        return message;

    }

    /**
     * Send an email from the user's mailbox to its recipient.
     * @param userId User's email address. The special value "me"
     * can be used to indicate the authenticated user.
     * @param emailContent Email to be sent.
     * @return The sent message
     * @throws MessagingException
     * @throws IOException
     */
    public static Message sendMessage(String userId,
                                      MimeMessage emailContent)
            throws MessagingException, IOException {
        Message message = createMessageWithEmail(emailContent);
        message = service.users().messages().send(userId, message).execute();

        System.out.println("Message id: " + message.getId());
        System.out.println(message.toPrettyString());
        return message;
    }


}
