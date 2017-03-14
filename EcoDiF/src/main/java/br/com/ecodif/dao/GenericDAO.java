package br.com.ecodif.dao;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaQuery;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

/**
 * Classe gen�rica que implementa m�todos padr�o para acesso a dados.
 * 
 * @author Bruno Costa
 *
 * @param <T>
 *            Classe de dom�nio
 */
public abstract class GenericDAO<T> {

	protected Session session;

	private Class<T> entityClass;

	public GenericDAO(Class<T> entityClass) {
		this.entityClass = entityClass;

		Configuration configuration = new Configuration();
		configuration.configure();

		SessionFactory factory = configuration.buildSessionFactory();
		session = factory.openSession();
		
	}

	/**
	 * Persisitir uma entidade de dom�nio no banco de dados
	 * 
	 * @param entity
	 */
	public void save(T entity) {
		Transaction tx = session.beginTransaction();
		session.save(entity);
		tx.commit();
	}

	/**
	 * Remover uma entidade no banco de dados
	 * 
	 * @param entity
	 */
	public void delete(T entity) {
		Transaction tx = session.beginTransaction();
		T entityToBeRemoved = (T) session.merge(entity);

		session.delete(entityToBeRemoved);
		tx.commit();
	}

	/**
	 * Atualizar uma entidade no banco de dados
	 * 
	 * @param entity
	 * @return 'true' if it was successful, 'false' if wasn't
	 */
	public T update(T entity) {
		Transaction tx = session.beginTransaction();
		T t = (T) session.merge(entity);
		tx.commit();
		
		return t;
	}

	/**
	 * Recuperar uma entidade no banco de dados
	 * 
	 * @param entityID
	 * @return entity
	 */
	public T find(int entityID) {
		
		return (T) session.get(entityClass, entityID);

	}

	/**
	 * Recuperar todos os registros da entidade no banco de dados
	 * 
	 * @return List of entities
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public List<T> findAll() {
		return session.createCriteria(entityClass).list();
	}

	/**
	 * Recuperar uma entidade com base em uma query de pesquisa
	 * 
	 * @param namedQuery
	 *            A namedQuery to execute in Database
	 * @param parameters
	 *            Parameters for query
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public T findOneResult(String strQuery, Map<String, Object> parameters) {
		T result = null;

		try {
			Query query = session.createQuery(strQuery);

			// Method that will populate parameters if they are passed not null
			// and empty
			if (parameters != null && !parameters.isEmpty()) {
				populateQueryParameters(query, parameters);
			}

			result = (T) query.uniqueResult();

		} catch (NoResultException e) {
			System.out.println("No entity found for query: " + strQuery);
		}

		catch (Exception e) {
			System.out.println("Error while running query: " + e.getMessage());
			e.printStackTrace();
		}

		return result;
	}

	/**
	 * Localizar muitas entidades com uma query de pesquisa
	 * 
	 * @param strQuery
	 * @param parameters
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<T> findManyResults(String strQuery,
			Map<String, Object> parameters) {
		List<T> result = null;

		try {
			Query query = session.createQuery(strQuery);

			// Method that will populate parameters if they are passed not null
			// and empty
			if (parameters != null && !parameters.isEmpty()) {
				populateQueryParameters(query, parameters);
			}

			result = query.list();

		} catch (NoResultException e) {
			System.out.println("No entity found for query: " + strQuery);
		} catch (Exception e) {
			System.out.println("Error while running query: " + e.getMessage());
			e.printStackTrace();
		}

		return result;
	}

	/**
	 * Popular par�metros em uma query de pesquisa.
	 * 
	 * @param query
	 * @param parameters
	 */
	public void populateQueryParameters(Query query,
			Map<String, Object> parameters) {

		for (Entry<String, Object> entry : parameters.entrySet()) {
			query.setParameter(entry.getKey(), entry.getValue());
		}
	}
}
