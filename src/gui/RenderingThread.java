package gui;

import figures.Figure;
import org.apache.commons.lang3.time.StopWatch;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

final class RenderingThread extends Thread {

    private final StopWatch stopWatch;
    private final List<Figure> figuresToDraw;
    private final List<Figure> figuresToDrawBuffer;
    private final List<Figure> figuresToRemoveBuffer;
    private final int computerThreads;
    private final AnimationPanel animationPanel;
    private long frameTime;
    private ExecutorService transformThreads;
    private boolean isAnimationRunning;
    private long lastFrameTime;

    public RenderingThread(AnimationPanel target, long frameTime) {
        super("RenderThread-1");
        this.computerThreads = Runtime.getRuntime().availableProcessors();
        this.animationPanel = target;
        this.stopWatch = new StopWatch();
        this.figuresToDraw = new ArrayList<>(20);
        this.figuresToDrawBuffer = new ArrayList<>(20);
        this.figuresToRemoveBuffer = new ArrayList<>(2);
        this.frameTime = frameTime;
        this.isAnimationRunning = false;
    }

    public synchronized void addFigure(Figure figureToAdd){
        this.figuresToDrawBuffer.add(figureToAdd);
    }

    public synchronized void removeFigure(Figure figureToAdd){
        this.figuresToRemoveBuffer.add(figureToAdd);
    }

    public synchronized boolean isAnimationRunning() {
        return isAnimationRunning;
    }

    public synchronized void setAnimationRunning(boolean animationRunning) {
        isAnimationRunning = animationRunning;
    }

    public synchronized long getLastFrameTime() {
        return lastFrameTime;
    }

    public synchronized void updateFrameTime(long value){
        this.frameTime = value;
    }

    @Override
    public void run() {
        stopWatch.start();
        while (true){

			if (!figuresToDrawBuffer.isEmpty()){
                    stopWatch.suspend();
                    figuresToDraw.addAll(figuresToDrawBuffer);
                    figuresToDrawBuffer.clear();
                    stopWatch.resume();
			}
            if (!figuresToRemoveBuffer.isEmpty()){
                stopWatch.suspend();
                figuresToDraw.removeAll(figuresToRemoveBuffer);
                figuresToRemoveBuffer.clear();
                stopWatch.resume();
            }

            if (!isAnimationRunning()) {
                try {
                    synchronized (this){
                        stopWatch.stop();
                        stopWatch.reset();
                        wait();
                        stopWatch.start();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            var deltaTime = stopWatch.getTime();
            if (deltaTime > frameTime){
                lastFrameTime = deltaTime;
                stopWatch.reset();
                stopWatch.start();
                transformFigures(deltaTime / 1000f);
                animationPanel.renderNextFrame(figuresToDraw);
            }
        }
    }

    private void transformFigures(final double deltaTime) {
        transformThreads = Executors.newFixedThreadPool(computerThreads);

        for (var figure : figuresToDraw){
            transformThreads.submit(() -> {
                var figureBounds = figure.getArea().getBounds();
                var figureCenter = new Vector2D(
                        figureBounds.x + figureBounds.width * 0.5f,
                        figureBounds.y + figureBounds.height * 0.5f);

                keepFigureInBounds(figure);
                keepScaleInBounds(figure);

                AffineTransform figureTransform = new AffineTransform();
                figureTransform.translate(figureCenter.getX(), figureCenter.getY());
                figureTransform.scale(figure.getPulsingSpeed(), figure.getPulsingSpeed());
                figureTransform.rotate(Math.toRadians(figure.getRotationSpeed() * deltaTime));
                figureTransform.translate( -figureCenter.getX(), -figureCenter.getY());
                figureTransform.translate(
                        figure.getVelocity().getX() * deltaTime,
                        figure.getVelocity().getY() * deltaTime);

                figure.getArea().transform(figureTransform);
            });
        }
        transformThreads.shutdown();
        try {
            transformThreads.awaitTermination(frameTime / 1000, TimeUnit.MILLISECONDS);
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }

    private void keepFigureInBounds(final Figure figure) {
        var figureBounds = figure.getArea().getBounds();
        var figureCenter = new Vector2D(
                figureBounds.x + figureBounds.width * 0.5f,
                figureBounds.y + figureBounds.height * 0.5f);
        var figureVelocityX = figure.getVelocity().getX();
        var figureVelocityY = figure.getVelocity().getY();

        if (figureCenter.getX() > animationPanel.getWidth()){
            figure.getArea().transform(
                    AffineTransform.getTranslateInstance(-(figureCenter.getX() - animationPanel.getWidth()), 0));
            figure.setVelocity(new Vector2D(-figureVelocityX, figureVelocityY));
        }
        else if (figureCenter.getX() < 0){
            figure.getArea().transform(
                    AffineTransform.getTranslateInstance(-figureCenter.getX(), 0));
            figure.setVelocity(new Vector2D(-figureVelocityX, figureVelocityY));
        }

        if (figureCenter.getY() > animationPanel.getHeight()){
            figure.getArea().transform(
                    AffineTransform.getTranslateInstance(0, -(figureCenter.getY() - animationPanel.getHeight())));
            figure.setVelocity(new Vector2D(figureVelocityX, -figureVelocityY));
        }
        else if (figureCenter.getY() < 0){
            figure.getArea().transform(
                    AffineTransform.getTranslateInstance(0, -figureCenter.getY()));
            figure.setVelocity(new Vector2D(figureVelocityX, -figureVelocityY));
        }

    }

    private void keepScaleInBounds(final Figure figure){
        var figureBounds = figure.getArea().getBounds();

        if (figureBounds.width > figure.getInitialWidth() * figure.getPulsingAmplitude() ||
                figureBounds.height > figure.getInitialHeight() * figure.getPulsingAmplitude())
            if (figure.getPulsingSpeed() > 1)
                figure.setPulsingSpeed(1 / figure.getPulsingSpeed());

        if (figureBounds.width < figure.getInitialWidth() / figure.getPulsingAmplitude() ||
                figureBounds.height < figure.getInitialHeight() / figure.getPulsingAmplitude())
            if (figure.getPulsingSpeed() < 1)
                figure.setPulsingSpeed(1 / figure.getPulsingSpeed());

    }
}
