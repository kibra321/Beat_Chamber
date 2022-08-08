package com.beatchamber.jpacontroller;

import com.beatchamber.entities.Clients;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import com.beatchamber.entities.CustomerReviews;
import java.util.ArrayList;
import java.util.List;
import com.beatchamber.entities.Orders;
import com.beatchamber.exceptions.IllegalOrphanException;
import com.beatchamber.exceptions.NonexistentEntityException;
import com.beatchamber.exceptions.RollbackFailureException;
import java.util.Collection;
import javax.annotation.Resource;
import javax.enterprise.context.SessionScoped;
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
 *
 * @author Massimo Di Girolamo
 */
@Named
@SessionScoped
public class ClientsJpaController implements Serializable {

    private final static Logger LOG = LoggerFactory.getLogger(ClientsJpaController.class);

    @Resource
    private UserTransaction utx;

    @PersistenceContext(unitName = "music_store_persistence")
    private EntityManager em;

    public ClientsJpaController() {
    }

    public void create(Clients clients) throws RollbackFailureException {
        if (clients.getCustomerReviewsList() == null) {
            clients.setCustomerReviewsList(new ArrayList<CustomerReviews>());
        }
        if (clients.getOrdersCollection() == null) {
            clients.setOrdersCollection(new ArrayList<Orders>());
        }

        try {
            utx.begin();
            List<CustomerReviews> attachedCustomerReviewsList = new ArrayList<CustomerReviews>();
            for (CustomerReviews customerReviewsListCustomerReviewsToAttach : clients.getCustomerReviewsList()) {
                customerReviewsListCustomerReviewsToAttach = em.getReference(customerReviewsListCustomerReviewsToAttach.getClass(), customerReviewsListCustomerReviewsToAttach.getReviewNumber());
                attachedCustomerReviewsList.add(customerReviewsListCustomerReviewsToAttach);
            }
            clients.setCustomerReviewsList(attachedCustomerReviewsList);
            Collection<Orders> attachedOrdersCollection = new ArrayList<Orders>();
            for (Orders ordersCollectionOrdersToAttach : clients.getOrdersCollection()) {
                ordersCollectionOrdersToAttach = em.getReference(ordersCollectionOrdersToAttach.getClass(), ordersCollectionOrdersToAttach.getOrderId());
                attachedOrdersCollection.add(ordersCollectionOrdersToAttach);
            }
            clients.setOrdersCollection(attachedOrdersCollection);
            em.persist(clients);
            for (CustomerReviews customerReviewsListCustomerReviews : clients.getCustomerReviewsList()) {
                Clients oldClientNumberOfCustomerReviewsListCustomerReviews = customerReviewsListCustomerReviews.getClientNumber();
                customerReviewsListCustomerReviews.setClientNumber(clients);
                customerReviewsListCustomerReviews = em.merge(customerReviewsListCustomerReviews);
                if (oldClientNumberOfCustomerReviewsListCustomerReviews != null) {
                    oldClientNumberOfCustomerReviewsListCustomerReviews.getCustomerReviewsList().remove(customerReviewsListCustomerReviews);
                    oldClientNumberOfCustomerReviewsListCustomerReviews = em.merge(oldClientNumberOfCustomerReviewsListCustomerReviews);
                }
            }
            for (Orders ordersCollectionOrders : clients.getOrdersCollection()) {
                Clients oldClientNumberOfOrdersCollectionOrders = ordersCollectionOrders.getClientNumber();
                ordersCollectionOrders.setClientNumber(clients);
                ordersCollectionOrders = em.merge(ordersCollectionOrders);
                if (oldClientNumberOfOrdersCollectionOrders != null) {
                    oldClientNumberOfOrdersCollectionOrders.getOrdersCollection().remove(ordersCollectionOrders);
                    oldClientNumberOfOrdersCollectionOrders = em.merge(oldClientNumberOfOrdersCollectionOrders);
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

    public void edit(Clients clients) throws IllegalOrphanException, NonexistentEntityException, Exception {

        try {
            utx.begin();
            Clients persistentClients = em.find(Clients.class, clients.getClientNumber());
            List<CustomerReviews> customerReviewsListOld = persistentClients.getCustomerReviewsList();
            List<CustomerReviews> customerReviewsListNew = clients.getCustomerReviewsList();
            Collection<Orders> ordersCollectionOld = persistentClients.getOrdersCollection();
            Collection<Orders> ordersCollectionNew = clients.getOrdersCollection();
            List<String> illegalOrphanMessages = null;
            for (CustomerReviews customerReviewsListOldCustomerReviews : customerReviewsListOld) {
                if (!customerReviewsListNew.contains(customerReviewsListOldCustomerReviews)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain CustomerReviews " + customerReviewsListOldCustomerReviews + " since its clientNumber field is not nullable.");
                }
            }
            for (Orders ordersCollectionOldOrders : ordersCollectionOld) {
                if (!ordersCollectionNew.contains(ordersCollectionOldOrders)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Orders " + ordersCollectionOldOrders + " since its clientNumber field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            List<CustomerReviews> attachedCustomerReviewsListNew = new ArrayList<CustomerReviews>();
            for (CustomerReviews customerReviewsListNewCustomerReviewsToAttach : customerReviewsListNew) {
                customerReviewsListNewCustomerReviewsToAttach = em.getReference(customerReviewsListNewCustomerReviewsToAttach.getClass(), customerReviewsListNewCustomerReviewsToAttach.getReviewNumber());
                attachedCustomerReviewsListNew.add(customerReviewsListNewCustomerReviewsToAttach);
            }
            customerReviewsListNew = attachedCustomerReviewsListNew;
            clients.setCustomerReviewsList(customerReviewsListNew);
            Collection<Orders> attachedOrdersCollectionNew = new ArrayList<Orders>();
            for (Orders ordersCollectionNewOrdersToAttach : ordersCollectionNew) {
                ordersCollectionNewOrdersToAttach = em.getReference(ordersCollectionNewOrdersToAttach.getClass(), ordersCollectionNewOrdersToAttach.getOrderId());
                attachedOrdersCollectionNew.add(ordersCollectionNewOrdersToAttach);
            }
            ordersCollectionNew = attachedOrdersCollectionNew;
            clients.setOrdersCollection(ordersCollectionNew);
            clients = em.merge(clients);
            for (CustomerReviews customerReviewsListNewCustomerReviews : customerReviewsListNew) {
                if (!customerReviewsListOld.contains(customerReviewsListNewCustomerReviews)) {
                    Clients oldClientNumberOfCustomerReviewsListNewCustomerReviews = customerReviewsListNewCustomerReviews.getClientNumber();
                    customerReviewsListNewCustomerReviews.setClientNumber(clients);
                    customerReviewsListNewCustomerReviews = em.merge(customerReviewsListNewCustomerReviews);
                    if (oldClientNumberOfCustomerReviewsListNewCustomerReviews != null && !oldClientNumberOfCustomerReviewsListNewCustomerReviews.equals(clients)) {
                        oldClientNumberOfCustomerReviewsListNewCustomerReviews.getCustomerReviewsList().remove(customerReviewsListNewCustomerReviews);
                        oldClientNumberOfCustomerReviewsListNewCustomerReviews = em.merge(oldClientNumberOfCustomerReviewsListNewCustomerReviews);
                    }
                }
            }
            for (Orders ordersCollectionNewOrders : ordersCollectionNew) {
                if (!ordersCollectionOld.contains(ordersCollectionNewOrders)) {
                    Clients oldClientNumberOfOrdersCollectionNewOrders = ordersCollectionNewOrders.getClientNumber();
                    ordersCollectionNewOrders.setClientNumber(clients);
                    ordersCollectionNewOrders = em.merge(ordersCollectionNewOrders);
                    if (oldClientNumberOfOrdersCollectionNewOrders != null && !oldClientNumberOfOrdersCollectionNewOrders.equals(clients)) {
                        oldClientNumberOfOrdersCollectionNewOrders.getOrdersCollection().remove(ordersCollectionNewOrders);
                        oldClientNumberOfOrdersCollectionNewOrders = em.merge(oldClientNumberOfOrdersCollectionNewOrders);
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
                Integer id = clients.getClientNumber();
                if (findClients(id) == null) {
                    throw new NonexistentEntityException("The clients with id " + id + " no longer exists.");
                }
            }
            throw ex;
        }
    }

    public void destroy(Integer id) throws IllegalOrphanException, NonexistentEntityException, NotSupportedException, SystemException, RollbackFailureException, RollbackException, HeuristicMixedException, HeuristicRollbackException {

        try {
            utx.begin();
            Clients clients;
            try {
                clients = em.getReference(Clients.class, id);
                clients.getClientNumber();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The clients with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<CustomerReviews> customerReviewsListOrphanCheck = clients.getCustomerReviewsList();
            for (CustomerReviews customerReviewsListOrphanCheckCustomerReviews : customerReviewsListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Clients (" + clients + ") cannot be destroyed since the CustomerReviews " + customerReviewsListOrphanCheckCustomerReviews + " in its customerReviewsList field has a non-nullable clientNumber field.");
            }
            Collection<Orders> ordersCollectionOrphanCheck = clients.getOrdersCollection();
            for (Orders ordersCollectionOrphanCheckOrders : ordersCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Clients (" + clients + ") cannot be destroyed since the Orders " + ordersCollectionOrphanCheckOrders + " in its ordersCollection field has a non-nullable clientNumber field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            em.remove(clients);
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

    public List<Clients> findClientsEntities() {
        return findClientsEntities(true, -1, -1);
    }

    public List<Clients> findClientsEntities(int maxResults, int firstResult) {
        return findClientsEntities(false, maxResults, firstResult);
    }

    private List<Clients> findClientsEntities(boolean all, int maxResults, int firstResult) {

        CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
        cq.select(cq.from(Clients.class));
        Query q = em.createQuery(cq);
        if (!all) {
            q.setMaxResults(maxResults);
            q.setFirstResult(firstResult);
        }
        return q.getResultList();

    }

    public Clients findClients(Integer id) {
        return em.find(Clients.class, id);
    }

    public int getClientsCount() {

        CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
        Root<Clients> rt = cq.from(Clients.class);
        cq.select(em.getCriteriaBuilder().count(rt));
        Query q = em.createQuery(cq);
        return ((Long) q.getSingleResult()).intValue();
    }

}
