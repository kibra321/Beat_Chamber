package com.beatchamber.jpacontroller;

import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import com.beatchamber.entities.GenreToTracks;
import java.util.ArrayList;
import java.util.List;
import com.beatchamber.entities.GenreToAlbum;
import com.beatchamber.entities.Genres;
import com.beatchamber.exceptions.IllegalOrphanException;
import com.beatchamber.exceptions.NonexistentEntityException;
import com.beatchamber.exceptions.RollbackFailureException;
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
public class GenresJpaController implements Serializable {

    private final static Logger LOG = LoggerFactory.getLogger(GenresJpaController.class);

    @Resource
    private UserTransaction utx;

    @PersistenceContext(unitName = "music_store_persistence")
    private EntityManager em;

    public GenresJpaController() {
    }

    public void create(Genres genres) throws RollbackFailureException {
        if (genres.getGenreToTracksList() == null) {
            genres.setGenreToTracksList(new ArrayList<GenreToTracks>());
        }
        if (genres.getGenreToAlbumList() == null) {
            genres.setGenreToAlbumList(new ArrayList<GenreToAlbum>());
        }

        try {
            utx.begin();
            List<GenreToTracks> attachedGenreToTracksList = new ArrayList<GenreToTracks>();
            for (GenreToTracks genreToTracksListGenreToTracksToAttach : genres.getGenreToTracksList()) {
                genreToTracksListGenreToTracksToAttach = em.getReference(genreToTracksListGenreToTracksToAttach.getClass(), genreToTracksListGenreToTracksToAttach.getTablekey());
                attachedGenreToTracksList.add(genreToTracksListGenreToTracksToAttach);
            }
            genres.setGenreToTracksList(attachedGenreToTracksList);
            List<GenreToAlbum> attachedGenreToAlbumList = new ArrayList<GenreToAlbum>();
            for (GenreToAlbum genreToAlbumListGenreToAlbumToAttach : genres.getGenreToAlbumList()) {
                genreToAlbumListGenreToAlbumToAttach = em.getReference(genreToAlbumListGenreToAlbumToAttach.getClass(), genreToAlbumListGenreToAlbumToAttach.getTablekey());
                attachedGenreToAlbumList.add(genreToAlbumListGenreToAlbumToAttach);
            }
            genres.setGenreToAlbumList(attachedGenreToAlbumList);
            em.persist(genres);
            for (GenreToTracks genreToTracksListGenreToTracks : genres.getGenreToTracksList()) {
                Genres oldGenreIdOfGenreToTracksListGenreToTracks = genreToTracksListGenreToTracks.getGenreId();
                genreToTracksListGenreToTracks.setGenreId(genres);
                genreToTracksListGenreToTracks = em.merge(genreToTracksListGenreToTracks);
                if (oldGenreIdOfGenreToTracksListGenreToTracks != null) {
                    oldGenreIdOfGenreToTracksListGenreToTracks.getGenreToTracksList().remove(genreToTracksListGenreToTracks);
                    oldGenreIdOfGenreToTracksListGenreToTracks = em.merge(oldGenreIdOfGenreToTracksListGenreToTracks);
                }
            }
            for (GenreToAlbum genreToAlbumListGenreToAlbum : genres.getGenreToAlbumList()) {
                Genres oldGenreIdOfGenreToAlbumListGenreToAlbum = genreToAlbumListGenreToAlbum.getGenreId();
                genreToAlbumListGenreToAlbum.setGenreId(genres);
                genreToAlbumListGenreToAlbum = em.merge(genreToAlbumListGenreToAlbum);
                if (oldGenreIdOfGenreToAlbumListGenreToAlbum != null) {
                    oldGenreIdOfGenreToAlbumListGenreToAlbum.getGenreToAlbumList().remove(genreToAlbumListGenreToAlbum);
                    oldGenreIdOfGenreToAlbumListGenreToAlbum = em.merge(oldGenreIdOfGenreToAlbumListGenreToAlbum);
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

    public void edit(Genres genres) throws NonexistentEntityException, Exception {

        try {
            utx.begin();
            Genres persistentGenres = em.find(Genres.class, genres.getGenreId());
            List<GenreToTracks> genreToTracksListOld = persistentGenres.getGenreToTracksList();
            List<GenreToTracks> genreToTracksListNew = genres.getGenreToTracksList();
            List<GenreToAlbum> genreToAlbumListOld = persistentGenres.getGenreToAlbumList();
            List<GenreToAlbum> genreToAlbumListNew = genres.getGenreToAlbumList();
            List<GenreToTracks> attachedGenreToTracksListNew = new ArrayList<GenreToTracks>();
            for (GenreToTracks genreToTracksListNewGenreToTracksToAttach : genreToTracksListNew) {
                genreToTracksListNewGenreToTracksToAttach = em.getReference(genreToTracksListNewGenreToTracksToAttach.getClass(), genreToTracksListNewGenreToTracksToAttach.getTablekey());
                attachedGenreToTracksListNew.add(genreToTracksListNewGenreToTracksToAttach);
            }
            genreToTracksListNew = attachedGenreToTracksListNew;
            genres.setGenreToTracksList(genreToTracksListNew);
            List<GenreToAlbum> attachedGenreToAlbumListNew = new ArrayList<GenreToAlbum>();
            for (GenreToAlbum genreToAlbumListNewGenreToAlbumToAttach : genreToAlbumListNew) {
                genreToAlbumListNewGenreToAlbumToAttach = em.getReference(genreToAlbumListNewGenreToAlbumToAttach.getClass(), genreToAlbumListNewGenreToAlbumToAttach.getTablekey());
                attachedGenreToAlbumListNew.add(genreToAlbumListNewGenreToAlbumToAttach);
            }
            genreToAlbumListNew = attachedGenreToAlbumListNew;
            genres.setGenreToAlbumList(genreToAlbumListNew);
            genres = em.merge(genres);
            for (GenreToTracks genreToTracksListOldGenreToTracks : genreToTracksListOld) {
                if (!genreToTracksListNew.contains(genreToTracksListOldGenreToTracks)) {
                    genreToTracksListOldGenreToTracks.setGenreId(null);
                    genreToTracksListOldGenreToTracks = em.merge(genreToTracksListOldGenreToTracks);
                }
            }
            for (GenreToTracks genreToTracksListNewGenreToTracks : genreToTracksListNew) {
                if (!genreToTracksListOld.contains(genreToTracksListNewGenreToTracks)) {
                    Genres oldGenreIdOfGenreToTracksListNewGenreToTracks = genreToTracksListNewGenreToTracks.getGenreId();
                    genreToTracksListNewGenreToTracks.setGenreId(genres);
                    genreToTracksListNewGenreToTracks = em.merge(genreToTracksListNewGenreToTracks);
                    if (oldGenreIdOfGenreToTracksListNewGenreToTracks != null && !oldGenreIdOfGenreToTracksListNewGenreToTracks.equals(genres)) {
                        oldGenreIdOfGenreToTracksListNewGenreToTracks.getGenreToTracksList().remove(genreToTracksListNewGenreToTracks);
                        oldGenreIdOfGenreToTracksListNewGenreToTracks = em.merge(oldGenreIdOfGenreToTracksListNewGenreToTracks);
                    }
                }
            }
            for (GenreToAlbum genreToAlbumListOldGenreToAlbum : genreToAlbumListOld) {
                if (!genreToAlbumListNew.contains(genreToAlbumListOldGenreToAlbum)) {
                    genreToAlbumListOldGenreToAlbum.setGenreId(null);
                    genreToAlbumListOldGenreToAlbum = em.merge(genreToAlbumListOldGenreToAlbum);
                }
            }
            for (GenreToAlbum genreToAlbumListNewGenreToAlbum : genreToAlbumListNew) {
                if (!genreToAlbumListOld.contains(genreToAlbumListNewGenreToAlbum)) {
                    Genres oldGenreIdOfGenreToAlbumListNewGenreToAlbum = genreToAlbumListNewGenreToAlbum.getGenreId();
                    genreToAlbumListNewGenreToAlbum.setGenreId(genres);
                    genreToAlbumListNewGenreToAlbum = em.merge(genreToAlbumListNewGenreToAlbum);
                    if (oldGenreIdOfGenreToAlbumListNewGenreToAlbum != null && !oldGenreIdOfGenreToAlbumListNewGenreToAlbum.equals(genres)) {
                        oldGenreIdOfGenreToAlbumListNewGenreToAlbum.getGenreToAlbumList().remove(genreToAlbumListNewGenreToAlbum);
                        oldGenreIdOfGenreToAlbumListNewGenreToAlbum = em.merge(oldGenreIdOfGenreToAlbumListNewGenreToAlbum);
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
                Integer id = genres.getGenreId();
                if (findGenres(id) == null) {
                    throw new NonexistentEntityException("The album with id " + id + " no longer exists.");
                }
            }
            throw ex;
        }
    }
    

    public void destroy(Integer id) throws IllegalOrphanException, NonexistentEntityException, NotSupportedException, SystemException, RollbackFailureException, RollbackException, HeuristicMixedException, HeuristicRollbackException {

        try {
            utx.begin();
            Genres genres;
            try {
                genres = em.getReference(Genres.class, id);
                genres.getGenreId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The genres with id " + id + " no longer exists.", enfe);
            }
            List<GenreToTracks> genreToTracksList = genres.getGenreToTracksList();
            for (GenreToTracks genreToTracksListGenreToTracks : genreToTracksList) {
                genreToTracksListGenreToTracks.setGenreId(null);
                genreToTracksListGenreToTracks = em.merge(genreToTracksListGenreToTracks);
            }
            List<GenreToAlbum> genreToAlbumList = genres.getGenreToAlbumList();
            for (GenreToAlbum genreToAlbumListGenreToAlbum : genreToAlbumList) {
                genreToAlbumListGenreToAlbum.setGenreId(null);
                genreToAlbumListGenreToAlbum = em.merge(genreToAlbumListGenreToAlbum);
            }
            em.remove(genres);
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

    public List<Genres> findGenresEntities() {
        return findGenresEntities(true, -1, -1);
    }

    public List<Genres> findGenresEntities(int maxResults, int firstResult) {
        return findGenresEntities(false, maxResults, firstResult);
    }

    private List<Genres> findGenresEntities(boolean all, int maxResults, int firstResult) {

        CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
        cq.select(cq.from(Genres.class));
        Query q = em.createQuery(cq);
        if (!all) {
            q.setMaxResults(maxResults);
            q.setFirstResult(firstResult);
        }
        return q.getResultList();

    }

    public Genres findGenres(Integer id) {

        return em.find(Genres.class, id);

    }

    public int getGenresCount() {

        CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
        Root<Genres> rt = cq.from(Genres.class);
        cq.select(em.getCriteriaBuilder().count(rt));
        Query q = em.createQuery(cq);
        return ((Long) q.getSingleResult()).intValue();
    }

}
