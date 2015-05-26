package Control;

import Interface.UserInterface;
import Model.Extractor;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Main {
    Extractor extractor;
    UserInterface userInterface;

    Main(){
        extractor = new Extractor();
        userInterface = new UserInterface(this);
    }

    public static void main(String[] args){
        Main main = new Main();
    }

    public ActionListener getListener(Action action){
        switch (action){
            case INPUTCHANGE:
                return new InputListener();
            default:
                return null;
        }

    }

    private class InputListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String regex = userInterface.getRegEx();
            String text = userInterface.getTextForRetrieval();

            userInterface.highlightContent(Extractor.search(regex,text));
        }
    }
}
