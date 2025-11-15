import java.util.*;

enum PieceType {
    KING, QUEEN, ROOK, BISHOP, KNIGHT, PAWN
}

//цвет фигур
enum Color {
    BLACK, WHITE
}

class Coordinates {
    private int x, y;
    private static final int BOARD_MAX_X = 7;
    private static final int BOARD_MAX_Y = 7;

    public Coordinates() { this(0, 0); }
    public Coordinates(int x, int y) { this.x = x; this.y = y; }

    public int getX() { return x; } //получение координаты x
    public int getY() { return y; } //получение координаты y

    //сложение координат
    public Coordinates add(Coordinates c) { return new Coordinates(x + c.x, y + c.y); }

    //вычитание координат
    public Coordinates subtract(Coordinates c) { return new Coordinates(x - c.x, y - c.y); }

    //умножение координат
    public Coordinates multiply(int n) { return new Coordinates(x * n, y * n); }

    //сравнение координат
    public boolean equals(Coordinates c) { return x == c.x && y == c.y; }

    //проверка принадлежности координат доске
    public boolean checkBound() {
        return x >= 0 && x <= BOARD_MAX_X && y >= 0 && y <= BOARD_MAX_Y;
    }

    //сравнение координат (с объектом)
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Coordinates)) return false;
        Coordinates c = (Coordinates) o;
        return x == c.x && y == c.y;
    }

    //получение хэш кода
    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }
}

public class lab4 {
    
}
