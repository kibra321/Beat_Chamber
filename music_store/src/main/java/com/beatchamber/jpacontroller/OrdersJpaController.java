package com.beatchamber.jpacontroller;

import com.beatchamber.beans.CheckoutBean;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import com.beatchamber.entities.Clients;
import com.beatchamber.entities.OrderTrack;
import java.util.ArrayList;
import java.util.Collection;
import com.beatchamber.entities.OrderAlbum;
import com.beatchamber.entities.Orders;
import com.beatchamber.exceptions.IllegalOrphanException;
import com.beatchamber.exceptions.NonexistentEntityException;
import com.beatchamber.exceptions.RollbackFailureException;
import java.util.List;
import java.util.Objects;
import javax.annotation.Resource;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The new invoices table
 * 
 * @author Massimo Di Girolamo
 */
@Named("ordersController")
@SessionScoped
public class OrdersJpaController implements Serializable {

    private final static Logger LOG = LoggerFactory.getLogger(OrdersJpaController.class);

    @Resource
    private UserTransaction utx;

    private CheckoutBean checkoutBean;

    @PersistenceContext(unitName = "music_store_persistence")
    private EntityManager em;

    @Inject
    private OrderAlbumJpaController orderAlbumController;

    @Inject
    private OrderTrackJpaController orderTrackController;

    @Inject
    private ClientsJpaController clientController;

    @Inject
    private AlbumsJpaController albumController;

    @Inject
    private TracksJpaController trackController;

    public OrdersJpaController() {
    }

    public void create(Orders orders) throws SystemException, RollbackFailureException {
        if (orders.getOrderTrackCollection() == null) {
            orders.setOrderTrackCollection(new ArrayList<OrderTrack>());
        }
        if (orders.getOrderAlbumCollection() == null) {
            orders.setOrderAlbumCollection(new ArrayList<OrderAlbum>());
        }

        try {

            utx.begin();
            Clients clientNumber = orders.getClientNumber();
            if (clientNumber != null) {
                clientNumber = em.getReference(clientNumber.getClass(), clientNumber.getClientNumber());
                orders.setClientNumber(clientNumber);
            }
            Collection<OrderTrack> attachedOrderTrackCollection = new ArrayList<OrderTrack>();
            for (OrderTrack orderTrackCollectionOrderTrackToAttach : orders.getOrderTrackCollection()) {
                orderTrackCollectionOrderTrackToAttach = em.getReference(orderTrackCollectionOrderTrackToAttach.getClass(), orderTrackCollectionOrderTrackToAttach.getOrderId());
                attachedOrderTrackCollection.add(orderTrackCollectionOrderTrackToAttach);
            }
            orders.setOrderTrackCollection(attachedOrderTrackCollection);
            Collection<OrderAlbum> attachedOrderAlbumCollection = new ArrayList<OrderAlbum>();
            for (OrderAlbum orderAlbumCollectionOrderAlbumToAttach : orders.getOrderAlbumCollection()) {
                orderAlbumCollectionOrderAlbumToAttach = em.merge(orderAlbumCollectionOrderAlbumToAttach);
                attachedOrderAlbumCollection.add(orderAlbumCollectionOrderAlbumToAttach);
            }
            orders.setOrderAlbumCollection(attachedOrderAlbumCollection);
            em.persist(orders);
            if (clientNumber != null) {
                clientNumber.getOrdersCollection().add(orders);
                clientNumber = em.merge(clientNumber);
            }
            for (OrderTrack orderTrackCollectionOrderTrack : orders.getOrderTrackCollection()) {
                Orders oldOrderIdOfOrderTrackCollectionOrderTrack = orderTrackCollectionOrderTrack.getOrderId();
                orderTrackCollectionOrderTrack.setOrderId(orders);
                orderTrackCollectionOrderTrack = em.merge(orderTrackCollectionOrderTrack);
                if (oldOrderIdOfOrderTrackCollectionOrderTrack != null) {
                    oldOrderIdOfOrderTrackCollectionOrderTrack.getOrderTrackCollection().remove(orderTrackCollectionOrderTrack);
                    oldOrderIdOfOrderTrackCollectionOrderTrack = em.merge(oldOrderIdOfOrderTrackCollectionOrderTrack);
                }
            }
            for (OrderAlbum orderAlbumCollectionOrderAlbum : orders.getOrderAlbumCollection()) {
                Orders oldOrderIdOfOrderAlbumCollectionOrderAlbum = orderAlbumCollectionOrderAlbum.getOrderId();
                orderAlbumCollectionOrderAlbum.setOrderId(orders);
                orderAlbumCollectionOrderAlbum = em.merge(orderAlbumCollectionOrderAlbum);
                if (oldOrderIdOfOrderAlbumCollectionOrderAlbum != null) {
                    oldOrderIdOfOrderAlbumCollectionOrderAlbum.getOrderAlbumCollection().remove(orderAlbumCollectionOrderAlbum);
                    oldOrderIdOfOrderAlbumCollectionOrderAlbum = em.merge(oldOrderIdOfOrderAlbumCollectionOrderAlbum);
                }
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

    public void edit(Orders orders) throws IllegalOrphanException, NonexistentEntityException, Exception {

        try {

            utx.begin();
            Orders persistentOrders = em.find(Orders.class, orders.getOrderId());
            Clients clientNumberOld = persistentOrders.getClientNumber();
            Clients clientNumberNew = orders.getClientNumber();
            Collection<OrderTrack> orderTrackCollectionOld = persistentOrders.getOrderTrackCollection();
            Collection<OrderTrack> orderTrackCollectionNew = orders.getOrderTrackCollection();
            Collection<OrderAlbum> orderAlbumCollectionOld = persistentOrders.getOrderAlbumCollection();
            Collection<OrderAlbum> orderAlbumCollectionNew = orders.getOrderAlbumCollection();
            List<String> illegalOrphanMessages = null;
            for (OrderTrack orderTrackCollectionOldOrderTrack : orderTrackCollectionOld) {
                if (!orderTrackCollectionNew.contains(orderTrackCollectionOldOrderTrack)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain OrderTrack " + orderTrackCollectionOldOrderTrack + " since its orderId field is not nullable.");
                }
            }
            for (OrderAlbum orderAlbumCollectionOldOrderAlbum : orderAlbumCollectionOld) {
                if (!orderAlbumCollectionNew.contains(orderAlbumCollectionOldOrderAlbum)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain OrderAlbum " + orderAlbumCollectionOldOrderAlbum + " since its orderId field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (clientNumberNew != null) {
                clientNumberNew = em.getReference(clientNumberNew.getClass(), clientNumberNew.getClientNumber());
                orders.setClientNumber(clientNumberNew);
            }
            Collection<OrderTrack> attachedOrderTrackCollectionNew = new ArrayList<OrderTrack>();
            for (OrderTrack orderTrackCollectionNewOrderTrackToAttach : orderTrackCollectionNew) {
                orderTrackCollectionNewOrderTrackToAttach = em.getReference(orderTrackCollectionNewOrderTrackToAttach.getClass(), orderTrackCollectionNewOrderTrackToAttach.getOrderId());
                attachedOrderTrackCollectionNew.add(orderTrackCollectionNewOrderTrackToAttach);
            }
            orderTrackCollectionNew = attachedOrderTrackCollectionNew;
            orders.setOrderTrackCollection(orderTrackCollectionNew);
            Collection<OrderAlbum> attachedOrderAlbumCollectionNew = new ArrayList<OrderAlbum>();
            for (OrderAlbum orderAlbumCollectionNewOrderAlbumToAttach : orderAlbumCollectionNew) {
                orderAlbumCollectionNewOrderAlbumToAttach = em.merge(orderAlbumCollectionNewOrderAlbumToAttach);
                attachedOrderAlbumCollectionNew.add(orderAlbumCollectionNewOrderAlbumToAttach);
            }
            orderAlbumCollectionNew = attachedOrderAlbumCollectionNew;
            orders.setOrderAlbumCollection(orderAlbumCollectionNew);
            orders = em.merge(orders);
            if (clientNumberOld != null && !clientNumberOld.equals(clientNumberNew)) {
                clientNumberOld.getOrdersCollection().remove(orders);
                clientNumberOld = em.merge(clientNumberOld);
            }
            if (clientNumberNew != null && !clientNumberNew.equals(clientNumberOld)) {
                clientNumberNew.getOrdersCollection().add(orders);
                clientNumberNew = em.merge(clientNumberNew);
            }
            for (OrderTrack orderTrackCollectionNewOrderTrack : orderTrackCollectionNew) {
                if (!orderTrackCollectionOld.contains(orderTrackCollectionNewOrderTrack)) {
                    Orders oldOrderIdOfOrderTrackCollectionNewOrderTrack = orderTrackCollectionNewOrderTrack.getOrderId();
                    orderTrackCollectionNewOrderTrack.setOrderId(orders);
                    orderTrackCollectionNewOrderTrack = em.merge(orderTrackCollectionNewOrderTrack);
                    if (oldOrderIdOfOrderTrackCollectionNewOrderTrack != null && !oldOrderIdOfOrderTrackCollectionNewOrderTrack.equals(orders)) {
                        oldOrderIdOfOrderTrackCollectionNewOrderTrack.getOrderTrackCollection().remove(orderTrackCollectionNewOrderTrack);
                        oldOrderIdOfOrderTrackCollectionNewOrderTrack = em.merge(oldOrderIdOfOrderTrackCollectionNewOrderTrack);
                    }
                }
            }
            for (OrderAlbum orderAlbumCollectionNewOrderAlbum : orderAlbumCollectionNew) {
                if (!orderAlbumCollectionOld.contains(orderAlbumCollectionNewOrderAlbum)) {
                    Orders oldOrderIdOfOrderAlbumCollectionNewOrderAlbum = orderAlbumCollectionNewOrderAlbum.getOrderId();
                    orderAlbumCollectionNewOrderAlbum.setOrderId(orders);
                    orderAlbumCollectionNewOrderAlbum = em.merge(orderAlbumCollectionNewOrderAlbum);
                    if (oldOrderIdOfOrderAlbumCollectionNewOrderAlbum != null && !oldOrderIdOfOrderAlbumCollectionNewOrderAlbum.equals(orders)) {
                        oldOrderIdOfOrderAlbumCollectionNewOrderAlbum.getOrderAlbumCollection().remove(orderAlbumCollectionNewOrderAlbum);
                        oldOrderIdOfOrderAlbumCollectionNewOrderAlbum = em.merge(oldOrderIdOfOrderAlbumCollectionNewOrderAlbum);
                    }
                }
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
                Integer id = orders.getOrderId();
                if (findOrders(id) == null) {
                    throw new NonexistentEntityException("The orders with id " + id + " no longer exists.");
                }
            }
            throw ex;
        }
    }

    public void destroy(Integer id) throws IllegalOrphanException, NonexistentEntityException, NotSupportedException, SystemException, RollbackFailureException, RollbackException, HeuristicMixedException, HeuristicRollbackException {

        try {

            utx.begin();
            Orders orders;
            try {
                orders = em.getReference(Orders.class, id);
                orders.getOrderId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The orders with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            Collection<OrderTrack> orderTrackCollectionOrphanCheck = orders.getOrderTrackCollection();
            for (OrderTrack orderTrackCollectionOrphanCheckOrderTrack : orderTrackCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Orders (" + orders + ") cannot be destroyed since the OrderTrack " + orderTrackCollectionOrphanCheckOrderTrack + " in its orderTrackCollection field has a non-nullable orderId field.");
            }
            Collection<OrderAlbum> orderAlbumCollectionOrphanCheck = orders.getOrderAlbumCollection();
            for (OrderAlbum orderAlbumCollectionOrphanCheckOrderAlbum : orderAlbumCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Orders (" + orders + ") cannot be destroyed since the OrderAlbum " + orderAlbumCollectionOrphanCheckOrderAlbum + " in its orderAlbumCollection field has a non-nullable orderId field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Clients clientNumber = orders.getClientNumber();
            if (clientNumber != null) {
                clientNumber.getOrdersCollection().remove(orders);
                clientNumber = em.merge(clientNumber);
            }
            em.remove(orders);
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

    public List<Orders> findOrdersEntities() {
        return findOrdersEntities(true, -1, -1);
    }

    public List<Orders> findOrdersEntities(int maxResults, int firstResult) {
        return findOrdersEntities(false, maxResults, firstResult);
    }

    private List<Orders> findOrdersEntities(boolean all, int maxResults, int firstResult) {

        CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
        cq.select(cq.from(Orders.class));
        Query q = em.createQuery(cq);
        if (!all) {
            q.setMaxResults(maxResults);
            q.setFirstResult(firstResult);
        }
        return q.getResultList();
    }

    public Orders findOrders(Integer id) {

        return em.find(Orders.class, id);
    }

    public int getOrdersCount() {

        CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
        Root<Orders> rt = cq.from(Orders.class);
        cq.select(em.getCriteriaBuilder().count(rt));
        Query q = em.createQuery(cq);
        return ((Long) q.getSingleResult()).intValue();
    }

    /**
     * @param clientNumber
     * @return The currently logged in client's purchases
     * @author Susan Vuu
     */
    public List<OrderTrack> getClientOrders(int clientNumber) {

        List<OrderTrack> allTracks = orderTrackController.findOrderTrackEntities();
        List<OrderTrack> foundTracks = new ArrayList<>();

        //find all of the tracks from the user
        allTracks.stream().filter(item -> (item.getOrderId().getClientNumber().getClientNumber() == clientNumber)).forEachOrdered(item -> {
            foundTracks.add(item);
        });

        return foundTracks;

    }
    
    /**
     * @param clientNumber
     * @return The recent order made by the client
     */
    public Orders findClientRecentOrder(Integer clientNumber) {
        List<Orders> allOrders = findOrdersEntities();
        List<Orders> clientOrders = new ArrayList<>();
        
        for(Orders order : allOrders) {
            if(Objects.equals(order.getClientNumber().getClientNumber(), clientNumber)) {
                clientOrders.add(order);
            }
        }
        
        if(clientOrders.isEmpty()){
            return null;
        }
        
        return clientOrders.get(clientOrders.size() - 1);
    }
}
