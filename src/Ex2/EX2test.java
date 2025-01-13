package Ex2;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
public class EX2test {
    @Test
    void isformTest()
    {

    }
    @Test
    void testGetcellValueAndSetcellValue() {
        dcell1 cell = new dcell1("Initial Value");
        assertEquals("Initial Value", cell.GetcellValue());

        cell.SetcellValue("New Value");
        assertEquals("New Value", cell.GetcellValue());
    }

    @Test
    void testIsNumber() {
        dcell1 cell = new dcell1("");

        assertTrue(SCell.isNumber("123"));
        assertTrue(SCell.isNumber("-123.45"));
        assertFalse(SCell.isNumber("abc"));
        assertFalse(SCell.isNumber(""));
    }

    @Test
    void testIsText() {
        dcell1 cell = new dcell1("");

        assertTrue(cell.isText("Hello"));
        assertTrue(cell.isText("This is a text"));
        assertFalse(cell.isText("=1+2"));
        assertFalse(cell.isText("123"));
        assertFalse(cell.isText(""));
    }

    @Test
    void testIsForm() {
        dcell1 cell = new dcell1("");

        assertTrue(SCell.isnumform("=1+2"));
        assertTrue(SCell.isnumform("=((1+2)*2)"));
        assertFalse(SCell.isnumform("1+2"));
        assertFalse(SCell.isnumform("Hello"));
        assertFalse(SCell.isnumform(""));
    }

    @Test
    void testComputeForm() {
        dcell1 cell = new dcell1("");

        Double result1 = null;
        try {
            result1 = SCell.computeForm("=1+2");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        assertEquals(1.0, result1); // מקום להחליף לערך המצופה

        Double result2 = null;
        try {
            result2 = SCell.computeForm("=((1+2)*2)");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        assertEquals(1.0, result2); // מקום להחליף לערך המצופה

        Double result3 = null;
        try {
            result3 = SCell.computeForm("123");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        assertEquals(-1.0, result3);
    }

    @Test
    void testEvaluateExpression() {
        dcell1 cell = new dcell1("");

        try {
            assertEquals(5.0, cell.evaluateExpression("1 2 + 2 *")); // 1+2*2 = 5
            assertEquals(5.0, cell.evaluateExpression("1 2 + 2 * 1 -")); // ((1+2)*2)-1 = 5
        } catch (Exception e) {
            fail("Evaluation threw an exception: " + e.getMessage());
        }
    }
}
