package pl.alrasek.mailreceiver.service;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.Properties;
import java.util.function.Predicate;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.search.*;

/**
 * Created by Aleksander.Rasek on 2017-10-30.
 */
public class GmailService {

    public static void main (String...s){
        checkMail("aleksander.rasek@gmail.com","ae49jk78bc#@!");
    }
    public static void checkMail(String username, String password) {
        Properties props = new Properties();
        props.setProperty("mail.store.protocol", "imaps");
        try {
            Session session = Session.getInstance(props, null);
            Store store = session.getStore();
            store.connect("imap.gmail.com", username, password);
            Folder inbox = store.getFolder("INBOX");
            inbox.open(Folder.READ_ONLY);

            //Message[] msgs = inbox.getMessages();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date october = sdf.parse("2017-09-01");
            SearchTerm newerThan = new ReceivedDateTerm(ComparisonTerm.GT, october);
            SearchTerm sender = new FromTerm(new InternetAddress("twoja_f@ktura.t-mobile.pl"));
            SearchTerm and = new AndTerm(newerThan,sender);
            Message[] msgs = inbox.search(and);
            Arrays.asList(msgs).stream()
                    .filter(filterBySender("twoja_f@ktura.t-mobile.pl"))
                    .forEach(msg -> printMessage(msg));
            inbox.close(false);
            store.close();

        } catch (Exception mex) {
            mex.printStackTrace();
        }
    }

    private static Predicate<? super Message> filterBySender(String sender) {
        return p -> {
            try {
                return p.getFrom()[0].toString().contains(sender);
            } catch (MessagingException e) {
                e.printStackTrace();
            }
            return false;
        };
    }

    private static void printMessages(Message[] msgs) throws MessagingException {

        for (Message msg : msgs) {
            printMessage(msg);
        }
    }

    private static void printMessage(Message msg) {
        try {
            Address[] in = msg.getFrom();
            for (Address address : in) {
                System.out.println("FROM:" + address.toString());
            }
            System.out.println("SENT DATE:" + msg.getSentDate());
            System.out.println("SUBJECT:" + msg.getSubject());
            if (msg.getContent() instanceof Multipart) {
                Multipart mp = (Multipart) msg.getContent();
                BodyPart bp = mp.getBodyPart(0);
                System.out.println("CONTENT:" + bp.getContent());
            } else if (msg.getContent() instanceof String) {
                System.out.println("CONTENT:" + msg.getContent());
                msg.getMessageNumber();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
