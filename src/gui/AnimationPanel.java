/*
 * Original author : tb
 * Fork: koxx4
 */

package gui;


import figures.Figure;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AnimationPanel extends JPanel {

	private final long frameTime;
	private final RenderingThread renderingThread;
	private final List<Figure> figuresOnScreen;
	private int targetedFPS;
	private Image backBuffer;
	private Graphics2D bufferGraphics;
	private Figure selectedFigure;
	private ActionListener selectedFigureCallback;

	public AnimationPanel(int initialWidth, int initialHeight, int targetFPS) {
		super();
		initialize(initialWidth, initialHeight);

		this.targetedFPS = targetFPS;
		this.frameTime = (long) ((1d / targetFPS) * 1000f);
		this.renderingThread = new RenderingThread(this, frameTime);
		this.figuresOnScreen = new ArrayList<>();

		SwingUtilities.invokeLater(() -> {
			createGraphicsContext(initialWidth, initialHeight);
			renderingThread.start();
		});
	}

	private void initialize(int initialWidth, int initialHeight) {
		this.setLayout(null);
		this.setDoubleBuffered(false);
		this.setBackground(Color.WHITE);
		this.setOpaque(false);
		this.setIgnoreRepaint(true);
		this.setPreferredSize(new Dimension(initialWidth, initialHeight));

		this.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				super.componentResized(e);
				createGraphicsContext(e.getComponent().getWidth(), e.getComponent().getHeight());
			}
		});

		this.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent mouseEvent) {

				new SwingWorker<Optional<Figure>, Void>(){

					@Override
					protected Optional<Figure> doInBackground() throws Exception {
						var mousePos = mouseEvent.getLocationOnScreen();
						SwingUtilities.convertPointFromScreen(mousePos, mouseEvent.getComponent());
						return figuresOnScreen
								.stream()
								.filter(figure -> figure.getArea().contains(mousePos) )
								.findFirst();
					}

					@Override
					protected void done() {
						try {
							if (SwingUtilities.isRightMouseButton(mouseEvent))
								this.get().ifPresent(AnimationPanel.this::deleteFigure);
							else if (SwingUtilities.isLeftMouseButton(mouseEvent))
								this.get().ifPresent(figure -> {
									AnimationPanel.this.selectedFigure = figure;
									if (selectedFigureCallback != null)
										selectedFigureCallback
												.actionPerformed(new ActionEvent(this, 0, null));
								});
						} catch (Exception e){
							e.printStackTrace();
						}
					}

				}.execute();
			}
		});

	}

	public Figure getSelectedFigure() {
		return selectedFigure;
	}

	public void setSelectedFigureCallback(ActionListener actionListener){
		this.selectedFigureCallback = actionListener;
	}

	public float getCurrentFPS(){
		return Math.round(1f / (renderingThread.getLastFrameTime() / 1000f));
	}

	public void addFigure(Figure figureToDraw) {
		renderingThread.addFigure(figureToDraw);
		figuresOnScreen.add(figureToDraw);
	}

	public void deleteFigure(Figure figureToDelete){
		renderingThread.removeFigure(figureToDelete);
		figuresOnScreen.remove(figureToDelete);
		if (figureToDelete == selectedFigure)
			selectedFigure = null;
	}

	public void toggleAnimation() {
		if (renderingThread.isAnimationRunning())
			renderingThread.setAnimationRunning(false);
		else {
			renderingThread.setAnimationRunning(true);
			synchronized (renderingThread){
				renderingThread.notify();
			}
		}
	}

	public void renderNextFrame(final List<Figure> figuresToDraw) {
		renderFigures(figuresToDraw);
		copyBufferToScreen();
	}

	private void copyBufferToScreen() {
		var g2d = (Graphics2D) getGraphics();
		getGraphics().drawImage(backBuffer, 0, 0, null);
		bufferGraphics.clearRect(0, 0,
				backBuffer.getWidth(null),
				backBuffer.getHeight(null));
		g2d.dispose();
	}

	private void renderFigures(final List<Figure> figuresToDraw) {
		for (var figure : figuresToDraw){
			bufferGraphics.setColor(figure.getColor());
			bufferGraphics.setStroke(new BasicStroke(0));
			if (figure == selectedFigure){
				bufferGraphics.setStroke(new BasicStroke(3));
				bufferGraphics.setColor(figure.getColor().brighter());
			}

				bufferGraphics.fill(figure.getArea());
				bufferGraphics.setColor(Color.BLACK);
				bufferGraphics.draw(figure.getArea());
				//this.paintBorder(bufferGraphics);
		}

	}

	private void applyRenderingHints() {
		((Graphics2D)getGraphics()).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		(bufferGraphics).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
	}

	public void createGraphicsContext(int width, int height){
		backBuffer = createImage(width, height);
		bufferGraphics = (Graphics2D) backBuffer.getGraphics();
		applyRenderingHints();
	}

	public void recreateGraphicsContext(int width, int height){
		var oldBackBuffer = createImage(backBuffer.getWidth(null),
				backBuffer.getHeight(null));

		backBuffer = createImage(width, height);
		bufferGraphics = (Graphics2D) backBuffer.getGraphics();
		bufferGraphics.drawImage(oldBackBuffer, 0, 0, null);
	}

	public int getTargetedFPS() {
		return targetedFPS;
	}

	public void setTargetedFPS(int targetedFPS) {
		this.targetedFPS = targetedFPS;
		this.renderingThread.updateFrameTime((long) ((1d / targetedFPS) * 1000f));
	}
}
