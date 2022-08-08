package com.beatchamber.jpacontroller;

import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import com.beatchamber.entities.Orders;
import com.beatchamber.entities.Albums;
import com.beatchamber.entities.OrderAlbum;
import com.beatchamber.exceptions.IllegalOrphanException;
import com.beatchamber.exceptions.NonexistentEntityException;
import com.beatchamber.exceptions.RollbackFailureException;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import java.util.List;
import javax.annotation.Resource;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Massimo Di Girolamo
 */
public class OrderAlbumJpaController implements Serializable {

    private final static Logger LOG = LoggerFactory.getLogger(OrderAlbumJpaController.class);

    @Resource
    private UserTransaction utx;

    @PersistenceContext(unitName = "music_store_persistence")
    private EntityManager em;

    public OrderAlbumJpaController() {
    }

    public void create(OrderAlbum orderAlbum) throws RollbackFailureException {

        try {

            utx.begin();
            Orders orderId = orderAlbum.getOrderId();
            if (orderId != null) {
                orderId = em.getReference(orderId.getClass(), orderId.getOrderId());
                orderAlbum.setOrderId(orderId);
            }
            Albums albumId = orderAlbum.getAlbumId();
            if (albumId != null) {
                albumId = em.getReference(albumId.getClass(), albumId.getAlbumNumber());
                orderAlbum.setAlbumId(albumId);
            }
            em.persist(orderAlbum);
            if (orderId != null) {
                orderId.getOrderAlbumCollection().add(orderAlbum);
                orderId = em.merge(orderId);
            }
            if (albumId != null) {
                albumId.getOrderAlbumCollection().add(orderAlbum);
                albumId = em.merge(albumId);
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

    public void edit(OrderAlbum orderAlbum) throws NonexistentEntityException, Exception {

        try {

            utx.begin();
            OrderAlbum persistentOrderAlbum = em.find(OrderAlbum.class, orderAlbum.getTablekey());
            Orders orderIdOld = persistentOrderAlbum.getOrderId();
            Orders orderIdNew = orderAlbum.getOrderId();
            Albums albumIdOld = persistentOrderAlbum.getAlbumId();
            Albums albumIdNew = orderAlbum.getAlbumId();
            if (orderIdNew != null) {
                orderIdNew = em.getReference(orderIdNew.getClass(), orderIdNew.getOrderId());
                orderAlbum.setOrderId(orderIdNew);
            }
            if (albumIdNew != null) {
                albumIdNew = em.getReference(albumIdNew.getClass(), albumIdNew.getAlbumNumber());
                orderAlbum.setAlbumId(albumIdNew);
            }
            orderAlbum = em.merge(orderAlbum);
            if (orderIdOld != null && !orderIdOld.equals(orderIdNew)) {
                orderIdOld.getOrderAlbumCollection().remove(orderAlbum);
                orderIdOld = em.merge(orderIdOld);
            }
            if (orderIdNew != null && !orderIdNew.equals(orderIdOld)) {
                orderIdNew.getOrderAlbumCollection().add(orderAlbum);
                orderIdNew = em.merge(orderIdNew);
            }
            if (albumIdOld != null && !albumIdOld.equals(albumIdNew)) {
                albumIdOld.getOrderAlbumCollection().remove(orderAlbum);
                albumIdOld = em.merge(albumIdOld);
            }
            if (albumIdNew != null && !albumIdNew.equals(albumIdOld)) {
                albumIdNew.getOrderAlbumCollection().add(orderAlbum);
                albumIdNew = em.merge(albumIdNew);
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
                Orders id = orderAlbum.getOrderId();
                if (findOrderAlbum(id) == null) {
                    throw new NonexistentEntityException("The orderAlbum with id " + id + " no longer exists.");
                }
            }
            throw ex;
        }
    }

    public void destroy(Integer id) throws IllegalOrphanException, NonexistentEntityException, NotSupportedException, SystemException, RollbackFailureException, RollbackException, HeuristicMixedException, HeuristicRollbackException {

        try {

            utx.begin();
            OrderAlbum orderAlbum;
            try {
                orderAlbum = em.getReference(OrderAlbum.class, id);
                orderAlbum.getTablekey();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The orderAlbum with id " + id + " no longer exists.", enfe);
            }
            Orders orderId = orderAlbum.getOrderId();
            if (orderId != null) {
                orderId.getOrderAlbumCollection().remove(orderAlbum);
                orderId = em.merge(orderId);
            }
            Albums albumId = orderAlbum.getAlbumId();
            if (albumId != null) {
                albumId.getOrderAlbumCollection().remove(orderAlbum);
                albumId = em.merge(albumId);
            }
            em.remove(orderAlbum);
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

    public List<OrderAlbum> findOrderAlbumEntities() {
        return findOrderAlbumEntities(true, -1, -1);
    }

    public List<OrderAlbum> findOrderAlbumEntities(int maxResults, int firstResult) {
        return findOrderAlbumEntities(false, maxResults, firstResult);
    }

    private List<OrderAlbum> findOrderAlbumEntities(boolean all, int maxResults, int firstResult) {

        CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
        cq.select(cq.from(OrderAlbum.class));
        Query q = em.createQuery(cq);
        if (!all) {
            q.setMaxResults(maxResults);
            q.setFirstResult(firstResult);
        }
        return q.getResultList();

    }

    public OrderAlbum findOrderAlbum(Orders id) {

        return em.find(OrderAlbum.class, id);
    }

    public int getOrderAlbumCount() {

        CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
        Root<OrderAlbum> rt = cq.from(OrderAlbum.class);
        cq.select(em.getCriteriaBuilder().count(rt));
        Query q = em.createQuery(cq);
        return ((Long) q.getSingleResult()).intValue();
    }

    /**
     * @param orderId
     * @return All order albums by order id
     * @author Susan Vuu
     */
    public List<OrderAlbum> getOrderAlbumsByOrderId(Orders orderId) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<OrderAlbum> cq = cb.createQuery(OrderAlbum.class);
        Root<OrderAlbum> rt = cq.from(OrderAlbum.class);
        
        cq.where(cb.equal(rt.get("orderId"), orderId));
        TypedQuery<OrderAlbum> query = em.createQuery(cq);
        
        return query.getResultList();
    }
}
