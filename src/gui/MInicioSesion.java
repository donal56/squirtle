package gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import obj.Conector;

public class MInicioSesion 
{
	private static JDialog dialog;

	public static void main(String[] args) 
	{
		new MInicioSesion();
	}

	public MInicioSesion()
	{
		dialog= new JDialog(dialog, "Inicio de sesión");
		dialog.setLayout(null);
		dialog.setResizable(false);
		dialog.setSize(380,260);
		dialog.setLocationRelativeTo(null);
		dialog.setIconImage(new ImageIcon("src/img/icon32x.png").getImage());
		dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

		
		final JTextField campo= new JTextField(1);
		final JPasswordField pass= new JPasswordField(1);
		JLabel etiq1= new JLabel("Usuario:");
		JLabel etiq2= new JLabel("Contraseña:");
		JButton boton= new JButton("Iniciar sesión");

		
		etiq1.setBounds(40,40,100,25);
		etiq2.setBounds(40,95,100,25);
		campo.setBounds(140,40,200,25);
		pass.setBounds(140,95,200,25);
		boton.setBounds(80,160,220,30);
		
		
		etiq1.setFont(new Font("Calibri light", Font.PLAIN, 18));
		etiq2.setFont(new Font("Calibri light", Font.PLAIN, 18));
		boton.setFont(new Font("Calibri light", Font.PLAIN, 20));
		boton.setBackground(Color.cyan);
		
		dialog.add(etiq1);	dialog.add(etiq2);	
		dialog.add(campo);	dialog.add(pass);		
		dialog.add(boton);	
		
		dialog.setVisible(true);
	
	    boton.addActionListener(new ActionListener()
	    {
	         public void actionPerformed(ActionEvent pE)
	         { 
	        	char[] p= pass.getPassword();
	     		String pass = String.valueOf(p);

				try 
				{
					Conector con= new Conector(campo.getText(), pass);
					dialog.dispose();
					new VentanaPrincipal(con);
				} 
				catch (Exception e) 
				{
					JOptionPane.showMessageDialog(null,"Usuario y/o contraseña incorrectos","Acceso denegado",JOptionPane.WARNING_MESSAGE);
				}
	         }
	     });
	}
}