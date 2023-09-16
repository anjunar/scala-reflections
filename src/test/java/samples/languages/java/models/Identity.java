package samples.languages.java.models;

import jakarta.validation.constraints.NotBlank;

public class Identity {
    
    private String id;
    
    @NotBlank
    private String category;
    
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
