package com.beatchamber.jpacontroller;

import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import com.beatchamber.entities.Clients;
import com.beatchamber.entities.CustomerReviews;
import com.beatchamber.entities.Tracks;
import com.beatchamber.exceptions.NonexistentEntityException;
import com.beatchamber.exceptions.RollbackFailureException;
import java.util.List;
import javax.annotation.Resource;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Join;
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
 * @author Massimo Di Girolamo
 */
@Named
@SessionScoped
public class CustomerReviewsJpaController implements Serializable {

    private final static Logger LOG = LoggerFactory.getLogger(CustomerReviewsJpaController.class);

    @Resource
    private UserTransaction utx;

    @PersistenceContext(unitName = "music_store_persistence")
    private EntityManager em;

    public CustomerReviewsJpaController() {
    }

    public void create(CustomerReviews customerReviews) throws RollbackFailureException {

        try {
            utx.begin();
            Clients clientNumber = customerReviews.getClientNumber();
            if (clientNumber != null) {
                clientNumber = em.getReference(clientNumber.getClass(), clientNumber.getClientNumber());
                customerReviews.setClientNumber(clientNumber);
            }
            Tracks trackId = customerReviews.getTrackId();
            if (trackId != null) {
                trackId = em.getReference(trackId.getClass(), trackId.getTrackId());
                customerReviews.setTrackId(trackId);
            }
            em.persist(customerReviews);
            if (clientNumber != null) {
                clientNumber.getCustomerReviewsList().add(customerReviews);
                clientNumber = em.merge(clientNumber);
            }
            if (trackId != null) {
                trackId.getCustomerReviewsList().add(customerReviews);
                trackId = em.merge(trackId);
            }
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

    public void edit(CustomerReviews customerReviews) throws NonexistentEntityException, Exception {

        try {

            utx.begin();
            CustomerReviews persistentCustomerReviews = em.find(CustomerReviews.class, customerReviews.getReviewNumber());
            Clients clientNumberOld = persistentCustomerReviews.getClientNumber();
            Clients clientNumberNew = customerReviews.getClientNumber();
            Tracks trackIdOld = persistentCustomerReviews.getTrackId();
            Tracks trackIdNew = customerReviews.getTrackId();
            if (clientNumberNew != null) {
                clientNumberNew = em.getReference(clientNumberNew.getClass(), clientNumberNew.getClientNumber());
                customerReviews.setClientNumber(clientNumberNew);
            }
            if (trackIdNew != null) {
                trackIdNew = em.getReference(trackIdNew.getClass(), trackIdNew.getTrackId());
                customerReviews.setTrackId(trackIdNew);
            }
            customerReviews = em.merge(customerReviews);
            if (clientNumberOld != null && !clientNumberOld.equals(clientNumberNew)) {
                clientNumberOld.getCustomerReviewsList().remove(customerReviews);
                clientNumberOld = em.merge(clientNumberOld);
            }
            if (clientNumberNew != null && !clientNumberNew.equals(clientNumberOld)) {
                clientNumberNew.getCustomerReviewsList().add(customerReviews);
                clientNumberNew = em.merge(clientNumberNew);
            }
            if (trackIdOld != null && !trackIdOld.equals(trackIdNew)) {
                trackIdOld.getCustomerReviewsList().remove(customerReviews);
                trackIdOld = em.merge(trackIdOld);
            }
            if (trackIdNew != null && !trackIdNew.equals(trackIdOld)) {
                trackIdNew.getCustomerReviewsList().add(customerReviews);
                trackIdNew = em.merge(trackIdNew);
            }
            utx.commit();
        } catch (NotSupportedException | SystemException | RollbackException | HeuristicMixedException | HeuristicRollbackException | SecurityException | IllegalStateException ex) {
            try {
                utx.rollback();
            } catch (IllegalStateException | SecurityException | SystemException re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = customerReviews.getReviewNumber();
                if (findCustomerReviews(id) == null) {
                    throw new NonexistentEntityException("The customerReviews with id " + id + " no longer exists.");
                }
            }
            throw ex;
        }
    }

    public void destroy(Integer id) throws NonexistentEntityException, RollbackFailureException, NotSupportedException, SystemException, RollbackException, HeuristicMixedException, HeuristicRollbackException {

        try {
            utx.begin();
            CustomerReviews customerReviews;
            try {
                customerReviews = em.getReference(CustomerReviews.class, id);
                customerReviews.getReviewNumber();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The customerReviews with id " + id + " no longer exists.", enfe);
            }
            Clients clientNumber = customerReviews.getClientNumber();
            if (clientNumber != null) {
                clientNumber.getCustomerReviewsList().remove(customerReviews);
                clientNumber = em.merge(clientNumber);
            }
            Tracks trackId = customerReviews.getTrackId();
            if (trackId != null) {
                trackId.getCustomerReviewsList().remove(customerReviews);
                trackId = em.merge(trackId);
            }
            em.remove(customerReviews);
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

    public List<CustomerReviews> findCustomerReviewsEntities() {
        return findCustomerReviewsEntities(true, -1, -1);
    }

    public List<CustomerReviews> findCustomerReviewsEntities(int maxResults, int firstResult) {
        return findCustomerReviewsEntities(false, maxResults, firstResult);
    }

    private List<CustomerReviews> findCustomerReviewsEntities(boolean all, int maxResults, int firstResult) {

        CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
        cq.select(cq.from(CustomerReviews.class));
        Query q = em.createQuery(cq);
        if (!all) {
            q.setMaxResults(maxResults);
            q.setFirstResult(firstResult);
        }
        return q.getResultList();

    }

    public CustomerReviews findCustomerReviews(Integer id) {

        return em.find(CustomerReviews.class, id);

    }

    public int getCustomerReviewsCount() {

        CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
        Root<CustomerReviews> rt = cq.from(CustomerReviews.class);
        cq.select(em.getCriteriaBuilder().count(rt));
        Query q = em.createQuery(cq);
        return ((Long) q.getSingleResult()).intValue();

    }

    /**
     * Method finds customer reviews based on the track Id
     *where the approval status is true
     * @param trackId
     * @return
     * 
     * @author Korjon Chang-Jones
     */
    public List<CustomerReviews> findCustomerReviewsByTrackId(Integer trackId) {

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<CustomerReviews> cq = cb.createQuery(CustomerReviews.class);
        Root<CustomerReviews> rt = cq.from(CustomerReviews.class);
        cq.where(cb.and(cb.equal(rt.get("trackId").get("trackId"), trackId),cb.equal(rt.get("approvalStatus"), true)));
        TypedQuery<CustomerReviews> query = em.createQuery(cq);

        return query.getResultList();
    }

    /**
     * Method finds reviews for all tracks on a specific album
     * where the approval status is true
     * @param albumNumber
     * @return 
     * 
     * @author Korjon Chang-Jones
     */
    public List<CustomerReviews> findAllReviews(Integer albumNumber) {

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<CustomerReviews> cq = cb.createQuery(CustomerReviews.class);
        Root<CustomerReviews> rt = cq.from(CustomerReviews.class);
        Join track = rt.join("trackId");
        Join album = track.join("albumNumber");
        cq.where(cb.and(cb.equal(album.get("albumNumber"), albumNumber), cb.equal(rt.get("approvalStatus"), true)));
        TypedQuery<CustomerReviews> query = em.createQuery(cq);

        return query.getResultList();
    }

}
