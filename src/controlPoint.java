// CS 335 Project 3
// 16 Nov 18
// Austin Williams
// Alexander Vanover

import java.awt.*;

public class controlPoint {

    boolean isEdge;
    boolean isSelected;
    int x;
    int y;
    Color currentColor;

    public controlPoint(boolean edge, int x, int y)
    {
        this.isEdge = edge;
        this.x = x;
        this.y = y;
        this.currentColor = Color.BLACK;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

}
