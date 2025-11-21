import java.util.*;
import java.time.*;

//исключение неправильного хода
class InvalidMoveException extends Exception {
    public InvalidMoveException(String message) {
        super(message);
    }
}

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

    //преобразование строки в координаты
    public static Coordinates convert(String str) throws InvalidMoveException {
        if (str == null || str.length() != 2) {
            throw new InvalidMoveException("Некорректный формат: '" + str + "'. Используйте, например, 'e2'.");
        }
        char file = Character.toUpperCase(str.charAt(0));
        char rank = str.charAt(1);
        if (file < 'A' || file > 'H' || rank < '1' || rank > '8') {
            throw new InvalidMoveException("Координаты '" + str + "' вне доски.");
        }
        return new Coordinates(file - 'A', rank - '1');
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

//Класс Доска
class Board {
    private Color activeColor = Color.WHITE; //текущий цвет
    private static final PieceType[][] SCHEME = {
            {PieceType.ROOK, PieceType.KNIGHT, PieceType.BISHOP, PieceType.QUEEN, PieceType.KING, PieceType.BISHOP, PieceType.KNIGHT, PieceType.ROOK},
            {PieceType.PAWN, PieceType.PAWN, PieceType.PAWN, PieceType.PAWN, PieceType.PAWN, PieceType.PAWN, PieceType.PAWN, PieceType.PAWN},
            {PieceType.PAWN, PieceType.PAWN, PieceType.PAWN, PieceType.PAWN, PieceType.PAWN, PieceType.PAWN, PieceType.PAWN, PieceType.PAWN},
            {PieceType.ROOK, PieceType.KNIGHT, PieceType.BISHOP, PieceType.KING, PieceType.QUEEN, PieceType.BISHOP, PieceType.KNIGHT, PieceType.ROOK}
    }; //шаблон заполнения доски

    private Square[][] board = new Square[8][8]; //доска

    public Board() {
        initBoard();
    }

    //инициализация доски
    private void initBoard() {
        for (int y = 0; y < 8; y++) {
            for (int x = 0; x < 8; x++) {
                if (y <= 1) {
                    board[x][y] = new Square(new Coordinates(x, y), SCHEME[y][x], Color.WHITE);
                } else if (y >= 6) {
                    board[x][y] = new Square(new Coordinates(x, y), SCHEME[y - 4][x], Color.BLACK);
                } else {
                    board[x][y] = new Square(new Coordinates(x, y));
                }
            }
        }
    }

    //вывод доски
    public void drawBoard() {
        for (int y = 7; y >= 0; y--) {
            System.out.print((y + 1));
            for (int x = 0; x < 8; x++) {
                Square sq = board[x][y];
                if (sq.hasPiece()) {
                    Piece p = sq.getPiece();
                    String symbol = switch (p.getType()) {
                        case PAWN -> p.getColor() == Color.WHITE ? "\u2659" : "\u265F";
                        case ROOK -> p.getColor() == Color.WHITE ? "\u2656" : "\u265C";
                        case KNIGHT -> p.getColor() == Color.WHITE ? "\u2658" : "\u265E";
                        case BISHOP -> p.getColor() == Color.WHITE ? "\u2657" : "\u265D";
                        case KING -> p.getColor() == Color.WHITE ? "\u2654" : "\u265A";
                        case QUEEN -> p.getColor() == Color.WHITE ? "\u2655" : "\u265B";
                    };
                    System.out.print("|" + symbol + " ");
                } else {
                    System.out.print("|  ");
                }
            }
            System.out.println("|");
        }
        System.out.println("  A  B  C  D  E  F  G  H");
    }

    //отображение ходов фигуры
    public boolean drawMoves(Coordinates pos) {
        List<Coordinates> moves = getMoves(pos);
        if (moves.isEmpty()) return false;

        for (int y = 7; y >= 0; y--) {
            System.out.print((y + 1));
            for (int x = 0; x < 8; x++) {
                boolean found = false;
                for (Coordinates m : moves) {
                    if (m.equals(new Coordinates(x, y))) {
                        System.out.print("| *");
                        found = true;
                        break;
                    }
                }
                if (!found) System.out.print("|  ");
            }
            System.out.println("|");
        }
        System.out.println("  A  B  C  D  E  F  G  H");
        getSquare(pos).printInfo();
        return true;
    }

    //получение ходов фигуры по координатам
    public List<Coordinates> getMoves(Coordinates pos) {
        return getSquare(pos).getMoves(board, activeColor);
    }

    //получение клетки по координатам
    public Square getSquare(Coordinates pos) {
        return board[pos.getX()][pos.getY()];
    }

    //проверка наличия фигуры на клетке
    public boolean sqHasPiece(Coordinates pos) {
        return getSquare(pos).hasPiece();
    }

    //получение фигуры на клетке
    public Piece getSqPiece(Coordinates pos) {
        return getSquare(pos).getPiece();
    }

    //смена текущего цвета
    public void changeActive() {
        activeColor = (activeColor == Color.WHITE) ? Color.BLACK : Color.WHITE;
    }

    //получение текущего цвета
    public Color getActive() {
        return activeColor;
    }

    //проверить возможность сдвинуть выбранную фигуру
    public boolean checkOwner(Coordinates pos) {
        return getSqPiece(pos).getColor() == getActive();
    }

    //передвижение фигуры
    public int movePiece(Coordinates c1, Coordinates c2) {
        int state = 0;
        if (!sqHasPiece(c1)) {
            System.out.println("Поле пустое");
            return state;
        }

        List<Coordinates> moves = getMoves(c1);
        boolean found = moves.stream().anyMatch(m -> m.equals(c2));

        if (found) {
            if (sqHasPiece(c2) && getSqPiece(c2).getType() == PieceType.KING) {
                state = (getActive() == Color.WHITE) ? 1 : 2;
            }
            getSqPiece(c1).incMoves();
            getSquare(c2).setPiece(getSqPiece(c1));
            getSquare(c1).removePiece();
            changeActive();
        } else {
            System.out.println("Фигура не может сюда пойти");
        }
        return state;
    }
}

//Класс Таймер
class Timer {
    private Instant startTime; //время начала игры
    private Duration duration; //длительность игры

    public Timer() {
        startTimer();
    }

    //запуск таймера
    public void startTimer() {
        startTime = Instant.now();
    }

    //остановка таймера
    public void stopTimer() {
        duration = Duration.between(startTime, Instant.now());
    }

    //вывод продолжительности игры
    public void printTime() {
        long hours = duration.toHours();
        long minutes = duration.toMinutesPart();
        long seconds = duration.toSecondsPart();
        System.out.printf("Время игры: %d:%02d:%02d%n", hours, minutes, seconds);
    }
}

//Класс Игрок
class Player {
    private String name; //имя
    private int wonGames = 0; //кол-во побед
    private int lostGames = 0; //кол-во поражений
    private int playedGames = 0; //кол-во игр

    public Player(String name) {
        this.name = name;
    }

    //очистка статистики игрока
    public void clearStats() {
        wonGames = lostGames = playedGames = 0;
    }

    //установка имени игрока
    public void setName(String name) { this.name = name; }

    //получение имени игрока
    public String getName() { return name; }

    //добавление выигранной игры
    public void incWonGames() { wonGames++; playedGames++; }

    //добавление проигранной игры
    public void incLostGames() { lostGames++; playedGames++; }

    //получение количества выигранных игр
    public int getWonGames() { return wonGames; }

    //получение количества проигранных игр
    public int getLostGames() { return lostGames; }

    //получение количества игр
    public int getPlayedGames() { return playedGames; }

    //вывод статистики игрока
    public void printStats() {
        System.out.println("Имя: " + name);
        System.out.println("Кол-во побед: " + wonGames);
        System.out.println("Кол-во поражений: " + lostGames);
        System.out.println("Всего игр: " + playedGames);
    }
}

//Класс Игра
class Game {
    private Player whitePlayer; //игрок 1
    private Player blackPlayer; //игрок 2
    private Board board; //доска
    private Timer timer; //таймер
    private static int gamesPlayed = 0; //счетчик игр

    public Game(Scanner scanner) {
        gamesPlayed++;
        System.out.print("Введите имя 1 игрока: ");
        whitePlayer = new Player(scanner.nextLine().trim());
        System.out.print("Введите имя 2 игрока: ");
        blackPlayer = new Player(scanner.nextLine().trim());
        board = new Board();
    }

    public static int getPlayedGames(){
        return gamesPlayed;
    }

    //вывод результатов игры
    public void printResults(int state) {
        if (state == -1) {
            System.out.println("Ничья!");
        } else if (state == 1) {
            System.out.println("Белые победили!");
            whitePlayer.incWonGames();
            blackPlayer.incLostGames();
        } else if (state == 2) {
            System.out.println("Черные победили!");
            blackPlayer.incWonGames();
            whitePlayer.incLostGames();
        }
        System.out.println();
        whitePlayer.printStats();
        System.out.println();
        blackPlayer.printStats();
        System.out.println();
        timer.printTime();
    }

    //вывод текущего цвета
    public void printActive() {
        Color active = board.getActive();
        if (active == Color.WHITE) {
            System.out.println("white: " + whitePlayer.getName());
        } else {
            System.out.println("black: " + blackPlayer.getName());
        }
    }

    //запуск игры
    public void play(Scanner scanner) {
        timer = new Timer();
        int state = 0;
        boolean prevWrong = false;

        while (state == 0) {
            System.out.print("\033[H\033[2J");
            System.out.flush();

            printActive();
            board.drawBoard();
            if (prevWrong) {
                System.out.println("Ошибка");
                prevWrong = false;
            }

            try{
                String s1 = scanner.nextLine().trim();
                if (s1.equals("exit")) {
                    state = -1;
                    continue;
                }

                Coordinates c1 = Coordinates.convert(s1);
                if (!c1.checkBound()) {
                    prevWrong = true;
                    continue;
                }

                if (!board.sqHasPiece(c1) || !board.checkOwner(c1)) {
                    prevWrong = true;
                    continue;
                }

                boolean hasMoves = board.drawMoves(c1);
                if (!hasMoves) {
                    prevWrong = true;
                    continue;
                }

                String s2 = scanner.nextLine().trim();
                Coordinates c2 = Coordinates.convert(s2);
                if (!c2.checkBound()) {
                    prevWrong = true;
                    continue;
                }

                state = board.movePiece(c1, c2);
            } catch (InvalidMoveException e) {
                System.out.println("Ошибка: " + e.getMessage());
                System.out.println("Попробуйте снова.");
            }
        }

        System.out.print("\033[H\033[2J");
        System.out.flush();
        timer.stopTimer();
        printResults(state);
    }
}

public class lab4 {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        boolean stop = false;
        do{
            Game game = new Game(scanner);
            game.play(scanner);
            System.out.println("Сыграть ещё раз? [y,n]: ");
            String s = scanner.nextLine().trim();
            if (s.charAt(0) == 'n') stop = true;
        }while (!stop);
        System.out.println("Сыграно игр: " + Game.getPlayedGames());
        scanner.close();
    }
}
