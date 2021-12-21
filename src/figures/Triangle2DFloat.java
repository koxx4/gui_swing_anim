package figures;

import java.awt.geom.Path2D;

public class Triangle2DFloat extends Path2D.Float {
    public Triangle2DFloat(float x, float y, float width, float height) {
        this.moveTo(x, y);
        this.lineTo(x + width / 2, y + height );
        this.lineTo(x + width, y);
        this.lineTo(x , y);
        this.closePath();
    }
}
