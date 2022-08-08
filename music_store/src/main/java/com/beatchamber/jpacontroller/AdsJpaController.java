package com.beatchamber.jpacontroller;

import com.beatchamber.entities.Ads;
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
 * @author Massimo Di Girolamo
 */
@Named
@SessionScoped
public class AdsJpaController implements Serializable {

    private final static Logger LOG = LoggerFactory.getLogger(AdsJpaController.class);

    @Resource
    private UserTransaction utx;

    @PersistenceContext(unitName = "music_store_persistence")
    private EntityManager em;

    public AdsJpaController() {
    }

    public void create(Ads ads) throws RollbackFailureException {

        try {
            utx.begin();
            em.persist(ads);
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

    public void edit(Ads ads) throws NonexistentEntityException, Exception {

        try {
            utx.begin();
            ads = em.merge(ads);
            utx.commit();

        } catch (NotSupportedException | SystemException | RollbackException | HeuristicMixedException | HeuristicRollbackException | SecurityException | IllegalStateException ex) {
            try {
                utx.rollback();
            } catch (IllegalStateException | SecurityException | SystemException re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = ads.getAdId();
                if (findAds(id) == null) {
                    throw new NonexistentEntityException("The ads with id " + id + " no longer exists.");
                }
            }
            throw ex;
        }
    }

    public void destroy(Integer id) throws IllegalOrphanException, NonexistentEntityException, NotSupportedException, SystemException, RollbackFailureException, RollbackException, HeuristicMixedException, HeuristicRollbackException {

        try {
            utx.begin();
            Ads ads;
            try {
                ads = em.getReference(Ads.class, id);
                ads.getAdId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The ads with id " + id + " no longer exists.", enfe);
            }
            em.remove(ads);
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

    public List<Ads> findAdsEntities() {
        return findAdsEntities(true, -1, -1);
    }

    public List<Ads> findAdsEntities(int maxResults, int firstResult) {
        return findAdsEntities(false, maxResults, firstResult);
    }

    private List<Ads> findAdsEntities(boolean all, int maxResults, int firstResult) {

        CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
        cq.select(cq.from(Ads.class));
        Query q = em.createQuery(cq);
        if (!all) {
            q.setMaxResults(maxResults);
            q.setFirstResult(firstResult);
        }
        return q.getResultList();
    }

    public Ads findAds(Integer id) {

        return em.find(Ads.class, id);
    }

    public int getAdsCount() {

        CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
        Root<Ads> rt = cq.from(Ads.class);
        cq.select(em.getCriteriaBuilder().count(rt));
        Query q = em.createQuery(cq);
        return ((Long) q.getSingleResult()).intValue();
    }

    /**
     * @param id The ad id
     * @return The ad's path
     */
    public String getAdPath(int id){
        Ads foundAd = em.find(Ads.class, id);
        
        //Starting path
        String startingPath = "ads/";
        String adName = foundAd.getFileName().replace(" ", "_").toLowerCase();
        //Return the whole path
        return startingPath + adName + ".png";
    }
    
    /**
     * @param id
     * @return The link of the ad
     */
    public String getAdLink(int id){
        return em.find(Ads.class, id).getLink();
    }
}
