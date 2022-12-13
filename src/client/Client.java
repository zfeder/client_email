//MODEL
package client;

import javafx.application.Platform;
import javafx.beans.property.SimpleListProperty;

import javafx.collections.FXCollections;
import javafx.beans.property.ListProperty;
import javafx.collections.ObservableList;
import app.Email;
import app.Message;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ConnectException;
import java.net.Socket;

import java.io.*;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Objects;

public class Client {
    private final String emailUser;
    private final ObservableList<Email> listEmailSend;
    private final ObservableList<Email> listEmailReceive;
    private final ListProperty<Email> listSend;
    private final ListProperty<Email> listReceive;
    private final SimpleListProperty<String> listNotify;

    public Client(String email) {
        emailUser = email;
        listEmailSend = FXCollections.observableList(new LinkedList<>());
        listSend = new SimpleListProperty<>();
        listSend.set(listEmailSend);
        listEmailReceive = FXCollections.observableList(new LinkedList<>());
        listReceive = new SimpleListProperty<>();
        listReceive.set(listEmailReceive);
        startClient startClientNew = new startClient(2, this.emailUser);
        startClientNew.start();
        listNotify = new SimpleListProperty<>();
        listNotify.set(FXCollections.observableArrayList(new ArrayList<>()));
    }

    public String getEmailUser() {
        return emailUser;
    }

    public ObservableList<Email> getListEmailSend() {
        return listEmailSend;
    }

    public ObservableList<Email> getListEmailReceive() {
        return listEmailReceive;
    }

    public ListProperty<Email> getListSend() {
        return listSend;
    }

    public ListProperty<Email> getListReceive() {
        return listReceive;
    }

    public SimpleListProperty<String> getListNotify() {
        return listNotify;
    }

    public void removeEmailReceive(Email email) {
        listEmailReceive.remove(email);
        startClient startClientRemoveEmailReceive = new startClient(3, email);
        startClientRemoveEmailReceive.start();
    }
    public void downloadEmail() {
        try {
            FileReader fileReader = new FileReader("email/" + emailUser + ".txt");
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            int count_line = (int) bufferedReader.lines().count();
            fileReader = new FileReader("email/" + emailUser + ".txt");
            bufferedReader = new BufferedReader(fileReader);
            for(int i = 0; i<count_line; i++) {
                String s = bufferedReader.readLine();
                if(s != null) {
                    String[] split = s.split("-");
                    if(Objects.equals(split[1], getEmailUser())) {
                        Email email_send = new Email(Integer.parseInt(split[0]), split[1], split[2], split[3], split[4],split[5],split[6]);
                        listEmailSend.add(email_send);
                    } else if (!Objects.equals(split[1], getEmailUser()) && Objects.equals(split[2], getEmailUser())) {
                        Email email_receive = new Email(Integer.parseInt(split[0]), split[1], split[2], split[3], split[4],split[5],split[6]);
                        listEmailReceive.add(email_receive);
                    }
                }
            }
        } catch(IOException e) {
            System.out.println("ERRORE: Generato in downloadEmail -> File non trovato, " + e+ "\n");
        }
    }

    public void sendNewEmail(Email email) throws IOException {
        FileWriter fileWriter = new FileWriter("email/" + emailUser + ".txt", true);
        fileWriter.write(0 + "-" + email.getSender() + "-" + email.getReceiver() + "-" + email.getObject() + "-" + email.getText() + "-" + email.getDate() + "-" + email.getOther_dest());
        fileWriter.write("\n");
        fileWriter.flush();
        startClient startClientSend = new startClient(1, email);
        startClientSend.start();
    }

    class startClient extends Thread {
        private final int operation;
        private final Object object;
        public startClient(int op, Object obj) {
            operation = op;
            object = obj;
            this.setDaemon(true);
        }
        @Override
        public void run() {
            Message message = new Message(operation, object);
            Message message_server;
            if(operation == 2) {
                while (true) {
                    try {
                        Thread.sleep(10000);
                    } catch (InterruptedException e) {
                        System.out.println("ERRORE: Generato in startClient, " + e + "\n");
                    }
                    message_server = connectionServer(message);
                    assert message_server != null;
                    if(message_server.getOperation() == 0) {
                        Email email = (Email) message_server.getObj();
                        System.out.println("Client: " + email.getReceiver() + " ha ricevuto un email da: " + email.getSender() + "\n");
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                listEmailReceive.add(email);
                                listNotify.add(0, "Nuova email da: " + email.getSender());
                            }
                        });
                    }
                }
            } else {
                message_server = connectionServer(message);
                assert message_server != null;
                Message message_server_final = message_server;
                if(message_server.getOperation() == 0) {
                    switch (this.operation) {
                        case 1 -> {
                            Email email = (Email) message_server.getObj();
                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
                                    listEmailSend.add(email);
                                    listNotify.add(0, "Email inviata con successo a: " + ((Email) message_server_final.getObj()).getReceiver());
                                }
                            });
                        }

                        case 3 -> Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
                                    listNotify.add(0, "Email rimossa con successo");
                                }
                            });

                        default -> {
                        }
                    }
                } else if (message_server.getOperation() == -1) {
                    switch (this.operation) {
                        case 1 -> {
                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
                                    listNotify.add(0, (String) message_server_final.getObj());
                                }
                            });
                        }

                        case 3 -> Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
                                    listNotify.add(0, (String) message_server_final.getObj());
                                }
                            });

                        default -> {
                        }
                    }
                }

            }
        }

        private Message connectionServer(Message message_server) {
            Socket clientSocket = null;
            ObjectOutputStream outputStream = null;
            ObjectInputStream inputStream = null;
            boolean verify = false;
            while(true) {
                try {
                    String host = "localhost";
                    int port = 54231;
                    clientSocket = new Socket(host, port);
                    outputStream = new ObjectOutputStream(clientSocket.getOutputStream());
                    outputStream.writeObject(message_server);
                    inputStream = new ObjectInputStream(clientSocket.getInputStream());
                    if(verify) {
                        verify = false;
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                listNotify.add(0, "Server online");
                            }
                        });
                    }
                    return (Message) inputStream.readObject();
                } catch (ConnectException ce) {
                    verify = true;
                    System.out.println("ERRORE 1: Generato in connectionServer -> Nessuna connessione al server, " + ce + "\n");
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            listNotify.add(0, "Server offline");
                        }
                    });
                    try {
                        Thread.sleep(20000); // Genera busy waiting
                    } catch (Exception ex1) {
                        System.out.println("ERRORE 2: Generato in connectionServer, " + ex1 + "\n");
                    }
                } catch (Exception ex2) {
                    System.out.println("ERRORE 3: Generato in connectionServer, " + ex2 + "\n");                                    
                } finally {
                    try {
                        if(outputStream != null)
                            outputStream.close();
                        if (inputStream != null)
                            inputStream.close();
                        if (clientSocket != null)
                            clientSocket.close();
                    } catch (Exception ex3) {
                        System.out.println("ERRORE 4: Generato in connectionServer, " + ex3 + "\n");
                    }
                }
            }
        }
    }
}
