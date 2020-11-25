package gui;

import java.awt.BorderLayout;
import java.awt.Choice;
import java.awt.Color;
import java.awt.Dialog.ModalityType;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.util.ArrayList;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.GroupLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.RowFilter;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

import obj.Conector;

public class VentanaPrincipal extends JFrame
{
	private Choice bdSel, tablaSel;
	private JButton agregar, cambiarUsuario, ajustes, squirtle;
	private JPanel topbar, panelTabla;
	private JScrollPane sc;
	private JTable tabla;
	private ArrayList<String> filtradas;
	public Conector con;
	private JTextField filtro;
	private TableRowSorter tbfiltro;
	
	public VentanaPrincipal(Conector c) throws Exception
	{
		con = c;
		iniciarComponentes();	
	}
	
	public void iniciarComponentes() throws SQLException
	{
		setTitle("Squirtle");
		setSize(new Dimension(1000, 680));
		setMinimumSize(new Dimension(800, 400));
		setIconImage(new ImageIcon("src/img/icon32x.png").getImage());
		setResizable(true);
		setLayout(new BorderLayout(0, 5));
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		
		topbar= new JPanel();
		panelTabla= new JPanel();
		
		
		bdSel= new Choice();
		tablaSel= new Choice();		
		filtro = new JTextField("Filtrar por clave primaria", 70);	
		bdSel.setFont(new Font("Calibri light", Font.PLAIN, 15));
		tablaSel.setFont(new Font("Calibri light", Font.PLAIN, 15));
		filtro.setFont(new Font("Calibri light", Font.PLAIN, 15));
		filtro.setMaximumSize(new Dimension(0, 25));
		
		
		agregar= crearBoton("agregar.png", "agregarHover.png", "agregarPressed.png", "Agregar un registro a la tabla");
		cambiarUsuario= crearBoton("cambiarUsuario.png", "cambiarUsuarioHover.png", "cambiarUsuarioPressed.png", "Iniciar sesión de SQL con otro usuario");
		ajustes= crearBoton("ajustes.png", "ajustesHover.png", "ajustesPressed.png", "Ajustes");
		squirtle= crearBoton("squirtle squad leader.png", "tilt.png", null, null);
		
		
		filtradas= new ArrayList();
		filtradas.add("schema");
		filtradas.add("mysql");
		filtradas.add("sys");
		filtradas.add("test");
		
		
		rellenarDbDisponibles();		
		dbSeleccionada();

		
		topbar.setLayout(new BoxLayout(topbar, BoxLayout.X_AXIS));
		topbar.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));	//top, left, bottom, right
		
		topbar.add(bdSel);
		topbar.add(Box.createRigidArea(new Dimension(10, 0)));
		
		topbar.add(tablaSel);
		topbar.add(Box.createRigidArea(new Dimension(40, 0)));
		
		topbar.add(filtro);
		topbar.add(Box.createRigidArea(new Dimension(40, 0)));
		
		topbar.add(agregar);
		topbar.add(Box.createRigidArea(new Dimension(10, 0)));
		
		topbar.add(cambiarUsuario);	
		topbar.add(Box.createRigidArea(new Dimension(30, 0)));
		
		topbar.add(ajustes);
		topbar.add(Box.createRigidArea(new Dimension(30, 0)));
		
		topbar.add(squirtle);
		
		
		add(topbar, BorderLayout.NORTH);
		add(panelTabla, BorderLayout.CENTER);
		pack();
		setVisible(true);
		setLocationRelativeTo(null);
		
		
		bdSel.addItemListener(new ItemListener() 
		{
			public void itemStateChanged(ItemEvent ie)
			{
				dbSeleccionada();
			}
		});
		
		
		tablaSel.addItemListener(new ItemListener() 
		{
			public void itemStateChanged(ItemEvent ie)
			{
				dibujarTabla(tablaSel.getSelectedItem(), bdSel.getSelectedItem());
			}
		});
		
		
		
		filtro.addFocusListener(new FocusListener() 
		{	
			public void focusLost(FocusEvent e) 
			{
				filtro.setText("Filtrar por clave primaria");
			}
			public void focusGained(FocusEvent e) 
			{
				filtro.setText("");	
			}
		});
		
		
		filtro.addKeyListener(new KeyAdapter()
		{
			public void keyReleased(KeyEvent e) 
			{
				tbfiltro = new TableRowSorter(tabla.getModel());
				tabla.setRowSorter(tbfiltro);
				tbfiltro.setRowFilter(RowFilter.regexFilter("(?i)"+filtro.getText(), 0));		
			}
		});
		
		
		agregar.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent arg0) 
			{
				new VRegistros(VentanaPrincipal.this, con, tablaSel.getSelectedItem(), null);
			}
			
		});

		
		cambiarUsuario.addActionListener(new ActionListener()			
		{
			public void actionPerformed(ActionEvent arg0) 
			{
				dispose();
				con.closeConnection();
				MInicioSesion a= new MInicioSesion();
			}
		});

		
		ajustes.addActionListener(new ActionListener() 
		{	
			public void actionPerformed(ActionEvent arg0) 
			{
				ventanaFiltro();
			}
		});
	}

	
	private void rellenarDbDisponibles() throws SQLException 
	{
		String query= "select schema_name from information_schema.schemata where ";
		
		for(int i= 0; i < filtradas.size(); i++)
		{
			if(i == 0)
				query+= "schema_name NOT LIKE '%" + filtradas.get(i) + "' ";
			else
				query+= "AND schema_name NOT LIKE '%" + filtradas.get(i) + "' ";
				
		}
		
		ArrayList<String> aux = con.getQuery(query, 1);

		bdSel.removeAll();
		for (int i= 0; i < aux.size(); i++)
		{
			bdSel.addItem(aux.get(i));	
		}
	}

	
	private JButton crearBoton(String ruta0, String ruta1, String ruta2, String string) 
	{
		JButton boton= new JButton(new ImageIcon("src/img/" + ruta0));
		boton.setContentAreaFilled(false);
		boton.setBorder(null);
		
		if(string != null)
		{
			boton.setToolTipText(string);
			boton.setPressedIcon(new ImageIcon("src/img/" + ruta2));
		}
		
		boton.setRolloverIcon(new ImageIcon("src/img/" + ruta1));
		
		return boton;
	}

	
	public void dbSeleccionada() 
	{
		try 
		{
			con.usarBD(bdSel.getSelectedItem());
			tablaSel.removeAll();
			
			ArrayList<String> a= con.getQuery("show tables", 1);	
			for (int i= 0; i < a.size(); i++)
			{
				tablaSel.addItem(a.get(i));
			}
			
			dibujarTabla(tablaSel.getSelectedItem(), bdSel.getSelectedItem());
		}
		
		catch (Exception e) 
		{
			JOptionPane.showMessageDialog(null, "No se pudo recuperar la bases de datos seleccionada");
		}
	}

	
	public void dibujarTabla(String tablaNom, String bdNom) 
	{
		
		DefaultTableModel modelo= new DefaultTableModel() 
		{
			public boolean isCellEditable(int row,int column){return false;}
		};
			
		ArrayList<ArrayList<String>> datos = null;
		ArrayList<String> columnas = null;
		tabla= new JTable(modelo);
		
		try 
		{
			datos = con.imprimirTabla(tablaNom, bdNom);
			columnas = con.getQuery("Desc " + tablaNom, 1);
			panelTabla.remove(sc);
		} 
		catch (SQLException e) 
		{
			modelo.addColumn("TABLA NO ENCONTRADA");
		}
		catch (Exception c) 
		{
			
		}
		
		for (int i = 0; i < datos.size(); i++) 
		{
			modelo.addColumn(columnas.get(i),datos.get(i).toArray());	
		}
		
		tabla.getTableHeader().setReorderingAllowed(false);
		tabla.getTableHeader().setResizingAllowed(false);
		tabla.addMouseListener(modificarRegistro);
		tabla.setFont(new Font("Calibri light", Font.PLAIN, 14));
	
		sc = new JScrollPane(tabla);
		panelTabla.setLayout(new BoxLayout(panelTabla, BoxLayout.Y_AXIS));
		panelTabla.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
		panelTabla.add(sc);
		
		setPreferredSize(getSize()); //Pack() redimensiona al tamaño predilecto
		pack();
	}
	
	
	private void ventanaFiltro()
	{
		final JDialog ventana = new JDialog(this, "Ajustes");
		GroupLayout layout= new GroupLayout(ventana.getContentPane());
		ventana.setModalityType(ModalityType.APPLICATION_MODAL);	
		ventana.setSize(350, 300);
		ventana.setLayout(layout);
		ventana.setResizable(false);
		ventana.setLocationRelativeTo(this);
		
		
		JLabel label= new JLabel("Agregue las bases de datos a filtrar (uno por línea):");
		final JTextArea campo= new JTextArea();
		JButton guardar= new JButton("Guardar");
		JScrollPane sc= new JScrollPane(campo);
		
		
		label.setFont(new Font("Calibri light", Font.PLAIN, 15));		
		guardar.setFont(new Font("Calibri light", Font.PLAIN, 20));
		guardar.setBackground(Color.cyan);
		
		
		for(int i= 0; i < filtradas.size(); i++)
			
		{
			campo.setText(filtradas.get(i) + "\n" + campo.getText());
		}
		
		
		guardar.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent arg0) 
			{
				String[] aux= campo.getText().split("\n");
				filtradas.clear();
				
				for(int i=0; i < aux.length; i++)
				{
					filtradas.add(aux[i]);
				}

				try 
				{
					rellenarDbDisponibles();
					dbSeleccionada();
					ventana.dispose();
				} 
				catch (SQLException e) 
				{		
					
				}		
			}
		});

		
		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);
		layout.setHorizontalGroup(
				layout.createSequentialGroup()
					.addGroup(layout.createParallelGroup()
							.addComponent(label)
								.addComponent(sc)
									.addComponent(guardar, GroupLayout.Alignment.CENTER)));
		layout.setVerticalGroup(
				layout.createSequentialGroup()
					.addComponent(label)
						.addComponent(sc)
							.addComponent(guardar));
		
		ventana.setVisible(true);
	}
	
	
	MouseAdapter modificarRegistro = new MouseAdapter() 
	{
		public void mouseClicked(MouseEvent e) 
		{
			if (e.getClickCount() == 2) 
			{
				new VRegistros(VentanaPrincipal.this, con, tablaSel.getSelectedItem(), VRegistros.insert(tabla.getValueAt(tabla.getSelectedRow(),0).toString()));	
				dibujarTabla(tablaSel.getSelectedItem(), bdSel.getSelectedItem());
			}
		};
	};
}