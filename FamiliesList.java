import java.io.Serializable;
import java.util.List;
import java.util.ArrayList;

public class FamiliesList implements Serializable{

    private String name;
    private ArrayList<ListItem> items;

    public FamiliesList(String name) {
    	 this.name = name;
         items = new ArrayList<ListItem>();
     }

     public String getName() {
         return name;
     }

     public void setName(String name) {
         this.name = name;
     }

     public List<ListItem> getItems() {
         return items;
     }

     public void addItem(String item) {
         items.add(new ListItem(item, false));
     }

     public void removeItem(int index) {
         items.remove(index);
     }

     public void updateItem(int index, String newItem) {
         items.get(index).setText(newItem);
     }
}

