package br.com.ecodif.dao;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.ejb.Stateless;

import org.hibernate.Criteria;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;

import br.com.ecodif.domain.Application;
import br.com.ecodif.searchcriteria.ApplicationSearchCriteria;

/**
 * Classe de acesso a dados (DAO) que abstrai as opera��es sobre a respectiva 
 * tabela de aplica��es no banco de dados
 * @author Everton Cavalcante
 */
@Stateless
public class ApplicationDAO extends GenericDAO<Application> {

	/** Construtor */
	public ApplicationDAO() {
		super(Application.class);
	}
	
	
	public Application update(Application entity){
		Transaction tx = session.beginTransaction();
		
		/*if(entity != null && entity.getEmmlReference() != null){
			
			try{
			File file = new File(entity.getEmmlReference());
			FileInputStream inputStream = new FileInputStream(file);
			Blob blob = Hibernate.getLobCreator(session)
								.createBlob(inputStream, file.length());
			System.out.println("atualizando emml file in database "+entity.getEmmlFile().length());
			entity.setEmmlFile(blob);
			}catch(Exception e){
				e.printStackTrace();
			}
		}*/
		
		Application app =  (Application) session.merge(entity);
		try {
		
			app.setEmmlFile(Hibernate.createBlob(entity.getEmmlFile().getBinaryStream()));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		tx.commit();
		
		return app;
	}
	
	/**
	 * Realiza uma consulta por aplica��es com base no nome
	 * @param name Nome da aplica��o objeto da consulta
	 * @return Aplica��o com o nome especificado como par�metro ou 
	 * 			<code>null</code> (objeto nulo) caso a aplica��o n�o tenha sido 
	 * 			encontrada
	 */
	public Application findApplicationByName(String name) {
		//Session session = (Session) em.getDelegate();
		Criteria crobject = session.createCriteria(Application.class);
		crobject.add(Restrictions.eq("name", name));
		return (Application) crobject.uniqueResult();
	}

	
	/**
	 * Retorna uma lista com as aplica��es pertencentes a um determinado usu�rio, 
	 * a partir do identificador do mesmo
	 * @param id Identificador do usu�rio
	 * @return Lista de aplica��es pertencentes ao usu�rio especificado
	 */
	public List<Application> findApplicationsByUserId(int id) {
		String strQuery = "Select a From Application a left join fetch a.user u WHERE u.id = :id";
		HashMap<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("id", id);		
		return super.findManyResults(strQuery, parameters);
	}
	
	
	/**
	 * Retorna uma lista com as aplica��es caracterizadas pela flag de 
	 * visualiza��o
	 * @param isPrivate Flag de visualiza��o, com valor <code>true</code> para 
	 * 						listar todas as aplica��es p�blicas ou 
	 * 						<code>false</code> para listar todas as aplica��es 
	 * 						privadas
	 * @return Lista de aplica��es caracterizadas pela flag de visualiza��o
	 */
	public List<Application> findPublicApplications(boolean isPrivate) {
		String strQuery = "Select a from Application a WHERE a._private = :isPrivate";
		HashMap<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("isPrivate", isPrivate);
		return super.findManyResults(strQuery, parameters);
	}


	/**
	 * Realiza uma consulta por aplica��es com base em crit�rios de busca, 
	 * representados por um objeto da classe <code>ApplicationSearchCriteria</code>
	 * @see br.com.ecodif.searchcriteria.ApplicationSearchCriteria
	 * 
	 * @param criteria Objeto da classe <code>ApplicationSearchCriteria</code> 
	 * 			que agrega os crit�rios de busca a serem considerados na consulta
	 * @return Conjunto de aplica��es que atendem aos crit�rios de busca 
	 * 			especificados
	 */
	@SuppressWarnings({ "unchecked", "static-access" })
	public Set<Application> findApplicationsByCriteria(
			ApplicationSearchCriteria criteria) {
		//Session session = (Session) em.getDelegate();
		Criteria crobject = session.createCriteria(Application.class);
		
		// Crit�rio de busca: nome da aplica��o
		if (criteria.getName() != null) {
			crobject.add(Restrictions.like("name", "%" + criteria.getName() + "%")
					.ignoreCase());
		}
		
		// Crit�rio de busca: palavras-chave
		if (criteria.getTags() != null) {
			crobject.add(Restrictions.like("tags", "%" + criteria.getTags() + "%")
					.ignoreCase());
		}
		
		// Crit�rio de busca: nome ou login de usu�rio
		if (criteria.getUser() != null) {
			Criteria cruser = crobject.createCriteria("user", "u");
			Criterion ulogin = 
				Restrictions.like("u.login", "%" + criteria.getUser() + "%")
					.ignoreCase();
			Criterion uname = 
				Restrictions.like("u.name", "%" + criteria.getUser() + "%")
					.ignoreCase();
			cruser.add(Restrictions.or(ulogin, uname));
		}
		
		// Crit�rio de busca: data de cria��o (aplica��o criadas em faixa de datas) 
		if (criteria.getStartDate() != null && criteria.getEndDate() != null)
		{
			GregorianCalendar gcstart = new GregorianCalendar();
			gcstart.setTime(criteria.getStartDate());
				
			GregorianCalendar gcend = new GregorianCalendar();
			gcend.setTime(criteria.getEndDate());
				
			crobject.add(Restrictions.between("creationDate", gcstart, gcend));
		}
		
		/*
		 * Crit�rio de busca: data de atualiza��o (aplica��es atualizadas h� 
		 * menos de um m�s da data atual)
		 */
		if (criteria.isUpdated()) {
			GregorianCalendar gcNow = (GregorianCalendar) GregorianCalendar
					.getInstance();
			GregorianCalendar gcLastUpdate = (GregorianCalendar) GregorianCalendar
					.getInstance();
			gcLastUpdate.add(gcLastUpdate.MONTH, -1);
			crobject.add(Restrictions.between("updateDate", gcLastUpdate, gcNow));
		}
		
		// Crit�rio de busca: apenas por aplica��es p�blicas
		crobject.add(Restrictions.eq("_private", false));
		
		/* 
		 * Realiza a consulta propriamente dita com base nos crit�rios de busca
		 * especificados 
		 */
		List<Application> applicationList = crobject.list();
		Set<Application> applicationSet = new HashSet<Application>();
		for (Application a : applicationList) {
			applicationSet.add(a);
		}
		
		return applicationSet;
	}
}
