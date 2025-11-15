import java.util.*;

//типы фигур
enum PieceType {
    KING, QUEEN, ROOK, BISHOP, KNIGHT, PAWN
}

//цвет фигур
enum Color {
    BLACK, WHITE
}

//координаты клетки/фигуры
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

//Абстрактный класс фигура
abstract class Piece {
    protected boolean isAlive = true; //состояние
    protected Color color; //цвет
    protected PieceType type; //тип
    protected Coordinates pos; //координаты
    protected int moves = 0; //кол-во ходов

    public Piece(PieceType type) {
        this(new Coordinates(0, 0), Color.WHITE, type);
    }

    public Piece(Coordinates pos, Color color, PieceType type) {
        this.pos = pos;
        this.color = color;
        this.type = type;
    }

    //установить координаты фигуры
    public void setPos(Coordinates pos) { this.pos = pos; }

    //добавить ход
    public void incMoves() { moves++; }

    //получить ходы
    public int getMoves() { return moves; }

    //проверка передвижения
    public boolean isMoved() { return moves > 0; }

    //получение шаблона фигуры
    public abstract List<Coordinates> getPattern();

    //получить координаты фигуры
    public Coordinates getPos() { return isAlive ? pos : new Coordinates(-1, -1); }

    //получить цвет фигуры
    public Color getColor() { return color; }

    //получить тип фигуры
    public PieceType getType() { return type; }

    //пометка уничтожения фигуры
    public void setDead() { isAlive = false; }

    //проверка состояния
    public boolean isAlive() { return isAlive; }

    //вывод информации
    public void printInfo() {
        String typeStr = switch (type) {
            case BISHOP -> "Слон";
            case KING -> "Король";
            case KNIGHT -> "Конь";
            case PAWN -> "Пешка";
            case QUEEN -> "Ферзь";
            case ROOK -> "Ладья";
        };
        String colorStr = color == Color.WHITE ? "Белый" : "Черный";
        System.out.println("Тип фигуры: " + typeStr);
        System.out.println("Цвет фигуры: " + colorStr);
        System.out.println("Текущая позиция: " + toString(pos));
        System.out.println("Живой - " + isAlive);
    }

    //вывод координат
    protected String toString(Coordinates coor) {
        return "" + (char)(coor.getX() + 'A') + (coor.getY() + 1);
    }
}

public class lab4 {
    
}
