package chatterboxclient;

import java.util.ArrayList;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * Model to be extended by all models in the client.
 */
public class ClientModel {

    private ArrayList<ChangeListener> listenerList = new ArrayList<>();

    public synchronized void addListener(ChangeListener cl) {
        listenerList.add(cl);
    }

    public synchronized void removeListener(ChangeListener cl) {
        listenerList.remove(cl);
    }

    protected void fireStateChanged(ChangeEvent e) {
        ArrayList<ChangeListener> copy;
        synchronized (this) {
            copy = (ArrayList<ChangeListener>) listenerList.clone();
        }
        for (ChangeListener cl : copy) {
            cl.stateChanged(e);
        }
    }
}
