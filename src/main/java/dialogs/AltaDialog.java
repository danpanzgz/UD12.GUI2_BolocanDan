/**
 * 
 */
package dialogs;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import modelo.Empresa;
import modelo.Trabajador;
import dao.AccesoTrabajador;
import exceptions.BDException;

/**
 * 
 * @author usuario
 *
 */
public class AltaDialog extends JDialog implements ActionListener, ItemListener {

	/**
	 * Elementos del JFrame
	 */
	JLabel etiquetaDni;
	JTextField areaDni;
	JLabel etiquetaNombre;
	JTextField areaNombre;
	JLabel etiquetaApellidos;
	JTextField areaApellidos;
	JLabel etiquetaDireccion;
	JTextField areaDireccion;
	JLabel etiquetaTelefono;
	JTextField areaTelefono;
	JLabel etiquetaPuesto;
	JComboBox comboPuesto;
	JButton aceptar;
	JButton cancelar;
	/**
	 * Variables a las que se pasar� el contenido de los JTextField y del combo box
	 */
	String dni = "";
	String nombre = "";
	String apellidos = "";
	String direccion = "";
	String telefono = "";
	String puesto = "";

	JPanel pDni;
	JPanel pNombre;
	JPanel pApellidos;
	JPanel pDireccion;
	JPanel pTelefono;
	JPanel pPuesto;
	JPanel pBotones;

	Empresa empresa;

	public AltaDialog(Empresa empresa) {
		this.empresa = empresa;
		setResizable(false);
		// t�tulo del di�log
		setTitle("Alta Trabajador");
		setSize(300, 350);
		setLayout(new FlowLayout());

		setLocationRelativeTo(null);

		// una fila por JPanel
		pDni = new JPanel();
		pNombre = new JPanel();
		pApellidos = new JPanel();
		pDireccion = new JPanel();
		pTelefono = new JPanel();
		pPuesto = new JPanel();
		pBotones = new JPanel();



		// Se crean los elementos y se añaden
		etiquetaDni = new JLabel("DNI                 ");
		areaDni = new JTextField(15);
		// Se añaden al JPanel
		pDni.add(etiquetaDni);
		pDni.add(areaDni);

		// Se crean los elementos y se añaden
		etiquetaNombre = new JLabel("Nombre         ");
		areaNombre = new JTextField(15);
		// Se añaden al JPanel
		pNombre.add(etiquetaNombre);
		pNombre.add(areaNombre);

		// Se crean los elementos y se a�aden
		etiquetaApellidos = new JLabel("Apellidos      ");
		areaApellidos = new JTextField(15);
		// Se añaden al JPanel
		pApellidos.add(etiquetaApellidos);
		pApellidos.add(areaApellidos);

		// Se crean los elementos y se añaden
		etiquetaDireccion = new JLabel("Direccion      ");
		areaDireccion = new JTextField(15);
		// Se añaden al JPanel
		pDireccion.add(etiquetaDireccion);
		pDireccion.add(areaDireccion);

		// Se crean los elementos y se a�aden
		etiquetaTelefono = new JLabel("Telefono       ");
		areaTelefono = new JTextField(15);
		// Se añaden al JPanel
		pTelefono.add(etiquetaTelefono);
		pTelefono.add(areaTelefono);

		// Se crean los elementos y se añaden
		etiquetaPuesto = new JLabel("Puesto                         ");
		pPuesto.add(etiquetaPuesto);
		// lista desplegable
		comboPuesto = new JComboBox();
		comboPuesto.addItem("Elija Puesto");
		comboPuesto.addItem("Programador");
		comboPuesto.addItem("Analista");
		comboPuesto.addItem("Arquitecto");
		comboPuesto.addItem("Jefe de Proyecto");
		comboPuesto.addItemListener(this);
		pPuesto.add(comboPuesto);

		// Añadir al JDialog los JPanel
		add(pDni);
		add(pNombre);
		add(pApellidos);
		add(pDireccion);
		add(pTelefono);
		add(pPuesto);

		aceptar = new JButton("Aceptar");
		aceptar.addActionListener(this);
		pBotones.add(aceptar);

		cancelar = new JButton("Cancelar");
		cancelar.addActionListener(this);
		pBotones.add(cancelar);

		add(pBotones);

		// Visible
		setVisible(true);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
	}

	@Override
	public void itemStateChanged(ItemEvent e) {
		// TODO Auto-generated method stub
		puesto = comboPuesto.getSelectedItem().toString();
	}

	@Override
	public void actionPerformed(ActionEvent e) {

		if (e.getSource() == aceptar) {
			try {
				dni = areaDni.getText().trim();
				nombre = areaNombre.getText().trim();
				apellidos = areaApellidos.getText().trim();
				direccion = areaDireccion.getText().trim();
				telefono = areaTelefono.getText().trim();
				puesto = comboPuesto.getSelectedItem().toString();

				if (comprobarErrores()) {

					Trabajador t = new Trabajador(
							0,
							dni,
							nombre,
							apellidos,
							direccion,
							telefono,
							puesto
					);

					boolean insertado = AccesoTrabajador.altaTrabajador(t);

					if (insertado) {
						empresa.getTrabajadores().add(t);
						JOptionPane.showMessageDialog(
								null,
								"Datos introducidos correctamente"
						);
						dispose();

					} else {
						JOptionPane.showMessageDialog(
								null,
								"Ya existe un trabajador con ese DNI",
								"Error",
								JOptionPane.ERROR_MESSAGE
						);
					}
				}

			} catch (BDException ex) {
				JOptionPane.showMessageDialog(
						null,
						ex.getMessage(),
						"Error",
						JOptionPane.ERROR_MESSAGE
				);
			}

		} else if (e.getSource() == cancelar) {
			dispose();
		}
	}

	/**
	 * M�todo que comprueba si no hay ning�n campo vac�o o si la longitud de los
	 * campos es la correcta
	 * 
	 * @return
	 */
	public boolean comprobarErrores() {

		if (dni.equals("")) {
			JOptionPane.showMessageDialog(
					null,
					"Debe introducir el DNI",
					"Error",
					JOptionPane.ERROR_MESSAGE
			);
			return false;
		}

		if (!AccesoTrabajador.validarDNI(dni)) {
			JOptionPane.showMessageDialog(
					null,
					"El DNI no es válido",
					"Error",
					JOptionPane.ERROR_MESSAGE
			);
			return false;
		}

		if (nombre.equals("")) {
			JOptionPane.showMessageDialog(
					null,
					"Debe introducir el nombre",
					"Error",
					JOptionPane.ERROR_MESSAGE
			);
			return false;
		}

		if (apellidos.equals("")) {
			JOptionPane.showMessageDialog(
					null,
					"Debe introducir los apellidos",
					"Error",
					JOptionPane.ERROR_MESSAGE
			);
			return false;
		}

		if (direccion.equals("")) {
			JOptionPane.showMessageDialog(
					null,
					"Debe introducir la dirección",
					"Error",
					JOptionPane.ERROR_MESSAGE
			);
			return false;
		}

		if (telefono.equals("")) {
			JOptionPane.showMessageDialog(
					null,
					"Debe introducir el teléfono",
					"Error",
					JOptionPane.ERROR_MESSAGE
			);
			return false;
		}

		if (!AccesoTrabajador.validarTelefono(telefono)) {
			JOptionPane.showMessageDialog(
					null,
					"El teléfono no es válido",
					"Error",
					JOptionPane.ERROR_MESSAGE
			);
			return false;
		}

		if (comboPuesto.getSelectedIndex() == 0) {
			JOptionPane.showMessageDialog(
					null,
					"Debe seleccionar un puesto",
					"Error",
					JOptionPane.ERROR_MESSAGE
			);
			return false;
		}

		return true;
	}

}
