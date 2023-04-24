/*import javax.swing.*;
import java.awt.*;

public class CheckBoxListCellRenderer extends DefaultListCellRenderer {

    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        if (value instanceof ListItem) {
            ListItem item = (ListItem) value;
            return super.getListCellRendererComponent(list, item.getName(), index, isSelected, cellHasFocus);
        } else {
            return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        }
    }
}*/