
package com.tech5.db;


import java.util.logging.Logger;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.text.MessageFormat;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import com.tech5.models.Habito;
import com.tech5.models.Usuario;

public class HabitoDAOImplem extends HabitoDAO {
	private static Logger logger = Logger.getLogger("UsuarioBBDAOImpl");
	
	private static HabitoDAOImplem instance = null;

	public static HabitoDAOImplem getInstance() {
		if (instance == null) {
			instance = new HabitoDAOImplem();
		}
		return instance;
	}
	
	
	
	//get{id}-obtener un habito por hid
	public Habito getHabito(int hid) {
		Habito habitoADevolver = null;
		try {
			Connection conn = this.datasource.getConnection();
			// ordenes sql
			String sql = "SELECT h.* FROM techfit.habito h WHERE h.hId=? LIMIT 1 "; 
			PreparedStatement pstm = conn.prepareStatement(sql);
			pstm.setInt(1, hid);

			ResultSet rs = pstm.executeQuery();
			if (rs.next()) {

				habitoADevolver = new Habito(
						rs.getInt("hid"), 
						rs.getString("titulo"),
						rs.getString("descripcion"),
						rs.getDate("fechaI"),
						rs.getInt("progreso"),
						rs.getInt("estado")
						
						);
			}

			pstm.close();
			conn.close();
			
			logger.info("Conexi�n exitosa getHabito");

		} catch (Exception e) {
			logger.severe("Error en la conexi�n de BBDD:" + e);
			habitoADevolver = null;
		}

		return habitoADevolver;

	}
	
	//GET -Obtener lista de habitos x id usuario:
	public List<Habito> getHabitoxUser(Usuario user) {
		List<Habito> habitListADevolver = new ArrayList<Habito>();
		
		//listADevolver.add(new Habito(1, "correr", "correr 10km al dia", 30, 30, null, uid));
		//listADevolver.add(new Habito(2, "verdura", "comer 5 verduras al dia", 50, 30, null, uid));
		//listADevolver.add(new Habito(3, "agua", "beber 2L de agua", 40, 40, null, uid));
		
	try {
		Connection conn = this.datasource.getConnection();

		String sql = "SELECT h.* FROM techfit.habito h LEFT JOIN techfit.usuario u ON h.usuario=u.uId WHERE u.uId=?";
		java.sql.PreparedStatement pstm = conn.prepareStatement(sql);
		pstm.setInt(1, user.getUid());
			ResultSet rs = pstm.executeQuery();

			while  (rs.next()) {

				habitListADevolver.add( new Habito(
						rs.getInt("hid"), 
						rs.getString("titulo"),
						rs.getString("descripcion"),
						rs.getDate("fechaI"),
						rs.getInt("progreso"),
						rs.getInt("estado")
						
						));
			}

			pstm.close();

			conn.close();

			logger.info("Conexi�n exitosa: getUserHabito");

		} catch (Exception e) {
			logger.severe("Error en la conexi�n de BBDD:" + e);
			habitListADevolver= null;
	}

		return habitListADevolver;
	}

	

	/*
	 //GET obtener la lista de habitos
	 @Override
	public List<Habito> getHabitoList() {
		// TODO Auto-generated method stub
		return null;
	}*/
	
	//POST insertar nuevo habito a la lista de habitos de un usuario
	@Override
	public boolean insertHabito(Habito newHabito) throws Exception {
		boolean estaInsertado = false;
		PreparedStatement pstm = null;
		Connection conn = null;
		
		try {
			conn = this.datasource.getConnection();
			String sql = "INSERT INTO techfit.habito ('hid', 'titulo', 'descripcion', 'fechaI', 'progreso','estado') VALUES (?,?,?,?,?,?)";
			pstm = conn.prepareStatement(sql);
			
			pstm.setInt(1,newHabito.getHid()); 
			pstm.setString(2,newHabito.getTitulo());
			pstm.setString(3,newHabito.getDescripcion());
			pstm.setDate(4,(Date) newHabito.getFechaI());
			pstm.setInt(5,newHabito.getProgreso());
			pstm.setInt(6,newHabito.getEstado());

			
			// execute the preparedstatement
			int rows = pstm.executeUpdate();
			if (pstm.getUpdateCount() == 0) {
				throw new Exception(MessageFormat.format("Nigun Objeto insertado \"{0}\"", sql));
			} else {
				estaInsertado = true;
				logger.info("Conexi�n exitosa insertHabito");
			}
			pstm.close();
			conn.close();
			logger.info("Inserci�n exitosa");
			estaInsertado = rows > 0 ? true : false;
		} catch (Exception e) {
			logger.severe("Error en la conexi�n de BBDD:" + e);
		} 
		return estaInsertado;

	}

	
	//PUT{id} actualizar datos de un habito
	@Override
	public boolean updateHabito(int hid,Habito habito) throws Exception {
		boolean estaActualizado = false;
		PreparedStatement pstm = null;
		Connection conn = null;

		conn = this.datasource.getConnection();
		String sql = "UPDATE techfit.habito SET " + "titulo=?, " + "descripcion=?, " + "fechaI=?, "
				+ "progreso=?, " + "estado=? " + "WHERE hId=?";
		
		pstm = conn.prepareStatement(sql);
		pstm.setString(2,habito.getTitulo());
		pstm.setString(3,habito.getDescripcion());
		pstm.setDate(4,(Date) habito.getFechaI());
		pstm.setInt(5,habito.getProgreso());
		pstm.setInt(6,habito.getEstado());
		
		
		pstm.executeUpdate();
		
		try {
			if (pstm.getUpdateCount() == 0) {
				throw new Exception(MessageFormat.format("Nigun Objeto esta actualizado \"{0}\"", sql));
			} else {
				estaActualizado = true;
				logger.info("Conexi�n exitosa updateTarea");
			}
			pstm.close();
			conn.close();
		} catch (

		Exception e) {
			logger.severe("Error en la conexi�n de BBDD:" + e);
		}

		return estaActualizado;
	}
	
	
	//DELETE{id} borrar un habito
	@Override
	public boolean delHabito(int hid) throws Exception {
		boolean estaBorrado = false;
		PreparedStatement pstm = null;
		Connection conn = null;
		try {
			conn = this.datasource.getConnection();
			String sql = "DELETE h.* FROM techfit.habito WHERE hId=?;";
			pstm = conn.prepareStatement(sql);
			pstm.setInt(1, hid);

			pstm.executeUpdate();
			if (pstm.getUpdateCount() == 0) {
				throw new Exception(MessageFormat.format("Objeto sin borrar", sql));
			} else {
				estaBorrado = true;
				logger.info("conexion exitosa borrar proyecto");
			}

		} catch (Exception e) {
			logger.severe("Error en la conexion" + e);
		} finally {
			pstm.close();

			conn.close();
		};		
		return estaBorrado;
	}

	
	

}
