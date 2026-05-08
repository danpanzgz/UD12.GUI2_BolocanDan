package dialogs;

import dao.AccesoTrabajador;
import exceptions.BDException;
import modelo.Empresa;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.regex.Pattern;

public class ListarDialog extends JDialog implements ActionListener {

    private static final String[] COLUMNAS = {"Identificador", "DNI", "Nombre", "Apellidos", "Direccion", "Telefono", "Puesto"};

    private final Empresa empresa;
    private JTable tabla;
    private JButton cerrar;
    private JButton filtrar;
    private JButton limpiar;
    private JTextField campoBuscar;
    private DefaultTableModel modeloTabla;
    private TableRowSorter<DefaultTableModel> sorter;

    public ListarDialog(Empresa empresa) {
        this.empresa = empresa;

        setResizable(false);
        setTitle("Listado Trabajadores");
        setSize(750, 700);
        setLayout(new FlowLayout());
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        JPanel panelFiltro = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelFiltro.add(new JLabel("Buscar"));

        campoBuscar = new JTextField(20);
        panelFiltro.add(campoBuscar);

        filtrar = new JButton("Filtrar");
        filtrar.setActionCommand("FILTRAR");
        filtrar.addActionListener(this);
        panelFiltro.add(filtrar);

        limpiar = new JButton("Limpiar");
        limpiar.setActionCommand("LIMPIAR");
        limpiar.addActionListener(this);
        panelFiltro.add(limpiar);

        add(panelFiltro);

        modeloTabla = new DefaultTableModel(new Object[0][0], COLUMNAS) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tabla = new JTable(modeloTabla);
        tabla.setAutoCreateRowSorter(true);
        sorter = new TableRowSorter<>(modeloTabla);
        tabla.setRowSorter(sorter);

        recargarTabla();

        JScrollPane jsp = new JScrollPane(tabla);
        jsp.setPreferredSize(new Dimension(700, 600));
        add(jsp);

        cerrar = new JButton("Cerrar");
        cerrar.addActionListener(this);
        add(cerrar);

        setVisible(true);
    }

    private void recargarTabla() {
        try {
            String[][] datos = AccesoTrabajador.listarTrabajadores();
            modeloTabla.setRowCount(0);

            for (String[] fila : datos) {
                modeloTabla.addRow(fila);
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
        }
    }
}
