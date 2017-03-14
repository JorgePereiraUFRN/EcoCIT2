package br.com.ecodif.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;

import br.com.ecodif.domain.Value;

import com.impetus.client.cassandra.common.CassandraConstants;

@Stateless
public class ValueDao2 {

	private EntityManager em;

	private EntityManagerFactory emf;

	public ValueDao2() {
		if (emf == null) {
			try {
				Map<String, String> props = new HashMap<String, String>();
				props.put(CassandraConstants.CQL_VERSION,
						CassandraConstants.CQL_VERSION_3_0);
				emf = Persistence.createEntityManagerFactory("persistenceUnit", props);
				em = emf.createEntityManager();
			} catch (Exception e) {

				e.printStackTrace();
			}
		}
	}
	
	public  synchronized void save(Value entity) {
		em.getTransaction().begin();
		em.persist(entity);
		em.getTransaction().commit();
	}
	
	
	public List<Value> findValuesByDataId(int dataID)  {
	
	    Query q = em.createQuery("Select p from " +Value.class.getSimpleName()+" p where p.dataId= :dataId ORDER BY at DESC");
	    q.setParameter("dataId", dataID);
	    q.setMaxResults(1000);
	    List<Value> results = q.getResultList();
	    
	    return results;
		
	}

}
