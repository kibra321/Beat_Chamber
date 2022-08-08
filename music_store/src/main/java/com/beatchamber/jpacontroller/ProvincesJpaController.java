package com.beatchamber.jpacontroller;

import com.beatchamber.entities.Provinces;
import com.beatchamber.exceptions.IllegalOrphanException;
import com.beatchamber.exceptions.NonexistentEntityException;
import com.beatchamber.exceptions.RollbackFailureException;
import java.io.Serializable;
import java.util.List;
import javax.annotation.Resource;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author kibra and Massimo Di Girolamo
 */
@Named
@SessionScoped
public class ProvincesJpaController implements Serializable {

    private final static Logger LOG = LoggerFactory.getLogger(ProvincesJpaController.class);

    @Resource
    private UserTransaction utx;

    @PersistenceContext(unitName = "music_store_persistence")
    private EntityManager em;

    public ProvincesJpaController() {
    }

    public void create(Provinces provinces) throws RollbackFailureException {

        try {
            utx.begin();
            em.persist(provinces);
            utx.commit();

        } catch (NotSupportedException | SystemException | RollbackException | HeuristicMixedException | HeuristicRollbackException | SecurityException | IllegalStateException ex) {
            try {
                utx.rollback();
                LOG.error("Rollback");
            } catch (IllegalStateException | SecurityException | SystemException re) {
                LOG.error("Rollback2");

                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
        }
    }

    public void edit(Provinces provinces) throws NonexistentEntityException, Exception {

        try {
            utx.begin();
            provinces = em.merge(provinces);
            utx.commit();
        } catch (NotSupportedException | SystemException | RollbackException | HeuristicMixedException | HeuristicRollbackException | SecurityException | IllegalStateException ex) {
            try {
                utx.rollback();
            } catch (IllegalStateException | SecurityException | SystemException re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = provinces.getProvinceId();
                if (findProvinces(id) == null) {
                    throw new NonexistentEntityException("The provinces with id " + id + " no longer exists.");
                }
            }
            throw ex;
        }
    }

    public void destroy(Integer id) throws IllegalOrphanException, NonexistentEntityException, NotSupportedException, SystemException, RollbackFailureException, RollbackException, HeuristicMixedException, HeuristicRollbackException {

        try {
            utx.begin();
            Provinces provinces;
            try {
                provinces = em.getReference(Provinces.class, id);
                provinces.getProvinceId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The provinces with id " + id + " no longer exists.", enfe);
            }
            em.remove(provinces);
            utx.commit();
        } catch (NotSupportedException | SystemException | NonexistentEntityException | RollbackException | HeuristicMixedException | HeuristicRollbackException | SecurityException | IllegalStateException ex) {
            try {
                utx.rollback();
            } catch (IllegalStateException | SecurityException | SystemException re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            throw ex;
        }
    }

    public List<Provinces> findProvincesEntities() {
        return findProvincesEntities(true, -1, -1);
    }

    public List<Provinces> findProvincesEntities(int maxResults, int firstResult) {
        return findProvincesEntities(false, maxResults, firstResult);
    }

    private List<Provinces> findProvincesEntities(boolean all, int maxResults, int firstResult) {

        CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
        cq.select(cq.from(Provinces.class));
        Query q = em.createQuery(cq);
        if (!all) {
            q.setMaxResults(maxResults);
            q.setFirstResult(firstResult);
        }
        return q.getResultList();
    }
    
    public Provinces findProvinces(Integer id) {
        if(id == -1 ){
            return em.find(Provinces.class, 1);
        }
        return em.find(Provinces.class, id);
    }
    

    
    public int getProvincesCount() {

        CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
        Root<Provinces> rt = cq.from(Provinces.class);
        cq.select(em.getCriteriaBuilder().count(rt));
        Query q = em.createQuery(cq);
        return ((Long) q.getSingleResult()).intValue();
    }

}
