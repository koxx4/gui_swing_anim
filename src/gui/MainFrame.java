package gui;

import figures.Figure;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.util.Random;

public class MainFrame extends JFrame {
    private AnimationPanel animationPanel;
    private final JPanel buttonsPanel;
    private final JButton addShapeButton;
    private final JButton toggleAnimationButton;
    private final JRadioButton squareRadioButton;
    private final JRadioButton ellipseRadioButton;
    private final JRadioButton triangleRadioButton;
    private final Random random = new Random();

    public MainFrame(int minWidth, int minHeight) {
        super("Animations");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null);
        this.setLayout(new BorderLayout());
        this.setMinimumSize(new Dimension(minWidth, minHeight));
        this.setSize(minWidth, minHeight);

        buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonsPanel.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
        addShapeButton = new JButton("Add next shape");
        toggleAnimationButton = new JButton("Toggle animation");
        animationPanel = new AnimationPanel(minWidth, minHeight, 60);
        squareRadioButton = new JRadioButton("Square");
        ellipseRadioButton = new JRadioButton("Circle");
        triangleRadioButton = new JRadioButton("Triangle");

        this.initializeComponents();
    }

    private void initializeComponents(){
        squareRadioButton.addActionListener((e) -> {

        });
        ellipseRadioButton.addActionListener((e) -> {

        });
        triangleRadioButton.addActionListener((e) -> {

        });

        //Make square default option
        squareRadioButton.doClick();

        addShapeButton.addActionListener((event) -> {
                    var newFigure = new Figure(new Rectangle2D.Double(100,100,50,50));
                    newFigure.setVelocity(new Vector2D(20,20));
                    newFigure.setRotationSpeed(2);
                    animationPanel.addFigure(newFigure);
                });

        toggleAnimationButton.addActionListener((event) -> animationPanel.toggleAnimation());

        buttonsPanel.add(addShapeButton);
        buttonsPanel.add(toggleAnimationButton);
        buttonsPanel.add(squareRadioButton);
        buttonsPanel.add(ellipseRadioButton);
        buttonsPanel.add(triangleRadioButton);
        this.add(buttonsPanel, BorderLayout.SOUTH);
        this.add(animationPanel, BorderLayout.CENTER);
        this.pack();
    }


}
