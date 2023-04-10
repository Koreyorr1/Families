import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

public class FamiliesListApp {

    private JFrame frame;
    private JList<String> list;
    private DefaultListModel<String> listModel;
    private JTextField textField;
    private ArrayList<FamiliesList> FamiliesLists;
    private FamiliesList currentList;
    private JButton backButton;

    public FamiliesListApp() {
        FamiliesLists = new ArrayList<>();
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

        Container contentPane = frame.getContentPane();
        contentPane.setLayout(new BorderLayout());

        listModel = new DefaultListModel<>();
        list = new JList<>(listModel);
        list.addMouseListener(new ListSelectionListener());
        contentPane.add(new JScrollPane(list), BorderLayout.CENTER);

        textField = new JTextField();
        textField.setVisible(true);
        textField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addListOrItem();
            }
        });
        
        backButton = new JButton("<");
        backButton.setVisible(false);
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                currentList = null;
                updateListModel();
            }
        });
        
        JButton addButton = new JButton("+");
        addButton.addActionListener(new AddButtonListener());

        JButton renameButton = new JButton("Rename");
        renameButton.addActionListener(new RenameButtonListener());

        JButton removeButton = new JButton("Remove");
        removeButton.addActionListener(new RemoveButtonListener());

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        topPanel.add(backButton);
        topPanel.add(addButton);
        topPanel.add(renameButton);
        topPanel.add(removeButton);

        contentPane.add(topPanel, BorderLayout.NORTH);
        contentPane.add(textField, BorderLayout.SOUTH);

        updateListModel();

        frame.setVisible(true);
    }

    private void updateListModel() {
        listModel.clear();

        if (currentList == null) {
            backButton.setVisible(false);

            for (FamiliesList List : FamiliesLists) {
                listModel.addElement(List.getName());
            }
        } else {
            backButton.setVisible(true);

            for (String item : currentList.getItems()) {
                listModel.addElement(item);
            }
        }
    }

    private class AddButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (textField.isVisible()) {
                addListOrItem();
            } else {
                textField.setVisible(true);
            }
        }
    }
    
    private void addListOrItem() {
        String inputText = textField.getText().trim();

        if (!inputText.isEmpty()) {
            if (currentList == null) {
                FamiliesLists.add(new FamiliesList(inputText));
            } else {
                currentList.addItem(inputText);
            }
            textField.setText("");
            updateListModel();
        }
    }

    private class RenameButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            int selectedIndex = list.getSelectedIndex();

            if (selectedIndex != -1) {
                String currentName = listModel.getElementAt(selectedIndex);

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

    private class ListSelectionListener extends MouseAdapter {
        @Override
        public void mouseClicked(MouseEvent e) {
            if (e.getClickCount() == 2 && !list.isSelectionEmpty()) {
                int selectedIndex = list.getSelectedIndex();

                if (currentList == null) {
                    currentList = FamiliesLists.get(selectedIndex);
                } else {
                    currentList = null;
                }

                updateListModel();
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

   