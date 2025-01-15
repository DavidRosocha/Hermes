package entities;

/**
 * Represents a category in the system.
 */
public class Category {

    /**
     * The unique identifier for the category.
     */
    private final String categoryId;

    /**
     * The name of the category.
     */
    private String categoryName;

    /**
     * The description of the category.
     */
    private String description;

    /**
     * Constructs a new category with a name and description.
     *
     * @param categoryName the name of the category
     * @param description  the description of the category
     * @param categoryId   the unique identifier for the category
     */
    public Category(String categoryName, String description, String categoryId) {
        this.categoryName = categoryName;
        this.description = description;
        this.categoryId = categoryId;  // Assign a unique ID to the category
    }

    /**
     * Gets the category ID.
     *
     * @return the unique identifier for the category
     */
    public String getCategoryId() {
        return categoryId;
    }

    /**
     * Gets the category name.
     *
     * @return the name of the category
     */
    public String getCategoryName() {
        return categoryName;
    }

    /**
     * Gets the category description.
     *
     * @return the description of the category
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets a new name for the category.
     *
     * @param newName the new name to be set for the category
     */
    public void setName(String newName) {
        categoryName = newName;
        System.out.println("New name has been set for the category");
    }

    /**
     * Sets a new description for the category.
     *
     * @param newDescription the new description to be set for the category
     */
    public void setDescription(String newDescription) {
        description = newDescription;
        System.out.println("New description has been set for the category");
    }

    // Override equals and hashCode for correct comparison in collections

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Category category = (Category) o;

        // Compare based on categoryName or categoryId
        return categoryName != null ? categoryName.equals(category.categoryName) : category.categoryName == null;
    }

    @Override
    public int hashCode() {
        return categoryName != null ? categoryName.hashCode() : 0;
    }
}
