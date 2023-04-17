import javax.swing.*;
import java.awt.*;

public class ListContentWindow {

    private JFrame frame;
    private JList<ListItem> itemList;
    private DefaultListModel<ListItem> itemListModel;
    private FamiliesList FamiliesList;

    public ListContentWindow(FamiliesList FamiliesList) {
        this.FamiliesList = FamiliesList;
        createAndShowGUI();
    }

    private void createAndShowGUI() {
        frame = new JFrame(FamiliesList.getName());
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(600, 400);

        Container contentPane = frame.getContentPane();
        contentPane.setLayout(new BorderLayout());

        itemListModel = new DefaultListModel<>();
        itemList = new JList<>(itemListModel);
        contentPane.add(new JScrollPane(itemList), BorderLayout.CENTER);

        for (ListItem item : FamiliesList.getItems()) {
            itemListModel.addElement(item);
        }

        frame.setVisible(true);
    }
}
