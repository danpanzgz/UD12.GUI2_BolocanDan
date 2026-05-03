package dialogs;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import dao.AccesoTrabajador;
import exceptions.BDException;
import modelo.Empresa;

public class BajaDialog extends JDialog implements ActionListener {

    Empresa empresa;
    JTable tabla;
    JButton cerrar;
    JButton borrar;

    String[][] datos;
    String[] columnas = {"Identificador", "DNI", "Nombre", "Apellidos", "Dirección", "Teléfono", "Puesto"};

    public BajaDialog(Empresa empresa) {
        this.empresa = empresa;

        setTitle("Listado Trabajadores");
        setSize(750, 700);
        setLayout(new FlowLayout());
        setLocationRelativeTo(null);
        setResizable(false);

        try {
            datos = AccesoTrabajador.listarTrabajadores();
        } catch (BDException e) {
            throw new RuntimeException(e);
        }

        tabla = new JTable(datos, columnas);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane jsp = new JScrollPane(tabla);
        jsp.setPreferredSize(new Dimension(700, 600));
        add(jsp);

        borrar = new JButton("Borrar");
        borrar.addActionListener(this);
        add(borrar);

        cerrar = new JButton("Cerrar");
        cerrar.addActionListener(this);
        add(cerrar);

        setVisible(true);
    }

    private void recargarTabla() {
        try {
            datos = AccesoTrabajador.listarTrabajadores();
            tabla.setModel(
                    new javax.swing.table.DefaultTableModel(datos, columnas)
            );
        } catch (BDException e) {
            JOptionPane.showMessageDialog(
                    this,
                    "Error al recargar la tabla"
            );
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        if (e.getSource() == cerrar) {
            dispose();
            return;
        }

        if (e.getSource() == borrar) {

            int fila = tabla.getSelectedRow();

            if (fila == -1) {
                JOptionPane.showMessageDialog(this, "Selecciona un trabajador");
                return;
            }

            int id = Integer.parseInt(datos[fila][0]);

            int opcion = JOptionPane.showConfirmDialog(
                    this,
                    "Seguro de eliminar este trabajador?",
                    "Confirmar borrado",
                    JOptionPane.YES_NO_OPTION
            );

            if (opcion == JOptionPane.YES_OPTION) {

                try {
                    boolean ok = empresa.bajaTrabajador(id);

                    if (ok) {
                        JOptionPane.showMessageDialog(this, "Trabajador eliminado");
                        recargarTabla();

                    } else {
                        JOptionPane.showMessageDialog(this, "No existe el trabajador");
                    }

                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Error al eliminar");
                }
            }
        }
    }
}