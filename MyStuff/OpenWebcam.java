package MyStuff;

import MyStuff.*;

import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.*;
import java.awt.event.*;
import javax.swing.filechooser.FileSystemView;
import java.io.*;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamPanel;
import com.github.sarxos.webcam.WebcamResolution;




import java.awt.image.BufferedImage;

//Written by Blake Lazarine and whoever wrote the webcam files

public class OpenWebcam extends JFrame implements ActionListener {

    private static final long serialVersionUID = 3517366452510566924L;

    //declare things
    private Dimension size = WebcamResolution.QVGA.getSize();
    private Webcam webcam = null;
    private WebcamPanel panel = null;
    //private JMenuItem getImg;
    private JMenuItem save;
    //private JMenuItem setSaveLocation;
    private JMenuItem trainingLoc;
    private JMenuItem prepareTraining;
    //private JMenuItem buildSpace;
    private JMenuItem reconstruct;
    private JMenuItem mergeFaces;
    private JMenuItem recFromFile;
    private File saveLocation;
    public JLabel firstGuess;
    public JLabel secondGuess;
    public JLabel thirdGuess;

    public OpenWebcam() {

        super("Super epic face recognizer");
        webcam = Webcam.getDefault();
        webcam.setViewSize(size);

        //initialize Bar, menu, and menuItems
        JMenuBar bar = new JMenuBar();
        JMenu menu = new JMenu("File");
        save = new JMenuItem("Save");
        //setSaveLocation = new JMenuItem("Save at...");
        trainingLoc = new JMenuItem("From Already Cropped Images");
        prepareTraining = new JMenuItem("From Normal Images");
        //buildSpace = new JMenuItem("Build FaceSpace");
        reconstruct = new JMenuItem("Reconstuct From Webcam");
        mergeFaces = new JMenuItem("Merge 2 faces");
        recFromFile = new JMenuItem("Recognize from file");
        //getImg = new JMenuItem("Recognize");
        //setSaveLocation.addActionListener(this);
        save.addActionListener(this);
        //getImg.addActionListener(FaceRecRun.project);
        trainingLoc.addActionListener(FaceRecRun.fromCropped);
        prepareTraining.addActionListener(FaceRecRun.fromNormal);
        //buildSpace.addActionListener(FaceRecRun.buildSpace);
        reconstruct.addActionListener(FaceRecRun.reconstruct);
        mergeFaces.addActionListener(FaceRecRun.mergeFaces);
        recFromFile.addActionListener(FaceRecRun.recFromFile);

        //add items to menu
        menu.add(prepareTraining);
        menu.add(trainingLoc);
        //menu.add(buildSpace);
        //menu.add(getImg);
        menu.add(reconstruct);
        menu.add(mergeFaces);
        menu.add(recFromFile);
        //menu.add(setSaveLocation);
        menu.add(save);

        //initialize guess labels
        firstGuess = new JLabel("first guess     ");
        secondGuess = new JLabel("second guess     ");
        thirdGuess = new JLabel("third guess     ");

        //add items to the bar
        bar.add(menu);
        bar.add(firstGuess);
        bar.add(secondGuess);
        bar.add(thirdGuess);
        setJMenuBar(bar);

        //create panel
        panel = new WebcamPanel(webcam, false);
        panel.setFPSDisplayed(true);
        panel.setMirrored(true);

        add(panel);

        if (webcam.isOpen()) {
            webcam.close();
        }

        webcam.open();
        panel.start();
    }

    //action listener for saving images from webcam
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == save) {
            JFileChooser jfc = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory().getAbsolutePath());

            jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

            int returnValue = jfc.showOpenDialog(null);

            if (returnValue == JFileChooser.APPROVE_OPTION) {
                saveLocation = jfc.getSelectedFile();
                System.out.println(saveLocation.getAbsolutePath());
                System.out.println("Saving");
                ResizableImage img = new ResizableImage(webcam.getImage());
                img.save(new File(saveLocation, "Name.png"));
            }



        }

        /*else if (e.getSource() == setSaveLocation) {

            JFileChooser jfc = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
            jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            int returnValue = jfc.showOpenDialog(null);

            if (returnValue == JFileChooser.APPROVE_OPTION) {
                saveLocation = jfc.getSelectedFile();
                System.out.println(saveLocation.getAbsolutePath());
            }
        }*/
    }

    //capture image durrently on webcam
    public BufferedImage getWebImg() {
        return webcam.getImage();
    }

}