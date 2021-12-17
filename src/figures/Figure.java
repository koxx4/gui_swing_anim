/*
 * Original author : tb
 * Fork: koxx4
 */

package figures;/*
 * Original author : tb
 * Fork: koxx4
 */

import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.util.concurrent.ThreadLocalRandom;

public class Figure {
    private final Shape shape;
    private Vector2D velocity;
    private double pulsingAmplitude;
    private double pulsingSpeed;
    private double rotationSpeed;
    private AffineTransform lastTransform;


    public Figure(Shape shape) {
        this.shape = shape;
        this.velocity = new Vector2D(0,0);
        this.pulsingAmplitude = 1;
        this.pulsingSpeed = 1;
        this.rotationSpeed = 0;
        this.lastTransform = new AffineTransform();
    }

//    public void tick(double deltaTime){
//        shapeArea.reset();
//        shapeArea = new Area(shape);
//
//        this.recalculateCenter();
//
//        shapeArea.transform(AffineTransform.getTranslateInstance(
//                this.velocity.getX() * deltaTime, this.velocity.getY() * deltaTime));
//        this.recalculateCenter();
//
//        shapeArea.transform(AffineTransform.getRotateInstance(
//                Math.toRadians(rotationSpeed * deltaTime), shapeArea.getBounds().width * 0.5f,
//                shapeArea.getBounds().height * 0.5f));
//
//        this.recalculateCenter();
//
//        this.recalculateCenter();
//
//        //this.scaleBy(pulsingSpeed * deltaTime, pulsingSpeed * deltaTime);
//
//        this.calculatePulseDirection();
//    }

//    private void scaleBy(double factorX, double factorY){
//        shapeArea.transform(AffineTransform.getTranslateInstance(
//                -this.shapeArea.getBounds().width * 0.5f, -this.shapeArea.getBounds().height * 0.5f));
//        shapeArea.transform(AffineTransform.getScaleInstance(factorX, factorY));
//        shapeArea.transform(AffineTransform.getTranslateInstance(
//                this.shapeArea.getBounds().width * 0.5f, this.shapeArea.getBounds().height * 0.5f));
//    }

//    private void calculatePulseDirection(){
//        var shapeBounds = this.shapeArea.getBounds();
//        if (shapeBounds.height > shapeBounds.height * pulsingAmplitude ||
//                shapeBounds.width > shapeBounds.width * pulsingAmplitude){
//
//            if (pulsingSpeed > 1)
//                this.pulsingSpeed = 1/pulsingSpeed;
//        }
//        else if(shapeBounds.height < shapeBounds.height / pulsingAmplitude ||
//                shapeBounds.width < shapeBounds.width / pulsingAmplitude){
//
//            if (pulsingSpeed < 1)
//                this.pulsingSpeed = 1/pulsingSpeed;
//        }
//
//    }

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

    public AffineTransform getLastTransform() {
        return lastTransform;
    }

    public void setLastTransform(AffineTransform lastTransform) {
        this.lastTransform = lastTransform;
    }
}
