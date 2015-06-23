package Model;

/**
 * Enum class contains characters which are meta-characters in regular expressions but occurs also as elements of other
 * metacharacter of plays significant role in regex matching process.
 */
public enum Special {
    ESCAPE_MARK("\\"), BEGINNING_OF_LINE("^"), POSIX("p"), OPEN_PARANTHESIS("("), CLOSE_PARANTHESIS(")"),
    OPEN_SQUARE_BRACKET("["),CLOSE_SQUARE_BRACKET("]"),OPEN_CURLY_BRACKET("{"),CLOSE_CURLY_BRACKET("}"),
    QUESTION_MARK("?"),END_OF_LINE("$"), AND("&"),PLUS("+"),STAR("*");

    private final String character;

    Special(String character){
        this.character = character;
    }

    /**
     * Provide an Enum object representing by given character.
     * @param character String with metacharacter.
     * @return Enum object of given character or null, if there is no Enum represented by given character.
     */
    public static Special getSpecial(String character){
        for(Special element : Special.values()){
            if(element.character.equals(character)){
                return element;
            }
        }
        return null;
    }

}

