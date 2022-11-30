package sim;
import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JLabel;
import java.awt.event.*;
import java.io.BufferedReader;
import java.nio.file.*;
import java.io.IOException;
import java.io.FileFilter;
import java.io.BufferedWriter;

//import javax.swing.Timer;


/**
 * Write a description of class SimControl here.
 *
 * @author (your name)
 * @version (a version number or a date)
 */
public class SimController
{
    private JFrame frame; 
    private JMenuBar menubar;
    private JMenu File;
    private Container contentPane;
    private JMenuItem Load;
    private JMenuItem Save;
    private JMenuItem Quit;
    
    private JOptionPane error_pane; 
    private JLabel runningLabel;
    private JLabel statusLabel;
    private JTextField delay_value;
    private JTextField run_to;
    private JPanel buttons_panel;
    private JButton Run;
    private JButton Stop;
    private JButton Step;
    private JButton Slower;
    private JButton Faster;
    private JButton Run_To;
    private JButton Reset;
    private Simulator sim = new Simulator();
    private Timer timer;
    private int delay = 500;
    private String for_status = "        Step :";
    private String pad = "         " ; 
    /**
     * Constructor for objects of class SimControl
     */
    public SimController()
    {
        makeFrame();
        frame.setVisible(true);
    }

    public void makeFrame()
    {
        frame = new JFrame("Sim Control");
        contentPane = frame.getContentPane();
        frame.setSize(1200, 800);
        menubar = new JMenuBar();
        frame.setJMenuBar(menubar);
        frame.setLayout(new BorderLayout());
        File = new JMenu("File");
        menubar.add(File);
        Load = new JMenuItem("Load Settings");
        Save = new JMenuItem("Save Settings");
        Quit = new JMenuItem("Quit");
        File.add(Load);
        File.add(Save);
        File.add(Quit);
        Load.addActionListener(load_file);
        Save.addActionListener(save_file);
        Quit.addActionListener((ActionEvent e) -> { quit();});
        buttons_panel = new JPanel();
        buttons_panel.setLayout(new GridLayout(4,2));

        delay_value = new JTextField(Integer.toString(delay));
        run_to = new JTextField(0);

        Run = new JButton("Run");
        Run.addActionListener((ActionEvent e) -> { run_event();});
        Stop = new JButton("Stop");
        Stop.addActionListener((ActionEvent e) -> { stop_event();});
        Step = new JButton("Step");
        Step.addActionListener((ActionEvent e) -> { step_event();});
        Slower = new JButton("Slower");
        Slower.addActionListener((ActionEvent e) -> { slower_event();});
        Faster = new JButton("Faster");
        Faster.addActionListener((ActionEvent e) -> { faster_event();});
        Run_To = new JButton("Run To");
        Run_To.addActionListener((ActionEvent e) -> { run_to_event();});
        Reset = new JButton("Reset");
        Reset.addActionListener((ActionEvent e) -> { reset();});
        buttons_panel.add(Run);
        buttons_panel.add(Step);
        timer = new Timer(delay, new_step);
        buttons_panel.add(Stop);
        buttons_panel.add(Slower);
        buttons_panel.add(Faster);
        buttons_panel.add(Reset);         
        buttons_panel.add(Run_To);
        contentPane.add(buttons_panel,BorderLayout.WEST);

        JPanel running_status_bar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        running_status_bar.setBorder(new CompoundBorder(new LineBorder(Color.DARK_GRAY),
                new EmptyBorder(4, 4, 4, 4)));
        runningLabel = new JLabel("Sim Not Running");
        running_status_bar.add(runningLabel);

        JPanel status_bar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        status_bar.setBorder(new CompoundBorder(new LineBorder(Color.DARK_GRAY),
                new EmptyBorder(4, 4, 4, 4)));
        statusLabel = new JLabel(sim.getDetails().concat(for_status.concat(Integer.toString(sim.getStep()))));
        status_bar.add(statusLabel);

        contentPane.add(running_status_bar,BorderLayout.NORTH);
        contentPane.add(status_bar,BorderLayout.SOUTH);

        running_status_bar.add(delay_value);
        buttons_panel.add(run_to);
        frame.pack();

    }

    public void run_event(){
        timer.start();
        runningLabel.setText("Sim Running");
    }

    public void stop_event(){
        timer.stop();
        runningLabel.setText("Sim Not Running");
    }

    public void step_event(){
        //if (timer == null){timer = new Timer(delay, new_step);};
        timer.stop();
        sim.simulateOneStep();
        statusLabel.setText(sim.getDetails().concat(for_status.concat(Integer.toString(sim.getStep()))));
        runningLabel.setText("Sim Not Running");
    }

    public void slower_event(){
        //if (timer == null){timer = new Timer(delay, new_step);};
        int new_delay = timer.getDelay()*2;
        delay_value.setText(Integer.toString(new_delay));
        timer.setDelay(new_delay);
    }

    public void faster_event(){
        //if (timer == null){timer = new Timer(delay, new_step);};
        int new_delay = timer.getDelay()/2;
        delay_value.setText(Integer.toString(new_delay));
        timer.setDelay(new_delay);
    }

    public void run_to_event(){
        int steps = Integer.parseInt(run_to.getText());
        while (steps!=0){
            sim.simulateOneStep();
            steps -= 1;
        }
        statusLabel.setText(sim.getDetails().concat(for_status.concat(Integer.toString(sim.getStep()))));
    }

    public void reset(){
        runningLabel = new JLabel("Sim Not Running");
        timer.stop();
        sim.reset();
        statusLabel.setText(sim.getDetails().concat(for_status.concat(Integer.toString(sim.getStep()))));

    }

    ActionListener new_step = new ActionListener() {
            public void actionPerformed(ActionEvent evt){
                sim.simulateOneStep();
                statusLabel.setText(sim.getDetails().concat(for_status.concat(Integer.toString(sim.getStep()))));

            }
        };

    ActionListener load_file = new ActionListener(){
            public void actionPerformed(ActionEvent evt){
                JFileChooser chooser = new JFileChooser();
                int r = chooser.showOpenDialog(frame);

                if (r == JFileChooser.APPROVE_OPTION){
                    JFrame error_frame = new JFrame();
                    java.io.File f = chooser.getSelectedFile();
                    
                    if (!f.getName().endsWith(".txt")){
                        
                        JOptionPane.showMessageDialog(error_frame,"Invalid file type. Must be a text file",
                        "File Load Error", JOptionPane.ERROR_MESSAGE);
                    }
                    try(BufferedReader file= Files.newBufferedReader(Paths.get(f.getPath()))){
                        String line;
                        int Sim_new_delay = 0;
                        int Sim_new_step = 0;
                        while ((line = file.readLine()) != null){
                            String[] contents = line.split(",");
                            
                            if (contents[0].equals(delays.DELAY.toString())){
                                Sim_new_delay = Integer.parseInt(contents[1]);
                            }
                            else if(contents[0].equals(delays.STEP.toString())){
                                Sim_new_step = Integer.parseInt(contents[1]);
                            }
                    }
                       if ((Sim_new_delay != 0) && (Sim_new_step != 0)){
                           SimController new_sim = new SimController();
                           new_sim.setDelay(Sim_new_delay);
                           new_sim.setStep(Sim_new_step);
                           quit();
                       }
                    }
                    catch(IOException ran){
                    
                    }
                    
                    
                    
                    
            
            }
            }
        };
        
        ActionListener save_file = new ActionListener(){
            public void actionPerformed(ActionEvent evt){
                JFileChooser chooser = new JFileChooser();
                int r = chooser.showSaveDialog(frame);

                if (r == JFileChooser.APPROVE_OPTION){
                    JFrame error_frame = new JFrame();
                    java.io.File f = chooser.getSelectedFile();
                    
                    if (!f.getName().endsWith(".txt")){
                        
                        JOptionPane.showMessageDialog(error_frame,"Invalid file type. Must be a text file",
                        "File Load Error", JOptionPane.ERROR_MESSAGE);
                    }
                    
                    String delay_line = "delay,";
                    delay_line = delay_line.concat(Integer.toString(timer.getDelay()));
                    
                    String step_line ="step,";
                    step_line = step_line.concat(Integer.toString(sim.getStep()));
                    try(BufferedWriter file= Files.newBufferedWriter(Paths.get(f.getPath()))){
                        file.write(delay_line);
                        file.newLine();
                        file.write(step_line);
                        
                    
                    }
                    catch(IOException ran){
                    
                    }
                    
                    
                    
                    
            
            }
            }
        };

    public Simulator getSimulator(){
        return sim;
    }
    
    public void setDelay(int new_delay){
        timer.setDelay(new_delay);
        delay_value.setText(Integer.toString(new_delay));
    }
    
    public void setStep(int steps_new){
        sim.simulate(steps_new);
        statusLabel.setText(Integer.toString(steps_new));
    }
    
    public void quit(){
        sim.endSimulation();
        frame.setVisible(false);
        frame.dispose();
    }
}