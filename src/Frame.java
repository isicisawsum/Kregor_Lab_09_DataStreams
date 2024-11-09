import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.awt.Color;

import static java.nio.file.StandardOpenOption.CREATE;


public class Frame extends JFrame{
    JPanel mainPnl;
    JPanel scrollArea;
    JPanel buttonArea;
    JPanel titlePanel;
    JLabel title;
    JTextArea searchArea;
    JTextArea scroll1;
    JTextArea scroll2;
    JScrollPane scrollLine1;
    JScrollPane scrollLine2;
    JButton load;
    JButton search;
    JButton quit;
    private static Set<String> stopWords = readStopwords();
    private static LinkedList<String> words = new LinkedList<>();
    //private static String totalText = "";

    public Frame(){
        mainPnl = new JPanel();
        mainPnl.setLayout(new BorderLayout());

        createTitleSearch();
        mainPnl.add(titlePanel, BorderLayout.NORTH);

        createSides();
        mainPnl.add(scrollArea, BorderLayout.CENTER);

        createButtonArea();
        mainPnl.add(buttonArea, BorderLayout.SOUTH);

        add(mainPnl);
        setSize(900,500);
        setLocation(0,0);
        setTitle("Data Streamer");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    private void createTitleSearch(){
        titlePanel = new JPanel();
        titlePanel.setLayout(new GridLayout(2, 1));

        title = new JLabel("DataStreams", JLabel.CENTER);
        title.setFont(new Font("Impact", Font.PLAIN, 30));

        searchArea = new JTextArea(1, 15);
        searchArea.setBorder((new TitledBorder(new LineBorder(Color.BLACK), "Search:")));
        searchArea.setEditable(true);

        titlePanel.add(title);
        titlePanel.add(searchArea);
    }

    public void createSides(){
        scrollArea = new JPanel();
        scrollArea.setLayout(new GridLayout(1, 2));

        scroll1 = new JTextArea(10 ,10);
        scroll2 = new JTextArea(10 ,10);
        scroll1.setEditable(false);
        scroll2.setEditable(false);
        scroll1.setBorder(new TitledBorder(new LineBorder(Color.BLACK), "File Text:"));
        scroll2.setBorder(new TitledBorder(new LineBorder(Color.BLACK), "Filtered File:"));


        scrollLine1 = new JScrollPane(scroll1);
        scrollLine1.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollLine2 = new JScrollPane(scroll2);
        scrollLine2.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        scrollArea.add(scrollLine1);
        scrollArea.add(scrollLine2);
    }


    private void createButtonArea(){
        buttonArea = new JPanel();
        buttonArea.setLayout(new GridLayout(1,3));

        load = new JButton();
        load.setText("LOAD FILE");
        load.addActionListener((ActionEvent ae) -> getFile());

        search = new JButton();
        search.setText("SEARCH");
        search.addActionListener((ActionEvent ae) -> searchFile());

        quit = new JButton();
        quit.setText("QUIT");
        quit.addActionListener((ActionEvent ae) -> System.exit(0));

        buttonArea.add(load);
        buttonArea.add(search);
        buttonArea.add(quit);
    }

    public void getFile(){
        JFileChooser chooser = new JFileChooser();
        File selectedFile;
        String rec = "";
        //String[] alphabet = {"a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z"};

        String total = "";


        try {
            Path workingDirectory = new File(System.getProperty("user.dir")).toPath();

            chooser.setCurrentDirectory(workingDirectory.toFile());

            if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                selectedFile = chooser.getSelectedFile();
                Path file = selectedFile.toPath();

                InputStream in =
                        new BufferedInputStream(Files.newInputStream(file, CREATE));
                BufferedReader reader =
                        new BufferedReader(new InputStreamReader(in));

                scroll1.setText("");
                scroll2.setText("");

                while (reader.ready()) {
                    rec = reader.readLine();
                    //System.out.println(rec);

                    rec = rec.toLowerCase();
                    rec = rec.replaceAll("[^a-z ]", "");

                    String[] lineWords = rec.split("\\s+");



                    for (String word : lineWords) {
                        scroll1.append(word + "\n");
                        if (!stopWords.contains(word)) { //filtered side
                            scroll2.append(word + "\n");
                            //words.add(word);
                        }
                    }
                }

                reader.close();
                System.out.println("\n\nData file read!");



            } else  // User closed the chooser without selecting a file
            {
                System.out.println("No file selected!!!");
                JOptionPane.showMessageDialog(this, "No file selected!");
            }
        }
        catch (FileNotFoundException e)
        {
            JOptionPane.showMessageDialog(this, "File not found!");
            e.printStackTrace(); //the file user entered was not found
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    private static Set<String> readStopwords(){ //reads the stop words file, returns a set and trims the space at the end
        Set<String> stopWords = new TreeSet<>();

        try (BufferedReader reader = new BufferedReader(new FileReader("src/StopWords"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                stopWords.add(line.trim().toLowerCase());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return stopWords;
    }

    private void searchFile(){
        String find = searchArea.getText().toLowerCase().trim(); //gets what's in the search box
        int count = 0;

        String[] wordList = scroll2.getText().toLowerCase().split("\\s+");

        if(!(find.equals(""))){
            if(!scroll2.getText().isEmpty()){
                for(String word: wordList){
                    if(word.equals(find)){
                        count ++;
                    }
                }
                if(count == 0){
                    JOptionPane.showMessageDialog(this, find + " not found in file");
                }
                else{
                    JOptionPane.showMessageDialog(this, find + " found " + count + " times.");
                }
            }
            else{
                JOptionPane.showMessageDialog(this, "No file found!");
            }
        }
        else{
            JOptionPane.showMessageDialog(this, "Search box is empty!");
        }
    }

}
