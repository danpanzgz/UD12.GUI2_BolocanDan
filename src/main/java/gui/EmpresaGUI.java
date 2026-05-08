package gui;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;

import dao.AccesoTrabajador;
import dialogs.AltaDialog;
import dialogs.BajaDialog;
import dialogs.ListarDialog;
import dialogs.ModificarDialog;
import exceptions.BDException;
import ficheros.FicheroDatos;
import modelo.Empresa;
import modelo.Trabajador;

public class EmpresaGUI extends JFrame implements ActionListener {

    Empresa empresa;

    JButton altaTrabajador;
    JButton bajaTrabajador;
    JButton modificaTrabajador;
    JButton buscaTrabajador;
    JButton listarTrabajadores;
    JButton salir;

    public EmpresaGUI() {
        super("Gestion de personal");

        setSize(800, 750);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new GridLayout(3, 2));
        setLocationRelativeTo(null);

        inicializarDatos();

        altaTrabajador = new JButton("Anadir Trabajador");
        altaTrabajador.addActionListener(this);
        altaTrabajador.setIcon(new ImageIcon("images/addUser.png"));
        add(altaTrabajador);

        bajaTrabajador = new JButton("Borrar Trabajador");
        bajaTrabajador.addActionListener(this);
        bajaTrabajador.setIcon(new ImageIcon("images/removeUser.png"));
        add(bajaTrabajador);

        modificaTrabajador = new JButton("Modificar Trabajador");
        modificaTrabajador.addActionListener(this);
        modificaTrabajador.setIcon(new ImageIcon("images/editUser.png"));
        add(modificaTrabajador);

        buscaTrabajador = new JButton("Buscar Trabajador");
        buscaTrabajador.addActionListener(this);
        buscaTrabajador.setIcon(new ImageIcon("images/searchUser.png"));
        add(buscaTrabajador);

        listarTrabajadores = new JButton("Listar Trabajadores");
        listarTrabajadores.addActionListener(this);
        listarTrabajadores.setIcon(new ImageIcon("images/list.png"));
        add(listarTrabajadores);

        salir = new JButton("Salir");
        salir.addActionListener(this);
        salir.setIcon(new ImageIcon("images/exit.png"));
        add(salir);

        setVisible(true);
    }

    private void inicializarDatos() {
        ArrayList<Trabajador> lista =
                FicheroDatos.obtenerTrabajadores("ficheroDatos\\empresa.dat");

        empresa = new Empresa(lista);

        try {
            AccesoTrabajador.insertar(lista);
        } catch (BDException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        if (e.getSource() == altaTrabajador) {
            new AltaDialog(empresa);
        }

        if (e.getSource() == bajaTrabajador) {
            new BajaDialog(empresa);
        }

        if (e.getSource() == modificaTrabajador) {
            new ModificarDialog(empresa);
        }

        if (e.getSource() == buscaTrabajador) {
            new ListarDialog(empresa);
        }

        if (e.getSource() == listarTrabajadores) {
            new ListarDialog(empresa);
        }

        if (e.getSource() == salir) {

            try {
                String[][] datos = AccesoTrabajador.listarTrabajadores();
                ArrayList<Trabajador> listaActualizada = new ArrayList<>();

                for (String[] fila : datos) {
                    Trabajador t = new Trabajador(
                            Integer.parseInt(fila[0]),
                            fila[1],
                            fila[2],
                            fila[3],
                            fila[4],
                            fila[5],
                            fila[6]
                    );
                    listaActualizada.add(t);
                }

                FicheroDatos.escribirTrabajadores(
                        "ficheroDatos\\empresa.dat",
                        listaActualizada
                );

            } catch (BDException ex) {
                ex.printStackTrace();
            }

            System.exit(0);
        }
    }

    public static void main(String[] args) {
        new EmpresaGUI();
    }
}
