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

//Класс Пешка
class Pawn extends Piece {
    private static final List<Coordinates> FIRST_PATTERN = Arrays.asList(
            new Coordinates(0, 1), new Coordinates(0, 2),
            new Coordinates(-1, 1), new Coordinates(1, 1)
    ); //шаблон первого хода

    private static final List<Coordinates> PATTERN = Arrays.asList(
            new Coordinates(0, 1),
            new Coordinates(-1, 1), new Coordinates(1, 1)
    ); //шаблон хода, кроме первого

    public Pawn() { super(PieceType.PAWN); }
    public Pawn(Coordinates pos, Color col) { super(pos, col, PieceType.PAWN); }

    //получение шаблона передвижения
    @Override
    public List<Coordinates> getPattern() {
        return isMoved() ? new ArrayList<>(PATTERN) : new ArrayList<>(FIRST_PATTERN);
    }
}

//Класс Слон
class Bishop extends Piece {
    private static final List<Coordinates> PATTERN = Arrays.asList(
            new Coordinates(-1,1), new Coordinates(1,1),
            new Coordinates(-1,-1), new Coordinates(1,-1)
    ); //шаблон хода

    public Bishop() { super(PieceType.BISHOP); }
    public Bishop(Coordinates pos, Color col) { super(pos, col, PieceType.BISHOP); }

    //получение шаблона хода
    @Override
    public List<Coordinates> getPattern() { return new ArrayList<>(PATTERN); }
}

//Класс Король
class King extends Piece {
    private static final List<Coordinates> PATTERN = Arrays.asList(
            new Coordinates(-1,1), new Coordinates(0,1), new Coordinates(1,1),
            new Coordinates(-1,0), new Coordinates(1,0),
            new Coordinates(-1,-1), new Coordinates(0,-1), new Coordinates(1,-1)
    ); //шаблон хода

    public King() { super(PieceType.KING); }
    public King(Coordinates pos, Color col) { super(pos, col, PieceType.KING); }

    //получение шаблона передвижения
    @Override
    public List<Coordinates> getPattern() { return new ArrayList<>(PATTERN); }
}

//Класс Конь
class Knight extends Piece {
    private static final List<Coordinates> PATTERN = Arrays.asList(
            new Coordinates(-1,2), new Coordinates(1,2),
            new Coordinates(2,1), new Coordinates(2,-1),
            new Coordinates(1,-2), new Coordinates(-1,-2),
            new Coordinates(-2,-1), new Coordinates(-2,1)
    ); //шаблон передвижения

    public Knight() { super(PieceType.KNIGHT); }
    public Knight(Coordinates pos, Color col) { super(pos, col, PieceType.KNIGHT); }

    //получение шаблона передвижения
    @Override
    public List<Coordinates> getPattern() { return new ArrayList<>(PATTERN); }
}

//Класс Ферзь
class Queen extends Piece {
    private static final List<Coordinates> PATTERN = Arrays.asList(
            new Coordinates(-1,1), new Coordinates(0,1), new Coordinates(1,1),
            new Coordinates(-1,0), new Coordinates(1,0),
            new Coordinates(-1,-1), new Coordinates(0,-1), new Coordinates(1,-1)
    ); //шаблон хода фигуры

    public Queen() { super(PieceType.QUEEN); }
    public Queen(Coordinates pos, Color col) { super(pos, col, PieceType.QUEEN); }

    //получение шаблона передвижения
    @Override
    public List<Coordinates> getPattern() { return new ArrayList<>(PATTERN); }
}

//Класс Ладья
class Rook extends Piece {
    private static final List<Coordinates> PATTERN = Arrays.asList(
            new Coordinates(0,1), new Coordinates(1,0),
            new Coordinates(-1,0), new Coordinates(0,-1)
    ); //шаблон хода фигуры

    public Rook() { super(PieceType.ROOK); }
    public Rook(Coordinates pos, Color col) { super(pos, col, PieceType.ROOK); }

    //получение шаблона передвижения
    @Override
    public List<Coordinates> getPattern() { return new ArrayList<>(PATTERN); }
}

//Класс Клетка
class Square {
    private Piece piece; //фигура на клетке
    private Coordinates pos; //координаты клетки

    public Square(Coordinates pos) {
        this.pos = pos;
        this.piece = null;
    }

    public Square(Coordinates pos, PieceType type, Color col) {
        this.pos = pos;
        setPiece(createPiece(pos, type, col));
    }

    //создание фигуры
    private Piece createPiece(Coordinates pos, PieceType type, Color col) {
        return switch (type) {
            case BISHOP -> new Bishop(pos, col);
            case KING -> new King(pos, col);
            case KNIGHT -> new Knight(pos, col);
            case PAWN -> new Pawn(pos, col);
            case QUEEN -> new Queen(pos, col);
            case ROOK -> new Rook(pos, col);
        };
    }

    //получение координат фигуры
    public Coordinates getPos() { return pos; }

    //поменять фигуру
    public void setPiece(Piece piece) {
        if (this.piece != null) this.piece.setDead();
        this.piece = piece;
        if (piece != null) piece.setPos(pos);
    }

    //получить фигуру на клетке
    public Piece getPiece() { return piece; }

    //убрать фигуру
    public void removePiece() { piece = null; }

    //проверить наличие фигуры
    public boolean hasPiece() { return piece != null; }

    //получить цвет фигуры
    public Color getPieceColor() { return piece.getColor(); }

    //получить тип фигуры
    public PieceType getPieceType() { return piece.getType(); }

    //получить шаблон хода фигуры
    public List<Coordinates> getPiecePattern() { return piece.getPattern(); }

    //получить доступные для фигуры на клетке ходы
    public List<Coordinates> getMoves(Square[][] board, Color active) {
        return switch (getPieceType()) {
            case BISHOP, ROOK, QUEEN -> getContMoves(board, active);
            case KING, KNIGHT -> getShortMoves(board, active);
            case PAWN -> getPawnMoves(board, active);
        };
    }

    //получить продолжительные ходы
    private List<Coordinates> getContMoves(Square[][] board, Color active) {
        List<Coordinates> moves = new ArrayList<>();
        List<Coordinates> pattern = getPiecePattern();
        Coordinates pos = getPos();

        for (Coordinates move : pattern) {
            int n = 1;
            boolean stop = false;
            while (!stop) {
                Coordinates tpos = pos.add(move.multiply(n));
                if (tpos.checkBound()) {
                    Square target = board[tpos.getX()][tpos.getY()];
                    if (!target.hasPiece()) {
                        moves.add(tpos);
                        n++;
                    } else if (target.getPieceColor() != active) {
                        moves.add(tpos);
                        stop = true;
                    } else {
                        stop = true;
                    }
                } else {
                    stop = true;
                }
            }
        }
        return moves;
    }

    //получить короткие ходы
    private List<Coordinates> getShortMoves(Square[][] board, Color active) {
        List<Coordinates> moves = new ArrayList<>();
        List<Coordinates> pattern = getPiecePattern();
        Coordinates pos = getPos();

        for (Coordinates move : pattern) {
            Coordinates tpos = pos.add(move);
            if (tpos.checkBound()) {
                Square target = board[tpos.getX()][tpos.getY()];
                if (!target.hasPiece() || target.getPieceColor() != active) {
                    moves.add(tpos);
                }
            }
        }
        return moves;
    }

    //получить ходы пешки
    private List<Coordinates> getPawnMoves(Square[][] board, Color active) {
        List<Coordinates> moves = new ArrayList<>();
        List<Coordinates> pattern = getPiecePattern();
        Coordinates pos = getPos();

        for (Coordinates move : pattern) {
            Coordinates tpos, forward;
            if (getPieceColor() == Color.WHITE) {
                tpos = pos.add(move);
                forward = pos.add(new Coordinates(0, 1));
            } else {
                tpos = pos.subtract(move);
                forward = pos.subtract(new Coordinates(0, 1));
            }

            if (tpos.checkBound()) {
                if (move.getX() == 0) {
                    if (!board[forward.getX()][forward.getY()].hasPiece()) {
                        moves.add(tpos);
                    }
                } else {
                    Square target = board[tpos.getX()][tpos.getY()];
                    if (target.hasPiece() && target.getPieceColor() != active) {
                        moves.add(tpos);
                    }
                }
            }
        }
        return moves;
    }

    //вывод информации о клетке и фигуре, стоящей на ней
    public void printInfo() {
        System.out.println("Клетка " + toString(pos));
        getPiece().printInfo();
    }

    //вывод координат
    private String toString(Coordinates coor) {
        return "" + (char)(coor.getX() + 'A') + (coor.getY() + 1);
    }
}

public class lab4 {
    
}
