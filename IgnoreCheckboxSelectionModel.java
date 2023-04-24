import java.awt.*;
import javax.swing.*;


class IgnoreCheckboxSelectionModel extends DefaultListSelectionModel {
    private JList<Object> list;

    public IgnoreCheckboxSelectionModel(JList<Object> list) {
        this.list = list;
    }

    @Override
    public void setSelectionInterval(int index0, int index1) {
        if (shouldIgnoreSelection(index0)) {
            return;
        }
        super.setSelectionInterval(index0, index1);
    }

    private boolean shouldIgnoreSelection(int index) {
        Point point = MouseInfo.getPointerInfo().getLocation();
        SwingUtilities.convertPointFromScreen(point, list);
        int selectedIndex = list.locationToIndex(point);
        if (selectedIndex == index) {
            Rectangle cellBounds = list.getCellBounds(index, index);
            return cellBounds != null && point.x <= cellBounds.x + 20; // 20 is an approximate width of the checkbox
        }
        return false;
    }
}
