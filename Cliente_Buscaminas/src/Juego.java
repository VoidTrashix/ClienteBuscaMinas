
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.TextArea;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.PrintWriter;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;

public class Juego extends JFrame implements ActionListener, MouseListener{
    public static JButton tabla_botones[][];
    int n;
    int m;
    static int minas;
    PrintWriter out;
    int color_jugador;
    
    JPanel panel= new JPanel();
    JPanel jugadores= new JPanel();
    
    static TextArea txtRojo= new TextArea(2, 12);
    static TextArea txtAzul= new TextArea(2, 12);
    static TextArea txtVerde= new TextArea(2, 12);
    static TextArea txtMorado= new TextArea(2, 12);
    
    Color rojo = Color.decode("#940B12");
    Color azul = Color.decode("#5DBCD2");
    Color verde = Color.decode("#23B14D");
    Color morado = Color.decode("#A349A3");
    
    Font arial = new Font("Arial", Font.BOLD, 15);

    public Juego(int n, int m, int minas, PrintWriter printWriter, int color) {
        if(n!=m)
            return;
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        this.n = n;
        this.m = m;
        this.minas = minas;
        this.out= printWriter;
        color_jugador= color;
        tabla_botones = new JButton [n][m];
        txtRojo.setEditable(false);
        txtAzul.setEditable(false);
        txtVerde.setEditable(false);
        txtMorado.setEditable(false);
        
        panel.setLayout(new GridLayout(n,m));
        jugadores.setLayout(new GridLayout(1,4));
        
        txtRojo.setBackground(rojo);
        txtAzul.setBackground(azul);
        txtVerde.setBackground(verde);
        txtMorado.setBackground(morado);
        
        txtRojo.setFont(arial);
        txtAzul.setFont(arial);
        txtVerde.setFont(arial);
        txtMorado.setFont(arial);
        txtRojo.setForeground(Color.WHITE);
        txtAzul.setForeground(Color.BLACK);
        txtVerde.setForeground(Color.BLACK);
        txtMorado.setForeground(Color.WHITE);
        jugadores.add(txtRojo);
        jugadores.add(txtAzul);
        jugadores.add(txtVerde);
        jugadores.add(txtMorado);
        
        //setLayout(new GridLayout(n,m));
        for (int x = 0;x<m;x++){
            for (int y = 0;y<n;y++){
                tabla_botones[x][y] = new JButton("");
                tabla_botones[x][y].addActionListener(this);
                tabla_botones[x][y].addMouseListener(this);
                tabla_botones[x][y].setName(x + "," + y);
                tabla_botones[x][y].setSize(new Dimension(30,30));
                /*tabla_botones[x][y].setSize(27, 27);
                tabla_botones[x][y].setPreferredSize(new Dimension(27,27));*/
                try{
                ImageIcon usuario= new ImageIcon(getClass().getResource("/campo.png"));
                if(y==0){
                        usuario= new ImageIcon(getClass().getResource("/campo_rojo.png"));
                    }
                    if(x==0){
                        usuario= new ImageIcon(getClass().getResource("/campo_azul.png"));
                    }
                    if(y==n-1){
                        usuario= new ImageIcon(getClass().getResource("/campo_verde.png"));
                    }
                    if(x==m-1){
                        usuario= new ImageIcon(getClass().getResource("/campo_morado.png"));
                    }
                    if(x==0 && y==0){
                        usuario= new ImageIcon(getClass().getResource("/campo_rojo_azul.png"));
                    }
                    if(x==0 && y==n-1){
                        usuario= new ImageIcon(getClass().getResource("/campo_azul_verde.png"));
                    }
                    if(x==m-1 && y==n-1){
                        usuario= new ImageIcon(getClass().getResource("/campo_verde_morado.png"));
                    }
                    if(x==m-1 && y==0){
                        usuario= new ImageIcon(getClass().getResource("/campo_morado_rojo.png"));
                    }
                ImageIcon icono= new ImageIcon(usuario.getImage().getScaledInstance(Juego.tabla_botones[x][y].getWidth(), Juego.tabla_botones[x][y].getHeight(), Image.SCALE_DEFAULT));
                tabla_botones[x][y].setIcon(icono);
                tabla_botones[x][y].setMargin(new Insets(0, 0, 0, 0));
                tabla_botones[x][y].setBackground(Color.WHITE);
                tabla_botones[x][y].setContentAreaFilled(false);
                tabla_botones[x][y].setFocusPainted(false);
                tabla_botones[x][y].setBorder(new LineBorder(Color.WHITE));
                } catch (Exception ex) {
                    System.out.println(ex);
                }
                tabla_botones[x][y].setEnabled(true);
                panel.add(tabla_botones[x][y]);
            }//end inner for
        }//end for
        this.getContentPane().add(panel, BorderLayout.CENTER);
        this.getContentPane().add(jugadores, BorderLayout.SOUTH);
        this.pack();
        this.setVisible(true);
        this.setResizable(false);
        this.addWindowListener(new WindowAdapter() {
      public void windowClosing(WindowEvent we) {
        int result = JOptionPane.showConfirmDialog(null, "Está Seguro que desea salir", "Salir", JOptionPane.YES_NO_OPTION);
        if (result == JOptionPane.YES_OPTION)
          System.exit(0);
      }
    });
        out.println("/PEDIRLISTA");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
       
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        
    }

    @Override
    public void mousePressed(MouseEvent e) {
        
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        String ubicacion= e.toString().substring(e.toString().indexOf("]")+5);
        int x= Integer.parseInt(ubicacion.substring(0,ubicacion.indexOf(",")));
        int y= Integer.parseInt(ubicacion.substring(ubicacion.indexOf(",")+1));
        
        //Click IZQ
        if (e.getButton() == MouseEvent.BUTTON1) {
            out.println("/Partida=" + x + "," + y + ";" + "IZQ]");
        }
        
        //Click DER
        if (e.getButton() == MouseEvent.BUTTON3) {
            out.println("/Partida=" + x + "," + y + ";" + "DER]");
        }
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        
    }

    @Override
    public void mouseExited(MouseEvent e) {
        
    }
}
