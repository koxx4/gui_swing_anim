/*
 * Original author : tb
 * Fork: koxx4
 */

package gui;


import figures.Figure;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

import javax.swing.*;

public class AnimationPanel extends JPanel implements ActionListener {

	private Image backBuffer;
	private Graphics2D screenGraphics;
	private Graphics2D bufferGraphics;
	private final Timer timer;
	private final List<Figure> figuresToDraw;
	private final ThreadLocalRandom random = ThreadLocalRandom.current();
	private final double fps;

	public AnimationPanel(int initialWidth, int initialHeight, double fps) {
		super();
		this.setBackground(Color.WHITE);
		this.setOpaque(true);
		this.setPreferredSize(new Dimension(initialWidth, initialHeight));
		this.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				super.componentResized(e);
				createGraphicsContext(e.getComponent().getWidth(), e.getComponent().getHeight());
			}
		});

		this.fps = fps;
		figuresToDraw = new ArrayList<>();
		timer = new Timer( (int) (1/fps * 1000), this);
	}

	public void addFigure(Figure figureToDraw) {
		SwingUtilities.invokeLater(() -> figuresToDraw.add(figureToDraw));
	}

	public void toggleAnimation() {
		if (timer.isRunning())
			timer.stop();
		else
			timer.start();
	}

	@Override
	public void setSize(int newWidth, int newHeight){
		super.setSize(newWidth, newHeight);
		this.createGraphicsContext(newWidth, newHeight);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		ExecutorService executorService = Executors.newCachedThreadPool();
		double deltaTime = 1 / fps;
		for (var figure : figuresToDraw){
			executorService.submit(() -> {
				var oldTransform = bufferGraphics.getTransform();
				var figureBounds = figure.getShape().getBounds();
				var center = new Vector2D(
						figureBounds.x + figureBounds.width * 0.5f,
						figureBounds.y + figureBounds.height * 0.5f);

				bufferGraphics.setTransform(figure.getLastTransform());

				bufferGraphics.rotate(figure.getRotationSpeed() * deltaTime, center.getX(), center.getY());
				bufferGraphics.translate(
						figure.getVelocity().getX() * deltaTime,
						figure.getVelocity().getY() * deltaTime);

				figure.setLastTransform(bufferGraphics.getTransform());

				bufferGraphics.setColor(Color.DARK_GRAY);
				bufferGraphics.fill(figure.getShape());
				bufferGraphics.draw(figure.getShape());
				bufferGraphics.setTransform(oldTransform);
			});
		}
		executorService.shutdown();
		try {
			executorService.awaitTermination(70, TimeUnit.MILLISECONDS);
		} catch (InterruptedException ex) {
			ex.printStackTrace();
		}

		screenGraphics.drawImage(backBuffer, 0, 0, null);
		bufferGraphics.clearRect(0, 0,
				backBuffer.getWidth(null),
				backBuffer.getHeight(null));
	}

	private void applyRenderingHints() {
		bufferGraphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		screenGraphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
	}

	public void createGraphicsContext(int width, int height){
		backBuffer = createImage(width, height);
		bufferGraphics = (Graphics2D) backBuffer.getGraphics();
		screenGraphics = (Graphics2D) getGraphics();
		applyRenderingHints();
	}

	public void recreateGraphicsContext(int width, int height){
		var oldBackBuffer = createImage(backBuffer.getWidth(null),
				backBuffer.getHeight(null));

		backBuffer = createImage(width, height);
		bufferGraphics = (Graphics2D) backBuffer.getGraphics();
		bufferGraphics.drawImage(oldBackBuffer, 0, 0, null);
		screenGraphics = (Graphics2D) getGraphics();
		applyRenderingHints();
	}

}
