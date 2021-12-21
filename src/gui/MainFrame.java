package gui;

import figures.Figure;
import figures.Triangle2DDouble;
import figures.Triangle2DFloat;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;

import javax.swing.*;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.util.Hashtable;
import java.util.concurrent.ThreadLocalRandom;

public class MainFrame extends JFrame {
    private final AnimationPanel animationPanel;
    private final JPanel buttonsPanel;
    private final JPanel controlPanel;
    private final JLabel fpsCounterLabel;
    private final JButton addShapeButton;
    private final JButton toggleAnimationButton;
    private final JRadioButton squareRadioButton;
    private final JRadioButton ellipseRadioButton;
    private final JRadioButton triangleRadioButton;
    private final JSlider velocityXSlider;
    private final JSlider velocityYSlider;
    private final JSlider rotationVelocitySlider;
    private final JSlider pulseVelocitySlider;
    private final JSlider pulseAmplitudeSlider;
    private final JSlider targetFPSSlider;
    private final ThreadLocalRandom random = ThreadLocalRandom.current();
    private Class<? extends Shape> selectedShape;
    private ChangeListener velXSliderCL;
    private ChangeListener velYSliderCL;
    private ChangeListener rVelSliderCL;
    private ChangeListener pVelSliderCL;
    private ChangeListener pASliderCL;

    public MainFrame(int minWidth, int minHeight) {
        super("Animations");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null);
        this.setLayout(new BorderLayout());
        this.setMinimumSize(new Dimension(minWidth, minHeight));
        this.setSize(minWidth, minHeight);

        buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        controlPanel = new JPanel();
        controlPanel.setLayout(new BoxLayout(controlPanel, BoxLayout.Y_AXIS));
        addShapeButton = new JButton("Add next shape");
        toggleAnimationButton = new JButton("Toggle animation");
        animationPanel = new AnimationPanel(minWidth, minHeight, 61);
        squareRadioButton = new JRadioButton("Square");
        ellipseRadioButton = new JRadioButton("Circle (reduces FPS!)");
        triangleRadioButton = new JRadioButton("Triangle");
        fpsCounterLabel = new JLabel("0 FPS");
        velocityXSlider = new JSlider(0,115,0);
        velocityYSlider = new JSlider(0,115,0);
        rotationVelocitySlider = new JSlider(0,360,0);
        pulseVelocitySlider = new JSlider();
        pulseAmplitudeSlider = new JSlider();
        targetFPSSlider = new JSlider(1, 200,61);

        this.initializeButtonsPanel();
        this.initializeControlPanel();
        this.initializeComponents();
    }

    private void initializeComponents(){
        animationPanel.setSelectedFigureCallback(
                e -> updateControlSlidersWithSelectedFigure(animationPanel.getSelectedFigure()));
        animationPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.BLACK, 2), "Animations"));

        this.add(buttonsPanel, BorderLayout.SOUTH);
        this.add(animationPanel, BorderLayout.CENTER);
        this.add(controlPanel, BorderLayout.EAST);
        this.pack();
    }

    private void initializeButtonsPanel(){
        buttonsPanel.setPreferredSize(new Dimension(100,60));

        squareRadioButton.addActionListener(e -> {
            selectedShape = Rectangle2D.Double.class;
            ellipseRadioButton.setSelected(false);
            triangleRadioButton.setSelected(false);
        });
        ellipseRadioButton.addActionListener(e -> {
            selectedShape = Ellipse2D.Double.class;
            squareRadioButton.setSelected(false);
            triangleRadioButton.setSelected(false);
        });
        triangleRadioButton.addActionListener(e -> {
            selectedShape = Triangle2DDouble.class;
            ellipseRadioButton.setSelected(false);
            squareRadioButton.setSelected(false);
        });

        //Make square default option
        squareRadioButton.doClick();

        addShapeButton.addActionListener(event -> {
            Shape newShape = null;
            try {
                newShape = selectedShape.getDeclaredConstructor(
                        double.class,
                        double.class,
                        double.class,
                        double.class).newInstance(100,100,
                        random.nextInt(20,100),random.nextInt(20,100));
            } catch (Exception e) {
                e.printStackTrace();
            }

            var newFigure = new Figure(newShape);

            newFigure.setVelocity(
                    new Vector2D(random.nextInt(5,50),random.nextInt(5,50)));

            newFigure.setRotationSpeed(random.nextInt(5,70));

            newFigure.setColor(new Color(
                    random.nextInt(255),
                    random.nextInt(255),
                    random.nextInt(255),
                    random.nextInt(190,255)));

            animationPanel.addFigure(newFigure);
        });

        toggleAnimationButton.addActionListener(event -> animationPanel.toggleAnimation());

        buttonsPanel.add(addShapeButton);
        buttonsPanel.add(toggleAnimationButton);
        buttonsPanel.add(squareRadioButton);
        buttonsPanel.add(ellipseRadioButton);
        buttonsPanel.add(triangleRadioButton);
    }

    private void initializeControlPanel(){
        fpsCounterLabel.setPreferredSize(new Dimension(100,50));
        fpsCounterLabel.setFont(new Font(Font.DIALOG_INPUT, Font.BOLD, 15));
        fpsCounterLabel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.BLACK), "Current FPS:"));

        targetFPSSlider.setSnapToTicks(true);
        targetFPSSlider.setMajorTickSpacing(1);
        targetFPSSlider.setLabelTable(targetFPSSlider.createStandardLabels(20));
        targetFPSSlider.setPaintTicks(true);
        targetFPSSlider.setPaintLabels(true);
        targetFPSSlider.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.GREEN, 2), "Targeted FPS"));
        targetFPSSlider.addChangeListener( event -> animationPanel.setTargetedFPS(targetFPSSlider.getValue()));


        velocityXSlider.setSnapToTicks(true);
        velocityXSlider.setMajorTickSpacing(1);
        velocityXSlider.setLabelTable(velocityXSlider.createStandardLabels(15));
        velocityXSlider.setPaintLabels(true);
        velocityXSlider.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.BLACK, 2), "Velocity X"));

        velocityYSlider.setSnapToTicks(true);
        velocityYSlider.setMajorTickSpacing(1);
        velocityYSlider.setLabelTable(velocityYSlider.createStandardLabels(15));
        velocityYSlider.setPaintLabels(true);
        velocityYSlider.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.BLACK, 2), "Velocity Y"));

        rotationVelocitySlider.setSnapToTicks(true);
        rotationVelocitySlider.setMajorTickSpacing(1);
        rotationVelocitySlider.setLabelTable(rotationVelocitySlider.createStandardLabels(40));
        rotationVelocitySlider.setPaintLabels(true);
        rotationVelocitySlider.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.BLUE, 2), "Rotation speed [angles]"));



        pulseVelocitySlider.setPreferredSize(new Dimension(300,60));
        pulseVelocitySlider.setLabelTable(getFloatSliderLabels(800, 1500, 100));
        pulseVelocitySlider.setMinimum(800);
        pulseVelocitySlider.setMaximum(1500);
        pulseVelocitySlider.setMajorTickSpacing(100);
        //pulseVelocitySlider.setSnapToTicks(true);
        pulseVelocitySlider.setPaintLabels(true);
        pulseVelocitySlider.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.RED, 2), "Pulse speed"));

        pulseAmplitudeSlider.setLabelTable(getFloatSliderLabels(1000, 3000, 300));
        pulseAmplitudeSlider.setMinimum(1000);
        pulseAmplitudeSlider.setMaximum(3000);
        pulseAmplitudeSlider.setMajorTickSpacing(100);
        pulseAmplitudeSlider.setSnapToTicks(true);
        pulseAmplitudeSlider.setPaintLabels(true);
        pulseAmplitudeSlider.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.RED, 2), "Pulse amplitude"));



        Timer fpsRefreshTimer = new Timer(500, (e) -> {
            var fps = animationPanel.getCurrentFPS();
            fpsCounterLabel.setText(String.format("%.2f FPS", fps));
            if (fps < 15)
                fpsCounterLabel.setText(String.format("%3.2f FPS %s", fps, "Masakryczne klatkowanie"));
            else if (fps < 30)
                fpsCounterLabel.setForeground(Color.RED);
            else
                fpsCounterLabel.setForeground(Color.BLACK);
        });
        fpsRefreshTimer.start();

        controlPanel.add(fpsCounterLabel);
        controlPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        controlPanel.add(targetFPSSlider);
        controlPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        controlPanel.add(velocityXSlider);
        controlPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        controlPanel.add(velocityYSlider);
        controlPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        controlPanel.add(rotationVelocitySlider);
        controlPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        controlPanel.add(pulseVelocitySlider);
        controlPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        controlPanel.add(pulseAmplitudeSlider);
    }

    private void updateControlSlidersWithSelectedFigure(Figure figure){

        velocityXSlider.removeChangeListener(velXSliderCL);
        velocityYSlider.removeChangeListener(velYSliderCL);
        rotationVelocitySlider.removeChangeListener(rVelSliderCL);
        pulseVelocitySlider.removeChangeListener(pVelSliderCL);
        pulseAmplitudeSlider.removeChangeListener(pASliderCL);

        velocityXSlider.setValue((int) figure.getVelocity().getX());
        velocityYSlider.setValue((int) figure.getVelocity().getY());
        rotationVelocitySlider.setValue((int) figure.getRotationSpeed());
        pulseVelocitySlider.setValue((int) figure.getPulsingSpeed() * 1000);
        pulseAmplitudeSlider.setValue((int) figure.getPulsingAmplitude() * 1000);

        velXSliderCL = changeEvent ->
                figure.setVelocity(new Vector2D(velocityXSlider.getValue(), figure.getVelocity().getY()));
        velYSliderCL = changeEvent ->
                figure.setVelocity(new Vector2D(figure.getVelocity().getX(), velocityYSlider.getValue()));
        rVelSliderCL = changeEvent ->
                figure.setRotationSpeed(rotationVelocitySlider.getValue());
        pVelSliderCL = changeEvent ->
                figure.setPulsingSpeed(pulseVelocitySlider.getValue() / 1000f);
        pASliderCL = changeEvent ->
                figure.setPulsingAmplitude(pulseAmplitudeSlider.getValue() / 1000f);

        velocityXSlider.addChangeListener(velXSliderCL);
        velocityYSlider.addChangeListener(velYSliderCL);
        rotationVelocitySlider.addChangeListener(rVelSliderCL);
        pulseVelocitySlider.addChangeListener(pVelSliderCL);
        pulseAmplitudeSlider.addChangeListener(pASliderCL);
    }

    private Hashtable getFloatSliderLabels(int startingNumber, int endNumber, int tick){
        Hashtable<Integer, JLabel> hashtable = new Hashtable();
        for (int i = startingNumber; i <= endNumber; i += tick)
            hashtable.put(i, new JLabel(String.format("%.2f", (i / 1000f) )));

        return hashtable;
    }

}
