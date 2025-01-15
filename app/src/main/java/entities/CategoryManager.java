package entities;
import java.util.List;

/**
 * defines the contract for managing categories in the system
 */
public interface CategoryManager {

    /**
     * creates a new category with the given name and description
     *
     * @param name        the name of the new category
     * @param description the description of the new category
     */
    void createCategory(String name, String description);

    /**
     * edits an existing category with new name and description
     *
     * @param category       the category to be edited
     * @param newName        the new name for the category
     * @param newDescription the new description for the category
     */
    void editCategory(Category category, String newName, String newDescription);

    /**
     * deletes a category from the system
     *
     * @param category the category to be deleted
     */
    void deleteCategory(Category category);

    /**
     * retrieves a list of all categories in the system
     *
     * @return a list of all categories
     */
    List<Category> listCategories();
}