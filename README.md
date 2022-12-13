# Client Email in JAVA

Applicazione Java che implementa un servizio di posta elettronica organizzato con un mail server che gestisce le caselle di posta elettronica degli utenti 
e i mail client necessari per permettere agli utenti di accedere alle proprie caselle di posta.

Il mail server gestisce una lista di caselle di posta elettronica e ne mantiene la persistenza utilizzando 
file (txt o binari) per memorizzare i messaggi in modo permanente.

Il mail server ha un’interfaccia grafica sulla quale viene visualizzato il log delle azioni effettuate dai mail clients e 
degli eventi che occorrono durante l’interazione tra i client e il server.

Una casella di posta elettronica contiene:
  Nome dell’account di mail associato alla casella postale. 
  Lista (eventualmente vuota) di messaggi. 
  I messaggi di posta elettronica sono istanze di una classe Email.

L’interfaccia permette di:
  Creare e inviare un messaggio a uno o più destinatari.
  Leggere i messaggi della casella di posta.
  Rispondere a un messaggio ricevuto, in Reply.
  Girare (forward) un messaggio a uno o più account di posta elettronica.
  Rimuovere un messaggio dalla casella di posta.
  
L’interfaccia mostra sempre la lista aggiornata dei messaggi in casella e, quando arriva un nuovo messaggio, notifica l’utente attraverso una finestra di dialogo.


L’applicazione è basata su architettura MVC, con Controller + viste e Model, seguendo i principi del pattern Observer Observable. 

I client e il server dell’applicazione permettono di parallelizzare le attività che non necessitano di esecuzione sequenziale e 
gestire gli eventuali problemi di accesso a risorse in mutua esclusione. 
L’applicazione è distribuita attraverso l’uso di Socket Java.

# Server Panel

<img src="https://github.com/zfeder/client_email/blob/main/image/server.png" width=40% height=40%>



# Client Panel

<img src="https://github.com/zfeder/client_email/blob/main/image/client.png" width=60% height=60%>



# New Email Client

<img src="https://github.com/zfeder/client_email/blob/main/image/new_email.png" width=60% height=60%>






