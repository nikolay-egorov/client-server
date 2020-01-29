package client;

import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import javax.swing.*;
import javax.swing.border.*;

/**
*
* Requires: /ImageClient.java
*           /images/[non-empty folder]
*
*/

public class ClientUI extends JFrame
{
    private static final long serialVersionUID = 1L;

    // Global java swing variables.
    private JButton connectButton = null;
    private JButton downloadButton = null;
    private JButton uploadButton = null;
    private JButton updateButton = null;
    private JButton setWallpaperButton = null;
    private JLabel hostLabel = null;
    private JLabel portLabel = null;
    private JLabel ownLabel = null;
    private JLabel serverLabel = null;
    private JLabel status = null;
    private JList<String> ownImageList = null;
    private JList<String> serverImageList = null;
    private JPanel mainPanel = null;
    private JPanel statusBar = null;
    private JScrollPane serverListScrollPane = null;
    private JScrollPane ownListScrollPane = null;
    private JTextField hostField = null;
    private JTextField portField = null;
    private Client ic = null;
    private String[] ownList = null;
    private String[] serverList = null;
  
    /**
    * Initialises the setup of the UI.
    **/
    public ClientUI()
    {   
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(600, 800));
        setTitle("Image Download and Upload Client");

        createComponents();
        createLayout();
        add(mainPanel);
        add(statusBar, BorderLayout.SOUTH);

        pack();
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    /**
    * Creates all UI components to be used.
    **/
    private void createComponents()
    {
        this.connectButton = new JButton("Connect");
        this.connectButton.addActionListener(new Connect());
        this.downloadButton = new JButton("Download");
        this.downloadButton.addActionListener(new Download());
        this.downloadButton.setEnabled(false);
        this.uploadButton = new JButton("Upload");
        this.uploadButton.addActionListener(new Upload());
        this.uploadButton.setEnabled(false);
        this.setWallpaperButton = new JButton("Set as Wallpaper");
        this.setWallpaperButton.addActionListener(new Wallpaper());
        this.setWallpaperButton.setEnabled(false);
        this.updateButton = new JButton("Update Lists");
        this.updateButton.addActionListener(new Update());
        this.updateButton.setEnabled(false);

        this.hostLabel = new JLabel("Host: ");
        this.hostField = new JTextField("127.0.0.1", 20);
        this.portLabel = new JLabel("Port: ");
        this.portField = new JTextField("6000", 15);

        this.ownLabel = new JLabel("My image list:");
        this.ownImageList = new JList<String>();
        this.ownListScrollPane = new JScrollPane();
        this.ownListScrollPane.getViewport().add(ownImageList);
        this.ownListScrollPane.setPreferredSize(new Dimension(10000, 9000));

        this.serverLabel = new JLabel("Server image list:");
        this.serverImageList = new JList<String>();
        this.serverListScrollPane = new JScrollPane();
        this.serverListScrollPane.getViewport().add(serverImageList);
        this.serverListScrollPane.setPreferredSize(new Dimension(10000, 9000));

        this.status = new JLabel("Ready to connect!");
        this.status.setHorizontalAlignment(SwingConstants.LEFT);
        
    }

    /**
    * Sets the layout for the UI components created in createComponents().
    **/
    private void createLayout()
    {
        Box connectBox = new Box(BoxLayout.LINE_AXIS);
        connectBox.add(this.hostLabel);
        connectBox.add(this.hostField);
        connectBox.add(Box.createHorizontalStrut(10));
        connectBox.add(this.portLabel);
        connectBox.add(this.portField);
        connectBox.add(Box.createHorizontalStrut(10));
        connectBox.add(this.connectButton);
        connectBox.add(Box.createHorizontalStrut(10));
        connectBox.add(this.updateButton);

        Box ownListBox = new Box(BoxLayout.PAGE_AXIS);
        ownListBox.add(this.ownLabel);
        ownListBox.add(Box.createVerticalStrut(5));
        ownListBox.add(this.ownListScrollPane);
        ownListBox.add(Box.createVerticalStrut(5));
        JPanel panel = new JPanel(new GridLayout(1,2));
        panel.add(this.uploadButton);
        panel.add(this.setWallpaperButton);
        ownListBox.add(panel);


        Box serverListBox = new Box(BoxLayout.PAGE_AXIS);
        serverListBox.add(this.serverLabel);
        serverListBox.add(Box.createVerticalStrut(5));
        serverListBox.add(this.serverListScrollPane);
        serverListBox.add(Box.createVerticalStrut(5));
        serverListBox.add(this.downloadButton);
        serverListBox.add(Box.createVerticalStrut(5));

        Box imageListBox = new Box(BoxLayout.LINE_AXIS);
        imageListBox.add(ownListBox);
        imageListBox.add(Box.createHorizontalStrut(5));
        imageListBox.add(new JSeparator(SwingConstants.VERTICAL));
        imageListBox.add(Box.createHorizontalStrut(5));
        imageListBox.add(serverListBox);

        this.mainPanel = new JPanel();
        this.mainPanel.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
        this.mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.PAGE_AXIS));
        this.mainPanel.add(connectBox);
        this.mainPanel.add(Box.createVerticalStrut(5));
        this.mainPanel.add(new JSeparator(SwingConstants.HORIZONTAL));
        this.mainPanel.add(Box.createVerticalStrut(5));
        this.mainPanel.add(imageListBox);

        this.statusBar = new JPanel();
        this.statusBar.setBorder(new BevelBorder(BevelBorder.LOWERED));
        this.statusBar.setPreferredSize(new Dimension(this.getWidth(), 20));
        this.statusBar.setLayout(new BoxLayout(statusBar, BoxLayout.LINE_AXIS));
        this.statusBar.add(this.status);
    }

    /**
    * Updates the status bar message with the string that passed to this method, and prints
    * to terminal what the string is.
    **/
    private void updateStatus(String statusMessage)
    {
        System.out.println("[CLIENT STATUS] " + statusMessage);
        this.status.setText(statusMessage);
    }    

    /**
    * Reads the image lists from the Client object.
    * Updates the server and user image lists, and calls updateStatus() to show the user what
    * the program is doing in the status bar.
    **/
    private void updateLists()
    {
        this.ic.getImageList();
        updateStatus("Retrieving list of own images.");
        this.ownList = this.ic.imageList.toArray(new String[ic.imageList.size()-1]);
        this.ownImageList = new JList<String>(this.ownList);
        updateStatus("List compiled! Retrieving server image list.");
        this.serverList = this.ic.serverImageList.toArray(new String[ic.imageList.size()-1]);
        this.serverImageList = new JList<String>(this.serverList);
        updateStatus("Finished reading from server.");

        this.ownImageList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        this.ownImageList.setSelectedIndex(0);
        this.ownListScrollPane.getViewport().add(this.ownImageList);

        this.serverImageList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        this.serverImageList.setSelectedIndex(0);
        this.serverListScrollPane.getViewport().add(this.serverImageList);
    }


    /**
    * Creates a new Client object using the host and port read from the corresponding
    * fields. Calls updateLists() to update the lists of the user and the server.
    **/
    private void connectToServer(String host, String port)
    {
        try
        {   updateStatus("Creating Client object.");
            this.ic = new Client(host, Integer.parseInt(port));
            updateLists();
            uploadButton.setEnabled(true);
            downloadButton.setEnabled(true);
            updateButton.setEnabled(true);
            setWallpaperButton.setEnabled(true);
            updateStatus("Connected to server and updated image lists.");
        }

        catch (Exception e)
        {   System.out.println("Exception: " + e.getMessage());
            e.printStackTrace();
            updateStatus("Error connecting to server! Is server running?");
        }
    }

    /**
    * Listener for the "Connect" button.
    **/
    private class Connect implements ActionListener 
    {
        /**
        * Calls connectToServer using text in host and port fields.
        **/
         public void actionPerformed(ActionEvent a) 
         {
            connectToServer(hostField.getText(), portField.getText());
         }
    }

    /**
    * Listener for the "Download" button.
    **/
    private class Download implements ActionListener
    {
        /**
        * Gets name of file selected in server images list, and uses Client object's
        * method downloadFromServer() to download from server. Updates lists afterwards.
        **/
        public void actionPerformed(ActionEvent a)
        {
            String fileName = serverList[serverImageList.getSelectedIndex()];
            for (String item: ownList)
                if(item.equals(fileName)){
                    updateStatus(fileName + " is already on the client");
                    return;
                }
            ic.downloadFromServer(fileName);
            updateLists();
            updateStatus("Downloaded " + fileName + " and updated lists.");
        }
    }

    /**
     * Listener for the "Set as Wallpaper" button.
     **/
    private class Wallpaper implements ActionListener
    {
        /**
         * Gets name of file selected in own images list, and uses Client object's
         * method setWallpaper() to send to server.
         **/
        public void actionPerformed(ActionEvent a)
        {
            String fileName = ownList[ownImageList.getSelectedIndex()];
            try {
                ic.setWallPaper(fileName);
            } catch (IOException e) {
                e.printStackTrace();
            }
            updateStatus("Set " + fileName + " as main Wallpaper.");
        }
    }


    /**
    * Listener for the "Upload" button.
    **/
    private class Upload implements ActionListener
    {
        /**
        * Gets name of file selected in own images list, and uses Client object's
        * method sendToServer() to send to server. Updates lists afterwards.
        **/
        public void actionPerformed(ActionEvent a)
        {
            String fileName = ownList[ownImageList.getSelectedIndex()];
            ic.sendToServer(fileName);
            updateLists();
            updateStatus("Uploaded " + fileName + " and updated lists.");
        }
    }

    /**
    * Listener for the "Update Lists" button.
    **/
    private class Update implements ActionListener
    {
        /**
        * Calls updateLists() method.
        **/
        public void actionPerformed(ActionEvent a)
        {
            updateLists();
            updateStatus("Updated lists.");
        }
    }

    /**
    *  Runs the user interface.
    **/
    public static void main(String[] args) 
    {
        SwingUtilities.invokeLater(new Runnable()
        {
            @Override
            public void run()
            {
                ClientUI icui = new ClientUI();
                icui.setVisible(true);
            }
        });
    }
}