import java.util.List;
public class Options {
    private int WindowHeight; // Initialising all variables with the same name and type as stored in JSON
    private int WindowWidth;
    private List<Colours> colours; // The colours as a list of the colours class
    // Getter and setter methods
    public int getWindowHeight() {
        return WindowHeight;
    }

    public void setWindowHeight(int WindowHeight) {
        this.WindowHeight = WindowHeight;
    }

    public int getWindowWidth() {
        return WindowWidth;
    }

    public void setWindowWidth(int WindowWidth) {
        this.WindowWidth = WindowWidth;
    }

    public List<Colours> getColours() {
        return colours;
    }

    public void setColours(List<Colours> colours) {
        this.colours = colours;
    }
}
