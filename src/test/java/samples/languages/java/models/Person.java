package samples.languages.java.models;

import jakarta.validation.constraints.Size;

public class Person extends Identity {

    @Size(min = 3, max = 80)
    private String firstName;
    
    @Size(min = 3, max = 80)
    private String lastName;
    
    @Size(min = 3, max = 80)
    private String category;

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    @Override
    public String getCategory() {
        return category;
    }
    
    @Override
    public void setCategory(String category) {
        this.category = category;
    }

/*
    @Override
    public String toString() {
        return "Person{" +
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                '}';
    }
*/
}
