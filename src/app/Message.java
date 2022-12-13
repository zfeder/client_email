package app;

import java.io.Serializable;

/*
OPERAZIONI:
   -1 - ERROR
    0 - SUCCESS
    1 - SEND NEW MAIL
    2 - CHECK NEW MAIL
    3 - REMOVE EMAIL RECEIVE
    4 - REMOVE EMAIL SEND
 */

public class Message implements Serializable{
    private final int operation;
    private final Object obj;

    public Message(int operation, Object obj) {
        this.operation = operation;
        this.obj = obj;
    }
    public int getOperation() {
        return operation;
    }
    public Object getObj() {
        return obj;
    }
}


