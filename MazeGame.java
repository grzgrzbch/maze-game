/**
 * MazeGame - Klasa główna gry labiryntowej.
 *
 * Gra skierowana jest dla osób z problemami neurologicznymi,
 * wspomagając ich koordynację i umiejętności obsługi myszki.
 * Użytkownik steruje kursorem przez labirynt, unikając ścian
 * i próbując dotrzeć do czerwonego punktu końcowego.
 *
 * Autor: Grzegorz Bach 188712 E2A
 */

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.Objects;
import javax.sound.sampled.*;

public class MazeGame extends JFrame {

    /**
     * Tablica dwuwymiarowa reprezentująca labirynt.
     * 1 - ściana, 0 - przejście.
     */

    private int[][] maze;

    /**
     * Rozmiar labiryntu w zależności od wybranego poziomu trudności.
     */
    private int mazeSize;
    /**
     * Rozmiar jednej komórki labiryntu (w pikselach).
     */
    private final int cellSize = 28;
    /**
     * Punkt końcowy labiryntu.
     */
    private Point finishPoint;
    /**
     * Flaga określająca, czy gra została przegrana.
     */
    private boolean gameOver = false;
    /**
     * Flaga określająca, czy gra została wygrana.
     */
    private boolean gameWon = false;
    /**
     * Layout zarządzający przełączaniem między ekranami (menu, gra).
     */
    private CardLayout cardLayout;
    /**
     * Główny panel aplikacji.
     */
    private JPanel mainPanel;
    /**
     * Obrazy tła dla różnych poziomów trudności.
     */
    private Image easyBackground;
    private Image mediumBackground;
    private Image hardBackground;
    private Image currentBackground;
    /**
     * Klip audio odtwarzany w tle.
     */
    private Clip backgroundMusic;
    /**
     * Konstruktor klasy MazeGame.
     * Inicjalizuje komponenty, ładuje obrazy i uruchamia menu główne.
     */
    public MazeGame() {
        loadBackgroundImages();
        setupMainMenu();
        playBackgroundMusic("Star Wars - Cantina Song.wav");
    }
    /**
     * Ładuje obrazy tła dla różnych poziomów trudności.
     * Wyświetla komunikat o błędzie w przypadku problemów z ładowaniem.
     */
    private void loadBackgroundImages() {
        try {
            easyBackground = new ImageIcon(Objects.requireNonNull(getClass().getResource("star_wars.png"))).getImage();
            mediumBackground = new ImageIcon(Objects.requireNonNull(getClass().getResource("endor.png"))).getImage();
            hardBackground = new ImageIcon(Objects.requireNonNull(getClass().getResource("death_star.png"))).getImage();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Błąd ładowania obrazów tła!", "Błąd", JOptionPane.ERROR_MESSAGE); // wyjatek okreslajacy wynik ladowania obrazow
        }
    }
    /**
     * Konfiguruje menu główne gry, w tym logo, przyciski i ich akcje.
     */
    private void setupMainMenu() {

        setTitle("Labirynt - Gra");
        setSize(1280, 1024);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        // Panel Menu Glownego
        JPanel menuPanel = new JPanel();
        menuPanel.setLayout(new GridBagLayout());
        menuPanel.setBackground(new Color(163, 60, 99));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10); // Odstepy miedzy elementami
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;

        // Logo
        ImageIcon logoIcon = new ImageIcon(Objects.requireNonNull(getClass().getResource("logo.png")));
        JLabel logoLabel = new JLabel(logoIcon);
        logoLabel.setHorizontalAlignment(JLabel.CENTER);

        Image image = logoIcon.getImage();
        Image scaledImage = image.getScaledInstance(200, 200, Image.SCALE_SMOOTH); // Wyskalowane logo w menu
        logoLabel.setIcon(new ImageIcon(scaledImage));

        menuPanel.add(logoLabel, gbc); // Dodanie logo do panelu

        // Przesuniecie do kolejnego wiersza
        gbc.gridy++;

        // Przyciski
        JButton startButton = new JButton("Rozpocznij grę");
        JButton infoButton = new JButton("Informacje o grze");
        JButton exitButton = new JButton("Wyjście z gry");

        // Ustawienie rozmiarów przycisków
        Dimension buttonSize = new Dimension(300, 60);
        startButton.setPreferredSize(buttonSize);
        infoButton.setPreferredSize(buttonSize);
        exitButton.setPreferredSize(buttonSize);

        // Dodanie przycisków do panelu
        menuPanel.add(startButton, gbc);
        gbc.gridy++;
        menuPanel.add(infoButton, gbc);
        gbc.gridy++;
        menuPanel.add(exitButton, gbc);

        mainPanel.add(menuPanel, "Menu");

        startButton.addActionListener(e -> showDifficultySelection());
        infoButton.addActionListener(e -> showGameInfo());
        exitButton.addActionListener(e -> System.exit(0));

        add(mainPanel);
        setVisible(true);
    }
    /**
     * Wyświetla okno wyboru poziomu trudności.
     *
     * Poziomy trudności:
     * - Łatwy: 15x15
     * - Średni: 25x25
     * - Trudny: 35x35
     */
    private void showDifficultySelection() {
        String[] options = {"Łatwy", "Średni", "Trudny"};
        int difficulty = JOptionPane.showOptionDialog(this, "Wybierz poziom trudności", "Poziom trudności",
                JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);
        if (difficulty == JOptionPane.CLOSED_OPTION) {
            cardLayout.show(mainPanel, "Menu");
        } else {
            startGame(difficulty + 1);
        }
    }
    /**
     * Wyświetla okno z informacjami o grze.
     */
    private void showGameInfo() {
        JOptionPane.showMessageDialog(this, """
                Gra skierowana dla osób z problemami neurologicznymi np. z dziecięcym
                porażeniem mózgowym, po przebytym udarze, z uszkodzeniem
                nerwów kończyny górnej oraz dla osób starszych. Posługiwanie się
                myszką i jednoczesne reagowanie na efekt na ekranie powoduje silną
                stymulację umysłu u wyżej wymienionych osób, co poprawia ich
                koordynację i zapoznanie z myszką i komputerem.
                
                Autor: Grzegorz Bach 188712 E2A""", "Informacje o grze", JOptionPane.INFORMATION_MESSAGE);
    }
    /**
     * Rozpoczyna grę, generując labirynt na podstawie wybranego poziomu trudności.
     *
     * @param difficulty Poziom trudności (1 - łatwy, 2 - średni, 3 - trudny).
     */
    private void startGame(int difficulty) {
        // Resetowanie stanu gry
        gameOver = false;
        gameWon = false;
        generateMaze(difficulty);
        // Wybór tla w zależności od trudnosci
        switch (difficulty) {
            case 1 -> currentBackground = easyBackground;
            case 2 -> currentBackground = mediumBackground;
            case 3 -> currentBackground = hardBackground;
            default -> currentBackground = easyBackground;
        }

        // Panel gry z labiryntem
        JPanel gamePanel = getJPanel();

        // Panel z tlem i gra
        JPanel backgroundPanel = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(Color.BLACK);
                g.fillRect(0, 0, getWidth(), getHeight());

                if (currentBackground != null) {
                    int x = (getWidth() - currentBackground.getWidth(null)) / 2;
                    int y = (getHeight() - currentBackground.getHeight(null)) / 2;
                    g.drawImage(currentBackground, x, y, this);
                }
            }
        };
        backgroundPanel.setLayout(new GridBagLayout());
        backgroundPanel.add(gamePanel); // Dodanie panelu gry na tlo

        // Dodanie do glownego panelu
        mainPanel.add(backgroundPanel, "Game");
        cardLayout.show(mainPanel, "Game");

        // Przenies kursor na początek labiryntu
        resetCursorToStart(gamePanel);
    }
    /**
     * Tworzy panel graficzny przedstawiający labirynt.
     * Obsługuje zdarzenia myszy podczas gry.
     *
     * @return JPanel reprezentujący labirynt.
     */
    private JPanel getJPanel() {
        JPanel gamePanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawMaze(g);
            }
        };
        gamePanel.setPreferredSize(new Dimension(mazeSize * cellSize, mazeSize * cellSize));
        gamePanel.setOpaque(false); // Ustawienie przezroczystosci

        gamePanel.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                handleMouseMovement(e);
            }
        });
        // Wykrywanie ruchow myszki podczas przechodzenia gry
        gamePanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseExited(MouseEvent e) {
                if (!gameOver && !gameWon) {
                    gameOver = true;
                    JOptionPane.showMessageDialog(MazeGame.this, "Wyszedłeś poza planszę! Gra przegrana!", "Koniec gry", JOptionPane.INFORMATION_MESSAGE);
                    cardLayout.show(mainPanel, "Menu");
                }
            }
        });
        return gamePanel;
    }
    /**
     * Odtwarza muzykę w tle.
     *
     * @param filePath Ścieżka do pliku audio.
     */
    private void playBackgroundMusic(String filePath) {
        try {
            if (backgroundMusic != null && backgroundMusic.isRunning()) {
                backgroundMusic.stop();
                backgroundMusic.close();
            }

            File musicFile = new File(Objects.requireNonNull(getClass().getResource(filePath)).toURI());
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(musicFile);
            backgroundMusic = AudioSystem.getClip();
            backgroundMusic.open(audioStream);
            backgroundMusic.loop(Clip.LOOP_CONTINUOUSLY);
            backgroundMusic.start();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Nie można odtworzyć muzyki!", "Błąd", JOptionPane.ERROR_MESSAGE);
        }
    }
    // funkcja zatrzymywania muzyki w przypadku, gdy chce zmienic zeby muzyka wlaczala sie po wlaczeniu poziomu zamiast programu
    //      private void stopBackgroundMusic() {
    //      if (backgroundMusic != null && backgroundMusic.isRunning()) {
    //      backgroundMusic.stop();
    //      backgroundMusic.close();
    //      }
    //}
    /**
     * Generuje labirynt o określonych wymiarach wypełniony ścianami.
     * Tworzy ścieżki za pomocą algorytmu Depth-First Search (DFS).
     *
     * @param difficulty Poziom trudności (określający rozmiar labiryntu).
     */
    private void generateMaze(int difficulty) {
        mazeSize = switch (difficulty) {
            case 1 -> 15;
            case 2 -> 25;
            case 3 -> 35;
            default -> 10;
        };

        maze = new int[mazeSize][mazeSize];
        for (int i = 0; i < mazeSize; i++) {
            for (int j = 0; j < mazeSize; j++) {
                maze[i][j] = 1;
            }
        }
        createPathDFS(0, 0);
        finishPoint = new Point(mazeSize - 1, mazeSize - 1);
        maze[mazeSize - 1][mazeSize - 1] = 0;
    }
    /**
     * Tworzy ścieżki w labiryncie za pomocą algorytmu DFS.
     *
     * @param x Współrzędna x obecnej pozycji.
     * @param y Współrzędna y obecnej pozycji.
     */
    private void createPathDFS(int x, int y) {
        maze[y][x] = 0;
        int[][] directions = {{0, 1}, {1, 0}, {0, -1}, {-1, 0}};
        Collections.shuffle(Arrays.asList(directions));

        for (int[] dir : directions) {
            int nx = x + dir[0];
            int ny = y + dir[1];
            if (nx >= 0 && nx < mazeSize && ny >= 0 && ny < mazeSize && maze[ny][nx] == 1) {
                int nnx = nx + dir[0];
                int nny = ny + dir[1];
                if (nnx >= 0 && nnx < mazeSize && nny >= 0 && nny < mazeSize && maze[nny][nnx] == 1) {
                    maze[ny][nx] = 0;
                    createPathDFS(nnx, nny);
                }
            }
        }
    }
    /**
     * Rysuje labirynt na podstawie tablicy `maze`.
     *
     * @param g Obiekt Graphics używany do rysowania.
     */
    private void drawMaze(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;

        // Rysowanie labiryntu z przezroczystością
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f)); // 50% przezroczystości

        for (int i = 0; i < mazeSize; i++) {
            for (int j = 0; j < mazeSize; j++) {
                g2d.setColor(maze[i][j] == 1 ? Color.BLACK : Color.WHITE);
                g2d.fillRect(j * cellSize, i * cellSize, cellSize, cellSize);
                g2d.setColor(Color.GRAY);
                g2d.drawRect(j * cellSize, i * cellSize, cellSize, cellSize);
            }
        }

        // Punkt koncowy
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
        g2d.setColor(Color.RED);
        g2d.fillRect(finishPoint.x * cellSize, finishPoint.y * cellSize, cellSize, cellSize);
    }
    /**
     * Obsługuje ruch kursora myszy w labiryncie.
     * Sprawdza, czy gracz dotarł do końca lub trafił na ścianę.
     *
     * @param e Obiekt MouseEvent zawierający szczegóły ruchu myszy.
     */
    private void handleMouseMovement(MouseEvent e) {
        if (gameOver || gameWon) return;
        int x = e.getX() / cellSize;
        int y = e.getY() / cellSize;
        if (x >= 0 && x < mazeSize && y >= 0 && y < mazeSize) {
            if (maze[y][x] == 1) {
                gameOver = true;
                JOptionPane.showMessageDialog(this, "Gra Przegrana!", "Koniec gry", JOptionPane.INFORMATION_MESSAGE);
                cardLayout.show(mainPanel, "Menu");
            } else if (x == finishPoint.x && y == finishPoint.y) {
                gameWon = true;
                JOptionPane.showMessageDialog(this, "Gratulacje, wygrałeś!", "Wygrana", JOptionPane.INFORMATION_MESSAGE);
                cardLayout.show(mainPanel, "Menu");
            }
        }
    }
    /**
     * Ustawia kursor myszy na początku planszy.
     *
     * @param gamePanel Panel gry, na którym znajduje się labirynt.
     */
    private void resetCursorToStart(JPanel gamePanel) {
        try {
            Point location = gamePanel.getLocationOnScreen();
            Robot robot = new Robot();
            robot.mouseMove(location.x + cellSize / 2, location.y + cellSize / 2);
        } catch (AWTException e) {
            e.printStackTrace();
        }
    }
    /**
     * Metoda główna programu. Uruchamia grę.
     *
     * @param args Argumenty wiersza poleceń (niewykorzystywane).
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(MazeGame::new);
    }
}