/*
 * Original author : tb
 * Fork: koxx4
 */

package figures;

import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;

import java.awt.*;
import java.awt.geom.Area;

public class Figure{
    private final Shape shape;
    private final Area area;
    private final double initialHeight;
    private final double initialWidth;
    private Color color;
    private Vector2D velocity;
    private double pulsingAmplitude;
    private double pulsingSpeed;
    private double rotationSpeed;

    public Figure(Shape shape, Color color) {
        this.shape = shape;
        this.area = new Area(shape);
        this.color = color;
        this.velocity = new Vector2D(0,0);
        this.pulsingAmplitude = 1;
        this.pulsingSpeed = 1;
        this.rotationSpeed = 0;
        this.initialWidth = shape.getBounds().width;
        this.initialHeight = shape.getBounds().height;
    }

    public Figure(Shape shape) {
        this(shape, Color.BLUE);
    }

    public Shape getShape() {
        return shape;
    }

    public Vector2D getVelocity() {
        return velocity;
    }

    public void setVelocity(Vector2D velocity) {
        this.velocity = velocity;
    }

    public double getPulsingAmplitude() {
        return pulsingAmplitude;
    }

    public void setPulsingAmplitude(double pulsingAmplitude) {
        this.pulsingAmplitude = pulsingAmplitude;
    }

    public double getPulsingSpeed() {
        return pulsingSpeed;
    }

    public void setPulsingSpeed(double pulsingSpeed) {
        this.pulsingSpeed = pulsingSpeed;
    }

    public double getRotationSpeed() {
        return rotationSpeed;
    }

    public void setRotationSpeed(double rotationSpeed) {
        this.rotationSpeed = rotationSpeed;
    }

    public Area getArea() {
        return area;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public double getInitialHeight() {
        return initialHeight;
    }

    public double getInitialWidth() {
        return initialWidth;
    }
}
