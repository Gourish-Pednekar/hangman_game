import java.awt.*;

public class CommonConstants {
    // file paths
    public static final String Java_E = "src/resources/javaez.txt";
    public static final String Java_M = "src/resources/javamed.txt";
    public static final String Java_H = "src/resources/javah.txt";
    public static final String Sql_E = "src/resources/sqlez.txt";
    public static final String Sql_M = "src/resources/sqlmed.txt";
    public static final String Sql_H = "src/resources/sqlh.txt";

    public static final String IMAGE_PATH = "resources/1.png";
    public static final String FONT_PATH = "resources/Cartoonero.ttf";

    // color config
    public static final Color PRIMARY_COLOR = Color.decode("#14212D");
    public static final Color SECONDARY_COLOR = Color.decode("#FCA311");
    public static final Color BACKGROUND_COLOR = Color.decode("#101820");

    // size config
    public static final Dimension FRAME_SIZE = new Dimension(540, 860);
    public static final Dimension BUTTON_PANEL_SIZE = new Dimension(FRAME_SIZE.width, (int)(FRAME_SIZE.height * 0.42));
    public static final Dimension RESULT_DIALOG_SIZE = new Dimension((int)(FRAME_SIZE.width/2), (int)(FRAME_SIZE.height/6));
}
