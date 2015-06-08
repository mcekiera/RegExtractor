package Model;

public enum Metacharacter {
    ESCAPE_MARK("\\"), BEGINNING_OF_LINE("^"), POSIX("p"), OPEN_PARANTHESIS("("), CLOSE_PARANTHESIS(")"),
    OPEN_SQUARE_BRACKET("["),CLOSE_SQUARE_BRACKET("]"),OPEN_CURLY_BRACKET("{"),CLOSE_CURLY_BRACKET("}"),
    QUESTION_MARK("?"),END_OF_LINE("$");

    private final String character;

    Metacharacter(String character){
        this.character = character;
    }



}

