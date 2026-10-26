package ec.edu.taller;

import java.util.*;

public class GestorIncidentes {

    private Queue<IncidenteSeguridad> colaIncidentes;
    private int capacidadMaxima;

    public GestorIncidentes(int capacidadMaxima) {
        this.capacidadMaxima = capacidadMaxima;
        this.colaIncidentes = new PriorityQueue<>(
                Comparator.comparingInt(IncidenteSeguridad::getPrioridad)
        );
    }

    public boolean registrarIncidente(IncidenteSeguridad incidente) {
        // TODO: implementar validaciones y registro en cola
        if (incidente == null){
            return false;
        }

        String otro = incidente.getCodigo();
        if (otro == null || otro.trim().isEmpty()) {
            return false;
        }

        if (colaIncidentes.size() >= capacidadMaxima) {
            return false;
        }

        if (existeIncidente(otro)) {
            return false;
        }

        colaIncidentes.offer(incidente);
        return true;
    }

    public boolean existeIncidente(String otro) {
        // TODO: verificar si existe un incidente por código
        if (otro == null || otro.trim().isEmpty()) {
            return false;
        }

        for (IncidenteSeguridad incidente : colaIncidentes) {
            if (otro.equals(incidente.getCodigo())) {
                return true;
            }
        }
        return false;
    }

    public IncidenteSeguridad consultarSiguienteIncidente() {
        // TODO: consultar sin eliminar
        return colaIncidentes.peek();
    }

    public IncidenteSeguridad atenderSiguienteIncidente() {
        // TODO: eliminar el primero de la cola y cambiar estado a ATENDIDO
        IncidenteSeguridad incidente = colaIncidentes.poll();

        if (incidente != null) {
            incidente.setEstado("ATENDIDO");
        }

        return incidente;
    }

    public int contarIncidentesPendientes() {
        // TODO: retornar cantidad de incidentes pendientes
        return colaIncidentes.size();
    }

    public int consultarEspaciosDisponibles() {
        // TODO: retornar capacidad disponible
        return capacidadMaxima - colaIncidentes.size();
    }

    public List<IncidenteSeguridad> listarIncidentes() {
        // TODO: retornar copia de los incidentes pendientes sin modificar la cola
        List<IncidenteSeguridad> lista = new ArrayList<>();

        Queue<IncidenteSeguridad> copia = new PriorityQueue<>(
                Comparator.comparingInt(IncidenteSeguridad::getPrioridad)
        );

        copia.addAll(colaIncidentes);

        while (!copia.isEmpty()) {
            lista.add(copia.poll());
        }

        return lista;
    }
}
