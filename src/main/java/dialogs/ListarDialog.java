package dialogs;

import dao.AccesoTrabajador;
import exceptions.BDException;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileWriter;
import java.io.IOException;
import java.util.regex.Pattern;

public class ListarDialog extends JDialog implements ActionListener {

    private static final String[] COLUMNAS = {"Identificador", "DNI", "Nombre", "Apellidos", "Direccion", "Telefono", "Puesto"};

    private JTable tabla;
    private JButton cerrar;
    private JButton filtrar;
    private JButton limpiar;
    private JButton exportarCsv;
    private JButton exportarJson;
    private JTextField campoBuscar;
    private DefaultTableModel modeloTabla;
    private TableRowSorter<DefaultTableModel> sorter;

    public ListarDialog() {
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

        exportarCsv = new JButton("Exportar CSV");
        exportarCsv.setActionCommand("EXPORTAR_CSV");
        exportarCsv.addActionListener(this);
        panelFiltro.add(exportarCsv);

        exportarJson = new JButton("Exportar JSON");
        exportarJson.setActionCommand("EXPORTAR_JSON");
        exportarJson.addActionListener(this);
        panelFiltro.add(exportarJson);

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

        if ("EXPORTAR_CSV".equals(e.getActionCommand())) {
            exportarVistaCSV();
            return;
        }

        if ("EXPORTAR_JSON".equals(e.getActionCommand())) {
            exportarVistaJSON();
            return;
        }

        if (e.getSource() == cerrar) {
            dispose();
        }
    }

    private void exportarVistaCSV() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Exportar CSV");
        int seleccion = chooser.showSaveDialog(this);
        if (seleccion != JFileChooser.APPROVE_OPTION) {
            return;
        }

        try (FileWriter writer = new FileWriter(chooser.getSelectedFile().getAbsolutePath())) {
            writer.append(String.join(",", COLUMNAS)).append("\n");

            for (int i = 0; i < tabla.getRowCount(); i++) {
                int filaModelo = tabla.convertRowIndexToModel(i);
                for (int c = 0; c < COLUMNAS.length; c++) {
                    Object v = modeloTabla.getValueAt(filaModelo, c);
                    String s = v == null ? "" : v.toString();
                    // CSV simple (por si hay comas, encapsulamos con comillas)
                    if (s.contains(",") || s.contains("\"") || s.contains("\n")) {
                        s = "\"" + s.replace("\"", "\"\"") + "\"";
                    }
                    writer.append(s);
                    if (c < COLUMNAS.length - 1) {
                        writer.append(",");
                    }
                }
                writer.append("\n");
            }

            JOptionPane.showMessageDialog(this, "Exportacion CSV completada");
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Error al escribir el archivo CSV", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void exportarVistaJSON() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Exportar JSON");
        int seleccion = chooser.showSaveDialog(this);
        if (seleccion != JFileChooser.APPROVE_OPTION) {
            return;
        }

        StringBuilder json = new StringBuilder();
        json.append("[\n");

        for (int i = 0; i < tabla.getRowCount(); i++) {
            int filaModelo = tabla.convertRowIndexToModel(i);
            json.append("  {\n");
            json.append("    \"id\": ").append(escapeJsonNum(modeloTabla.getValueAt(filaModelo, 0))).append(",\n");
            json.append("    \"dni\": \"").append(escapeJsonStr(modeloTabla.getValueAt(filaModelo, 1))).append("\",\n");
            json.append("    \"nombre\": \"").append(escapeJsonStr(modeloTabla.getValueAt(filaModelo, 2))).append("\",\n");
            json.append("    \"apellido\": \"").append(escapeJsonStr(modeloTabla.getValueAt(filaModelo, 3))).append("\",\n");
            json.append("    \"direccion\": \"").append(escapeJsonStr(modeloTabla.getValueAt(filaModelo, 4))).append("\",\n");
            json.append("    \"telefono\": \"").append(escapeJsonStr(modeloTabla.getValueAt(filaModelo, 5))).append("\",\n");
            json.append("    \"puesto\": \"").append(escapeJsonStr(modeloTabla.getValueAt(filaModelo, 6))).append("\"\n");
            json.append("  }");
            if (i < tabla.getRowCount() - 1) {
                json.append(",");
            }
            json.append("\n");
        }

        json.append("]");

        try (FileWriter writer = new FileWriter(chooser.getSelectedFile().getAbsolutePath())) {
            writer.write(json.toString());
            JOptionPane.showMessageDialog(this, "Exportacion JSON completada");
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Error al escribir el archivo JSON", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private static String escapeJsonStr(Object v) {
        if (v == null) {
            return "";
        }
        String s = v.toString();
        return s.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }

    private static String escapeJsonNum(Object v) {
        if (v == null) {
            return "0";
        }
        String s = v.toString().trim();
        if (s.isEmpty()) {
            return "0";
        }
        return s;
    }
}
