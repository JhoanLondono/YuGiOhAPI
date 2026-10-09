
public class Card
{
    private String nombre;
    private int atk;
    private int def;
    private String imagen;

    public Card(
            String nombre,
            int atk,
            int def,
            String imagen
    )
    {
        this.nombre = nombre;
        this.atk = atk;
        this.def = def;
        this.imagen = imagen;
    }

    public String getNombre()
    {
        return nombre;
    }

    public int getAtk()
    {
        return atk;
    }

    public int getDef()
    {
        return def;
    }

    public String getImagen()
    {
        return imagen;
    }
}
