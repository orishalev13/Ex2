package Ex2;
// Add your documentation below:

public abstract class CellEntry  implements Index2D {
private String line;
private int x;
private int y;
    @Override
    public boolean isValid(int x, int y) {
         this.x
        return false;
    }

    @Override
    public int getX() {
        if (isValid(x,y))
            return x;
        return Ex2Utils.ERR;}

    @Override
    public int getY() {
      if (isValid(x,y))
        return y;
        return Ex2Utils.ERR;}
}
