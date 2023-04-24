import java.io.Serializable;
import java.util.List;
import java.util.ArrayList;

public class FamiliesList implements Serializable{

    private String name;
    private transient ArrayList<ListItem> items;
    private ArrayList<String> itemNames;

    public FamiliesList(String name) {
    	 this.name = name;
         this.items = new ArrayList<ListItem>();
         this.itemNames = new ArrayList<String>();
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
     
     public int countItems() {
    	 return items.size();
     }
     
     public ArrayList<String> getItemNames() {
    	 return itemNames;
     }
     
     public void setItems(ArrayList<ListItem> items) {
    	    this.items = items;
    	}


     public void addItem(String item) {
         items.add(new ListItem(item, false));
         itemNames.add(item);
     }

     public void removeItem(int index) {
         items.remove(index);
     }

     public void updateItem(int index, String newItem) {
         items.get(index).setText(newItem);
     }
}

