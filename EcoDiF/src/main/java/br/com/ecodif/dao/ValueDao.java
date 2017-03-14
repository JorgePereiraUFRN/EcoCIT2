package br.com.ecodif.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.ejb.Stateless;

import br.com.ecodif.domain.Value;

@Stateless
public class ValueDao {


	
	private ValueDao(){
		
	}
	

	private Connection getConnection() {
		
		Connection con = null;

		try {
			
				String url = "jdbc:c*:datastax://localhost/valuesdb?consistencyLevel=ONE";

				Properties props = new Properties();
				props.setProperty("user", "cassandra");
				props.setProperty("password", "cassandra");

				Class.forName("com.github.cassandra.jdbc.CassandraDriver");
				con = DriverManager.getConnection(url, props);
		
				System.out.println("ecodif: open connection");
				
		} catch (Exception e) {
			e.printStackTrace();
		}
		return con;
	}

	/**
	 * Método responsável por encerrar a conexão com o banco de dados.
	 * 
	 * @param con
	 *            - conexão a ser encerrada.
	 * @param pst
	 *            - o statement que estava sendo executado a ser encerrado.
	 */
	private  void close(Connection con, PreparedStatement pst) {

		if (pst != null) {
			try {
				pst.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		if (con != null) {
			try {
				con.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		System.out.println("ecodif: close connection");
	}

	public void save(Value value) {
		Connection con = getConnection();
		PreparedStatement pst = null;
		String sql = "insert into values (at, data_id, value) values (?, ?, ?)";
		try {
			
			pst = con.prepareStatement(sql);
			pst.setDate(1, new Date(value.getAt().getTime()));
			pst.setInt(2, value.getDataId());
			pst.setString(3, value.getValue());

			pst.executeUpdate();
			
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			close(con, pst);
		}
	}

	public List<Value> findValuesByDataId(int dataID) {
		ResultSet rs = null;
		PreparedStatement pst = null;
		Connection con = getConnection();
		
		String sql = "Select * from values  where data_id = ? ORDER BY at DESC;";
		List<Value> values = new ArrayList<Value>();
		try {
			pst = con.prepareStatement(sql);
			pst.setInt(1, dataID);
			pst.execute();
			rs = pst.getResultSet();
			while (rs.next()) {
				Value value = new Value();

				value.setAt(rs.getDate("at"));
				value.setDataId(rs.getInt("data_id"));
				value.setValue(rs.getString("value"));

				values.add(value);
			}
			
			pst.close();
			con.close();

		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			close(con, pst);
		}
		return values;
	}

}
