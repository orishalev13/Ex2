
   package Ex2;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

   @Nested
   class Ex2SheetTest {
        private Ex2Sheet sheet;

        @BeforeEach
        void setUp() {
            sheet = new Ex2Sheet(5, 5); // יצירת גיליון בגודל 5x5
        }

        @Test
        void testSetAndGet() {
            sheet.set(0, 0, "10"); // הגדרת ערך בתא (0, 0)
            assertEquals("10", sheet.get(0, 0).getData()); // בדיקה שהערך הוגדר נכון
        }

        @Test
        void testSetFormula() {
            sheet.set(0, 0, "5");
            sheet.set(0, 1, "10");
            sheet.set(0, 2, "=A1+A2"); // נוסחה שמחברת את A1 ו-A2
            assertEquals("15.0", sheet.value(0, 2)); // בדיקה שהנוסחה חושבה נכון
        }

        @Test
        void testInvalidCoordinates() {
            assertThrows(IllegalArgumentException.class, () -> sheet.set(-1, 0, "10")); // קואורדינטות לא חוקיות
        }

        @Test
        void testSaveAndLoad() throws Exception {
            sheet.set(0, 0, "Hello");
            sheet.set(0, 1, "=A1");
            sheet.save("test_sheet.csv"); // שמירת הגיליון לקובץ

            Ex2Sheet newSheet = new Ex2Sheet(5, 5);
            newSheet.load("test_sheet.csv"); // טעינת הגיליון מהקובץ
            assertEquals("Hello", newSheet.get(0, 0).getData()); // בדיקה שהערך נטען נכון
        }
    }


   class SCellTest {

       @Test
       void testGetTypeNumber() {
           SCell cell = new SCell("42"); // מספר
           assertEquals(Ex2Utils.NUMBER, cell.getType()); // בדיקה שהסוג הוא NUMBER
       }

       @Test
       void testGetTypeText() {
           SCell cell = new SCell("Hello"); // טקסט
           assertEquals(Ex2Utils.TEXT, cell.getType()); // בדיקה שהסוג הוא TEXT
       }

       @Test
       void testGetTypeFormula() {
           SCell cell = new SCell("=A1+B2"); // נוסחה
           assertEquals(Ex2Utils.FORM, cell.getType()); // בדיקה שהסוג הוא FORM
       }

       @Test
       void testComputeForm() {
           assertEquals(15.0, SCell.computeForm("=10+5")); // חישוב נוסחה פשוטה
           assertNull(SCell.computeForm("=A1+B2")); // נוסחה עם הפניות לתאים (לא ניתן לחשב בלי גיליון)
       }

       @Test
       void testIsNumber() {
           assertTrue(SCell.isNumber("42")); // בדיקה שמחרוזת היא מספר
           assertFalse(SCell.isNumber("Hello")); // בדיקה שמחרוזת אינה מספר
       }
   }

   class CellEntryTest {

       @Test
       void testGetRow() {
           assertEquals(0, CellEntry.getRow("A0")); // בדיקה שחילוץ השורה נכון
           assertEquals(10, CellEntry.getRow("B10")); // בדיקה עם שורה דו-ספרתית
       }

       @Test
       void testGetColumn() {
           assertEquals(0, CellEntry.getColumn("A0")); // בדיקה שחילוץ העמודה נכון
           assertEquals(1, CellEntry.getColumn("B10")); // בדיקה עם עמודה B
       }

       @Test
       void testToSheetind() {
           CellEntry entry = new CellEntry("A1");
           assertEquals(0, entry.toSheetind().get(0)); // בדיקת עמודה
           assertEquals(1, entry.toSheetind().get(1)); // בדיקת שורה
       }

       @Test
       void testIsValid() {
           CellEntry validEntry = new CellEntry("Z99"); // קואורדינטות חוקיות
           assertTrue(validEntry.isValid());

           CellEntry invalidEntry = new CellEntry("AA100"); // קואורדינטות לא חוקיות
           assertFalse(invalidEntry.isValid());
       }

       @Test
       void testConstructorWithInvalidCoordinates() {
           assertThrows(IllegalArgumentException.class, () -> new CellEntry("AA100")); // בדיקה שהקונסטרקטור זורק שגיאה בקואורדינטות לא חוקיות
       }
   }