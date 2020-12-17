import model.Category;
import model.CostManagerException;
import model.DerbyDBModel;
import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class AddAndDeleteCategoryTest {
    DerbyDBModel derbyDBModel;
    private final static String MOCK_CATEGORY_EXIST = "Food";
    private final static String MOCK_CATEGORY_NOT_EXIST = "lalala";

    @BeforeEach
    public void setUp()
    {
        derbyDBModel = new DerbyDBModel();
    }

    @Test
    public void failAddTest() {
        try {
            Category category = new Category(MOCK_CATEGORY_EXIST);
            derbyDBModel.addNewCategory(category);
            fail("The addition succeed when it should failed");
        } catch (CostManagerException e) {
            Assertions.assertEquals("This category already exists", e.getMessage());
        }
    }

    @Test
    public void succeedAddTest() {
        try {
            Category category = new Category(MOCK_CATEGORY_NOT_EXIST);
            derbyDBModel.addNewCategory(category);
            derbyDBModel.deleteCategory(category);
        } catch (CostManagerException e) {
            fail("The exception was thrown even though it was not supposed to be thrown");
        }
    }

    @Test
    public void failDeleteTest() {
        try {
            Category category = new Category(MOCK_CATEGORY_NOT_EXIST);
            derbyDBModel.deleteCategory(category);
            fail("The addition succeed when it should failed");
        } catch (CostManagerException e) {
            Assertions.assertEquals("This category doesn't exist", e.getMessage());
        }
    }

    @Test
    public void succeedDeleteTest() {
        try {
            Category category = new Category(MOCK_CATEGORY_EXIST);
            derbyDBModel.deleteCategory(category);
            derbyDBModel.addNewCategory(category);
        } catch (CostManagerException e) {
            fail("The exception was thrown even though it was not supposed to be thrown ");
        }
    }
}