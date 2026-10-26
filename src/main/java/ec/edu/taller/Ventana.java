package ec.edu.taller;

import javax.swing.*;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Ventana {

    private JPanel Panel;
    private JTabbedPane tabbedPane1;
    private JLabel lblIncidente;
    private JTextField txtCodigo;
    private JComboBox<String> cmbTipo;
    private JTextArea txtDescripcion;
    private JComboBox<String> cmbPrioridad;
    private JButton btnRegistrar;
    private JList<String> lstIncidentes;
    private JButton btnSiguiente;
    private JButton btnAtender;
    private JButton btnListar;
    private JTextArea txtDatos;
    private JLabel lblPendientes;
    private JLabel lblDisponible;
    private JLabel lblCapacidad;

    private static final int CAPACIDAD = 10;

    private final DefaultListModel<String> modeloLista =
            new DefaultListModel<>();

    private final List<IncidenteUI> incidentes =
            new ArrayList<>();

    private IncidenteUI incidenteActual;

    // Clase auxiliar para manejar los datos en esta ventana.
    private static class IncidenteUI {
        String codigo;
        String tipo;
        String descripcion;
        int prioridad;
        String estado;

        IncidenteUI(String codigo, String tipo,
                    String descripcion, int prioridad) {
            this.codigo = codigo;
            this.tipo = tipo;
            this.descripcion = descripcion;
            this.prioridad = prioridad;
            this.estado = "PENDIENTE";
        }

        @Override
        public String toString() {
            return codigo + " | " + tipo
                    + " | Prioridad: " + prioridad
                    + " | " + estado;
        }
    }

    public Ventana() {

        // Configurar la lista visual.
        lstIncidentes.setModel(modeloLista);

        // Configurar el área de datos.
        txtDatos.setEditable(false);
        txtDatos.setLineWrap(true);
        txtDatos.setWrapStyleWord(true);
        txtDatos.setFont(new java.awt.Font(
                "Monospaced", java.awt.Font.PLAIN, 14));


        lblIncidente.setText("Nuevo incidente");

        // Cargar casos de prueba al iniciar.
        cargarCasosPrueba();

        // Configurar los botones.
        btnRegistrar.addActionListener(
                e -> registrarIncidente());

        btnSiguiente.addActionListener(
                e -> mostrarSiguiente());

        btnAtender.addActionListener(
                e -> atenderIncidente());

        btnListar.addActionListener(
                e -> listarIncidentes());

        // Mostrar el primer incidente y los contadores.
        actualizarContadores();
        mostrarPrimero();
    }

    private void cargarCasosPrueba() {

        // CASO 1: Ya estaba registrado
        incidentes.add(new IncidenteUI(
                "INC-001",
                "MALWARE",
                "Software malicioso detectado",
                1
        ));

        // CASO 2: NUEVO
        incidentes.add(new IncidenteUI(
                "INC-008",
                "SOFTWARE",
                "Error en el funcionamiento del software",
                1
        ));

        // CASO 3: Ya estaba registrado
        incidentes.add(new IncidenteUI(
                "INC-002",
                "HARDWARE",
                "Falla en el equipo de computo",
                2
        ));

        // CASO 4: NUEVO
        incidentes.add(new IncidenteUI(
                "INC-004",
                "HARDWARE",
                "Problema en un componente del equipo",
                2
        ));

        // CASO 5: Ya estaba registrado
        incidentes.add(new IncidenteUI(
                "INC-003",
                "RED",
                "Problema de conexion de red",
                3
        ));

        // CASO 6: NUEVO
        incidentes.add(new IncidenteUI(
                "INC-007",
                "SOFTWARE",
                "Error en una aplicacion",
                3
        ));

        // CASO 7: NUEVO
        incidentes.add(new IncidenteUI(
                "INC-006",
                "HARDWARE",
                "Falla en un dispositivo",
                3
        ));

        // CASO 8: NUEVO
        incidentes.add(new IncidenteUI(
                "INC-005",
                "RED",
                "Intermitencia en la conexion de red",
                4
        ));

        ordenarPorPrioridad();
    }

    private void ordenarPorPrioridad() {
        incidentes.sort(
                Comparator.comparingInt(i -> i.prioridad)
        );
    }

    private int contarPendientes() {
        int total = 0;

        for (IncidenteUI incidente : incidentes) {
            if ("PENDIENTE".equals(incidente.estado)) {
                total++;
            }
        }

        return total;
    }

    private void actualizarContadores() {

        int pendientes = contarPendientes();
        int disponibles = CAPACIDAD - pendientes;

        lblPendientes.setText(
                "PENDIENTES: " + pendientes);

        lblDisponible.setText(
                "DISPONIBLES: " + disponibles);

        lblCapacidad.setText(
                "CAPACIDAD: " + CAPACIDAD);
    }

    private void mostrarDatos(IncidenteUI incidente) {

        if (incidente == null) {
            txtDatos.setText(
                    "No hay incidentes pendientes.");
            lblIncidente.setText("Sin incidentes pendientes");
            return;
        }

        txtDatos.setText(
                "CODIGO: " + incidente.codigo + "\n\n"
                        + "TIPO: " + incidente.tipo + "\n\n"
                        + "PRIORIDAD: " + incidente.prioridad + "\n\n"
                        + "ESTADO: " + incidente.estado + "\n\n"
                        + "DESCRIPCION: " + incidente.descripcion
        );

        txtDatos.setCaretPosition(0);

        lblIncidente.setText(
                "Incidente actual: " + incidente.codigo);
    }


    // Devuelve el pendiente de mayor prioridad (menor numero),
    // equivalente al peek() de la PriorityQueue.
    private IncidenteUI obtenerSiguientePendiente() {

        ordenarPorPrioridad();

        for (IncidenteUI incidente : incidentes) {
            if ("PENDIENTE".equals(incidente.estado)) {
                return incidente;
            }
        }

        return null;
    }

    // Muestra el incidente de mayor prioridad (el que se atendera).
    private void mostrarPrimero() {

        incidenteActual = obtenerSiguientePendiente();

        mostrarDatos(incidenteActual);
        actualizarContadores();
    }

    // Solo para VER: avanza al siguiente pendiente segun el orden
    // de prioridad. No atiende ni modifica ningun incidente.
    private void mostrarSiguiente() {

        ordenarPorPrioridad();

        List<IncidenteUI> pendientes = new ArrayList<>();
        for (IncidenteUI incidente : incidentes) {
            if ("PENDIENTE".equals(incidente.estado)) {
                pendientes.add(incidente);
            }
        }

        if (pendientes.isEmpty()) {
            incidenteActual = null;
            mostrarDatos(null);
            actualizarContadores();
            return;
        }

        int posicion = pendientes.indexOf(incidenteActual);
        incidenteActual = pendientes.get(
                (posicion + 1) % pendientes.size());

        mostrarDatos(incidenteActual);
        actualizarContadores();
    }


    private void registrarIncidente() {

        String codigo = txtCodigo.getText().trim();
        String tipo = (String) cmbTipo.getSelectedItem();
        String descripcion =
                txtDescripcion.getText().trim();
        String prioridadTexto =
                (String) cmbPrioridad.getSelectedItem();

        if (codigo.isEmpty() || descripcion.isEmpty()
                || tipo == null || prioridadTexto == null) {

            JOptionPane.showMessageDialog(
                    Panel,
                    "Completa todos los campos del incidente.",
                    "Datos incompletos",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        // Verificar que no se repita el código.
        for (IncidenteUI incidente : incidentes) {
            if (incidente.codigo.equalsIgnoreCase(codigo)) {
                JOptionPane.showMessageDialog(
                        Panel,
                        "Ya existe un incidente con ese código.",
                        "Código duplicado",
                        JOptionPane.WARNING_MESSAGE
                );
                return;
            }
        }

        // Verificar la capacidad de incidentes pendientes.
        if (contarPendientes() >= CAPACIDAD) {
            JOptionPane.showMessageDialog(
                    Panel,
                    "Se alcanzó la capacidad máxima de incidentes.",
                    "Capacidad completa",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        int prioridad;

        try {
            prioridad = Integer.parseInt(prioridadTexto);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(
                    Panel,
                    "La prioridad debe ser un número.",
                    "Prioridad incorrecta",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        IncidenteUI nuevo = new IncidenteUI(
                codigo, tipo, descripcion, prioridad);

        incidentes.add(nuevo);

        ordenarPorPrioridad();
        actualizarContadores();
        mostrarPrimero();

        // Limpiar los campos del formulario.
        txtCodigo.setText("");
        txtDescripcion.setText("");
        cmbTipo.setSelectedIndex(0);
        cmbPrioridad.setSelectedIndex(0);

        JOptionPane.showMessageDialog(
                Panel,
                "Incidente registrado correctamente."
        );
    }

    private void atenderIncidente() {

        // Se atiende el de mayor prioridad, no el que este en pantalla.
        IncidenteUI siguiente = obtenerSiguientePendiente();

        if (siguiente == null) {
            JOptionPane.showMessageDialog(
                    Panel,
                    "No hay un incidente pendiente para atender.",
                    "Aviso",
                    JOptionPane.INFORMATION_MESSAGE
            );
            return;
        }

        siguiente.estado = "ATENDIDO";

        JOptionPane.showMessageDialog(
                Panel,
                "Incidente " + siguiente.codigo
                        + " atendido correctamente."
        );

        listarIncidentes();
        mostrarPrimero();
    }

    private void listarIncidentes() {

        ordenarPorPrioridad();
        modeloLista.clear();

        if (incidentes.isEmpty()) {
            modeloLista.addElement(
                    "No existen incidentes registrados.");
        } else {
            for (IncidenteUI incidente : incidentes) {
                modeloLista.addElement(incidente.toString());
            }
        }

        actualizarContadores();
    }

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {

            JFrame ventana =
                    new JFrame("Sistema de gestión de incidentes");

            Ventana formulario = new Ventana();

            ventana.setContentPane(formulario.Panel);

            ventana.setDefaultCloseOperation(
                    JFrame.EXIT_ON_CLOSE);

            ventana.pack();

            ventana.setMinimumSize(
                    new Dimension(700, 450));

            ventana.setLocationRelativeTo(null);
            ventana.setVisible(true);
        });
    }
}
