import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Base64;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.JsonObjectParser;
import com.google.api.client.json.gson.GsonFactory;




public class FamiliesListApp extends Main {

    private JFrame frame;
    private JList<Object> list;
    private CustomListModel listModel;
    private ArrayList<FamiliesList> FamiliesLists;
    private FamiliesList currentList;
    private JButton backButton;
    private ArrayList<Boolean> itemCheckStates;
    private CheckBoxListMouseListener checkBoxListMouseListener;
    private JPanel checkBoxPanel;
    private static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();


    public FamiliesListApp() {
        FamiliesLists = new ArrayList<>();
        itemCheckStates = new ArrayList<>();
       
        
       // initializeFirebase();
        loadData();
    }

    public void createAndShowGUI() {
        // Set the Nimbus Look and Feel


        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        frame = new JFrame("Families List App");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 400);
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                saveData();
                System.exit(0);
            }
        });

        checkBoxListMouseListener = new CheckBoxListMouseListener();

        Container contentPane = frame.getContentPane();
        contentPane.setLayout(new BorderLayout());

        listModel = new CustomListModel();
        list = new JList<Object>(listModel);
        
        //list.setSelectionModel(new IgnoreCheckboxSelectionModel(list)); // Set the custom selection model
        list.addMouseListener(checkBoxListMouseListener);
        
       list.setCellRenderer(new CheckBoxListRenderer()); // Set the custom cell renderer
        list.setSelectionModel(new DefaultListSelectionModel() { // Allow multiple selections with checkboxes
            @Override
            public void setSelectionInterval(int index0, int index1) {
                if (isSelectedIndex(index0)) {
                    super.removeSelectionInterval(index0, index1);
                } else {
                    super.addSelectionInterval(index0, index1);
                }
            }
        });
        list.getSelectionModel().addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            @Override
            public void valueChanged(javax.swing.event.ListSelectionEvent e) {
                if (!e.getValueIsAdjusting() && list.getSelectedIndex() != -1) {
                    int selectedIndex = list.getSelectedIndex();
                    list.clearSelection(); // Clear the selection to allow reselection of the same item

                    if (currentList == null) {
                        currentList = FamiliesLists.get(selectedIndex);
                        updateListModel();
                    }
                }
            }
        });
        
        // Create a separate panel for checkboxes
        checkBoxPanel = new JPanel(new BorderLayout());
        checkBoxPanel.setLayout(new BoxLayout(checkBoxPanel, BoxLayout.Y_AXIS));


        // Add the checkBoxPanel to the left of the JScrollPane
        JPanel combinedPanel = new JPanel(new BorderLayout());
        combinedPanel.add(checkBoxPanel, BorderLayout.WEST);
        combinedPanel.add(new JScrollPane(list), BorderLayout.CENTER);

       
        contentPane.add(new JScrollPane(list), BorderLayout.CENTER);
        
        contentPane.add(combinedPanel, BorderLayout.CENTER);
        
        
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        backButton = new JButton("<");
        backButton.setVisible(false);
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                currentList = null;
                updateListModel();
            }
        });
        leftPanel.add(backButton);
        
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton addButton = new JButton("+");
        addButton.addActionListener(new AddButtonListener());

        JButton renameButton = new JButton("Rename");
        renameButton.addActionListener(new RenameButtonListener());

        JButton removeButton = new JButton("Remove");
        removeButton.addActionListener(new RemoveButtonListener());

        
        rightPanel.add(addButton);
        rightPanel.add(renameButton);
        rightPanel.add(removeButton);
        
        JPanel topPanel = new JPanel(new BorderLayout());
        
        
        topPanel.add(leftPanel, BorderLayout.WEST);
        topPanel.add(rightPanel, BorderLayout.EAST);
        
        contentPane.add(checkBoxPanel, BorderLayout.WEST);
        contentPane.add(new JScrollPane(list), BorderLayout.CENTER);
        contentPane.add(topPanel, BorderLayout.NORTH);


        updateListModel();
        
        frame.setVisible(true);
    }
    
    private void updateListModel() {
    	
    	
    	int countCheckboxes = checkBoxPanel.getComponentCount();
    	
    	System.out.print(countCheckboxes);
    	
    	
    	checkBoxPanel.removeAll();
    	
    	
        listModel.clear();
        itemCheckStates.clear();
        
        
        
        if (currentList == null) {
        	
            backButton.setVisible(false);
            
            
            
            for (FamiliesList list : FamiliesLists) {
            	
            	
                listModel.addElement(list);
            }
            
            
            
        } else {

        	
            
            backButton.setVisible(true);
            checkBoxPanel.revalidate();
            checkBoxPanel.repaint();
        
            list.removeMouseListener(checkBoxListMouseListener);
            list.setCellRenderer(new CheckBoxListRenderer());

            for (ListItem item : currentList.getItems()) {
            	
            	CustomCheckBox checkBox = new CustomCheckBox();
                checkBoxPanel.add(checkBox);
                
                listModel.addElement(item);
                itemCheckStates.add(false);
            }
        }
    }
    
    private static final String DATA_FILE = "temp.ser";

    private void saveData() {
        try (FileOutputStream fos = new FileOutputStream(DATA_FILE);
        		ObjectOutputStream oos = new ObjectOutputStream(fos)) {
        	
        	String base64String = serializeObjectToBase64(oos);
            oos.writeObject(FamiliesLists);
            super.writeToDatabase(base64String);
            
        } catch (IOException e) {
            e.printStackTrace();
        }
        
    }

    private void loadData() {
    	 File file = new File(DATA_FILE);
    	 super.readFromDatabase();
    	    if (file.exists()) {
    	        try {
    	        	
    	            FileInputStream fis = new FileInputStream(file);
    	            ObjectInputStream ois = new ObjectInputStream(fis);
    	            FamiliesLists = (ArrayList<FamiliesList>) ois.readObject();
    	            ois.close();
    	            fis.close();
    	        } catch (IOException | ClassNotFoundException e) {
    	            e.printStackTrace();
    	        }

    	        // Convert the deserialized item names to ListItem objects
    	        for (FamiliesList list : FamiliesLists) {
    	            ArrayList<ListItem> items = new ArrayList<>();
    	            for (String itemName : list.getItemNames()) {
    	                items.add(new ListItem(itemName, false));
    	            }
    	            list.setItems(items);
    	        }
    	    }
    }

    public static String serializeObjectToBase64(Object object) {
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
            objectOutputStream.writeObject(object);
            objectOutputStream.close();
            return Base64.getEncoder().encodeToString(byteArrayOutputStream.toByteArray());
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private class CustomListModel extends DefaultListModel<Object> {
    	}
    
    private class AddButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (currentList == null) {
                JDialog dialog = new JDialog(frame, "Add List", true);
                dialog.setSize(300, 200);
                dialog.setLocationRelativeTo(frame);

                dialog.setLayout(new BorderLayout());

                JPanel formPanel = new JPanel(new GridLayout(3, 1));
                JLabel listNameLabel = new JLabel("List Name:");
                JTextField listNameField = new JTextField();
                JLabel shareWithLabel = new JLabel("Share with:");
                JTextField shareWithField = new JTextField();

                formPanel.add(listNameLabel);
                formPanel.add(listNameField);
                formPanel.add(shareWithLabel);
                formPanel.add(shareWithField);

                JButton submitButton = new JButton("Submit");
                submitButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        String listName = listNameField.getText().trim();
                        String shareWith = shareWithField.getText().trim(); // You can use this value to share the list if needed.
                        
                        if (!listName.isEmpty()) {
                            FamiliesLists.add(new FamiliesList(listName));
                            updateListModel();
                            dialog.dispose();
                        }
                    }
                });

                dialog.add(formPanel, BorderLayout.CENTER);
                dialog.add(submitButton, BorderLayout.SOUTH);
                dialog.setVisible(true);
            } else {
                JDialog dialog = new JDialog(frame, "Add Item", true);
                dialog.setSize(300, 120);
                dialog.setLocationRelativeTo(frame);

                dialog.setLayout(new BorderLayout());

                JPanel formPanel = new JPanel(new GridLayout(2, 1));
                JLabel itemNameLabel = new JLabel("Item Name:");
                JTextField itemNameField = new JTextField();

                formPanel.add(itemNameLabel);
                formPanel.add(itemNameField);

                JButton submitButton = new JButton("Submit");
                submitButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        String itemName = itemNameField.getText().trim();

                        if (!itemName.isEmpty()) {
                            currentList.addItem(itemName);
                            updateListModel();
                            dialog.dispose();
                        }
                    }
                });

                dialog.add(formPanel, BorderLayout.CENTER);
                dialog.add(submitButton, BorderLayout.SOUTH);
                dialog.setVisible(true);
            }
        }
    }


    private class RenameButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            int selectedIndex = list.getSelectedIndex();

            if (selectedIndex != -1) {
            	if (currentList == null) {
            		FamiliesList currentName = (FamiliesList) listModel.getElementAt(selectedIndex);
            		
            		 String newName = (String) JOptionPane.showInputDialog(
                             frame,
                             "Enter a new name:",
                             "Rename",
                             JOptionPane.PLAIN_MESSAGE,
                             null,
                             null,
                             currentName);
            		 if (newName != null && !newName.trim().isEmpty()) {
            			 FamiliesLists.get(selectedIndex).setName(newName);
            		 }
            		
            		}else {
            	
            			ListItem currentName = (ListItem) listModel.getElementAt(selectedIndex);
            		
                String newName = (String) JOptionPane.showInputDialog(
                        frame,
                        "Enter a new name:",
                        "Rename",
                        JOptionPane.PLAIN_MESSAGE,
                        null,
                        null,
                        currentName);
                
                if (newName != null && !newName.trim().isEmpty()) {
                        currentList.updateItem(selectedIndex, newName);
                }
                    updateListModel();
                }
            }
        }
    }

    private class RemoveButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            int selectedIndex = list.getSelectedIndex();

            if (selectedIndex != -1)
            {
                if (currentList == null) {
                    FamiliesLists.remove(selectedIndex);
                } else {
                    currentList.removeItem(selectedIndex);
                }

                updateListModel();
            }
        }
    }
    
    private class CustomCheckBox extends JCheckBox {
        private boolean showCheckbox = true;

        public CustomCheckBox() {
            super();
        }

        public void setShowCheckbox(boolean showCheckbox) {
            this.showCheckbox = showCheckbox;
        }

        @Override
        protected void paintComponent(Graphics g) {
            if (showCheckbox) {
                super.paintComponent(g);
            }
        }
    }

    private class RoundedPanel extends JPanel {
        private int cornerRadius = 15;

        public RoundedPanel() {
            super();
            setBackground(new Color(230, 230, 230)); // Set the background color
            setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3)); // Add some padding
        }

        @Override
        protected void paintComponent(Graphics g) {
            g.setColor(getBackground());
            g.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, cornerRadius, cornerRadius);
            super.paintComponent(g);
            g.setColor(Color.BLACK); // Set the border color
            g.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, cornerRadius, cornerRadius); // Draw the border
        }
    }


    private class CheckBoxListRenderer extends RoundedPanel implements ListCellRenderer<Object> {

        private JCheckBox checkBox;
        private JLabel label;

        public CheckBoxListRenderer() {
            setLayout(new BorderLayout());
            setOpaque(false);
            checkBox = new JCheckBox();
            label = new JLabel();
            add(checkBox, BorderLayout.WEST);
            add(label, BorderLayout.CENTER);
        }

        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                      boolean isSelected, boolean cellHasFocus) {

            if (value instanceof ListItem) {
                ListItem item = (ListItem) value;

                label.setText(item.getText());
                setSelected(item.isChecked());
                setBackground(isSelected ? list.getSelectionBackground() : list.getBackground());
                setForeground(isSelected ? list.getSelectionForeground() : list.getForeground());
                setShowCheckbox(true);
                checkBox.setEnabled(false);
                checkBox.setVisible(false);
            } else if (value instanceof FamiliesList) {
                FamiliesList familyList = (FamiliesList) value;

                label.setText(familyList.getName());
                setSelected(false);
                setBackground(isSelected ? list.getSelectionBackground() : list.getBackground());
                setForeground(isSelected ? list.getSelectionForeground() : list.getForeground());
                setEnabled(false); // Disable the checkbox for list names
                setShowCheckbox(false);
                checkBox.setEnabled(false);
                checkBox.setVisible(false);
            }

            return this;
        }

        private void setSelected(boolean selected) {
            checkBox.setSelected(selected);
        }

        private void setShowCheckbox(boolean showCheckbox) {
            checkBox.setVisible(showCheckbox);
        }
    }
    
    private class CheckBoxListMouseListener extends MouseAdapter {
        @Override
        public void mouseClicked(MouseEvent e) {
            int index = list.locationToIndex(e.getPoint());

            if (index != -1 && currentList != null) {
                ListItem item = (ListItem) list.getModel().getElementAt(index);
                Rectangle cellBounds = list.getCellBounds(index, index);
                int xOffset = e.getPoint().x - cellBounds.x;

                JCheckBox tempCheckBox = new JCheckBox();
                int checkBoxWidth = tempCheckBox.getPreferredSize().width;

                // If the click occurred within the checkbox's width
                if (xOffset >= 0 && xOffset <= checkBoxWidth) {
                    item.setChecked(!item.isChecked());
                    list.repaint();
                }
            }
        }
    }

    public static void runLists() {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                FamiliesListApp app = new FamiliesListApp();
                app.createAndShowGUI();
            }
        });
    }
}

   
