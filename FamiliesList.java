
import java.util.ArrayList;

public class FamiliesList {

    private String name;
    private ArrayList<String> items;

    public FamiliesList(String name) {
    	 this.name = name;
         items = new ArrayList<>();
     }

     public String getName() {
         return name;
     }

     public void setName(String name) {
         this.name = name;
     }

     public ArrayList<String> getItems() {
         return items;
     }

     public void addItem(String item) {
         items.add(item);
     }

     public void removeItem(int index) {
         items.remove(index);
     }

     public void updateItem(int index, String newItem) {
         items.set(index, newItem);
     }
}

