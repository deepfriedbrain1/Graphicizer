
package src;

import java.awt.Button;
import java.awt.FileDialog;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuItem;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.awt.image.MemoryImageSource;
import java.awt.image.PixelGrabber;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

/**
 *
 * @author Alberto Fernandez Saucedo
 */
public class Graphicizer extends Frame implements ActionListener 
{
    BufferedImage bufferedImage,
                  bufferedImageBackup;
    Image image;
    Menu menu;
    MenuBar menuBar;
    MenuItem menuItem1,
             menuItem2,
             menuItem3,
             menuItem4;
    Button button1,
           button2,
           button3,
           button4,
           button5;
    FileDialog dialog;
    
    Graphicizer()
    {
       setSize(400, 360);
       setTitle("The Graphicizer");
       setVisible(true);
       
       this.addWindowListener(
            new WindowAdapter()
            {
                public void windowClosing(WindowEvent e)
                {
                    System.exit(0);
                }
            }
        );
       
       // Add the butons the application uses as drawing tools
       button1 = new Button("Emboss");
       button1.setBounds(30, getHeight() - 50, 60, 20);
       add(button1);
       button1.addActionListener(this);
       
       button2 = new Button("Sharpen");
       button2.setBounds(100, getHeight() - 50, 60, 20);
       add(button2);
       button2.addActionListener(this);
       
       button3 = new Button("Brighten");
       button3.setBounds(170, getHeight() - 50, 60, 20);
       add(button3);
       button3.addActionListener(this);
       
       button4 = new Button("Blur");
       button4.setBounds(240, getHeight() - 50, 60, 20);
       add(button4);
       button4.addActionListener(this);
       
       button5 = new Button("Reduce");
       button5.setBounds(310, getHeight() - 50, 60, 20);
       add(button5);
       button5.addActionListener(this);
       
       
       //Add a File menu with the items Open, Save As, Undo, and Exit
       menuBar = new MenuBar();
       
       menu = new Menu("File");
       
       menuItem1 = new MenuItem("Open");
       menu.add(menuItem1);
       menuItem1.addActionListener(this);
       
       menuItem2 = new MenuItem("Save As");
       menu.add(menuItem2);
       menuItem2.addActionListener(this);
       
       menuItem3 = new MenuItem("Undo");
       menu.add(menuItem3);
       menuItem3.addActionListener(this);
               
       menuItem4 = new MenuItem("Exit");
       menu.add(menuItem4);
       menuItem4.addActionListener(this);
       
       menuBar.add(menu);
       
       setMenuBar(menuBar);
       
       //Create a FileDialog object used to display File dialog box
       // when user wants to open or save files
       dialog = new FileDialog(this, "File Dialog");
       
    }//end constructor

    @Override
    public void actionPerformed(ActionEvent event) {
        // Load menu item
        if(event.getSource() == menuItem1){
            dialog.setMode(FileDialog.LOAD);
            dialog.setVisible(true);
            
            try{
                if(!dialog.getFile().equals("")){
                    File input = new File(dialog.getDirectory()
                    + dialog.getFile());
                    
                    bufferedImage = ImageIO.read(input);
                    
                    setSize(
                        getInsets().left + getInsets().right + 
                        Math.max(400, bufferedImage.getWidth() 
                        + 60),
                        getInsets().top + getInsets().bottom +
                        Math.max(340, bufferedImage.getHeight()
                        + 60));
                    
                    setButtonBounds();
                }
            }
            catch(IOException ioe){
                System.out.println(ioe.getMessage());
            }
            
            repaint();
            
        }//end if (LOAD)
        
        //Save As menu item
        if(event.getSource() == menuItem2){
            dialog.setMode(FileDialog.SAVE);
            dialog.setVisible(true);
            
            try{
                if(!dialog.getFile().equals("")){
                    String filename = dialog.getFile();
                    
                    File outputFile = new File(dialog.getDirectory()
                    + filename);
                    
                    ImageIO.write(
                            bufferedImage, 
                            filename.substring( // 
                                    filename.length() - 3, filename.length()), 
                            outputFile);
                }
            }
            catch(IOException ioe){
                System.out.println(ioe.getMessage());
            }
        }//end if (SAVE)
        
        // Exit menu item
        if(event.getSource() == menuItem4){
            System.exit(0);
        }//end Exit menu item
        
        // Emboss button
        if(event.getSource() == button1){
            bufferedImageBackup = bufferedImage;
            
            int width = bufferedImage.getWidth();
            int height = bufferedImage.getHeight();
            int[] pixels = new int[width * height];
            
            PixelGrabber pg = new PixelGrabber(
            bufferedImage, 0, 0, width, height, pixels, 0, width);
            
            try{
                pg.grabPixels();
            }
            catch(InterruptedException ie){
                System.out.println(ie.getMessage());
            }
            
            for(int i = 0; i <= 1; i++){
                for(int j = 0; j < height-1; j++){
                    pixels[i + j * width] = 0x88888888;
                }
            }
            
            for(int i = width-2; i <= width-1; i++){
                for(int j = 0; j < height-1; j++){
                    pixels[i + j * width] = 0x88888888;
                }
            }
            
            for(int i = 0; i <= width-1; i++){
                for(int j = 0; j <= 1; j++){
                    pixels[i + j * width] = 0x88888888;
                }
            }
            
            for(int i = 2; i < width-1; i++){
                for(int j = 2; j < height-1; j++){
                    int red = ((pixels[(i + 1) + j * width + 1] & 0xFF)
                            - (pixels[ i+ j * width] & 0xFF)) + 128;
                    
                    int green = (((pixels[(i + 1) + j * width + 1]
                            & 0xFF00) / 0x100) % 0x100 - ((pixels[i + j * width]
                            & 0xFF00) / 0x100) % 0x100) + 128;
                    
                    int blue = (((pixels[(i + 1) + j * width + 1]
                            & 0xFF0000) / 0x10000)
                            % 0x100 - ((pixels[i + j * width] & 0xFF000) / 0x10000)
                            % 0x100) + 128;
                    
                    int avg = (red + green + blue) / 3;
                    
                    pixels[i + j * width] = (0xff000000 | avg << 16 | avg << 8 | avg);
                }
            }
            
            image = createImage(new MemoryImageSource(width,
                height, pixels, 0, width));
            
            bufferedImage = new BufferedImage(
                    width, height, BufferedImage.TYPE_INT_BGR);
            
            bufferedImage.createGraphics().drawImage(image, 0 , 0, this);
            repaint();
            
        }//end if (Emboss button)
        
        //Sharpen button
        if(event.getSource() == button2){
            bufferedImageBackup = bufferedImage;
            
            Kernel kernel = new Kernel(3, 3, new float [] {
            0.0f, -1.0f, 0.0f,
            -1.0f, 5.0f, -1.0f,
            0.0f, -1.0f, 0.0f
            });
            
            ConvolveOp convolveOp = new ConvolveOp(
                kernel, ConvolveOp.EDGE_NO_OP, null);
            
            BufferedImage temp = new BufferedImage(bufferedImage.getWidth(),
                bufferedImage.getHeight(), BufferedImage.TYPE_INT_ARGB);
            
            convolveOp.filter(bufferedImage, temp);
            
            bufferedImage = temp;
            
            repaint();
            
        }//end if (Sharpen button)
        
        //Brighten button
        if(event.getSource() == button3){
            bufferedImageBackup = bufferedImage;
            
            Kernel kernel = new Kernel(1, 1, new float [] {3});
            
            ConvolveOp convolveOp = new ConvolveOp(kernel);
            
            BufferedImage temp = new BufferedImage(
                    bufferedImage.getWidth(), bufferedImage.getHeight(),
                    BufferedImage.TYPE_INT_ARGB);
            
            convolveOp.filter(bufferedImage, temp);
            
            bufferedImage = temp;
            
            repaint();
            
        }//end Brighten button
        
        // Blur button
        if(event.getSource() == button4){
            bufferedImageBackup = bufferedImage;
            
            Kernel kernel = new Kernel(3, 3, new float[] 
            {0.25f, 0, 0.25f,
             0, 0, 0,
             0.25f, 0, 0.25f});
            
            ConvolveOp convolveOp = new ConvolveOp(kernel);
            
            BufferedImage temp = new BufferedImage(
                bufferedImage.getWidth(), bufferedImage.getHeight(),
                BufferedImage.TYPE_INT_ARGB);
            
            convolveOp.filter(bufferedImage, temp);
            
            bufferedImage = temp;
            
            repaint();
            
        }//end Blur button
        
        //Reduce button
        if(event.getSource() == button5){
            bufferedImageBackup = bufferedImage;
            
            image = bufferedImage.getScaledInstance(
                bufferedImage.getWidth()/2, 
                bufferedImage.getHeight()/2, 
                0);
            
            bufferedImage = new BufferedImage(
                bufferedImage.getWidth()/2,
                bufferedImage.getHeight()/2,
                BufferedImage.TYPE_INT_BGR);
            
            bufferedImage.createGraphics().drawImage(image, 0, 0, this);
            
            setSize(getInsets().left + getInsets().right + Math.max(400,
                    bufferedImage.getWidth() + 60),
                    getInsets().top + getInsets().bottom +
                    Math.max(340, bufferedImage.getHeight() + 60));
            
            setButtonBounds();
            
            repaint();
            
        }//end Reduce button
        
        // Undo menu item
        if(event.getSource() == menuItem3){
            if(bufferedImageBackup != null){
                bufferedImage = bufferedImageBackup;
                
                setSize(getInsets().left + getInsets().right + Math.max
                (400, bufferedImage.getWidth() + 60),
                 getInsets().top + getInsets().bottom +
                    Math.max(340, bufferedImage.getHeight() + 60));
            }
            
            setButtonBounds();
            
            repaint();
                
        }//end Undo menu item
            
    }//end actionPerformed
    
    @Override
    public void paint(Graphics g)
    {
        if(bufferedImage != null){
            g.drawImage(bufferedImage, getSize().width / 2
            - bufferedImage.getWidth() / 2,
            getInsets().top + 20, this);
        }
    }//end paint
    
    public void setButtonBounds(){
            button1.setBounds(30, getHeight() - 30, 60, 20);
            button2.setBounds(100, getHeight() - 30, 60, 20);
            button3.setBounds(170, getHeight() - 30, 60, 20);
            button4.setBounds(240, getHeight() - 30, 60, 20);
            button5.setBounds(310, getHeight() - 30, 60, 20); 
    }
    
    public static void main(String[] args)
    {
        new Graphicizer();
        
    }//end main
    
}//end Graphicizer
