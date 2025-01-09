package Ex2;

import java.io.IOException;
// Add your documentation below:

public class Ex2Sheet implements Sheet {
    //private Cell[][] table;
    //private int[][] depth;
   // private final int width;
    //private final int height;
    //private final Map<String, Cell> cells;
    // Add your code here
    private SCell[][] cells;

    public Ex2Sheet(int width, int height) {
        cells = new SCell[width][height];
    }

    @Override
    public boolean isIn(int x, int y) {
        return x >= 0 && x < width() && y >= 0 && y < height();
    }

    @Override
    public int width() {
        return cells.length;
    }

    @Override
    public int height() {
        return cells[0].length;
    }

    @Override
    public void set(int x, int y, String c) {
        if (isIn(x, y)) {
            cells[x][y] = new SCell(c);
        }
    }

    @Override
    public Cell get(int x, int y) {
        return isIn(x, y) ? cells[x][y] : null;
    }

    @Override
    public Cell get(String entry) {
        int x = Ex2Utils..getColumn(entry);
        int y = Ex2Utils.getRow(entry);
        return get(x, y);
    }

    @Override
    public String value(int x, int y) {
        Cell cell = get(x, y);
        return cell != null ? cell.getData() : "";
    }

    @Override
    public String eval(int x, int y) {
        SCell cell = (SCell) get(x, y);
        if (cell == null) return "";

        String data = cell.getData();
        int type = cell.getType();

        if (type == Ex2Utils.NUMBER) {
            return data;
        } else if (type == Ex2Utils.TEXT) {
            return data;
        } else if (type == Ex2Utils.FORM) {
            Double result = Ex2Utils.evaluateFormula(data, this);
            return result != null ? result.toString() : "ERR_FORM";
        } else {
            return "ERR_FORM";
        }
    }

    @Override
    public void eval() {
        for (int x = 0; x < width(); x++) {
            for (int y = 0; y < height(); y++) {
                eval(x, y);
            }
        }
    }

    @Override
    public int[][] depth() {
        int[][] depth = new int[width()][height()];
        for (int x = 0; x < width(); x++) {
            for (int y = 0; y < height(); y++) {
                depth[x][y] = computeDepth(x, y);
            }
        }
        return depth;
    }

    private int computeDepth(int x, int y) {
        // Placeholder for depth computation logic
        return 0;
    }

    @Override
    public void save(String fileName) {
        // Placeholder for save logic
    }

    @Override
    public void load(String fileName) {
        // Placeholder for load logic
    }
}

   /** public Ex2Sheet(int x, int y, int width, int height, Map<String, Cell> cells) {
        this.width = width;
        this.height = height;
        this.cells = cells;
        table = new SCell[x][y];
        for(int i=0;i<x;i=i+1) {
            for(int j=0;j<y;j=j+1) {
                table[i][j] = new SCell("");
            }
        }
        eval();
    }
    public Ex2Sheet(int width, int height, Map<String, Cell> cells) {
        this(Ex2Utils.WIDTH, Ex2Utils.HEIGHT, cells, height, width);
    }

    @Override
    public String value(int x, int y) {
        String ans = Ex2Utils.EMPTY_CELL;
        // Add your code here

        Cell c = get(x,y);
        if(c!=null) {ans = c.toString();}

        /////////////////////
        return ans;
    }

    @Override
    public Cell get(int x, int y) {
        return table[x][y];
    }

    @Override
    public Cell get(String cords) {
        Cell ans = null;
        // Add your code here

        /////////////////////
        return ans;
    }

    @Override
    public int width() {
        return table.length;
    }
    @Override
    public int height() {
        return table[0].length;
    }
    @Override
    public void set(int x, int y, String s) {
        Cell c = new SCell(s);
        table[x][y] = c;
        // Add your code here

        /////////////////////
    }
    @Override
    public void eval() {
        int[][] dd = depth();
        // Add your code here

        // ///////////////////
    }

    @Override
    public boolean isIn(int xx, int yy) {
        boolean ans = xx>=0 && yy>=0;
        // Add your code here

        /////////////////////
        return ans;
    }

    @Override
    public int[][] depth() {
        int[][] ans = new int[width()][height()];
        // Add your code here

        // ///////////////////
        return ans;
    }

    @Override
    public void load(String fileName) throws IOException {
        // Add your code here

        /////////////////////
    }

    @Override
    public void save(String fileName) throws IOException {
        // Add your code here

        /////////////////////
    }

    @Override
    public String eval(int x, int y) {
        String ans = null;
        if(get(x,y)!=null) {ans = get(x,y).toString();}
        // Add your code here

        /////////////////////
        return ans;
        }
}
