import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.awt.event.KeyAdapter;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.awt.Graphics2D;

import java.io.File; 
import java.io.IOException;
import javax.imageio.ImageIO;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.ImageIcon;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Sokoban {
    private static JFrame window;

    private static final int maxWidth = 25;
    private static final int maxHeight = 12;
    
    private static final String path = "Sprites\\Original\\";
    private static final String extension = ".png";
    private static final String barrier = path + "black-square" + extension;
    private static final String background = path + "white-square" + extension;
    private static final String logo = path + "logo" + extension;

    private static String player = path + "flushed-face" + extension;
    private static String box = path + "box" + extension;
    private static String destination = path + "milky-way" + extension;

    private static int width = 7;
    private static int height = 5;
    private static int boxQuantity = 5;
    private static String[][] world = new String[height][width];
    private static int[] playerCoord;
    private static int[][] boxCoords;
    private static int[][] destinationCoords;
    private static boolean isKeyPressed = false;
    private static List<int[]> availableCoords = new ArrayList<int[]>();

    public static void main(String[] args)
    {
        if (width > maxWidth)
        {
            print("The max width is `" + Integer.toString(maxWidth) + "`!");
            return;
        }
        else if (height > maxHeight)
        {
            print("The max height is `" + Integer.toString(maxHeight) + "`!");
            return;
        }
        if (boxQuantity > width * height / 2)
        {
            print("The max box quantity is `" + Integer.toString(width * height / 2) + "`!");
            boxQuantity = width * height / 2;
        }
        new Sokoban();
    }

    private static void print(Object object)
    {
        System.out.println(object);
    }

    private void GenerateCombinedImages()
    {
        File file = new File("Sprites/Combined");
        if (!file.exists())
        {
            file.mkdir();
        }
        try
        {
            final String newPath = "Sprites\\Combined\\";
            final String newPlayer = newPath + "player" + extension;
            final String newBox = newPath + "box" + extension;
            final String newDestination = newPath + "destination" + extension;
            ImageIO.write(CombineImage(background, player), "PNG", new File(newPlayer));
            ImageIO.write(CombineImage(background, box), "PNG", new File(newBox));
            ImageIO.write(CombineImage(background, destination), "PNG", new File(newDestination));
            player = newPlayer;
            box = newBox;
            destination = newDestination;
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    private BufferedImage CombineImage(String... paths)
    {
        BufferedImage combinedImage = new BufferedImage(50, 50, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics2d = combinedImage.createGraphics();
        for (String path : paths)
        {
            try {
                BufferedImage image = ImageIO.read(getClass().getClassLoader().getResource(path));
                graphics2d.drawImage(image, 0, 0, null);
            }
            catch (IOException e)
            {
                e.printStackTrace();
                return null;
            }
        }
        return combinedImage;
    }

    private int[] RandomCoord()
    {
        if (availableCoords.size() == 1)
        {
            return availableCoords.get(0);
        }
        Random random = new Random();
        int randomIndex = random.nextInt(availableCoords.size());
        int[] randomCoord = availableCoords.get(randomIndex);
        availableCoords.remove(randomIndex);
        return randomCoord;
    }

    private void ProceduralGeneration()
    {
        for (int x = 0; x < width; x++)
        {
            for (int y = 0; y < height; y++)
            {
                availableCoords.add(new int[]{x, y});
            }
        }
        playerCoord = RandomCoord();
        boxCoords = new int[boxQuantity][2];
        destinationCoords = new int[boxQuantity][2];
        for (int i = 0; i < boxQuantity; i++)
        {
            boxCoords[i] = RandomCoord();
        }
        for (int i = 0; i < boxQuantity; i++)
        {
            destinationCoords[i] = RandomCoord();
        }
        availableCoords = new ArrayList<int[]>();
    }

    private void GenerateWorld()
    {
        for (int y = 0; y < height; y++)
        {
            for (int x = 0; x < width; x++)
            {
                world[y][x] = background;
            }
        }
    }

    private void RenderWindow()
    {
        window = new JFrame();
        window.setTitle("Sokoban");
        window.setIconImage(Toolkit.getDefaultToolkit().getImage(logo));
        window.setSize(((width + 2) * 50) + 16, ((height + 2) * 50) + 39);
        window.setLocationRelativeTo(null);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.getContentPane().setBackground(Color.BLACK);
        window.setLayout(null);
        window.setResizable(false);
        window.setVisible(true);
    }

    private void RenderWorld()
    {
        Container container = window.getContentPane();
        container.removeAll();

        GenerateWorld();
        world[playerCoord[1]][playerCoord[0]] = player;
        for (int[] boxCoord : boxCoords)
        {
            world[boxCoord[1]][boxCoord[0]] = box;
        }
        for (int[] destinationCoord : destinationCoords)
        {
            world[destinationCoord[1]][destinationCoord[0]] = destination;
        }
        for (int boxCoordIndex = 0; boxCoordIndex < boxCoords.length; boxCoordIndex++)
        {
            for (int desCoordIndex = 0; desCoordIndex < destinationCoords.length; desCoordIndex++)
            {
                if (Arrays.equals(boxCoords[boxCoordIndex], destinationCoords[desCoordIndex]))
                {
                    world[destinationCoords[desCoordIndex][1]][destinationCoords[desCoordIndex][0]] = barrier;
                }
            }
        }
        for (int y : new int[]{0, height + 1})
        {
            for (int x = 0; x < width + 2; x++)
            {
                JLabel label = new JLabel();
                label.setIcon(new ImageIcon(barrier));
                Dimension size = label.getPreferredSize();
                label.setBounds(x * 50, y * 50, size.width, size.height);
                container.add(label);
            }
        }

        for (int x : new int[]{0, width + 1})
        {
            for (int y = 0; y < height; y++)
            {
                JLabel label = new JLabel();
                label.setIcon(new ImageIcon(barrier));
                Dimension size = label.getPreferredSize();
                label.setBounds(x * 50, (y + 1) * 50, size.width, size.height);
                container.add(label);
            }
        }

        for (int y = 0; y < height; y++)
        {
            for (int x = 0; x < width; x++)
            {
                JLabel label = new JLabel();
                label.setIcon(new ImageIcon(world[y][x]));
                Dimension size = label.getPreferredSize();
                label.setBounds((x + 1) * 50, (y + 1) * 50, size.width, size.height);
                container.add(label);
            }
        }
        window.repaint();
    }

    private boolean Contains(int[][] coords, int[] coord)
    {
        for (int[] coordinate: coords)
        {
            if (Arrays.equals(coord, coordinate))
            {
                return true;
            }
        }
        return false;
    }

    private boolean GameOver()
    {
        for (int[] boxCoord : boxCoords)
        {
            if (!Contains(destinationCoords, boxCoord))
            {
                return false;
            }
        }
        return true;
    }

    public Sokoban()
    {
        GenerateCombinedImages();
        RenderWindow();
        ProceduralGeneration();
        RenderWorld();

        window.addKeyListener(
            new KeyAdapter() {
                public void keyPressed(KeyEvent e)
                {
                    if (isKeyPressed)
                    {
                        return;
                    }
                    else
                    {
                        isKeyPressed = true;
                    }
                    int key = e.getKeyCode();
                    if (key == KeyEvent.VK_W || key == KeyEvent.VK_UP)
                    {
                        if (playerCoord[1] - 1 >= 0 && Contains(destinationCoords, new int[]{playerCoord[0], playerCoord[1] - 1}))
                        {
                            return;
                        }
                        else if (playerCoord[1] - 1 < 0 && Contains(destinationCoords, new int[]{playerCoord[0], height - 1}))
                        {
                            return;
                        }
                        else if (playerCoord[1] - 1 >= 0 && playerCoord[1] - 2 >= 0 && Contains(boxCoords, new int[]{playerCoord[0], playerCoord[1] - 1}) && Contains(boxCoords, new int[]{playerCoord[0], playerCoord[1] - 2}))
                        {
                            return;
                        }
                        else if (playerCoord[1] - 1 >= 0 && playerCoord[1] - 2 < 0 && Contains(boxCoords, new int[]{playerCoord[0], playerCoord[1] - 1}) && Contains(boxCoords, new int[]{playerCoord[0], height - 1}))
                        {
                            return;
                        }
                        else if (playerCoord[1] - 1 < 0 && playerCoord[1] - 2 < 0 && Contains(boxCoords, new int[]{playerCoord[0], height - 1}) && Contains(boxCoords, new int[]{playerCoord[0], height - 2}))
                        {
                            return;
                        }

                        if (playerCoord[1] - 1 >= 0)
                        {
                            playerCoord[1] -= 1;
                        }
                        else
                        {
                            playerCoord[1] = height - 1;
                        }

                        for (int boxCoordIndex = 0; boxCoordIndex < boxCoords.length; boxCoordIndex++)
                        {
                            if (Arrays.equals(boxCoords[boxCoordIndex], playerCoord))
                            {
                                if (boxCoords[boxCoordIndex][1] - 1 >= 0)
                                {
                                    boxCoords[boxCoordIndex][1] -= 1;
                                }
                                else
                                {
                                    boxCoords[boxCoordIndex][1] = height - 1;
                                }
                            }
                        }
                    }
                    else if (key == KeyEvent.VK_A || key == KeyEvent.VK_LEFT)
                    {
                        if (playerCoord[0] - 1 >= 0 && Contains(destinationCoords, new int[]{playerCoord[0] - 1, playerCoord[1]}))
                        {
                            return;
                        }
                        else if (playerCoord[0] - 1 < 0 && Contains(destinationCoords, new int[]{width - 1, playerCoord[1]}))
                        {
                            return;
                        }
                        else if (playerCoord[0] - 1 >= 0 && playerCoord[0] - 2 >= 0 && Contains(boxCoords, new int[]{playerCoord[0] - 1, playerCoord[1]}) && Contains(boxCoords, new int[]{playerCoord[0] - 2, playerCoord[1]}))
                        {
                            return;
                        }
                        else if (playerCoord[0] - 1 >= 0 && playerCoord[0] - 2 < 0 && Contains(boxCoords, new int[]{playerCoord[0] - 1, playerCoord[1]}) && Contains(boxCoords, new int[]{width - 1, playerCoord[1]}))
                        {
                            return;
                        }
                        else if (playerCoord[0] - 1 < 0 && playerCoord[0] - 2 < 0 && Contains(boxCoords, new int[]{width - 1, playerCoord[1]}) && Contains(boxCoords, new int[]{width - 2, playerCoord[1]}))
                        {
                            return;
                        }

                        if (playerCoord[0] - 1 >= 0)
                        {
                            playerCoord[0] -= 1;
                        }
                        else
                        {
                            playerCoord[0] = width - 1;
                        }
                        
                        for (int boxCoordIndex = 0; boxCoordIndex < boxCoords.length; boxCoordIndex++)
                        {
                            if (Arrays.equals(boxCoords[boxCoordIndex], playerCoord))
                            {
                                if (boxCoords[boxCoordIndex][0] - 1 >= 0)
                                {
                                    boxCoords[boxCoordIndex][0] -= 1;
                                }
                                else
                                {
                                    boxCoords[boxCoordIndex][0] = width - 1;
                                }
                            }
                        }
                    }
                    else if (key == KeyEvent.VK_S || key == KeyEvent.VK_DOWN)
                    {
                        if (playerCoord[1] + 1 < height && Contains(destinationCoords, new int[]{playerCoord[0], playerCoord[1] + 1}))
                        {
                            return;
                        }
                        else if (playerCoord[1] + 1 >= height && Contains(destinationCoords, new int[]{playerCoord[0], 0}))
                        {
                            return;
                        }
                        else if (playerCoord[1] + 1 < height && playerCoord[1] + 2 < height && Contains(boxCoords, new int[]{playerCoord[0], playerCoord[1] + 1}) && Contains(boxCoords, new int[]{playerCoord[0], playerCoord[1] + 2}))
                        {
                            return;
                        }
                        else if (playerCoord[1] + 1 < height && playerCoord[1] + 2 >= height && Contains(boxCoords, new int[]{playerCoord[0], playerCoord[1] + 1}) && Contains(boxCoords, new int[]{playerCoord[0], 0}))
                        {
                            return;
                        }
                        else if (playerCoord[1] + 1 >= height && playerCoord[1] + 2 >= height && Contains(boxCoords, new int[]{playerCoord[0], 0}) && Contains(boxCoords, new int[]{playerCoord[0], 1}))
                        {
                            return;
                        }

                        if (playerCoord[1] + 1 < height)
                        {
                            playerCoord[1] += 1;
                        }
                        else
                        {
                            playerCoord[1] = 0;
                        }

                        for (int boxCoordIndex = 0; boxCoordIndex < boxCoords.length; boxCoordIndex++)
                        {
                            if (Arrays.equals(boxCoords[boxCoordIndex], playerCoord))
                            {
                                if (boxCoords[boxCoordIndex][1] + 1 < height)
                                {
                                    boxCoords[boxCoordIndex][1] += 1;
                                }
                                else
                                {
                                    boxCoords[boxCoordIndex][1] = 0;
                                }
                            }
                        }
                    }
                    else if (key == KeyEvent.VK_D || key == KeyEvent.VK_RIGHT)
                    {
                        if (playerCoord[0] + 1 < width && Contains(destinationCoords, new int[]{playerCoord[0] + 1, playerCoord[1]}))
                        {
                            return;
                        }
                        else if (playerCoord[0] + 1 >= width && Contains(destinationCoords, new int[]{0, playerCoord[1]}))
                        {
                            return;
                        }
                        else if (playerCoord[0] + 1 < width && playerCoord[0] + 2 < width && Contains(boxCoords, new int[]{playerCoord[0] + 1, playerCoord[1]}) && Contains(boxCoords, new int[]{playerCoord[0] + 2, playerCoord[1]}))
                        {
                            return;
                        }
                        else if (playerCoord[0] + 1 < width && playerCoord[0] + 2 >= width && Contains(boxCoords, new int[]{playerCoord[0] + 1, playerCoord[1]}) && Contains(boxCoords, new int[]{0, playerCoord[1]}))
                        {
                            return;
                        }
                        else if (playerCoord[0] + 1 >= width && playerCoord[0] + 2 >= width && Contains(boxCoords, new int[]{0, playerCoord[1]}) && Contains(boxCoords, new int[]{1, playerCoord[1]}))
                        {
                            return;
                        }

                        if (playerCoord[0] + 1 < width)
                        {
                            playerCoord[0] += 1;
                        }
                        else
                        {
                            playerCoord[0] = 0;
                        }

                        for (int boxCoordIndex = 0; boxCoordIndex < boxCoords.length; boxCoordIndex++)
                        {
                            if (Arrays.equals(boxCoords[boxCoordIndex], playerCoord))
                            {
                                if (boxCoords[boxCoordIndex][0] + 1 < width)
                                {
                                    boxCoords[boxCoordIndex][0] += 1;
                                }
                                else
                                {
                                    boxCoords[boxCoordIndex][0] = 0;
                                }
                            }
                        }
                    }

                    if (key == KeyEvent.VK_R || GameOver())
                    {
                        ProceduralGeneration();
                    }

                    RenderWorld();
                }

                public void keyReleased(KeyEvent event)
                {
                    isKeyPressed = false;
                }
            }
        );
    }
}
