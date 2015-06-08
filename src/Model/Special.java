package Model;

public enum Special {
    ESCAPE_MARK("\\"), BEGINNING_OF_LINE("^"), POSIX("p"), OPEN_PARANTHESIS("("), CLOSE_PARANTHESIS(")"),
    OPEN_SQUARE_BRACKET("["),CLOSE_SQUARE_BRACKET("]"),OPEN_CURLY_BRACKET("{"),CLOSE_CURLY_BRACKET("}"),
    QUESTION_MARK("?"),END_OF_LINE("$"), AND("&)");

    private final String character;

    Special(String character){
        this.character = character;
    }



}

