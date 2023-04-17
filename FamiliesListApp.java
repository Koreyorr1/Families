import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.ArrayList;

public class FamiliesListApp {

    private JFrame frame;
    private JList<ListItem> list;
    private DefaultListModel<ListItem> listModel;
    private ArrayList<FamiliesList> FamiliesLists;
    private FamiliesList currentList;
    private JButton backButton;
    private ArrayList<Boolean> itemCheckStates;


    public FamiliesListApp() {
        FamiliesLists = new ArrayList<>();
        itemCheckStates = new ArrayList<>();
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


        Container contentPane = frame.getContentPane();
        contentPane.setLayout(new BorderLayout());

        listModel = new DefaultListModel<>();
        list = new JList<ListItem>(listModel);
        list.setSelectionModel(new IgnoreCheckboxSelectionModel(list)); // Set the custom selection model
        list.addMouseListener(new CheckBoxListMouseListener());
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
        
        contentPane.add(new JScrollPane(list), BorderLayout.CENTER);
        
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
        
        contentPane.add(topPanel, BorderLayout.NORTH);

        updateListModel();
        
        frame.setVisible(true);
    }
    private void updateListModel() {
        listModel.clear();
        itemCheckStates.clear();

        if (currentList == null) {
            backButton.setVisible(false);
            list.setCellRenderer(new DefaultListCellRenderer());

            for (FamiliesList list : FamiliesLists) {
                listModel.addElement((ListItem) list.getItems());
            }
        } else {
            backButton.setVisible(true);
            list.setCellRenderer(new CheckBoxListRenderer());;

            for (ListItem item : currentList.getItems()) {
                listModel.addElement(item);
                itemCheckStates.add(false);
            }
        }
    }
    
    
    private static final String DATA_FILE = "families_list_data.ser";

    private void saveData() {
        try (FileOutputStream fos = new FileOutputStream(DATA_FILE);
             ObjectOutputStream oos = new ObjectOutputStream(fos)) {

            oos.writeObject(FamiliesLists);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadData() {
        File file = new File(DATA_FILE);
        if (file.exists()) {
            try (FileInputStream fis = new FileInputStream(file);
                 ObjectInputStream ois = new ObjectInputStream(fis)) {

                FamiliesLists = (ArrayList<FamiliesList>) ois.readObject();
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
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
                ListItem currentName = listModel.getElementAt(selectedIndex);

                String newName = (String) JOptionPane.showInputDialog(
                        frame,
                        "Enter a new name:",
                        "Rename",
                        JOptionPane.PLAIN_MESSAGE,
                        null,
                        null,
                        currentName);

                if (newName != null && !newName.trim().isEmpty()) {
                    if (currentList == null) {
                        FamiliesLists.get(selectedIndex).setName(newName);
                    } else {
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
    
    private class CheckBoxListRenderer extends JCheckBox implements ListCellRenderer<ListItem> {

        @Override
        public Component getListCellRendererComponent(JList<? extends ListItem> list, ListItem value, int index,
                                                      boolean isSelected, boolean cellHasFocus) {
            setText(value.getText());
            setSelected(value.isChecked());
            setOpaque(true);

            if (isSelected) {
                setBackground(list.getSelectionBackground());
                setForeground(list.getSelectionForeground());
            } else {
                setBackground(list.getBackground());
                setForeground(list.getForeground());
            }

            setEnabled(list.isEnabled());
            setFont(list.getFont());

            if (currentList != null) {
                setVisible(true);
            } else {
                setVisible(false);
            }

            return this;
        }
    }
    
    private class CheckBoxListMouseListener extends MouseAdapter {
        @Override
        public void mouseClicked(MouseEvent e) {
            int index = list.locationToIndex(e.getPoint());

            if (index != -1 && currentList != null) {
                ListItem item = list.getModel().getElementAt(index);
                Rectangle checkBoxBounds = list.getCellBounds(index, index);
                int xOffset = e.getPoint().x - checkBoxBounds.x;

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


    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                FamiliesListApp app = new FamiliesListApp();
                app.createAndShowGUI();
            }
        });
    }
}

   
