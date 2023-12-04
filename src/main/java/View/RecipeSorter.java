package View;

import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import java.util.PriorityQueue;

import java.util.Comparator;

public class RecipeSorter {

    String sortorder;
    PriorityQueue<RecipeNode> pq;

    RecipeSorter(String sortorder){
        this.sortorder = sortorder;
        this.pq = new PriorityQueue<RecipeNode>(comparator(sortorder));
    }
    
    private Comparator<RecipeNode> comparator(String sort){
        switch(sort){
            case "Newest to Oldest":
                System.out.println("Current sort order is " + sort);
                return new Chronological(-1);
            case "Oldest to Newest":
                System.out.println("Current sort order is " + sort);
                return new Chronological(1);
            case "Alphabetically":
                System.out.println("Current sort order is " + sort);
                return new Alphabetical(1);
            case "Reverse Alphabetically":
                System.out.println("Current sort order is " + sort);
                return new Alphabetical(-1);
    }
        System.out.println("invalid sort order");
        return null;
    }

    public ListView<HBox> sort(ListView<HBox> list){
        for (HBox h: list.getItems()){
            RecipeNode r = (RecipeNode) h;
            pq.add(r);
        }
        ListView<HBox> sorted = new ListView<>();
        while(pq.peek() != null){
            sorted.getItems().add(pq.peek());
            pq.remove(pq.peek());
        }

        return sorted;
    }
    
}

class Chronological implements Comparator<RecipeNode>{
    int order;
    Chronological(int order){
        this.order = order;
    }
    
    public int compare(RecipeNode r1, RecipeNode r2){
        return r1.getRecipeID() - r2.getRecipeID() > 0 ? order : -order;
    }
}

class Alphabetical implements Comparator<RecipeNode>{
    int order;
    Alphabetical(int order){
        this.order = order;
    }

    public int compare(RecipeNode r1, RecipeNode r2){
        return order * r1.getRecipeTitle().compareTo(r2.getRecipeTitle());
    }
}