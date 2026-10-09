
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class YgoDuelGui implements BattleListener
{
    private JPanel panel1;

    private JLabel jugadorcarta1;
    private JLabel jugadorcarta2;
    private JLabel jugadorcarta3;

    private JLabel jnom1;
    private JLabel jnom2;
    private JLabel jnom3;

    private JLabel jatk1;
    private JLabel jatk2;
    private JLabel jatk3;

    private JLabel jdef1;
    private JLabel jdef2;
    private JLabel jdef3;

    private JButton jbutton1;
    private JButton jbutton2;
    private JButton jbutton3;

    private JLabel maquinacarta1;
    private JLabel maquinacarta2;
    private JLabel maquinacarta3;

    private JLabel mnom1;
    private JLabel mnom2;
    private JLabel mnom3;

    private JLabel matk1;
    private JLabel matk2;
    private JLabel matk3;

    private JLabel mdef1;
    private JLabel mdef2;
    private JLabel mdef3;

    private JButton iniciarduelo;
    private JButton reiniciarduelo;

    private JTextArea areabatalla;

    private final YgoApiClient apiClient;

    private List<Card> cartasJugador;
    private List<Card> cartasMaquina;

    private Duel duel;

    private boolean cartasCargadas;

    public YgoDuelGui()
    {
        apiClient = new YgoApiClient();

        cartasJugador = new ArrayList<>();
        cartasMaquina = new ArrayList<>();

        cartasCargadas = false;

        configurarInterfaz();

        iniciarduelo.addActionListener(
                e -> cargarCartas()
        );

        jbutton1.addActionListener(
                e -> elegirCarta(0)
        );

        jbutton2.addActionListener(
                e -> elegirCarta(1)
        );

        jbutton3.addActionListener(
                e -> elegirCarta(2)
        );

        reiniciarduelo.addActionListener(
                e -> reiniciarDuelo()
        );
    }

    private void configurarInterfaz()
    {
        areabatalla.setEditable(false);

        jbutton1.setEnabled(false);
        jbutton2.setEnabled(false);
        jbutton3.setEnabled(false);

        reiniciarduelo.setEnabled(false);

        agregarLog("Bienvenido a Yu-Gi-Oh! Duel Lite");
        agregarLog("Presiona INICIAR DUELO para cargar las cartas.");
    }

    private void agregarLog(String mensaje)
    {
        areabatalla.append(mensaje + "\n");

        areabatalla.setCaretPosition(
                areabatalla.getDocument().getLength()
        );
    }

    private void cargarCartas()
    {
        iniciarduelo.setEnabled(false);

        jbutton1.setEnabled(false);
        jbutton2.setEnabled(false);
        jbutton3.setEnabled(false);

        reiniciarduelo.setEnabled(false);

        cartasCargadas = false;

        agregarLog("Cargando cartas desde YGOProDeck...");

        SwingWorker<List<Card>, Void> worker =
                new SwingWorker<List<Card>, Void>()
                {
                    @Override
                    protected List<Card> doInBackground()
                            throws Exception
                    {
                        List<Card> cartas = new ArrayList<>();

                        for (int i = 0; i < 6; i++)
                        {
                            cartas.add(
                                    apiClient.obtenerCartaAleatoria()
                            );
                        }

                        return cartas;
                    }

                    @Override
                    protected void done()
                    {
                        try
                        {
                            List<Card> cartas = get();

                            cartasJugador.clear();
                            cartasMaquina.clear();

                            cartasJugador.addAll(
                                    cartas.subList(0, 3)
                            );

                            cartasMaquina.addAll(
                                    cartas.subList(3, 6)
                            );

                            mostrarCartas();

                            duel = new Duel(
                                    cartasJugador,
                                    cartasMaquina,
                                    YgoDuelGui.this
                            );

                            duel.iniciarDuelo();

                            cartasCargadas = true;

                            iniciarduelo.setEnabled(false);
                            reiniciarduelo.setEnabled(true);

                            actualizarBotones();

                            agregarLog(
                                    "Las seis cartas se cargaron correctamente."
                            );

                            agregarLog("¡El duelo ha comenzado!");

                            agregarLog(
                                    "Selecciona una carta de tu lado."
                            );
                        }
                        catch (InterruptedException ex)
                        {
                            Thread.currentThread().interrupt();

                            mostrarError(
                                    "Se interrumpió la carga de cartas."
                            );
                        }
                        catch (ExecutionException ex)
                        {
                            Throwable causa = ex.getCause();

                            String detalle = causa != null
                                    ? causa.getMessage()
                                    : ex.getMessage();

                            mostrarError(
                                    "No se pudieron cargar las cartas: "
                                            + detalle
                            );
                        }
                        catch (RuntimeException ex)
                        {
                            mostrarError(
                                    "No se pudo iniciar el duelo: "
                                            + ex.getMessage()
                            );
                        }
                    }
                };

        worker.execute();
    }

    private void mostrarCartas()
    {
        for (int i = 0; i < 3; i++)
        {
            mostrarCartaJugador(
                    cartasJugador.get(i),
                    i
            );

            mostrarCartaMaquina(
                    cartasMaquina.get(i),
                    i
            );
        }
    }

    private void mostrarCartaJugador(Card carta, int indice)
    {
        obtenerNombreJugador(indice).setText(
                "Nombre: " + carta.getNombre()
        );

        obtenerAtkJugador(indice).setText(
                "ATK: " + carta.getAtk()
        );

        obtenerDefJugador(indice).setText(
                "DEF: " + carta.getDef()
        );

        cargarImagen(
                carta.getImagen(),
                obtenerImagenJugador(indice)
        );
    }

    private void mostrarCartaMaquina(Card carta, int indice)
    {
        obtenerNombreMaquina(indice).setText(
                "Nombre: " + carta.getNombre()
        );

        obtenerAtkMaquina(indice).setText(
                "ATK: " + carta.getAtk()
        );

        obtenerDefMaquina(indice).setText(
                "DEF: " + carta.getDef()
        );

        cargarImagen(
                carta.getImagen(),
                obtenerImagenMaquina(indice)
        );
    }

    private JLabel obtenerImagenJugador(int indice)
    {
        if (indice == 0) return jugadorcarta1;
        if (indice == 1) return jugadorcarta2;

        return jugadorcarta3;
    }

    private JLabel obtenerNombreJugador(int indice)
    {
        if (indice == 0) return jnom1;
        if (indice == 1) return jnom2;

        return jnom3;
    }

    private JLabel obtenerAtkJugador(int indice)
    {
        if (indice == 0) return jatk1;
        if (indice == 1) return jatk2;

        return jatk3;
    }

    private JLabel obtenerDefJugador(int indice)
    {
        if (indice == 0) return jdef1;
        if (indice == 1) return jdef2;

        return jdef3;
    }

    private JLabel obtenerImagenMaquina(int indice)
    {
        if (indice == 0) return maquinacarta1;
        if (indice == 1) return maquinacarta2;

        return maquinacarta3;
    }

    private JLabel obtenerNombreMaquina(int indice)
    {
        if (indice == 0) return mnom1;
        if (indice == 1) return mnom2;

        return mnom3;
    }

    private JLabel obtenerAtkMaquina(int indice)
    {
        if (indice == 0) return matk1;
        if (indice == 1) return matk2;

        return matk3;
    }

    private JLabel obtenerDefMaquina(int indice)
    {
        if (indice == 0) return mdef1;
        if (indice == 1) return mdef2;

        return mdef3;
    }

    private void cargarImagen(String url, JLabel etiqueta)
    {
        etiqueta.setText("Cargando imagen...");

        SwingWorker<ImageIcon, Void> worker =
                new SwingWorker<ImageIcon, Void>()
                {
                    @Override
                    protected ImageIcon doInBackground()
                            throws Exception
                    {
                        ImageIcon icono = new ImageIcon(
                                java.net.URI.create(url).toURL()
                        );

                        Image imagen = icono.getImage()
                                .getScaledInstance(
                                        120,
                                        120,
                                        Image.SCALE_SMOOTH
                                );

                        return new ImageIcon(imagen);
                    }

                    @Override
                    protected void done()
                    {
                        try
                        {
                            etiqueta.setIcon(get());
                            etiqueta.setText("");
                        }
                        catch (Exception ex)
                        {
                            etiqueta.setIcon(null);
                            etiqueta.setText("Imagen no disponible");
                        }
                    }
                };

        worker.execute();
    }

    private void elegirCarta(int indice)
    {
        if (!cartasCargadas || duel == null)
        {
            JOptionPane.showMessageDialog(
                    panel1,
                    "Primero debes cargar las cartas."
            );

            return;
        }

        if (duel.termino())
        {
            return;
        }

        if (indice < 0 || indice >= cartasJugador.size())
        {
            return;
        }

        // Desactivamos los botones mientras se resuelve la ronda.
        jbutton1.setEnabled(false);
        jbutton2.setEnabled(false);
        jbutton3.setEnabled(false);

        Card cartaElegida = cartasJugador.get(indice);

        agregarLog(
                "Has seleccionado: " + cartaElegida.getNombre()
        );

        duel.elegirCarta(indice);

        actualizarBotones();

        if (!duel.termino())
        {
            agregarLog("Selecciona tu siguiente carta.");
        }
    }

    private void actualizarBotones()
    {
        if (!cartasCargadas || duel == null || duel.termino())
        {
            jbutton1.setEnabled(false);
            jbutton2.setEnabled(false);
            jbutton3.setEnabled(false);

            return;
        }

        // Solo habilitamos botones que todavía tengan una carta.
        jbutton1.setEnabled(cartasJugador.size() >= 1);
        jbutton2.setEnabled(cartasJugador.size() >= 2);
        jbutton3.setEnabled(cartasJugador.size() >= 3);
    }

    private void reiniciarDuelo()
    {
        cartasCargadas = false;

        cartasJugador.clear();
        cartasMaquina.clear();

        duel = null;

        areabatalla.setText("");

        limpiarCartas();

        iniciarduelo.setEnabled(true);
        reiniciarduelo.setEnabled(false);

        jbutton1.setEnabled(false);
        jbutton2.setEnabled(false);
        jbutton3.setEnabled(false);

        agregarLog("Duelo reiniciado.");
        agregarLog(
                "Presiona INICIAR DUELO para cargar nuevas cartas."
        );
    }

    private void limpiarCartas()
    {
        for (int i = 0; i < 3; i++)
        {
            obtenerImagenJugador(i).setIcon(null);
            obtenerImagenJugador(i).setText("Imagen");

            obtenerNombreJugador(i).setText("Nombre:");
            obtenerAtkJugador(i).setText("ATK:");
            obtenerDefJugador(i).setText("DEF:");

            obtenerImagenMaquina(i).setIcon(null);
            obtenerImagenMaquina(i).setText("Imagen");

            obtenerNombreMaquina(i).setText("Nombre:");
            obtenerAtkMaquina(i).setText("ATK:");
            obtenerDefMaquina(i).setText("DEF:");
        }
    }

    private void mostrarError(String mensaje)
    {
        cartasCargadas = false;

        iniciarduelo.setEnabled(true);
        reiniciarduelo.setEnabled(false);

        jbutton1.setEnabled(false);
        jbutton2.setEnabled(false);
        jbutton3.setEnabled(false);

        agregarLog(mensaje);

        JOptionPane.showMessageDialog(
                panel1,
                mensaje,
                "Error",
                JOptionPane.ERROR_MESSAGE
        );
    }

    @Override
    public void onTurn(
            String playerCard,
            String aiCard,
            String winner
    )
    {
        agregarLog("Jugador: " + playerCard);
        agregarLog("Máquina: " + aiCard);
        agregarLog("Resultado: " + winner);
        agregarLog("--------------------------------");
    }

    @Override
    public void onScoreChanged(
            int playerScore,
            int aiScore
    )
    {
        agregarLog(
                "Marcador: Jugador "
                        + playerScore
                        + " - Máquina "
                        + aiScore
        );
    }

    @Override
    public void onDuelEnded(String winner)
    {
        agregarLog("¡Fin del duelo! Ganador: " + winner);

        actualizarBotones();

        JOptionPane.showMessageDialog(
                panel1,
                "¡Fin del duelo! Ganador: " + winner
        );
    }

    public static void main(String[] args)
    {
        SwingUtilities.invokeLater(
                new Runnable()
                {
                    @Override
                    public void run()
                    {
                        JFrame frame = new JFrame(
                                "Yu-Gi-Oh! Duel Lite"
                        );

                        YgoDuelGui gui = new YgoDuelGui();

                        frame.setContentPane(gui.panel1);

                        frame.setDefaultCloseOperation(
                                JFrame.EXIT_ON_CLOSE
                        );

                        frame.pack();

                        frame.setLocationRelativeTo(null);

                        frame.setMinimumSize(
                                new Dimension(900, 650)
                        );

                        frame.setVisible(true);
                    }
                }
        );
    }
}
