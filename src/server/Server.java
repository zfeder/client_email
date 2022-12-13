//MODEL
package server;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import app.Email;
import app.Message;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

import java.net.URL;
import java.util.ResourceBundle;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class Server {

    private final startServer start_server;

    private Map<String, ReentrantReadWriteLock> userSync; // Uso dei semafori

    private Map<String, ArrayList<Email>> emailListUser;

    private final ObservableList<logsView> logView; // Dati da visualizzare in tabella del Server

    public Server() throws FileNotFoundException {
        logView = FXCollections.observableArrayList();
        logView.add(0, new logsView("Avvio del server"));
        configUserSync();
        this.start_server = new startServer();
        start_server.setDaemon(true);
        start_server.start();

    }

    private void configUserSync() throws FileNotFoundException {
        Scanner s = new Scanner(new File("./resources/listemail/emails.txt"));
        ArrayList<String> listemail = new ArrayList<String>();
        userSync = new HashMap<>();
        emailListUser = new HashMap<>();
        while(s.hasNext()) {
            listemail.add(s.next());
        }
        s.close();
        System.out.println("USER:");
        for(int i = 0; i<listemail.size(); i++) {
            System.out.println(listemail.get(i));
            userSync.put(listemail.get(i), new ReentrantReadWriteLock());
            emailListUser.put(listemail.get(i), new ArrayList<>());
            configEmailListUser(listemail.get(i));
        }
        System.out.println("\nCOMANDI:");
    }

    private void configEmailListUser(String userMail) {
        Lock userLock = userSync.get(userMail).readLock();
        userLock.lock();
        try {
            FileReader file = new FileReader("email/" + userMail + ".txt");
            BufferedReader buffer = new BufferedReader(file);
            String line = buffer.readLine();
            while(line != null) {
                String[] split = line.split("-");
                Email email = new Email(Integer.parseInt(split[0]), split[1], split[2], split[3], split[4], split[5], split[6]);
                emailListUser.get(userMail).add(email);
                line = buffer.readLine();
            }
        } catch (Exception e) {
            System.out.println("ERRORE: Generato in configEmailListUser, " + e + "\n");
        }
        userLock.unlock();
    }

    private boolean controlNewEmail(String userMail) {
        Lock userLock = userSync.get(userMail).readLock();
        userLock.lock();
        boolean control = false;
        try {
            FileReader file = new FileReader("email/" + userMail + ".txt");
            BufferedReader buffer = new BufferedReader(file);
            int count_line = (int) buffer.lines().count();
            file = new FileReader("email/" + userMail + ".txt");
            buffer = new BufferedReader(file);
            for(int i = 0; i<count_line; i++) {
                String s = buffer.readLine();
                String[] email = s.split("-");
                if(Objects.equals(email[0], "1")) {
                    System.out.println("Nuova email nel client: " + userMail);
                    control = true;
                }
            }
        } catch (IOException e) {
            System.out.println("ERRORE: Generato in controlNewEmail, " + e + "\n");
        }
        userLock.unlock();
        return control;
    }

    // Scarica una nuova email dal server, imposta ID = 0 e segna l'email come letta
    private Email notifyNewEmail(String userMail) {
        Lock userLock = userSync.get(userMail).readLock();
        userLock.lock();
        emailListUser.get(userMail).removeIf(email -> email.getID() == 1);
        Email email = null;
        try {
            FileReader file = new FileReader("email/" + userMail + ".txt");
            BufferedReader buffer = new BufferedReader(file);
            int count_line = (int) buffer.lines().count();
            file = new FileReader("email/" + userMail + ".txt");
            buffer = new BufferedReader(file);
            for(int i = 0; i<count_line; i++) {
                String s = buffer.readLine();
                String[] split = s.split("-");
                if(Objects.equals(split[0], "1")) {
                    email = new Email(0, split[1], split[2], split[3], split[4],split[5],split[6]);
                    emailListUser.get(userMail).add(email);
                }
            }
        } catch (IOException e) {
            System.out.println("ERRORE: Generato in notifyNewEmail, " + e + "\n");
        }
        userLock.unlock();
        return email;
    }

    private void addNewEmail(String userMail) {
        Lock userLock = userSync.get(userMail).writeLock();
        userLock.lock();
        try {
            FileWriter fileWriter = new FileWriter("email/" + userMail + ".txt");
            for(Email email: emailListUser.get(userMail)) {
                FileWriter fileWriter1 = new FileWriter("email/" + userMail + ".txt", true);
                fileWriter1.write(0+"-"+email.getSender()+"-"+email.getReceiver()+"-"+email.getObject()+"-"
                        +email.getText()+"-"+email.getDate()+"-"+email.getOther_dest());
                fileWriter1.write("\n");
                fileWriter1.flush();
            }
        } catch (Exception e) {
            System.out.println("ERRORE: Generato in addNewEmail, " + e + "\n");
        }
        userLock.unlock();
    }

    private boolean sendNewMail(Email email, String userMail) {
        Lock userLock = userSync.get(userMail).readLock();
        userLock.lock();
        boolean email_send = true;
        try {
            FileWriter fileWriter = new FileWriter("email/" + email.getReceiver() + ".txt", true);
            fileWriter.write(1 + "-" + email.getSender() + "-" + email.getReceiver() + "-" + email.getObject() +
                    "-" + email.getText() + "-" + email.getDate() + "-" + email.getOther_dest());
            fileWriter.write("\n");
            fileWriter.flush();
            emailListUser.get(userMail).add(email);
            userLock.unlock();
            return email_send;
        } catch (Exception e) {
            System.out.println("ERRORE: Generato in sendNewMail, " + e + "\n");
            email_send = false;
            userLock.unlock();
            return email_send;
        }
    }

    private boolean removeEmail(Email email, String userMail) {
        Lock userLock = userSync.get(userMail).writeLock();
        userLock.lock();
        boolean control = false;
        try {
            ArrayList<Email> ListaMail = new ArrayList<>();
            int i = 0;
            FileReader file = new FileReader("email/" + userMail + ".txt");
            BufferedReader buffer = new BufferedReader(file);
            int count_line = (int) buffer.lines().count();
            file = new FileReader("email/" + userMail + ".txt");
            buffer = new BufferedReader(file);
            for(int l = 0; l<count_line; l++) {
                String s = buffer.readLine();
                String[] split = s.split("-");
                Email email1 = new Email(Integer.parseInt(split[0]), split[1], split[2], split[3], split[4], split[5], split[6]);
                ListaMail.add(email1);
                i++;
            }
            FileWriter fwOb = new FileWriter("email/" + userMail + ".txt", false);
            PrintWriter pwOb = new PrintWriter(fwOb, false);
            pwOb.flush();
            pwOb.close();
            fwOb.close();
            for (int k=0; k<ListaMail.size(); k++) {
                if (Objects.equals(email.getSender(), ListaMail.get(k).getSender()) &&
                        Objects.equals(email.getReceiver(), ListaMail.get(k).getReceiver()) &&
                        Objects.equals(email.getObject(), ListaMail.get(k).getObject()) &&
                        Objects.equals(email.getDate(), ListaMail.get(k).getDate())) {
                    emailListUser.get(userMail).remove(ListaMail.get(k));
                    System.out.println("Rimozione email nel client: " + userMail);
                } else {
                    FileWriter fileWriter1 = new FileWriter("email/" + userMail + ".txt", true);
                    fileWriter1.write(ListaMail.get(k).getID()
                            + "-" + ListaMail.get(k).getSender()
                            + "-" + ListaMail.get(k).getReceiver()
                            + "-" + ListaMail.get(k).getObject()
                            + "-" + ListaMail.get(k).getText()
                            + "-" + ListaMail.get(k).getDate()
                            + "-" + ListaMail.get(k).getOther_dest());
                    fileWriter1.write("\n");
                    fileWriter1.flush();
                }
            }
            control = true;
        } catch (IOException e) {
            System.out.println("ERRORE: Generato in removeEmail, " + e + "\n");
        }
        userLock.unlock();
        return control;
    }

    class startServer extends Thread {
        private ServerSocket serverSocket;
        private ExecutorService executorService;

        @Override
        public void run() {
            executorService = null;
            try {
                executorService = Executors.newSingleThreadExecutor();
                int port = 54231;
                serverSocket = new ServerSocket(port);
                while (true) {
                    Socket connection = serverSocket.accept();
                    executorService.execute(new Request(connection));
                }
            } catch (Exception e) {
                System.out.println("ERRORE 1: Generato in startServer, " + e + "\n");
            } finally {
                try {
                    if(serverSocket != null)
                        serverSocket.close();
                    if(executorService != null && !executorService.isShutdown())
                        Server.this.serverClose();
                } catch (IOException e) {
                    System.out.println("ERRORE 2: Generato in startServer, " + e + "\n");
                }
            }
        }

        class Request implements Runnable {
            Socket clientSocket;
            Server clientServer;

            public Request(Socket socket) {
                clientSocket = socket;
                clientServer = Server.this;
            }

            public void run() {
                Message message;
                ObjectInputStream inputStream = null;
                try {
                    inputStream = new ObjectInputStream(clientSocket.getInputStream());
                    Object msg_input = inputStream.readObject();
                    if(msg_input instanceof Message) {
                        message = (Message) msg_input;
                    } else {
                        return;
                    }
                    Object obj = null;
                    short status = -1;
                    String user;
                    String logMsg = null;
                    switch (message.getOperation()) {
                        case 1 -> {
                            user = ((Email) message.getObj()).getSender();
                            File file = new File("email/" + (((Email) message.getObj()).getReceiver()) + ".txt");
                            if (file.isFile()) {
                                if (sendNewMail((Email) message.getObj(), user)) {
                                    status = 0;
                                    obj = (Email) message.getObj();
                                    logMsg = "Email inviata con successo dal client: " + user ;
                                } else {
                                    obj = "Errore nell'inivio dell'email";
                                    logMsg = "Errore nell'invio dell'email" + (Email) message.getObj();
                                }
                            } else {
                                removeEmail((Email) message.getObj(), user);
                                obj = "Indirizzo non esistente" + ((Email) message.getObj()).getReceiver();
                                logMsg = "Indirizzo non esistente" + ((Email) message.getObj()).getReceiver();
                            }
                        }

                        case 2 -> {
                            user = (String) message.getObj();
                            if (controlNewEmail(user)) {
                                Email email = notifyNewEmail(user);
                                addNewEmail(user);
                                status = 0;
                                obj = Objects.requireNonNull(email);
                                logMsg = "Nuova email nel client: " + user;
                            } else {
                                obj = "Nessuna nuova email";
                                logMsg = "Nessuna nuova email nel client: " + user;
                            }
                        }

                        case 3 -> {
                            user = (((Email) message.getObj()).getReceiver());
                            if (removeEmail((Email) message.getObj(), user)) {
                                status = 0;
                                obj = "Rimozione email avvenuta con successo";
                                logMsg = "Rimozione email avvenuta con successo";
                            } else {
                                obj = "Errore nella rimozione dell'email";
                                logMsg = "Errore nella rimozione dell'email";
                            }
                        }

                        default -> {
                            obj = null;
                        }
                    }

                    ObjectOutputStream outputStream = null;
                    try {
                        Message m = new Message(status, obj);
                        outputStream = new ObjectOutputStream(clientSocket.getOutputStream());
                        outputStream.writeObject(m);
                        outputStream.close();
                    } catch (IOException e) {
                        System.out.println("ERRORE 1: Generato in startServer -> Request, " + e + "\n");
                    } finally {
                        try {
                            if(outputStream != null)
                                outputStream.close();
                        } catch (IOException e) {
                            System.out.println("ERRORE 2: Generato in startServer -> Request, " + e + "\n");
                        }
                    }
                    logView.add(0, new logsView(logMsg));
                } catch (IOException | ClassNotFoundException e) {
                    throw new RuntimeException(e);
                } finally {
                    try {
                        if(clientSocket != null)
                            clientSocket.close();
                        if(inputStream != null)
                            inputStream.close();
                    } catch (IOException e) {
                        System.out.println("ERRORE 3: Generato in startServer -> Request, " + e + "\n");
                    }
                }
            }
        }
    }
    public void setOn() {
        startServer serverButtOn = new startServer();
        serverButtOn.start();
        String logMsg = "Avvio del sever";
        logView.add(0, new logsView(logMsg));
    }
    public void setOff() {
        try {
            serverClose();
            start_server.serverSocket.close();
            String logMsg = "Chiusura del server";
            logView.add(0, new logsView(logMsg));
        } catch (IOException e) {
            System.out.println("ERRORE: Chiusura del server fallita,  " + e + "\n");
        }
    }
    public void serverClose() {
        start_server.executorService.shutdown();
        try {
            if(!start_server.executorService.awaitTermination(60, TimeUnit.SECONDS)) {
                start_server.executorService.shutdownNow();
                if(!start_server.executorService.awaitTermination(60, TimeUnit.SECONDS))
                    System.out.println("ERRORE: Generato in serverClose -> Thread in esecuzione");
            }
        } catch (InterruptedException ie) {
            start_server.executorService.shutdownNow();
            Thread.currentThread().interrupt();
            System.out.println("ERRORE: Generato in serverCLose, " + ie + "\n");
        }
    }
    public ObservableList<logsView> getLogView() {
        return logView;
    }
    class logsView {

        private final String tableEvent;

        private final String tableTime;

        public logsView(String event) {
            Date calendario = Calendar.getInstance().getTime();
            SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss");
            tableTime = sdf.format(calendario);
            tableEvent = event;
        }
        public String getTableEvent() {
            return tableEvent;
        }

        public String getTableTime() {
            return tableTime;
        }

    }
}
