import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.lang.management.ManagementFactory;
import com.sun.management.OperatingSystemMXBean;
import java.util.*;

public class SysMood {

    static JProgressBar cpuBar = new JProgressBar(0,100);
    static JProgressBar ramBar = new JProgressBar(0,100);
    static JLabel mood = new JLabel("Mood: --", JLabel.CENTER);
    static JLabel suggestion = new JLabel("Suggestion: --", JLabel.CENTER);
    static JPanel appPanel = new JPanel();

    public static void main(String[] args) {

        JFrame f = new JFrame("SysMood");
        f.setSize(800,500);
        f.setLayout(new BorderLayout());
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setLocationRelativeTo(null);

        f.getContentPane().setBackground(new Color(240,248,255));

        // Title
        JLabel title = new JLabel("System Mood Analyzer", JLabel.CENTER);
        title.setFont(new Font("Arial",Font.BOLD,22));
        title.setForeground(new Color(25,25,112));
        f.add(title,BorderLayout.NORTH);

        // Center Panel
        JPanel p = new JPanel(new GridLayout(6,1,10,10));
        p.setBackground(new Color(224,255,255));

        cpuBar.setStringPainted(true);
        ramBar.setStringPainted(true);

        mood.setFont(new Font("Arial",Font.BOLD,16));
        suggestion.setFont(new Font("Arial",Font.ITALIC,13));

        p.add(new JLabel("CPU Usage:"));
        p.add(cpuBar);
        p.add(new JLabel("RAM Usage:"));
        p.add(ramBar);
        p.add(mood);
        p.add(suggestion);

        f.add(p,BorderLayout.CENTER);

        // Apps Panel
        appPanel.setLayout(new BoxLayout(appPanel,BoxLayout.Y_AXIS));
        JScrollPane scroll = new JScrollPane(appPanel);
        scroll.setPreferredSize(new Dimension(250,0));
        scroll.setBorder(BorderFactory.createTitledBorder("Active Applications"));
        f.add(scroll,BorderLayout.EAST);

        // Button
        JButton btn = new JButton("Analyze System");
        btn.setBackground(new Color(70,130,180));
        btn.setForeground(Color.WHITE);
        f.add(btn,BorderLayout.SOUTH);

        btn.addActionListener(e -> {
            update();
            loadApps();
        });

        f.setVisible(true);
    }

    // CPU + RAM
    static void update() {
        try {
            OperatingSystemMXBean os = (OperatingSystemMXBean)
                    ManagementFactory.getOperatingSystemMXBean();

            int cpu = (int)(Math.max(os.getSystemCpuLoad(),0)*100);
            int ram = (int)(((double)(os.getTotalPhysicalMemorySize()
                    - os.getFreePhysicalMemorySize())
                    / os.getTotalPhysicalMemorySize())*100);

            cpuBar.setValue(cpu);
            ramBar.setValue(ram);

            if(cpu<40 && ram<40){
                mood.setText("Mood: 😊 Healthy");
                mood.setForeground(Color.GREEN);
                suggestion.setText("System running smoothly");
            }
            else if(cpu<70){
                mood.setText("Mood: 😐 Stressed");
                mood.setForeground(Color.ORANGE);
                suggestion.setText("Close few apps");
            }
            else{
                mood.setText("Mood: 😡 Unstable");
                mood.setForeground(Color.RED);
                suggestion.setText("High load!");
            }

        } catch(Exception e){
            e.printStackTrace();
        }
    }

    // SHOW ONLY USER APPS
    static void loadApps() {
        try {
            appPanel.removeAll();
            Set<String> set = new TreeSet<>();

            Process process = Runtime.getRuntime().exec(
                "powershell \"gps | where {$_.MainWindowTitle -ne ''} | select ProcessName, MainWindowTitle\""
            );

            BufferedReader br = new BufferedReader(
                    new InputStreamReader(process.getInputStream()));

            String line;
            while ((line = br.readLine()) != null) {

                line = line.trim().toLowerCase();

                if (line.length() > 0 && !line.contains("processname")) {

                    String[] parts = line.split("\\s+", 2);
                    if(parts.length < 2) continue;

                    String app = parts[0].trim();
                    String title = parts[1].trim();

                    // remove garbage
                    if(app.equals("") || app.contains("-") || title.equals("")) continue;

                    app = app + ".exe";

                    // filter system junk
                    if(title.length() > 3 &&
                       !app.contains("host") &&
                       !app.contains("system") &&
                       !app.contains("frame") &&
                       !app.contains("input") &&
                       !app.contains("service") &&
                       !app.contains("settings") &&
                       !app.contains("search") &&
                       !app.contains("runtime")) {

                        set.add(app);
                    }
                }
            }

            // UI
            for (String a : set) {

                JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT));
                row.setBackground(new Color(245,245,245));
                row.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

                JLabel l = new JLabel(a.replace(".exe",""));
                l.setFont(new Font("Arial",Font.BOLD,14));

                JButton x = new JButton("❌");
                x.setPreferredSize(new Dimension(50,25));
                x.setBackground(new Color(255,99,71));
                x.setForeground(Color.WHITE);

                x.addActionListener(e -> {
                    int confirm = JOptionPane.showConfirmDialog(null,
                            "Close " + a + "?");

                    if(confirm == 0){
                        try{
                            Runtime.getRuntime().exec("taskkill /IM "+a+" /F");
                            loadApps();
                        }catch(Exception ex){
                            ex.printStackTrace();
                        }
                    }
                });

                row.add(l);
                row.add(x);
                appPanel.add(row);
            }

            appPanel.revalidate();
            appPanel.repaint();

        } catch(Exception e){
            e.printStackTrace();
        }
    }
}