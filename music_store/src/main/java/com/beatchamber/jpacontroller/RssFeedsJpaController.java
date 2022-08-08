package com.beatchamber.jpacontroller;

import com.beatchamber.entities.RssFeeds;
import com.beatchamber.exceptions.IllegalOrphanException;
import com.beatchamber.exceptions.NonexistentEntityException;
import com.beatchamber.exceptions.RollbackFailureException;
import com.beatchamber.exceptions.NonexistentEntityException;
import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Random;
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
 * @author Massimo Di Girolamo
 */
@Named
@SessionScoped
public class RssFeedsJpaController implements Serializable {

    private final static Logger LOG = LoggerFactory.getLogger(RssFeedsJpaController.class);

    @Resource
    private UserTransaction utx;

    @PersistenceContext(unitName = "music_store_persistence")
    private EntityManager em;

    public RssFeedsJpaController() {
    }

    public void create(RssFeeds rssFeeds) throws RollbackFailureException {

        try {
            utx.begin();
            em.persist(rssFeeds);
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

    public void edit(RssFeeds rssFeeds) throws NonexistentEntityException, Exception {

        try {

            utx.begin();
            rssFeeds = em.merge(rssFeeds);
            utx.commit();
        } catch (NotSupportedException | SystemException | RollbackException | HeuristicMixedException | HeuristicRollbackException | SecurityException | IllegalStateException ex) {
            try {
                utx.rollback();
            } catch (IllegalStateException | SecurityException | SystemException re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = rssFeeds.getRssId();
                if (findRssFeeds(id) == null) {
                    throw new NonexistentEntityException("The rssFeeds with id " + id + " no longer exists.");
                }
            }
            throw ex;
        }
    }

    public void destroy(Integer id) throws IllegalOrphanException, NonexistentEntityException, NotSupportedException, SystemException, RollbackFailureException, RollbackException, HeuristicMixedException, HeuristicRollbackException {

        try {
            utx.begin();
            RssFeeds rssFeeds;
            try {
                rssFeeds = em.getReference(RssFeeds.class, id);
                rssFeeds.getRssId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The rssFeeds with id " + id + " no longer exists.", enfe);
            }
            em.remove(rssFeeds);
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

    public List<RssFeeds> findRssFeedsEntities() {
        return findRssFeedsEntities(true, -1, -1);
    }

    public List<RssFeeds> findRssFeedsEntities(int maxResults, int firstResult) {
        return findRssFeedsEntities(false, maxResults, firstResult);
    }

    private List<RssFeeds> findRssFeedsEntities(boolean all, int maxResults, int firstResult) {

        CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
        cq.select(cq.from(RssFeeds.class));
        Query q = em.createQuery(cq);
        if (!all) {
            q.setMaxResults(maxResults);
            q.setFirstResult(firstResult);
        }
        return q.getResultList();
    }

    public RssFeeds findRssFeeds(Integer id) {

        return em.find(RssFeeds.class, id);
    }

    public int getRssFeedsCount() {

        CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
        Root<RssFeeds> rt = cq.from(RssFeeds.class);
        cq.select(em.getCriteriaBuilder().count(rt));
        Query q = em.createQuery(cq);
        return ((Long) q.getSingleResult()).intValue();
    }

    /**
     * @return A random rss feed to display
     * @author Susan Vuu
     */
    public String getRandomRssFeed(){
        List<RssFeeds> rssFeeds = findRssFeedsEntities(true, 0, 0);
        Random random = new Random();
        int randomIndex = random.nextInt(rssFeeds.size());
        return rssFeeds.get(randomIndex).getLink();
    }
}
