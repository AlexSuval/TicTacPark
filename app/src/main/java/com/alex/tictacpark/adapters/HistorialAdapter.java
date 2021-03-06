package com.alex.tictacpark.adapters;

/**
 * Created by Alex on 18/01/2016.
 */

        import android.content.Context;
        import android.support.v7.widget.RecyclerView;
        import android.view.ContextMenu;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.TextView;

        import com.alex.tictacpark.R;
        import com.alex.tictacpark.models.Historial;

        import java.util.ArrayList;
        import java.util.List;

/**
 * Adapter de la sección Historial - Los adapter son los encargados de unir la interfaz gráfica
 * con los datos.
 */
public class HistorialAdapter extends RecyclerView.Adapter<HistorialAdapter.ViewHolder>{

    private int rowLayout;
    private Context mContext;
    private List<Historial> historial; // Lista historial
    private int posicion;

    public int getPosicion() {
        return posicion;
    }

    public void setPosicion(int posicion) {
        this.posicion = posicion;
    }

    /* Métodos necesarios y siempre utilizados para aplicar la vista en forma de 'Card View' */
    // Constructor por defecto
    public HistorialAdapter(){}

    // Constructor
    public HistorialAdapter(ArrayList<Historial> historial, int rowLayout, Context context) {
        this.historial = historial;
        this.rowLayout = rowLayout;
        this.mContext = context;
    }

    // Método que crea el listado de Cards
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(rowLayout, viewGroup, false);
        return new ViewHolder(v);
    }

    // Método que unifica los datos con las vistas.
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        Historial h = historial.get(i); // Se obtiene el objeto historial correspondiente.

        // Se aplica el nombre
        viewHolder.nombre.setText(h.getNombre());

        // Se aplica la fecha
        viewHolder.fecha.setText(h.getFecha());

        // Se aplica la duración
        viewHolder.duracion.setText(h.getDuracion());

        // Se aplica el precio
        viewHolder.precio.setText(h.getPrecio());

        // Se aplica el precio/hora
        viewHolder.precio_hora.setText(h.getPrecio_hora());

        // Capturamos la posición antes de cargar el contexto del menú
        final ViewHolder holder=viewHolder;
        viewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener(){
            @Override
            public boolean onLongClick(View v){
                setPosicion(holder.getPosition());
                return false;
            }
        });
    }

    @Override
    public int getItemCount() {
        return historial == null ? 0 : historial.size();
    }

    @Override
    public void onViewRecycled(ViewHolder holder) {
        holder.itemView.setOnLongClickListener(null);
        super.onViewRecycled(holder);
    }

    // Clase que inicializa los elementos gráficos de la interfaz.
    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener{
        public TextView nombre;
        public TextView fecha;
        public TextView duracion;
        public TextView precio;
        public TextView precio_hora;

        public ViewHolder(View itemView) {
            super(itemView);
            nombre = (TextView) itemView.findViewById(R.id.tv_nombre_parking);
            fecha = (TextView) itemView.findViewById(R.id.tv_fecha);
            duracion = (TextView) itemView.findViewById(R.id.tv_duracion);
            precio = (TextView) itemView.findViewById(R.id.tv_precio);
            precio_hora = (TextView) itemView.findViewById(R.id.tv_precio_hora);
            itemView.setOnCreateContextMenuListener(this);
        }

        // Menú para gestionar el click prolongado en las Cards
        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            menu.setHeaderTitle("Seleccione la acción que desee realizar: ");
            menu.add(0, R.id.acceder_parking, 0, "Ir al parking");
            menu.add(0, R.id.borrar_entrada, 0, "Borrar entrada");
        }
    }
}