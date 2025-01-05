public class CellEntry implements Index2D {
    private String content;
    private int x;
    private int y;

    private static final int ERROR_CODE = Ex2Utils.ERR; // שימוש בקבוע עבור קוד שגיאה

    @Override
    public boolean isValid() {
        return x >= 0 && y >= 0;
    }

    @Override
    public int getX() {
        return x == -1 ? ERROR_CODE : x; // התנאי מוודא תקינות ומחזיר שגיאה במידת הצורך
    }

    @Override
    public int getY() {
        return y == -1 ? ERROR_CODE : y; // התנאי מוודא תקינות ומחזיר שגיאה במידת הצורך
    }
}