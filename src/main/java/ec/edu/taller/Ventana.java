
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
    private int indiceActual = -1;

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

        // Opciones del formulario de ingreso.
        cmbTipo.removeAllItems();
        cmbTipo.addItem("MALWARE");
        cmbTipo.addItem("HARDWARE");
        cmbTipo.addItem("SOFTWARE");
        cmbTipo.addItem("RED");
        cmbTipo.addItem("OTRO");

        cmbPrioridad.removeAllItems();
        cmbPrioridad.addItem("1");
        cmbPrioridad.addItem("2");
        cmbPrioridad.addItem("3");
        cmbPrioridad.addItem("4");
        cmbPrioridad.addItem("5");

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
        mostrarSiguiente();
    }

    private void cargarCasosPrueba() {

        incidentes.add(new IncidenteUI(
                "INC-001",
                "MALWARE",
                "Software malicioso detectado",
                1
        ));

        incidentes.add(new IncidenteUI(
                "INC-002",
                "HARDWARE",
                "Falla en el equipo de computo",
                2
        ));

        incidentes.add(new IncidenteUI(
                "INC-003",
                "RED",
                "Problema de conexion de red",
                3
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


    private void mostrarSiguiente() {

        ordenarPorPrioridad();

        if (incidentes.isEmpty()) {
            incidenteActual = null;
            indiceActual = -1;
            mostrarDatos(null);
            actualizarContadores();
            return;
        }

        int total = incidentes.size();
        int inicio = indiceActual;

        // Buscar el siguiente incidente pendiente.
        for (int i = 1; i <= total; i++) {

            int siguiente = (inicio + i) % total;

            IncidenteUI incidente = incidentes.get(siguiente);

            if ("PENDIENTE".equals(incidente.estado)) {

                incidenteActual = incidente;
                indiceActual = siguiente;

                mostrarDatos(incidenteActual);
                actualizarContadores();
                return;
            }
        }

        // No quedan incidentes pendientes.
        incidenteActual = null;
        indiceActual = -1;

        mostrarDatos(null);
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
        mostrarSiguiente();

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

        if (incidenteActual == null
                || !"PENDIENTE".equals(incidenteActual.estado)) {

            JOptionPane.showMessageDialog(
                    Panel,
                    "No hay un incidente pendiente para atender.",
                    "Aviso",
                    JOptionPane.INFORMATION_MESSAGE
            );
            return;
        }

        incidenteActual.estado = "ATENDIDO";

        JOptionPane.showMessageDialog(
                Panel,
                "Incidente " + incidenteActual.codigo
                        + " atendido correctamente."
        );

        actualizarContadores();
        listarIncidentes();
        mostrarSiguiente();
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
