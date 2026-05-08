package dialogs;

import dao.AccesoTrabajador;
import exceptions.BDException;
import modelo.Empresa;
import modelo.Trabajador;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.regex.Pattern;

public class ModificarDialog extends JDialog implements ActionListener, ItemListener, ListSelectionListener {

    private static final String[] COLUMNAS = {"Identificador", "DNI", "Nombre", "Apellidos", "Direccion", "Telefono", "Puesto"};
    private static final String[] PUESTOS = {"Elija Puesto", "Programador", "Analista", "Arquitecto", "Jefe de Proyecto"};

    private final Empresa empresa;
    private JTable tabla;
    private DefaultTableModel modeloTabla;
    private TableRowSorter<DefaultTableModel> sorter;
    private JTextField campoBuscar;
    private JTextField areaId;
    private JTextField areaDni;
    private JTextField areaNombre;
    private JTextField areaApellidos;
    private JTextField areaDireccion;
    private JTextField areaTelefono;
    private JComboBox<String> comboPuesto;
    private JButton modificar;
    private JButton cerrar;

    private String dni = "";
    private String nombre = "";
    private String apellidos = "";
    private String direccion = "";
    private String telefono = "";
    private String puesto = "";

    public ModificarDialog(Empresa empresa) {
        this.empresa = empresa;

        setTitle("Modificar Trabajador");
        setSize(980, 720);
        ((JComponent) getContentPane()).setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        setLayout(new BorderLayout(20, 20));
        setLocationRelativeTo(null);
        setResizable(false);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        add(crearPanelSuperior(), BorderLayout.NORTH);
        add(crearPanelTabla(), BorderLayout.CENTER);
        add(crearPanelFormulario(), BorderLayout.SOUTH);

        recargarTabla();
        setVisible(true);
    }

    private JPanel crearPanelSuperior() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        panel.add(new JLabel("Buscar"));
        campoBuscar = new JTextField(20);
        campoBuscar.addActionListener(this);
        panel.add(campoBuscar);

        JButton botonBuscar = new JButton("Filtrar");
        botonBuscar.setActionCommand("FILTRAR");
        botonBuscar.addActionListener(this);
        panel.add(botonBuscar);

        JButton botonLimpiar = new JButton("Limpiar");
        botonLimpiar.setActionCommand("LIMPIAR");
        botonLimpiar.addActionListener(this);
        panel.add(botonLimpiar);

        return panel;
    }

    private JScrollPane crearPanelTabla() {
        modeloTabla = new DefaultTableModel(new Object[0][0], COLUMNAS) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tabla = new JTable(modeloTabla);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabla.getSelectionModel().addListSelectionListener(this);
        tabla.setAutoCreateRowSorter(true);

        sorter = new TableRowSorter<>(modeloTabla);
        tabla.setRowSorter(sorter);

        JScrollPane scrollPane = new JScrollPane(tabla);
        scrollPane.setPreferredSize(new Dimension(920, 320));
        return scrollPane;
    }

    private JPanel crearPanelFormulario() {
        JPanel panelPrincipal = new JPanel(new BorderLayout());
        JPanel formulario = new JPanel(new GridLayout(4, 4, 8, 8));
        formulario.add(new JLabel("Identificador"));
        areaId = new JTextField(15);
        areaId.setEditable(false);
        formulario.add(areaId);
        formulario.add(new JLabel("DNI"));
        areaDni = new JTextField(15);
        formulario.add(areaDni);
        formulario.add(new JLabel("Nombre"));
        areaNombre = new JTextField(15);
        formulario.add(areaNombre);
        formulario.add(new JLabel("Apellidos"));
        areaApellidos = new JTextField(15);
        formulario.add(areaApellidos);
        formulario.add(new JLabel("Direccion"));
        areaDireccion = new JTextField(15);
        formulario.add(areaDireccion);
        formulario.add(new JLabel("Telefono"));
        areaTelefono = new JTextField(15);
        formulario.add(areaTelefono);
        formulario.add(new JLabel("Puesto"));
        comboPuesto = new JComboBox<>(PUESTOS);
        comboPuesto.addItemListener(this);
        formulario.add(comboPuesto);
        panelPrincipal.add(formulario, BorderLayout.CENTER);
        JPanel botones = new JPanel(new FlowLayout(FlowLayout.CENTER));
        modificar = new JButton("Guardar cambios");
        modificar.addActionListener(this);
        botones.add(modificar);
        cerrar = new JButton("Cerrar");
        cerrar.addActionListener(this);
        botones.add(cerrar);
        panelPrincipal.add(botones, BorderLayout.SOUTH);
        return panelPrincipal;
    }

    private void recargarTabla() {
        try {
            String[][] datos = AccesoTrabajador.listarTrabajadores();
            modeloTabla.setRowCount(0);

            for (String[] fila : datos) {
                modeloTabla.addRow(fila);
            }

            if (modeloTabla.getRowCount() > 0) {
                tabla.setRowSelectionInterval(0, 0);
            } else {
                limpiarFormulario();
            }

        } catch (BDException e) {
            JOptionPane.showMessageDialog(
                    this,
                    e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void aplicarFiltro() {
        String texto = campoBuscar.getText().trim();

        if (texto.isEmpty()) {
            sorter.setRowFilter(null);
            return;
        }

        sorter.setRowFilter(RowFilter.regexFilter("(?i)" + Pattern.quote(texto)));
    }

    private void cargarFilaSeleccionada() {
        int filaVista = tabla.getSelectedRow();

        if (filaVista == -1) {
            limpiarFormulario();
            return;
        }

        int filaModelo = tabla.convertRowIndexToModel(filaVista);

        areaId.setText(modeloTabla.getValueAt(filaModelo, 0).toString());
        areaDni.setText(modeloTabla.getValueAt(filaModelo, 1).toString());
        areaNombre.setText(modeloTabla.getValueAt(filaModelo, 2).toString());
        areaApellidos.setText(modeloTabla.getValueAt(filaModelo, 3).toString());
        areaDireccion.setText(modeloTabla.getValueAt(filaModelo, 4).toString());
        areaTelefono.setText(modeloTabla.getValueAt(filaModelo, 5).toString());
        comboPuesto.setSelectedItem(modeloTabla.getValueAt(filaModelo, 6).toString());
    }

    private void limpiarFormulario() {
        areaId.setText("");
        areaDni.setText("");
        areaNombre.setText("");
        areaApellidos.setText("");
        areaDireccion.setText("");
        areaTelefono.setText("");
        comboPuesto.setSelectedIndex(0);
    }

    private boolean comprobarErrores() {
        if (areaId.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Selecciona un trabajador", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        if (dni.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Debe introducir el DNI", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        if (!AccesoTrabajador.validarDNI(dni)) {
            JOptionPane.showMessageDialog(this, "El DNI no es valido", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        if (nombre.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Debe introducir el nombre", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        if (apellidos.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Debe introducir los apellidos", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        if (direccion.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Debe introducir la direccion", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        if (telefono.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Debe introducir el telefono", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        if (!AccesoTrabajador.validarTelefono(telefono)) {
            JOptionPane.showMessageDialog(this, "El telefono no es valido", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        if (comboPuesto.getSelectedIndex() == 0) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar un puesto", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        return true;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if ("FILTRAR".equals(e.getActionCommand())) {
            aplicarFiltro();
            return;
        }

        if ("LIMPIAR".equals(e.getActionCommand())) {
            campoBuscar.setText("");
            sorter.setRowFilter(null);
            return;
        }

        if (e.getSource() == cerrar) {
            dispose();
            return;
        }

        if (e.getSource() == modificar) {
            dni = areaDni.getText().trim();
            nombre = areaNombre.getText().trim();
            apellidos = areaApellidos.getText().trim();
            direccion = areaDireccion.getText().trim();
            telefono = areaTelefono.getText().trim();
            puesto = comboPuesto.getSelectedItem().toString();

            if (!comprobarErrores()) {
                return;
            }

            Trabajador trabajador = new Trabajador(
                    Integer.parseInt(areaId.getText().trim()),
                    dni,
                    nombre,
                    apellidos,
                    direccion,
                    telefono,
                    puesto
            );

            try {
                boolean actualizado = AccesoTrabajador.modificaTrabajador(trabajador);

                if (actualizado) {
                    actualizarEmpresa(trabajador);
                    JOptionPane.showMessageDialog(this, "Trabajador modificado correctamente");
                    recargarTabla();
                    reSeleccionarTrabajador(trabajador.getIdentificador());
                } else {
                    JOptionPane.showMessageDialog(this, "No se pudo modificar el trabajador", "Error", JOptionPane.ERROR_MESSAGE);
                }

            } catch (BDException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void actualizarEmpresa(Trabajador trabajador) {
        int posicion = empresa.devolverPosicion(trabajador.getIdentificador());

        if (posicion != -1) {
            empresa.modificarTrabajador(
                    posicion,
                    trabajador.getDni(),
                    trabajador.getNombre(),
                    trabajador.getApellidos(),
                    trabajador.getDireccion(),
                    trabajador.getTelefono(),
                    trabajador.getPuesto()
            );
        }
    }

    private void reSeleccionarTrabajador(int id) {
        for (int i = 0; i < modeloTabla.getRowCount(); i++) {
            Object valor = modeloTabla.getValueAt(i, 0);

            if (valor != null && Integer.parseInt(valor.toString()) == id) {
                int filaVista = tabla.convertRowIndexToView(i);

                if (filaVista != -1) {
                    tabla.setRowSelectionInterval(filaVista, filaVista);
                }
                break;
            }
        }
    }

    @Override
    public void itemStateChanged(ItemEvent e) {
        if (e.getStateChange() == ItemEvent.SELECTED) {
            puesto = comboPuesto.getSelectedItem().toString();
        }
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        if (!e.getValueIsAdjusting()) {
            cargarFilaSeleccionada();
        }
    }
}
