package gui;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.ArrayList;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import obj.Conector;

public class VRegistros extends JDialog
{
	private VentanaPrincipal frame;
	private JPanel panel = new JPanel();
	private GridBagConstraints gbc= new GridBagConstraints();
	private ArrayList<String> datos, foreignKeys, datatypes;
	private ArrayList<String> consulta;
	public  Conector con;
	private String columnPK;
	private String tabla;
	private String nomPK;
	private JButton guardar = new JButton("Guardar");
	private JButton eliminar = new JButton("Eliminar");
    
	public VRegistros(VentanaPrincipal ventana ,Conector conexion ,String tb , String PK)  
	{
			super(ventana);
			setModalityType(ModalityType.APPLICATION_MODAL);
		
			frame= ventana;
			con = conexion;
			tabla = tb;
			nomPK = PK;
			
			setTitle("Modificar registro");
			panel.setLayout(new GridBagLayout());
			panel.setFont(new Font("Calibri light", Font.PLAIN, 15));
			
			gbc.insets=new Insets(10,10,10,10);
			gbc.anchor=GridBagConstraints.CENTER; 
			gbc.fill = GridBagConstraints.PAGE_START;
		    gbc.gridwidth=GridBagConstraints.REMAINDER;
		    panel.add(new JLabel(firstUpperCase(tabla)), gbc);

		    try 
		    {    	
		    	datos= con.getQuery("desc " + tabla , 1);
		    	datatypes= con.getQuery("desc " + tabla, 2);
		    	foreignKeys= con.getQuery("desc " + tabla , 4);
		    	
		    	columnPK = con.getQuery("SHOW INDEXES FROM "+ tabla + " WHERE Key_name = 'PRIMARY'", 5).get(0);
		    	consulta = con.consultarRegistro(tabla, columnPK, nomPK);
		    	
		    	for (int i = 0; i < datos.size(); i++) 
		    	{
		    		JLabel lb = new JLabel(firstUpperCase(datos.get(i)));
		    		addComponentes(lb, new JTextField(20), 1);
		    		lb.setToolTipText(datatypes.get(i));
		    		
		    		if(datos.get(i).equals(columnPK))
		    			lb.setForeground(Color.red);
		    		
		    		if(foreignKeys.get(i).equals("MUL"))
		    			lb.setForeground(Color.blue);
		    	}
		    	
		    	
		    	if (consulta != null & nomPK != null) 
		    	{	
		    		rellenarDatos();
		    		setFont();
		    		guardar.addActionListener(accionModificar);
		    		eliminar.addActionListener(accionEliminar);
		    	}
		    	else 
		    	{
		    		setFont();
		    		setTitle("Insertar registro");
		    		eliminar.setText("Limpiar");
		    		eliminar.addActionListener(accionLimpiar);
		    		guardar.addActionListener(accionAgregar);
		    	}	
		    } 
		    catch (SQLException e) 
		    {
				JOptionPane.showMessageDialog(null, "Error al consultar el registro");
			}
		    
		    JPanel botones =new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 5));
		    botones.add(guardar);
		    botones.add(eliminar);
		    panel.add(botones, gbc);
			  
			setSize(800,720);
			setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			add(panel);
			pack();
			setLocationRelativeTo(null);
			setVisible(true);
	}

	private void rellenarDatos() throws SQLException 
	{
		for (int i = 0, k = 0 ; k < consulta.size(); i++) 
		{
			if (panel.getComponent(i) instanceof JTextField) 
			{
				((JTextField) panel.getComponent(i)).setText(consulta.get(k));
				
		    	k++;
			}
		}
	}

	private void setFont()
	{
		for (int i = 0; i < panel.getComponentCount(); i++) 
		{
			if (panel.getComponent(i) instanceof JLabel) 
			{
				panel.getComponent(i).setFont(null);
			}
		}
	}
	
	
	private void addComponentes(JLabel lb , JTextField txt, int pos) 
	{
        gbc.gridwidth=1;
        panel.add(lb, gbc);
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        panel.add(txt, gbc);	
	}
	
	
	private String firstUpperCase(String word) 
	{
		return word.substring(0, 1).toUpperCase() + word.substring(1); 
	}
	
	
	public static String insert(String word) 
	{
		return ("'" + word + "'").trim();
	}
	
	ActionListener accionAgregar = new ActionListener() 
	{
		public void actionPerformed(ActionEvent arg0) 
		{
			String query ="";
			int i = 0 ,k = 0;
	    	
			while (k < con.numeroDeColumnas(tabla)) 
			{
				if (panel.getComponent(i) instanceof JTextField) 
				{
					if (k!=0) 
						query += " , ";
						
					query += insert(((JTextField) panel.getComponent(i)).getText());
			    	k++;  	
				}
				i++;
			}		
			
			try 
			{
				con.setQuery("Insert Into " + tabla + " Values("+ query + " )");
				JOptionPane.showMessageDialog(null, "Registro agregado exitosamente.");
				frame.dibujarTabla(tabla, con.getBD());
				dispose();
			} 
			catch (SQLException e2) 
			{
				 JOptionPane.showMessageDialog(null, "Error de guardado: " + "\n" + e2);
			}
		}
	 };
	 
	 
	 ActionListener accionModificar = new ActionListener() 
	 {
			public void actionPerformed(ActionEvent arg0) 
			{
				String query ="";
				int i = 0 ,k = 0;
				
				while (k < con.numeroDeColumnas(tabla)) 
				{
					if (panel.getComponent(i) instanceof JTextField) 
					{
						if (k!=0) {query += " , ";}
						query += ((JLabel) panel.getComponent(i-1)).getText() +" = "+ insert(((JTextField) panel.getComponent(i)).getText());
				    	k++;  	
					}
					i++;
				}			
				
				try 
				{
					con.setQuery("UPDATE " + tabla + " SET "+ query + " WHERE " + columnPK + " = " + nomPK);
					JOptionPane.showMessageDialog(null, "Registro modificado exitosamente.");
					dispose();
				} 
				catch (SQLException e) 
				{
					 JOptionPane.showMessageDialog(null, "Error de guardado: " + "\n" + "Otros registros dependen de este.","Error",JOptionPane.ERROR_MESSAGE);	
				}
			}
			 
		 };
		 
	ActionListener accionLimpiar= new ActionListener()
	{
		public void actionPerformed(ActionEvent arg0) 
		{
			for (int i = 0; i < panel.getComponentCount(); i++) 
			{
				if (panel.getComponent(i) instanceof JTextField) 
				{
					((JTextField) panel.getComponent(i)).setText("");
				}
			}
		}
	};

	
	ActionListener accionEliminar = new ActionListener() 
	{
		public void actionPerformed(ActionEvent arg0) 
		{
			try 
			{
				con.setQuery("DELETE FROM " + tabla + " WHERE " + columnPK + " = " + nomPK);
				JOptionPane.showMessageDialog(null, "Registro eliminado exitosamente.");
				dispose();
			} 
			catch (SQLException e4) 
			{
				 JOptionPane.showMessageDialog(null, "Error:\n" + e4 , "Error", JOptionPane.ERROR_MESSAGE);	
			}
		} 
	};
}