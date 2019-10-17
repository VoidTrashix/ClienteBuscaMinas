
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;
import java.util.StringTokenizer;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import java.awt.Image;
import java.awt.TextArea;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import static javax.swing.WindowConstants.DISPOSE_ON_CLOSE;

public class Principal {
    
    String serverAdress;
    Scanner in;
    PrintWriter out;
    JFrame frame= new JFrame("Lobby Buscaminas");
    JTextField txtMensaje= new JTextField(40);
    JTextArea messageArea= new JTextArea(10,34);
    JTextArea listaUsuarios= new JTextArea(10,10);
    JButton btnJugar= new JButton("Jugar");
    static Juego juego;
    
    static String MiNombre;
    static int fila=0;
    static int columna=0;
    static int minas_totales=0;
    
    JPanel panel_arriba= new JPanel();
    JPanel panel_abajo= new JPanel();
    
    TextArea puntosrojo= new TextArea(12, 2);
    TextArea puntosazul= new TextArea(12, 2);
    TextArea puntosverde= new TextArea(12, 2);
    TextArea puntosmorado= new TextArea(12, 2);
    
    Color c_rojo = Color.decode("#940B12");
    Color c_azul = Color.decode("#5DBCD2");
    Color c_verde = Color.decode("#23B14D");
    Color c_morado = Color.decode("#A349A3");
    
    Font arial = new Font("Arial", Font.BOLD, 20);
    
    int jugadores=0;
    
    public static void main(String[] args) throws Exception {
        Principal cliente= new Principal();
        cliente.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        cliente.frame.setVisible(true);
        cliente.run();
    }
    
    public Principal(){
        txtMensaje.setEditable(false);
        messageArea.setEditable(false);
        btnJugar.setEnabled(false);
        listaUsuarios.setEditable(false);
        
        panel_arriba.setLayout(new GridBagLayout());
        panel_abajo.setLayout(new GridBagLayout());
        
        listaUsuarios.setFont(arial);
        listaUsuarios.setForeground(Color.RED);
        messageArea.setFont(arial);
        txtMensaje.setFont(arial);
        
        btnJugar.setBackground(Color.DARK_GRAY);
        btnJugar.setForeground(Color.WHITE);
        
        panel_arriba.add(new JScrollPane(messageArea));
        panel_arriba.add(new JScrollPane(listaUsuarios));
        panel_abajo.add(txtMensaje);
        panel_abajo.add(btnJugar);
        
        frame.getContentPane().add(panel_arriba, BorderLayout.NORTH);
        frame.getContentPane().add(panel_abajo, BorderLayout.SOUTH);
        
        frame.setResizable(false);
        frame.pack();
        frame.setLocationRelativeTo(null);
        
        txtMensaje.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                out.println("/MENSAJE" + txtMensaje.getText());
                txtMensaje.setText("");
            }
        });
        
        btnJugar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                out.println("/JUGAR");
            }
        });
        
        while (true) {            
        try{
            serverAdress= getIP();
            if(validarIP(serverAdress)){
            Socket socket= new Socket(serverAdress, 59001);
            break;
            }else{
                System.exit(0);
            }
        }catch(Exception e){}
        System.exit(0);
        }
    }
    
    private String getNombre(){
        return JOptionPane.showInputDialog(frame, "Nombre de pantalla: ", "Selecciona nombre de pantalla", JOptionPane.PLAIN_MESSAGE);
    }
    
    private String getIP(){
        return JOptionPane.showInputDialog(null, "Dirección IP: ", "Introdusca la IP del servidor", JOptionPane.PLAIN_MESSAGE);
    }

    private void run() throws IOException, LineUnavailableException, UnsupportedAudioFileException {
        try{
            Socket socket= new Socket(serverAdress, 59001);
            in= new Scanner(socket.getInputStream());
            out= new PrintWriter(socket.getOutputStream(), true);
            while (in.hasNextLine()) {
                String mensaje_recibido= in.nextLine();
                if(mensaje_recibido.startsWith("SUBMITNAME")){
                    MiNombre= getNombre();
                    if(MiNombre.isEmpty())
                        System.exit(0);
                    out.println(MiNombre);
                } else if(mensaje_recibido.startsWith("NAMEACCEPTED")){
                    this.frame.setTitle("Chat - " + mensaje_recibido.substring(13));
                    txtMensaje.setEditable(true);
                    messageArea.append("Te haz unido a la sala\n");
                    messageArea.setCaretPosition(messageArea.getText().length());
                } else if(mensaje_recibido.startsWith("MESSAGE")){
                    messageArea.append(mensaje_recibido.substring(8) + "\n");
                    messageArea.setCaretPosition(messageArea.getText().length());
                } else if(mensaje_recibido.equals("ADMIN")){
                        btnJugar.setEnabled(true);
                } else if(mensaje_recibido.equals("NOADMIN")){
                        btnJugar.setEnabled(false);
                } else if(mensaje_recibido.equals("LIMPIAR_LISTA")){
                        listaUsuarios.setText("-Usuarios-\nLider - ");
                        listaUsuarios.setCaretPosition(listaUsuarios.getText().length());
                } else if(mensaje_recibido.equals("LIMPIAR_TEXTOS")){
                        jugadores=0;
                        if(juego!=null){
                            Juego.txtRojo.setText("");
                            Juego.txtAzul.setText("");
                            Juego.txtVerde.setText("");
                            Juego.txtMorado.setText("");
                        }
                }else if(mensaje_recibido.startsWith("COLOR")){
                    if(juego!=null){
                        jugadores= Integer.parseInt(mensaje_recibido.substring(6));
                        switch(jugadores){
                            case 0:
                                if(Juego.txtRojo!=null){
                                    Juego.txtRojo.setText("Conectado");
                                }
                                break;
                            case 1:
                                if(Juego.txtAzul!=null){
                                    Juego.txtAzul.setText("Conectado");
                                }
                                break;
                            case 2:
                                if(Juego.txtVerde!=null){
                                    Juego.txtVerde.setText("Conectado");
                                }
                                break;
                            case 3:
                                if(Juego.txtMorado!=null){
                                    Juego.txtMorado.setText("Conectado");
                                }
                                break;
                        }
                    }
                }else if(mensaje_recibido.startsWith("USUARIOS")){
                    if(juego!=null){
                        switch(jugadores){
                            case 0:
                                if(Juego.txtRojo!=null){
                                    Juego.txtRojo.setText(mensaje_recibido.substring(9));
                                }
                                break;
                            case 1:
                                if(Juego.txtAzul!=null){
                                    Juego.txtAzul.setText(mensaje_recibido.substring(9));
                                }
                                break;
                            case 2:
                                if(Juego.txtVerde!=null){
                                    Juego.txtVerde.setText(mensaje_recibido.substring(9));
                                }
                                break;
                            case 3:
                                if(Juego.txtMorado!=null){
                                    Juego.txtMorado.setText(mensaje_recibido.substring(9));
                                }
                                break;
                        }
                    }
                }else if(mensaje_recibido.startsWith("BANDERAS")){
                    if(juego!=null){
                        switch(jugadores){
                            case 0:
                                if(Juego.txtRojo!=null){
                                    Juego.txtRojo.setText(Juego.txtRojo.getText().toString() + "\nBanderas: " + mensaje_recibido.substring(9));
                                }
                                break;
                            case 1:
                                if(Juego.txtAzul!=null){
                                    Juego.txtAzul.setText(Juego.txtAzul.getText().toString() + "\nBanderas: " + mensaje_recibido.substring(9));
                                }
                                break;
                            case 2:
                                if(Juego.txtVerde!=null){
                                    Juego.txtVerde.setText(Juego.txtVerde.getText().toString() + "\nBanderas: " + mensaje_recibido.substring(9));
                                }
                                break;
                            case 3:
                                if(Juego.txtMorado!=null){
                                    Juego.txtMorado.setText(Juego.txtMorado.getText().toString() + "\nBanderas: " + mensaje_recibido.substring(9));
                                }
                                break;
                        }
                    }
                }else if(mensaje_recibido.startsWith("ESTADO")){
                    if(juego!=null){
                        switch(jugadores){
                            case 0:
                                if(Juego.txtRojo!=null){
                                    Juego.txtRojo.setText(Juego.txtRojo.getText().toString() + "\n" + mensaje_recibido.substring(7));
                                }
                                break;
                            case 1:
                                if(Juego.txtAzul!=null){
                                    Juego.txtAzul.setText(Juego.txtAzul.getText().toString() + "\n" + mensaje_recibido.substring(7));
                                }
                                break;
                            case 2:
                                if(Juego.txtVerde!=null){
                                    Juego.txtVerde.setText(Juego.txtVerde.getText().toString() + "\n" + mensaje_recibido.substring(7));
                                }
                                break;
                            case 3:
                                if(Juego.txtMorado!=null){
                                    Juego.txtMorado.setText(Juego.txtMorado.getText().toString() + "\n" + mensaje_recibido.substring(7));
                                }
                                break;
                        }
                    }
                }else if(mensaje_recibido.startsWith("LIST")){
                    listaUsuarios.append(mensaje_recibido.substring(5) + "\n");
                } else if(mensaje_recibido.startsWith("INICIAR")){
                    crearJuego(mensaje_recibido, out);
                } else if(mensaje_recibido.startsWith("ACTUALIZAR")){
                    if(Juego.tabla_botones!=null){
                        int x= Integer.parseInt(mensaje_recibido.substring(mensaje_recibido.indexOf("=")+1, mensaje_recibido.indexOf(",")));
                        int y= Integer.parseInt(mensaje_recibido.substring(mensaje_recibido.indexOf(",")+1, mensaje_recibido.indexOf(";")));
                        String estado= mensaje_recibido.substring(mensaje_recibido.indexOf(";")+1, mensaje_recibido.indexOf(":"));
                        String resultado= "";
                        int color= -1;
                        if(mensaje_recibido.contains("/")){
                            resultado= (mensaje_recibido.substring(mensaje_recibido.indexOf(":")+1, mensaje_recibido.indexOf("/")));
                            color= Integer.parseInt(mensaje_recibido.substring(mensaje_recibido.indexOf("/")+1));
                        }else{
                            resultado= (mensaje_recibido.substring(mensaje_recibido.indexOf(":")+1));
                        }
                        if(estado.equals("Oculta")){
                                ImageIcon usuario= new ImageIcon(getClass().getResource("/campo.png"));
                                if(y==0){
                                    usuario= new ImageIcon(getClass().getResource("/campo_rojo.png"));
                                }
                                if(x==0){
                                    usuario= new ImageIcon(getClass().getResource("/campo_azul.png"));
                                }
                                if(y==fila-1){
                                    usuario= new ImageIcon(getClass().getResource("/campo_verde.png"));
                                }
                                if(x==columna-1){
                                    usuario= new ImageIcon(getClass().getResource("/campo_morado.png"));
                                }
                                if(x==0 && y==0){
                                    usuario= new ImageIcon(getClass().getResource("/campo_rojo_azul.png"));
                                }
                                if(x==0 && y==fila-1){
                                    usuario= new ImageIcon(getClass().getResource("/campo_azul_verde.png"));
                                }
                                if(x==columna-1 && y==fila-1){
                                    usuario= new ImageIcon(getClass().getResource("/campo_verde_morado.png"));
                                }
                                if(x==columna-1 && y==0){
                                    usuario= new ImageIcon(getClass().getResource("/campo_morado_rojo.png"));
                                }
                                ImageIcon icono= new ImageIcon(usuario.getImage().getScaledInstance(Juego.tabla_botones[x][y].getWidth(), Juego.tabla_botones[x][y].getHeight(), Image.SCALE_DEFAULT));
                                Juego.tabla_botones[x][y].setIcon(icono);
                                Juego.tabla_botones[x][y].setMargin(new Insets(0, 0, 0, 0));
                                Juego.tabla_botones[x][y].setBackground(Color.WHITE);
                                Juego.tabla_botones[x][y].setContentAreaFilled(false);
                                Juego.tabla_botones[x][y].setFocusPainted(false);
                                Juego.tabla_botones[x][y].setText("");
                        }else if(estado.equals("Visible")){
                            Juego.tabla_botones[x][y].setIcon(null);
                            if(validarSiEsNumero(resultado)){
                                if(resultado.equals("0")){
                                switch (color){
                                    case 0:
                                        Color rojo = Color.decode("#940B12");
                                        Juego.tabla_botones[x][y].setBackground(rojo);
                                        Juego.tabla_botones[x][y].setOpaque(true);
                                        break;
                                    case 1:
                                        Color azul = Color.decode("#5DBCD2");
                                        Juego.tabla_botones[x][y].setBackground(azul);
                                        Juego.tabla_botones[x][y].setOpaque(true);
                                        break;
                                    case 2:
                                        Color verde = Color.decode("#23B14D");
                                        Juego.tabla_botones[x][y].setBackground(verde);
                                        Juego.tabla_botones[x][y].setOpaque(true);
                                        break;
                                    case 3:
                                        Color morado = Color.decode("#A349A3");
                                        Juego.tabla_botones[x][y].setBackground(morado);
                                        Juego.tabla_botones[x][y].setOpaque(true);
                                        break;
                                }
                                Juego.tabla_botones[x][y].setText("");
                                }else{
                                switch (color){
                                    case 0:
                                        Color rojo = Color.decode("#940B12");
                                        Juego.tabla_botones[x][y].setBackground(rojo);
                                        Juego.tabla_botones[x][y].setOpaque(true);
                                        Juego.tabla_botones[x][y].setForeground(Color.WHITE);
                                        break;
                                    case 1:
                                        Color azul = Color.decode("#5DBCD2");
                                        Juego.tabla_botones[x][y].setBackground(azul);
                                        Juego.tabla_botones[x][y].setOpaque(true);
                                        break;
                                    case 2:
                                        Color verde = Color.decode("#23B14D");
                                        Juego.tabla_botones[x][y].setBackground(verde);
                                        Juego.tabla_botones[x][y].setOpaque(true);
                                        break;
                                    case 3:
                                        Color morado = Color.decode("#A349A3");
                                        Juego.tabla_botones[x][y].setBackground(morado);
                                        Juego.tabla_botones[x][y].setOpaque(true);
                                        Juego.tabla_botones[x][y].setForeground(Color.WHITE);
                                        break;
                                }
                                Juego.tabla_botones[x][y].setText(resultado);
                                }
                            }else if(resultado.equals("mina")){
                                ImageIcon usuario= new ImageIcon(getClass().getResource("/mina.png")) ;
                                ImageIcon icono= new ImageIcon(usuario.getImage().getScaledInstance(Juego.tabla_botones[x][y].getWidth(), Juego.tabla_botones[x][y].getHeight(), Image.SCALE_AREA_AVERAGING));
                                Juego.tabla_botones[x][y].setIcon(icono);
                                switch (color){
                                    case 0:
                                        Color rojo = Color.decode("#940B12");
                                        Juego.tabla_botones[x][y].setBackground(rojo);
                                        Juego.tabla_botones[x][y].setOpaque(true);
                                        break;
                                    case 1:
                                        Color azul = Color.decode("#5DBCD2");
                                        Juego.tabla_botones[x][y].setBackground(azul);
                                        Juego.tabla_botones[x][y].setOpaque(true);
                                        break;
                                    case 2:
                                        Color verde = Color.decode("#23B14D");
                                        Juego.tabla_botones[x][y].setBackground(verde);
                                        Juego.tabla_botones[x][y].setOpaque(true);
                                        break;
                                    case 3:
                                        Color morado = Color.decode("#A349A3");
                                        Juego.tabla_botones[x][y].setBackground(morado);
                                        Juego.tabla_botones[x][y].setOpaque(true);
                                        break;
                                }
                                Juego.tabla_botones[x][y].setMargin(new Insets(0, 0, 0, 0));
                                Juego.tabla_botones[x][y].setContentAreaFilled(false);
                                Juego.tabla_botones[x][y].setFocusPainted(false);
                                Juego.tabla_botones[x][y].setText("");
                            }else if(resultado.startsWith("bandera")){
                                int jugador= Integer.parseInt(resultado.substring(7, resultado.indexOf("|")));
                                switch(jugador){
                                    case 0:
                                        ImageIcon bandera1= new ImageIcon(getClass().getResource("/banderaJ1.png"));
                                ImageIcon iconobandera1= new ImageIcon(bandera1.getImage().getScaledInstance(Juego.tabla_botones[x][y].getWidth(), Juego.tabla_botones[x][y].getHeight(), Image.SCALE_DEFAULT));
                                Juego.tabla_botones[x][y].setIcon(iconobandera1);
                                Juego.tabla_botones[x][y].setMargin(new Insets(0, 0, 0, 0));
                                Juego.tabla_botones[x][y].setBackground(Color.YELLOW);
                                Juego.tabla_botones[x][y].setContentAreaFilled(false);
                                Juego.tabla_botones[x][y].setFocusPainted(false);
                                Juego.tabla_botones[x][y].setText("");
                                        break;
                                        case 1:
                                            ImageIcon bandera2= new ImageIcon(getClass().getResource("/banderaJ2.png"));
                                ImageIcon iconobandera2= new ImageIcon(bandera2.getImage().getScaledInstance(Juego.tabla_botones[x][y].getWidth(), Juego.tabla_botones[x][y].getHeight(), Image.SCALE_DEFAULT));
                                Juego.tabla_botones[x][y].setIcon(iconobandera2);
                                Juego.tabla_botones[x][y].setMargin(new Insets(0, 0, 0, 0));
                                Juego.tabla_botones[x][y].setBackground(Color.YELLOW);
                                Juego.tabla_botones[x][y].setContentAreaFilled(false);
                                Juego.tabla_botones[x][y].setFocusPainted(false);
                                Juego.tabla_botones[x][y].setText("");
                                        break;
                                        case 2:
                                            ImageIcon bandera3= new ImageIcon(getClass().getResource("/banderaJ3.png"));
                                ImageIcon iconobandera3= new ImageIcon(bandera3.getImage().getScaledInstance(Juego.tabla_botones[x][y].getWidth(), Juego.tabla_botones[x][y].getHeight(), Image.SCALE_DEFAULT));
                                Juego.tabla_botones[x][y].setIcon(iconobandera3);
                                Juego.tabla_botones[x][y].setMargin(new Insets(0, 0, 0, 0));
                                Juego.tabla_botones[x][y].setBackground(Color.YELLOW);
                                Juego.tabla_botones[x][y].setContentAreaFilled(false);
                                Juego.tabla_botones[x][y].setFocusPainted(false);
                                Juego.tabla_botones[x][y].setText("");
                                        break;
                                        case 3:
                                            ImageIcon bandera4= new ImageIcon(getClass().getResource("/banderaJ4.png"));
                                ImageIcon iconobandera4= new ImageIcon(bandera4.getImage().getScaledInstance(Juego.tabla_botones[x][y].getWidth(), Juego.tabla_botones[x][y].getHeight(), Image.SCALE_DEFAULT));
                                Juego.tabla_botones[x][y].setIcon(iconobandera4);
                                Juego.tabla_botones[x][y].setMargin(new Insets(0, 0, 0, 0));
                                Juego.tabla_botones[x][y].setBackground(Color.YELLOW);
                                Juego.tabla_botones[x][y].setContentAreaFilled(false);
                                Juego.tabla_botones[x][y].setFocusPainted(false);
                                Juego.tabla_botones[x][y].setText("");
                                        break;
                                }   
                            }
                        }
            }
                }else if(mensaje_recibido.startsWith("MUERTO")){
                    //JOptionPane.showMessageDialog(null, "Haz muerto");
            } else if(mensaje_recibido.equals("LIMPIAR_PUNTOS")){
                        puntosrojo= null;
                        puntosazul= null;
                        puntosverde= null;
                        puntosmorado= null;
                } else if(mensaje_recibido.startsWith("PUNTOS")){
                    int color= Integer.parseInt(mensaje_recibido.substring(7,8));
                    String Nombre= mensaje_recibido.substring(mensaje_recibido.indexOf("=") + 1, mensaje_recibido.indexOf("&"));
                    String Punts= mensaje_recibido.substring(mensaje_recibido.indexOf("&")+1);
                    switch (color){
                        case 0:
                            puntosrojo= new TextArea(2, 25);
                            puntosrojo.setText(Nombre + "\n" + Punts);
                            puntosrojo.setBackground(c_rojo);
                            puntosrojo.setFont(arial);
                            puntosrojo.setForeground(Color.WHITE);
                            puntosrojo.setEditable(false);
                            break;
                        case 1:
                            puntosazul= new TextArea(2, 25);
                            puntosazul.setText(Nombre + "\n" + Punts);
                            puntosazul.setBackground(c_azul);
                            puntosazul.setFont(arial);
                            puntosrojo.setEditable(false);
                            break;
                        case 2:
                            puntosverde= new TextArea(2, 25);
                            puntosverde.setText(Nombre + "\n" + Punts);
                            puntosverde.setBackground(c_verde);
                            puntosverde.setFont(arial);
                            puntosrojo.setEditable(false);
                            break;
                        case 3:
                            puntosmorado= new TextArea(2, 25);
                            puntosmorado.setText(Nombre + "\n" + Punts);
                            puntosmorado.setBackground(c_morado);
                            puntosmorado.setFont(arial);
                            puntosmorado.setForeground(Color.WHITE);
                            puntosrojo.setEditable(false);
                            break;
                    }
                } else if(mensaje_recibido.equals("FIN")){
                    if(juego!=null){
                        int s=0;
                    JFrame puntos= new JFrame();
                    JPanel panel = new JPanel();
                    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
                    puntos.setLayout(new FlowLayout());
                    if(puntosrojo!=null){
                        panel.add(puntosrojo);
                        s++;
                    }
                    if(puntosazul!=null){
                        panel.add(puntosazul);
                        s++;
                    }
                    if(puntosverde!=null){
                        panel.add(puntosverde);
                        s++;
                    }
                    if(puntosmorado!=null){
                        panel.add(puntosmorado);
                        s++;
                    }
                    if(s>0){
                        puntos.add(panel);
                    puntos.pack();
                    puntos.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
                    puntos.setTitle("Puntos");
                    puntos.setLocationRelativeTo(null);
                    puntos.setVisible(true);
                    puntos.setResizable(false);
                    puntos.addWindowListener(new WindowAdapter() {
                        public void windowClosing(WindowEvent we) {
                            puntos.dispose();
                            puntosrojo= null;
                            puntosazul= null;
                            puntosverde= null;
                            puntosmorado= null;
                            juego.dispose();
                            juego= null;
                        }
                    });
                    }
                    }
            }
                
            }
        } finally { 
            frame.setVisible(false);
            frame.dispose();
            if(juego!=null){
                juego.setVisible(false);
                juego.dispose();
            }
        }
    }
    
    public static void crearJuego(String mensaje, PrintWriter out){
        int x= Integer.parseInt(mensaje.substring(mensaje.indexOf("=")+1, mensaje.indexOf(",")));
        int y= Integer.parseInt(mensaje.substring(mensaje.indexOf(",")+1, mensaje.indexOf(";")));
        int minas= Integer.parseInt(mensaje.substring(mensaje.indexOf(";")+1, mensaje.indexOf("]")));
        int color= Integer.parseInt(mensaje.substring(mensaje.indexOf("]")+1));
        fila= x;
        columna= y;
        minas_totales= minas;
        
        if(juego==null){
        juego= new Juego(x, y, minas, out, color);
        juego.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        juego.setTitle("Tablero de " + MiNombre);
        juego.setLocationRelativeTo(null);
        }else{
            JOptionPane.showMessageDialog(null, "Se ha creado un nuevo tablero");
            juego= new Juego(x, y, minas, out, color);
            juego.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
            juego.setVisible(true);
            juego.setTitle("Tablero de " + MiNombre);
            juego.setLocationRelativeTo(null);
        }
    }
    
    public static void tablerodeString(String texto, int x, int y){
        String linea= texto.substring(texto.indexOf("]")+1);
        int[][] cuadricula= new int[x][y];
        StringTokenizer tokens=new StringTokenizer(linea, " ");
        int fila=0;
        int columna=0;
        while(tokens.hasMoreTokens()){
            String str=tokens.nextToken();
            cuadricula[fila][columna]= Integer.parseInt(str);
            columna++;
            if(columna==x){
                fila++;
                columna=0;
            }
        }
    }
    
    public static boolean validarSiEsNumero(String mensaje){
        boolean numerico= false;
        try {
            int num= Integer.parseInt(mensaje);
            numerico= true;
        } catch (Exception e) {
        }
        return numerico;
    }
    
    public static boolean validarIP(final String ip) {
        String patron = "^((0|1\\d?\\d?|2[0-4]?\\d?|25[0-5]?|[3-9]\\d?)\\.){3}(0|1\\d?\\d?|2[0-4]?\\d?|25[0-5]?|[3-9]\\d?)$";
        return ip.matches(patron);
    }
}
