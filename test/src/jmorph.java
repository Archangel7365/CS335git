// CS 335 Project 3
// 16 Nov 18
// Austin Williams
// Alexander Vanover

import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.io.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.imageio.*;

import static java.awt.Color.RED;

public class jmorph extends JFrame {

    // Instance variables
    private BufferedImage image;   // the image
    private BufferedImage image2;  // second image
    private JPanel imagesHolder;
    private MyImageObj left;       //
    private MyImageObj right;
    private JButton previewMorph; //
    private JButton startMorph;  //
    private JButton setRight;    //
    private JButton setLeft;    //
    private JButton reset;//
    private int x, y;              //
    private boolean firstdrag=true;//
    private JLabel FPSLabel;   //
    private JSlider FPS;
    private JSlider timeslider; private JLabel timeLabel;
    private int FramesPerSecond = 30;
    private int time = 30, initialTime = 30, nFrames;
    private Timer animationTimer;

    // Preview Morph
    private JFrame previewMorph_frame;

    // Constructor for the frame
    public jmorph() {

        super();				// call JFrame constructor

        this.buildMenus();		// helper method to build menus
        this.buildComponents();		// helper method to set up components
        this.buildDisplay();		// Lay out the components on the display
    }

    //  Builds the menus to be attached to this JFrame object
    //  Primary side effect:  menus are added via the setJMenuBar call
    //  		Action listeners for the menu items are anonymous inner
    //		classes here
    //  This helper method is called once by the constructor

    private void buildMenus () {

        final JFileChooser fc = new JFileChooser(".");
        JMenuBar bar = new JMenuBar();
        this.setJMenuBar (bar);
        JMenu fileMenu = new JMenu ("File");
        JMenuItem fileopen = new JMenuItem ("Open");
        JMenuItem fileexit = new JMenuItem ("Exit");


        // GRID SIZE
        JMenu gridMenu = new JMenu("Grid");
        JMenuItem five = new JMenuItem("5 x 5");
        JMenuItem ten = new JMenuItem("10 X 10");
        JMenuItem twenty = new JMenuItem("20 X 20");
        JMenuItem thirty = new JMenuItem("30 X 30");

        fileopen.addActionListener(
                new ActionListener () {
                    public void actionPerformed (ActionEvent e) {
                        int returnVal = fc.showOpenDialog(jmorph.this);
                        if (returnVal == JFileChooser.APPROVE_OPTION) {
                            File file = fc.getSelectedFile();
                            try {
                                image = ImageIO.read(file);

                            } catch (IOException e1){};

                            left.setImage(image);
                            left.showImage();

                            right.setImage(image2);
                            right.showImage();
                        }
                    }
                }
        );
        fileexit.addActionListener(new ActionListener () {
                                       public void actionPerformed (ActionEvent e) {
                                           System.exit(0);
                                       }
                                   }
        );

        five.addActionListener(new ActionListener() {
            public void actionPerformed (ActionEvent e) {
                left.controlPointDensity = 5;
                right.controlPointDensity = 5;
                left.cps = new controlPoint[left.controlPointDensity][left.controlPointDensity];
                right.cps = new controlPoint[right.controlPointDensity][right.controlPointDensity];
                left.genCPs();
                right.genCPs();
                repaint();
            }
        });

        ten.addActionListener(new ActionListener() {
            public void actionPerformed (ActionEvent e) {
                left.controlPointDensity = 10;
                right.controlPointDensity = 10;
                left.cps = new controlPoint[left.controlPointDensity][left.controlPointDensity];
                right.cps = new controlPoint[right.controlPointDensity][right.controlPointDensity];
                left.genCPs();
                right.genCPs();
                repaint();
            }
        });

        twenty.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                left.controlPointDensity = 20;
                right.controlPointDensity = 20;
                left.cps = new controlPoint[left.controlPointDensity][left.controlPointDensity];
                right.cps = new controlPoint[right.controlPointDensity][right.controlPointDensity];
                left.genCPs();
                right.genCPs();
                repaint();
            }
        });
        thirty.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                left.controlPointDensity = 30;
                right.controlPointDensity = 30;
                left.cps = new controlPoint[left.controlPointDensity][left.controlPointDensity];
                right.cps = new controlPoint[right.controlPointDensity][right.controlPointDensity];
                left.genCPs();
                right.genCPs();
                repaint();
            }
        });

        //////
        fileopen.addActionListener(
                new ActionListener () {
                    public void actionPerformed (ActionEvent e) {
                        int returnVal = fc.showOpenDialog(jmorph.this);
                        if (returnVal == JFileChooser.APPROVE_OPTION) {
                            File file = fc.getSelectedFile();
                            try {
                                image = ImageIO.read(file);

                            } catch (IOException e1){};
                        }
                    }
                }
        );
        fileexit.addActionListener(
                new ActionListener () {
                    public void actionPerformed (ActionEvent e) {
                        System.exit(0);
                    }
                }
        );

        gridMenu.add(five);
        gridMenu.add(ten);
        gridMenu.add(twenty);
        gridMenu.add(thirty);
        bar.add(gridMenu);

        fileMenu.add(fileopen);
        fileMenu.add(fileexit);
        bar.add(fileMenu);
    }

    //  Allocate components (these are all instance vars of this frame object)
    //  and set up action listeners for each of them
    //  This is called once by the constructor

    private void buildComponents() {

        imagesHolder = new JPanel();

        // Create components to in which to display image and GUI controls
        // reads a default image
        left = new MyImageObj(readImage("boat.gif"), 10);
        right = new MyImageObj(readImage("boat2.gif"), 10);
        left.setPartner(right);
        right.setPartner(left);

        //view = new MyImageObj();
        reset = new JButton("Reset");
        setLeft = new JButton("Set Left Picture");
        setRight = new JButton("Set Right Picture");
        previewMorph = new JButton("Preview Morph");
        startMorph = new JButton ("Start Morph");

        FPSLabel = new JLabel("v FPS");
        FPS = new JSlider(SwingConstants.VERTICAL, 1, 60, 30);
        FPS.setMajorTickSpacing(1);
        FPS.setPaintTicks(true);
        FPS.setPaintTicks(true);

        timeLabel = new JLabel("Time Slider^");
        timeslider = new JSlider(SwingConstants.VERTICAL, 1, 10, 5);
        timeslider.setMajorTickSpacing(1);
        timeslider.setPaintTicks(true);
        timeslider.setPaintTicks(true);

        // slider event triggers a display of thresholded image
        FPS.addChangeListener(
                new ChangeListener() {
                    public void stateChanged (ChangeEvent e) {
                        FramesPerSecond = FPS.getValue();
                        FPSLabel.setText("FPS: " + FPS.getValue());
                    }
                }
        );

        timeslider.addChangeListener(
                new ChangeListener() {
                    public void stateChanged(ChangeEvent e) {
                        time = timeslider.getValue();
                        initialTime = timeslider.getValue();
                        timeLabel.setText("Time: " + timeslider.getValue());
                    }
                }
        );

        animationTimer = new Timer((1 / FramesPerSecond)*1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                for (int i = 0; i < left.controlPointDensity; i++) {
                    if (nFrames <= 0) {
                        animationTimer.stop();
                        break;
                    }
                    else {
                        for (int j = 0; j < left.controlPointDensity; j++) {
                            left.cps[i][j].setX(left.cps[i][j].x + ((right.cps[i][j].x - left.cps[i][j].x) / nFrames));
                            left.cps[i][j].setY(left.cps[i][j].y + ((right.cps[i][j].y - left.cps[i][j].y) / nFrames));
                            left.repaint();
                        }
                    }
                }
                nFrames--;
            }
        });

        // Button listeners activate the buffered image object in order
        // to display appropriate function
        reset.addActionListener(
                new ActionListener () {
                    public void actionPerformed (ActionEvent e) {
                        left.reset();
                        right.reset();
                        left.repaint();
                        right.repaint();
                    }
                }
        );
        // New PreviewMorph window
        previewMorph.addActionListener(
                new ActionListener () {
                    public void actionPerformed (ActionEvent e) {
                        // New JFrame for preview morph
                        previewMorph_frame = new JFrame("Preview Morph");
                        previewMorph_frame.setVisible(true);
                        previewMorph_frame.setSize(800, 500);
                        previewMorph_frame.setResizable(false);

                        // Removing right picture then adding images holder to preview frame
                        imagesHolder.remove(right);
                        previewMorph_frame.add(imagesHolder);

                        nFrames = FramesPerSecond*time;
                        animationTimer.start();


                    }
                }
        );
        setRight.addActionListener(
                new ActionListener () {
                    public void actionPerformed (ActionEvent e) {
                        final JFileChooser fc = new JFileChooser(".");
                        int returnVal = fc.showOpenDialog(jmorph.this);
                        if (returnVal == JFileChooser.APPROVE_OPTION) {
                            File file = fc.getSelectedFile();
                            try {
                                image2 = ImageIO.read(file);

                            } catch (IOException e1){};
                        }
                        right.setImage(image2);
                        right.showImage();
                    }
                }
        );
        setLeft.addActionListener(
                new ActionListener () {
                    public void actionPerformed (ActionEvent e) {
                        final JFileChooser fc = new JFileChooser(".");
                        int returnVal = fc.showOpenDialog(jmorph.this);
                        if (returnVal == JFileChooser.APPROVE_OPTION) {
                            File file = fc.getSelectedFile();
                            try {
                                image = ImageIO.read(file);

                            } catch (IOException e1){};
                        }
                        left.setImage(image);
                        left.showImage();
                    }
                }
        );
        startMorph.addActionListener(
                new ActionListener () {
                    public void actionPerformed (ActionEvent e) {

                    }
                }
        );
    }

    // This helper method adds all components to the content pane of the
    // JFrame object.  Specific layout of components is controlled here

    private void buildDisplay () {

        // Build first JPanel
        JPanel controlPanel = new JPanel();
        GridLayout grid = new GridLayout (1, 5);
        controlPanel.setLayout(grid);
        controlPanel.add(reset);
        controlPanel.add(previewMorph);
        controlPanel.add(startMorph);
        controlPanel.add(setLeft);
        controlPanel.add(setRight);

        // Build second JPanel
        JPanel sliderPanel = new JPanel();
        BorderLayout layout = new BorderLayout (5, 5);
        sliderPanel.setLayout (layout);

        // FPS Slider
        sliderPanel.add(FPSLabel,BorderLayout.NORTH);
        sliderPanel.add(FPS,BorderLayout.WEST);

        // Time Slider
        sliderPanel.add(timeLabel,BorderLayout.SOUTH);
        sliderPanel.add(timeslider,BorderLayout.EAST);

        imagesHolder.add(left, BorderLayout.WEST);
        imagesHolder.add(right, BorderLayout.EAST);

        // Add panels and image data component to the JFrame
        Container c = this.getContentPane();
        //c.add(left, BorderLayout.WEST);
        //c.add(right, BorderLayout.EAST);
        c.add(imagesHolder);
        c.add(controlPanel, BorderLayout.SOUTH);
        c.add(sliderPanel, BorderLayout.WEST);

    }

    // This method reads an Image object from a file indicated by
    // the string provided as the parameter.  The image is converted
    // here to a BufferedImage object, and that new object is the returned
    // value of this method.
    // The mediatracker in this method can throw an exception

    public BufferedImage readImage (String file) {

        Image image = Toolkit.getDefaultToolkit().getImage(file);
        MediaTracker tracker = new MediaTracker (new Component () {});
        tracker.addImage(image, 0);
        try { tracker.waitForID (0); }
        catch (InterruptedException e) {}
        BufferedImage bim = new BufferedImage
                (image.getWidth(this), image.getHeight(this),
                        BufferedImage.TYPE_INT_RGB);
        Graphics2D big = bim.createGraphics();
        big.drawImage (image, 0, 0, this);
        return bim;

    }

    // The main method allocates the "window" and makes it visible.
    // The windowclosing event is handled by an anonymous inner (adapter)
    // class
    // Command line arguments are ignored.

    public static void main(String[] argv) {

        JFrame frame = new jmorph();
        frame.pack();
        frame.setVisible(true);
        frame.addWindowListener (
                new WindowAdapter () {
                    public void windowClosing ( WindowEvent e) {
                        System.exit(0);
                    }
                }
        );
    }

    public class MyImageObj extends JLabel {

        // instance variable to hold the buffered image
        private BufferedImage bim = null;
        private BufferedImage filteredbim = null;
        private MouseAdapter ma;
        private controlPoint[][] cps;
        private int[] clickedCP;
        private int controlPointDensity;
        private MyImageObj partner;

        //  tell the paintcomponent method what to draw
        private boolean showfiltered=false;

        // Default constructor
        public MyImageObj() {
        }

        // This constructor stores a buffered image passed in as a parameter
        public MyImageObj(BufferedImage img, int cpd) {
            bim = img;
            this.controlPointDensity = cpd;
            cps = new controlPoint[controlPointDensity][controlPointDensity];
            filteredbim = new BufferedImage
                    (bim.getWidth(), bim.getHeight(), BufferedImage.TYPE_INT_RGB);
            setPreferredSize(new Dimension(bim.getWidth(), bim.getHeight()));
            genCPs();
            clickedCP = new int[2];
            this.addMouseMotionListener(
                    new MouseMotionAdapter() {
                        public void mouseDragged(MouseEvent event) {
                            Graphics g = bim.getGraphics();
                            g.setColor(Color.black);
                            int temp_x = event.getX();
                            int temp_y = event.getY();
                            boolean onControlPoint = false;
                            clickedCP[0] = -1;
                            clickedCP[1] = -1;
                            for (int i = 0; i < controlPointDensity; i++) {
                                for (int j = 0; j < controlPointDensity; j++) {
                                    if (((cps[i][j].x - 10 < temp_x) && (temp_x < cps[i][j].x + 10)) && ((cps[i][j].y - 10 < temp_y) && (temp_y < cps[i][j].y + 10)) && (!cps[i][j].isEdge)) {
                                        clickedCP[0] = i;
                                        clickedCP[1] = j;
                                        onControlPoint = true;
                                    }
                                }
                            }
                            if (onControlPoint) {
                                int p1 = clickedCP[0];
                                int p2 = clickedCP[1];
                                if (firstdrag) {
                                    cps[clickedCP[0]][clickedCP[1]].x = event.getX();
                                    cps[clickedCP[0]][clickedCP[1]].y = event.getY();
                                    cps[clickedCP[0]][clickedCP[1]].currentColor = Color.RED;
                                    partner.cps[clickedCP[0]][clickedCP[1]].currentColor = Color.RED;
                                    firstdrag = false;
                                    repaint();
                                    partner.repaint();
                                }
                                else {
                                    showImage();
                                    cps[clickedCP[0]][clickedCP[1]].x = event.getX();
                                    cps[clickedCP[0]][clickedCP[1]].y = event.getY();
                                    repaint();
                                }
                            }

                        }
                    }
            );

            // Listen for mouse release to detect when we've stopped painting
            this.addMouseListener(
                new MouseAdapter() {
                    public void mouseReleased(MouseEvent event) {
                        if (clickedCP[0] != -1 && clickedCP[1] != -1) {
                            firstdrag = true;
                            cps[clickedCP[0]][clickedCP[1]].x = event.getX();
                            cps[clickedCP[0]][clickedCP[1]].y = event.getY();
                            cps[clickedCP[0]][clickedCP[1]].currentColor = Color.BLACK;
                            partner.cps[clickedCP[0]][clickedCP[1]].currentColor = Color.BLACK;
                            clickedCP = new int[2];
                            repaint();
                            partner.repaint();
                        }
                    }
                }
            );
            this.repaint();
        }

        // This mutator changes the image by resetting what is stored
        // The input parameter img is the new image;  it gets stored as an
        //     instance variable
        public void setImage(BufferedImage img) {
            if (img == null) return;
            bim = img;
            filteredbim = new BufferedImage
                    (bim.getWidth(), bim.getHeight(), BufferedImage.TYPE_INT_RGB);
            setPreferredSize(new Dimension(bim.getWidth(), bim.getHeight()));
            showfiltered=false;
            this.repaint();
        }

        // accessor to get a handle to the bufferedimage object stored here
        public BufferedImage getImage() {
            return bim;
        }

        //  show current image by a scheduled call to paint()
        public void showImage()
        {
            if (bim == null) return;
            showfiltered=false;
            this.repaint();
        }

        public void setPartner(MyImageObj partner) {
            this.partner = partner;
        }

        public void genCPs() {
            for (int i = 0; i < controlPointDensity; i++) {
                for (int j = 0; j < controlPointDensity; j++) {
                    boolean edge = false;
                    if (i == 0 || j == 0 || i == controlPointDensity - 1|| j == controlPointDensity - 1) {
                        edge = true;
                    }
                    cps[i][j] = new controlPoint(edge, j * bim.getWidth()/(controlPointDensity - 1), i * bim.getHeight()/(controlPointDensity-1));
                }
            }
        }

        //  get a graphics context and show either filtered image or
        //  regular image
        public void paintComponent(Graphics g)
        {
            Graphics2D big = (Graphics2D) g;
            big.drawImage(bim, 0, 0, null);
            for (int i = 0; i < this.controlPointDensity; i++) {
                for (int j = 0; j < this.controlPointDensity; j++) {
                    big.setColor(cps[i][j].currentColor);
                    big.fillOval(cps[i][j].x - 5, cps[i][j].y - 5, 10, 10);
                    big.setColor(Color.BLACK);
                    if (i - 1 > 0) {
                        big.drawLine(cps[i][j].x, cps[i][j].y, cps[i-1][j].x,cps[i-1][j].y);
                    }
                    if (j - 1 > 0) {
                        big.drawLine(cps[i][j].x, cps[i][j].y, cps[i][j-1].x, cps[i][j-1].y);
                    }
                    if (i < controlPointDensity - 1) {
                        big.drawLine(cps[i][j].x, cps[i][j].y, cps[i+1][j].x, cps[i+1][j].y);
                    }
                    if (j < controlPointDensity - 1)
                    {
                        big.drawLine(cps[i][j].x, cps[i][j].y, cps[i][j+1].x, cps[i][j+1].y);
                    }
                    if (i < controlPointDensity - 1 && j < controlPointDensity - 1) {
                        big.drawLine(cps[i][j].x, cps[i][j].y, cps[i+1][j+1].x, cps[i+1][j+1].y);
                    }
                }
            }
        }

        public void reset() {
            for (int i = 0; i < controlPointDensity; i++) {
                for (int j = 0; j < controlPointDensity; j++) {
                    cps[i][j].x = j * bim.getWidth()/(controlPointDensity - 1);
                    cps[i][j].y = i * bim.getHeight()/(controlPointDensity - 1);
                }
            }
        }
    }
}