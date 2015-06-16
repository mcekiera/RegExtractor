package Model;

public enum Special {
    ESCAPE_MARK("\\"), BEGINNING_OF_LINE("^"), POSIX("p"), OPEN_PARANTHESIS("("), CLOSE_PARANTHESIS(")"),
    OPEN_SQUARE_BRACKET("["),CLOSE_SQUARE_BRACKET("]"),OPEN_CURLY_BRACKET("{"),CLOSE_CURLY_BRACKET("}"),
    QUESTION_MARK("?"),END_OF_LINE("$"), AND("&"),PLUS("+"),STAR("*");

    private final String character;

    Special(String character){
        this.character = character;
    }

    public static Special getSpecial(String character){
        for(Special element : Special.values()){
            if(element.character.equals(character)){
                return element;
            }
        }
        return null;
    }

}

