
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class Duel
{
    private List<Card> playerCards;
    private List<Card> aiCards;

    private int playerScore;
    private int aiScore;

    private boolean playerStartsRound;
    private boolean duelStarted;
    private boolean duelEnded;

    private Card pendingAiCard;

    private BattleListener listener;
    private Random random;

    public Duel(
            List<Card> playerCards,
            List<Card> aiCards,
            BattleListener listener
    )
    {
        this.playerCards = new ArrayList<>(playerCards);
        this.aiCards = new ArrayList<>(aiCards);
        this.listener = listener;

        random = new Random();

        playerScore = 0;
        aiScore = 0;

        playerStartsRound = true;
        duelStarted = false;
        duelEnded = false;

        pendingAiCard = null;
    }

    public void iniciarDuelo()
    {
        if (playerCards.size() != 3 || aiCards.size() != 3)
        {
            throw new IllegalStateException(
                    "Cada jugador debe tener tres cartas."
            );
        }

        playerScore = 0;
        aiScore = 0;

        duelEnded = false;
        duelStarted = true;
        pendingAiCard = null;

        // Elegimos al azar quién selecciona primero.
        playerStartsRound = random.nextBoolean();

        if (listener != null)
        {
            listener.onScoreChanged(playerScore, aiScore);

            if (playerStartsRound)
            {
                listener.onTurn(
                        "Jugador",
                        "Máquina",
                        "El jugador selecciona primero."
                );
            }
            else
            {
                listener.onTurn(
                        "Jugador",
                        "Máquina",
                        "La máquina selecciona primero."
                );
            }
        }

        prepararRonda();
    }

    private void prepararRonda()
    {
        if (!duelStarted || duelEnded)
        {
            return;
        }

        pendingAiCard = null;

        if (!playerStartsRound)
        {
            seleccionarCartaMaquina();

            if (listener != null)
            {
                listener.onTurn(
                        "Esperando al jugador",
                        pendingAiCard.getNombre(),
                        "La máquina eligió su carta primero."
                );
            }
        }
    }

    private void seleccionarCartaMaquina()
    {
        if (aiCards.isEmpty())
        {
            return;
        }

        int indice = random.nextInt(aiCards.size());

        pendingAiCard = aiCards.remove(indice);
    }

    public void elegirCarta(int indiceJugador)
    {
        if (!duelStarted || duelEnded)
        {
            return;
        }

        if (indiceJugador < 0
                || indiceJugador >= playerCards.size())
        {
            return;
        }

        // El jugador siempre elige una carta propia.
        Card cartaJugador = playerCards.remove(indiceJugador);

        Card cartaMaquina;

        if (playerStartsRound)
        {
            // El jugador seleccionó primero.
            seleccionarCartaMaquina();
            cartaMaquina = pendingAiCard;
        }
        else
        {
            // La máquina ya había seleccionado su carta.
            cartaMaquina = pendingAiCard;
        }

        if (cartaMaquina == null)
        {
            if (listener != null)
            {
                listener.onTurn(
                        cartaJugador.getNombre(),
                        "Sin carta",
                        "No fue posible seleccionar la carta de la máquina."
                );
            }

            finalizarDuelo();
            return;
        }

        resolverRonda(cartaJugador, cartaMaquina);
    }

    private void resolverRonda(
            Card cartaJugador,
            Card cartaMaquina
    )
    {
        String ganadorRonda;

        if (cartaJugador.getAtk() > cartaMaquina.getAtk())
        {
            ganadorRonda = "Jugador";
            playerScore++;
        }
        else if (cartaMaquina.getAtk() > cartaJugador.getAtk())
        {
            ganadorRonda = "Máquina";
            aiScore++;
        }
        else if (cartaJugador.getDef() > cartaMaquina.getDef())
        {
            ganadorRonda = "Jugador";
            playerScore++;
        }
        else if (cartaMaquina.getDef() > cartaJugador.getDef())
        {
            ganadorRonda = "Máquina";
            aiScore++;
        }
        else
        {
            ganadorRonda = "Empate";
        }

        if (listener != null)
        {
            listener.onTurn(
                    cartaJugador.getNombre(),
                    cartaMaquina.getNombre(),
                    ganadorRonda
            );

            listener.onScoreChanged(
                    playerScore,
                    aiScore
            );
        }

        pendingAiCard = null;

        if (playerScore >= 2
                || aiScore >= 2
                || playerCards.isEmpty()
                || aiCards.isEmpty())
        {
            finalizarDuelo();
        }
        else
        {
            // La siguiente ronda empieza el otro jugador.
            playerStartsRound = !playerStartsRound;

            if (listener != null)
            {
                if (playerStartsRound)
                {
                    listener.onTurn(
                            "Jugador",
                            "Máquina",
                            "El jugador seleccionará primero en la siguiente ronda."
                    );
                }
                else
                {
                    listener.onTurn(
                            "Jugador",
                            "Máquina",
                            "La máquina seleccionará primero en la siguiente ronda."
                    );
                }
            }

            prepararRonda();
        }
    }

    private void finalizarDuelo()
    {
        duelEnded = true;
        duelStarted = false;

        String ganador;

        if (playerScore > aiScore)
        {
            ganador = "Jugador";
        }
        else if (aiScore > playerScore)
        {
            ganador = "Máquina";
        }
        else
        {
            ganador = "Empate";
        }

        if (listener != null)
        {
            listener.onDuelEnded(ganador);
        }
    }

    public boolean esTurnoJugador()
    {
        // El jugador siempre debe elegir su propia carta.
        return duelStarted && !duelEnded;
    }

    public boolean termino()
    {
        return duelEnded;
    }

    public int getPlayerScore()
    {
        return playerScore;
    }

    public int getAiScore()
    {
        return aiScore;
    }

    public List<Card> getPlayerCards()
    {
        return Collections.unmodifiableList(playerCards);
    }

    public List<Card> getAiCards()
    {
        return Collections.unmodifiableList(aiCards);
    }
}
