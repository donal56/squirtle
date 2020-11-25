/*Desde la versión 6.x se tira una excepcion de zona horaria debido a que el servidor de MySql usa un formato 
* diferente del que el conector espera, esto se arregla si cambiamos los parametros a la zonaUTC con
* jdbc:mysql://localhost:3306/fussa?useLegacyDatetimeCode=false&serverTimezone=UTC
*/

package obj;

import java.sql.Connection;
import java.sql.Statement;
import java.util.ArrayList;
import javax.swing.JOptionPane;
import java.sql.ResultSet;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Conector 
{
	private Connection conexion= null;
	private Statement declaracion= null;
	private ResultSet resultado= null;
	private String usuario;
	private String contraseña;
	private String bd;
	
	public Conector(String usu, String pass) throws Exception
	{
			usuario= usu;
			contraseña= pass;
			
			conexion= DriverManager.getConnection(
					"jdbc:mysql://localhost:3306/mysql?useLegacyDatetimeCode=false&serverTimezone=UTC", 
					usuario, contraseña);
	}
	
	
	public void usarBD(String basedatos) throws Exception
	{
		bd = basedatos;
		setQuery("use " + bd);
	}
	
	
    public ArrayList<String> getQuery(String query,int columna)  throws SQLException  
	{
		ArrayList<String> rows = new ArrayList<String>();
	
			declaracion = conexion.createStatement();
			resultado = declaracion.executeQuery(query);
			
			while(resultado.next())
			{
				rows.add(resultado.getString(columna));
			}
	
		return rows;
	}
	
    
	public void setQuery(String query) throws SQLException
	{
		declaracion=conexion.createStatement();
		declaracion.execute(query);   
	}
    
    
	public ArrayList<ArrayList<String>> imprimirTabla(String tabla, String bd) throws SQLException
    {
		ArrayList<ArrayList<String>> datos = new ArrayList<ArrayList<String>>();
    	for (int i= 0; i < numeroDeColumnas(tabla); i++)
    	{
    		datos.add(getQuery("Select * From "+tabla, i+1));
    	}
		return datos;
    }
	
	
	public ArrayList<String> consultarRegistro(String tabla,String columnPK,String nomPK) throws SQLException
    {
		ArrayList<String> datos = new ArrayList<String>();
		try 
		{
			for (int i= 0; i < numeroDeColumnas(tabla); i++)
	    	{
	    		datos.add(getQuery("Select * From "+ tabla + " Where " + columnPK + " = "+nomPK, i+1).get(0));    	
	    	}
		} 
		catch (Exception e) 
		{
			return null;
		}
		
		return datos;
    }
    
    
    public int numeroDeColumnas(String tabla) 
    {
    	int noCol= 0;
		try {
			noCol = Integer.parseInt(getQuery("select count(*)  from information_schema.columns where  table_name= '" + tabla + "' "
												+ "AND table_schema= '" + bd + "'",1).get(0));
		} 
		catch (SQLException e) 
		{	
			e.printStackTrace();
		}
    	return noCol;
    }
    
    public String getBD()
    {
    	return bd;
    }
    
	public void closeConnection() 
	{
		conexion=null;
		JOptionPane.showMessageDialog(null, "Sesión finalizada", "Conexión terminada.", JOptionPane.WARNING_MESSAGE);
	}		
}