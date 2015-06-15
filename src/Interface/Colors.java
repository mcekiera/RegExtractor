package Interface;

import java.awt.*;

public enum Colors{
    LRED(new Color(0xF6666)),LYELLOW(new Color(0xFFFF99)),LGREEN(new Color(0x66FF66)),GRAY(new Color(0xCCCCCC)),
    LBLUE(new Color(0x00FFFF)),LVIOLET(new Color(0xFF66FF)),RED(new Color(0xFF0000)),YELLOW(new Color(0xFFFF00)),
    GOLD(new Color(0xCCCC90)),GREEN(new Color(0x00CC00)),BLUE(new Color(0x0000FF)),VIOLET(new Color(0x9900CC)),
    PINK(new Color(0xFF0099)),DRED(new Color(0x990000)),DYELLOW(new Color(0xCC9900)),DGREEN(new Color(0x003300)),
    DBLUE(new Color(0x003399)),DVIOLET(new Color(0x330066)),DPINK(new Color(0x990066));


    private final Color color;

    Colors(Color color){
        this.color = color;
    }

    public Color asColor(){
        return this.color;
    }
}
