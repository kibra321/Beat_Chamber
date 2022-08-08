package com.beatchamber.jpacontroller;

import com.beatchamber.entities.GenreToTracks;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import com.beatchamber.entities.Genres;
import com.beatchamber.entities.Tracks;
import com.beatchamber.exceptions.IllegalOrphanException;
import com.beatchamber.exceptions.NonexistentEntityException;
import com.beatchamber.exceptions.RollbackFailureException;
import java.util.List;
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
public class GenreToTracksJpaController implements Serializable {

    private final static Logger LOG = LoggerFactory.getLogger(GenreToTracksJpaController.class);

    @Resource
    private UserTransaction utx;

    @PersistenceContext(unitName = "music_store_persistence")
    private EntityManager em;

    public GenreToTracksJpaController() {
    }

    public void create(GenreToTracks genreToTracks) throws RollbackFailureException {

        try {
            utx.begin();
            Genres genreId = genreToTracks.getGenreId();
            if (genreId != null) {
                genreId = em.getReference(genreId.getClass(), genreId.getGenreId());
                genreToTracks.setGenreId(genreId);
            }
            Tracks trackId = genreToTracks.getTrackId();
            if (trackId != null) {
                trackId = em.getReference(trackId.getClass(), trackId.getTrackId());
                genreToTracks.setTrackId(trackId);
            }
            em.persist(genreToTracks);
            if (genreId != null) {
                genreId.getGenreToTracksList().add(genreToTracks);
                genreId = em.merge(genreId);
            }
            if (trackId != null) {
                trackId.getGenreToTracksList().add(genreToTracks);
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

    public void edit(GenreToTracks genreToTracks) throws NonexistentEntityException, Exception {

        try {
            utx.begin();
            GenreToTracks persistentGenreToTracks = em.find(GenreToTracks.class, genreToTracks.getTablekey());
            Genres genreIdOld = persistentGenreToTracks.getGenreId();
            Genres genreIdNew = genreToTracks.getGenreId();
            Tracks trackIdOld = persistentGenreToTracks.getTrackId();
            Tracks trackIdNew = genreToTracks.getTrackId();
            if (genreIdNew != null) {
                genreIdNew = em.getReference(genreIdNew.getClass(), genreIdNew.getGenreId());
                genreToTracks.setGenreId(genreIdNew);
            }
            if (trackIdNew != null) {
                trackIdNew = em.getReference(trackIdNew.getClass(), trackIdNew.getTrackId());
                genreToTracks.setTrackId(trackIdNew);
            }
            genreToTracks = em.merge(genreToTracks);
            if (genreIdOld != null && !genreIdOld.equals(genreIdNew)) {
                genreIdOld.getGenreToTracksList().remove(genreToTracks);
                genreIdOld = em.merge(genreIdOld);
            }
            if (genreIdNew != null && !genreIdNew.equals(genreIdOld)) {
                genreIdNew.getGenreToTracksList().add(genreToTracks);
                genreIdNew = em.merge(genreIdNew);
            }
            if (trackIdOld != null && !trackIdOld.equals(trackIdNew)) {
                trackIdOld.getGenreToTracksList().remove(genreToTracks);
                trackIdOld = em.merge(trackIdOld);
            }
            if (trackIdNew != null && !trackIdNew.equals(trackIdOld)) {
                trackIdNew.getGenreToTracksList().add(genreToTracks);
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
                Integer id = genreToTracks.getTablekey();
                if (findGenreToTracks(id) == null) {
                    throw new NonexistentEntityException("The genreToTracks with id " + id + " no longer exists.");
                }
            }
            throw ex;
        }
    }

    public void destroy(Integer id) throws IllegalOrphanException, NonexistentEntityException, NotSupportedException, SystemException, RollbackFailureException, RollbackException, HeuristicMixedException, HeuristicRollbackException {

        try {
            utx.begin();
            GenreToTracks genreToTracks;
            try {
                genreToTracks = em.getReference(GenreToTracks.class, id);
                genreToTracks.getTablekey();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The genreToTracks with id " + id + " no longer exists.", enfe);
            }
            Genres genreId = genreToTracks.getGenreId();
            if (genreId != null) {
                genreId.getGenreToTracksList().remove(genreToTracks);
                genreId = em.merge(genreId);
            }
            Tracks trackId = genreToTracks.getTrackId();
            if (trackId != null) {
                trackId.getGenreToTracksList().remove(genreToTracks);
                trackId = em.merge(trackId);
            }
            em.remove(genreToTracks);
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

    public List<GenreToTracks> findGenreToTracksEntities() {
        return findGenreToTracksEntities(true, -1, -1);
    }

    public List<GenreToTracks> findGenreToTracksEntities(int maxResults, int firstResult) {
        return findGenreToTracksEntities(false, maxResults, firstResult);
    }

    private List<GenreToTracks> findGenreToTracksEntities(boolean all, int maxResults, int firstResult) {

        CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
        cq.select(cq.from(GenreToTracks.class));
        Query q = em.createQuery(cq);
        if (!all) {
            q.setMaxResults(maxResults);
            q.setFirstResult(firstResult);
        }
        return q.getResultList();
    }

    public GenreToTracks findGenreToTracks(Integer id) {

        return em.find(GenreToTracks.class, id);
    }

    public int getGenreToTracksCount() {

        CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
        Root<GenreToTracks> rt = cq.from(GenreToTracks.class);
        cq.select(em.getCriteriaBuilder().count(rt));
        Query q = em.createQuery(cq);
        return ((Long) q.getSingleResult()).intValue();
    }

}
