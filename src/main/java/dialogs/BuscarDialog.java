package dialogs;

import dao.AccesoTrabajador;
import exceptions.BDException;
import modelo.Trabajador;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class BuscarDialog extends JDialog implements ActionListener {

    private final JRadioButton radioId;
    private final JRadioButton radioDni;
    private final JTextField campoValor;
    private final JButton botonBuscar;
    private final JButton botonCerrar;

    private final JTextField areaId;
    private final JTextField areaDni;
    private final JTextField areaNombre;
    private final JTextField areaApellidos;
    private final JTextField areaDireccion;
    private final JTextField areaTelefono;
    private final JTextField areaPuesto;

    public BuscarDialog() {
        setTitle("Buscar Trabajador");
        setSize(720, 360);
        setLayout(new BorderLayout(12, 12));
        setLocationRelativeTo(null);
        setResizable(false);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        JPanel superior = new JPanel(new FlowLayout(FlowLayout.LEFT));
        radioId = new JRadioButton("Por ID", true);
        radioDni = new JRadioButton("Por DNI");
        ButtonGroup group = new ButtonGroup();
        group.add(radioId);
        group.add(radioDni);
        superior.add(radioId);
        superior.add(radioDni);

        superior.add(new JLabel("Valor"));
        campoValor = new JTextField(18);
        superior.add(campoValor);

        botonBuscar = new JButton("Buscar");
        botonBuscar.addActionListener(this);
        superior.add(botonBuscar);

        add(superior, BorderLayout.NORTH);

        JPanel formulario = new JPanel(new GridLayout(4, 4, 8, 8));
        formulario.setBorder(BorderFactory.createEmptyBorder(0, 12, 0, 12));

        formulario.add(new JLabel("Identificador"));
        areaId = campoSoloLectura();
        formulario.add(areaId);
        formulario.add(new JLabel("DNI"));
        areaDni = campoSoloLectura();
        formulario.add(areaDni);

        formulario.add(new JLabel("Nombre"));
        areaNombre = campoSoloLectura();
        formulario.add(areaNombre);
        formulario.add(new JLabel("Apellidos"));
        areaApellidos = campoSoloLectura();
        formulario.add(areaApellidos);

        formulario.add(new JLabel("Direccion"));
        areaDireccion = campoSoloLectura();
        formulario.add(areaDireccion);
        formulario.add(new JLabel("Telefono"));
        areaTelefono = campoSoloLectura();
        formulario.add(areaTelefono);

        formulario.add(new JLabel("Puesto"));
        areaPuesto = campoSoloLectura();
        formulario.add(areaPuesto);
        formulario.add(new JLabel(""));
        formulario.add(new JLabel(""));

        add(formulario, BorderLayout.CENTER);

        JPanel inferior = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        botonCerrar = new JButton("Cerrar");
        botonCerrar.addActionListener(this);
        inferior.add(botonCerrar);
        add(inferior, BorderLayout.SOUTH);

        setVisible(true);
    }

    private static JTextField campoSoloLectura() {
        JTextField t = new JTextField(18);
        t.setEditable(false);
        return t;
    }

    private void mostrarTrabajador(Trabajador t) {
        if (t == null) {
            limpiar();
            JOptionPane.showMessageDialog(this, "No se ha encontrado ningun trabajador");
            return;
        }

        areaId.setText(Integer.toString(t.getIdentificador()));
        areaDni.setText(t.getDni());
        areaNombre.setText(t.getNombre());
        areaApellidos.setText(t.getApellidos());
        areaDireccion.setText(t.getDireccion());
        areaTelefono.setText(t.getTelefono());
        areaPuesto.setText(t.getPuesto());
    }

    private void limpiar() {
        areaId.setText("");
        areaDni.setText("");
        areaNombre.setText("");
        areaApellidos.setText("");
        areaDireccion.setText("");
        areaTelefono.setText("");
        areaPuesto.setText("");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == botonCerrar) {
            dispose();
            return;
        }

        if (e.getSource() != botonBuscar) {
            return;
        }

        String valor = campoValor.getText().trim();
        if (valor.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Introduce un valor para buscar", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            if (radioId.isSelected()) {
                int id;
                try {
                    id = Integer.parseInt(valor);
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "El ID debe ser un numero entero", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                mostrarTrabajador(AccesoTrabajador.buscarTrabajadorPorId(id));
            } else {
                String dni = valor.toUpperCase();
                if (!AccesoTrabajador.validarDNI(dni)) {
                    JOptionPane.showMessageDialog(this, "El DNI no es valido", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                mostrarTrabajador(AccesoTrabajador.buscarTrabajadorPorDni(dni));
            }
        } catch (BDException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}

