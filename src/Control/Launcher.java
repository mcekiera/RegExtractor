package Control;

import java.awt.*;

/**
 * Only to launch application.
 */
public class Launcher {

    /**
     * Main method.
     * @param args
     */
    public static void main(String[] args){
        EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {
                new Main();
            }

        });
    }
}
