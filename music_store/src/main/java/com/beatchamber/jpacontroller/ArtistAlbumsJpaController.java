package com.beatchamber.jpacontroller;

import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import com.beatchamber.entities.Albums;
import com.beatchamber.entities.ArtistAlbums;
import com.beatchamber.entities.Artists;
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
public class ArtistAlbumsJpaController implements Serializable {

    private final static Logger LOG = LoggerFactory.getLogger(ArtistAlbumsJpaController.class);

    @Resource
    private UserTransaction utx;

    @PersistenceContext(unitName = "music_store_persistence")
    private EntityManager em;

    public ArtistAlbumsJpaController() {
    }

    public void create(ArtistAlbums artistAlbums) throws RollbackFailureException {

        try {

            utx.begin();
            Albums albumNumber = artistAlbums.getAlbumNumber();
            if (albumNumber != null) {
                albumNumber = em.getReference(albumNumber.getClass(), albumNumber.getAlbumNumber());
                artistAlbums.setAlbumNumber(albumNumber);
            }
            Artists artistId = artistAlbums.getArtistId();
            if (artistId != null) {
                artistId = em.getReference(artistId.getClass(), artistId.getArtistId());
                artistAlbums.setArtistId(artistId);
            }
            em.persist(artistAlbums);
            if (albumNumber != null) {
                albumNumber.getArtistAlbumsList().add(artistAlbums);
                albumNumber = em.merge(albumNumber);
            }
            if (artistId != null) {
                artistId.getArtistAlbumsList().add(artistAlbums);
                artistId = em.merge(artistId);
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

    public void edit(ArtistAlbums artistAlbums) throws NonexistentEntityException, Exception {

        try {
            utx.begin();
            ArtistAlbums persistentArtistAlbums = em.find(ArtistAlbums.class, artistAlbums.getTablekey());
            Albums albumNumberOld = persistentArtistAlbums.getAlbumNumber();
            Albums albumNumberNew = artistAlbums.getAlbumNumber();
            Artists artistIdOld = persistentArtistAlbums.getArtistId();
            Artists artistIdNew = artistAlbums.getArtistId();
            if (albumNumberNew != null) {
                albumNumberNew = em.getReference(albumNumberNew.getClass(), albumNumberNew.getAlbumNumber());
                artistAlbums.setAlbumNumber(albumNumberNew);
            }
            if (artistIdNew != null) {
                artistIdNew = em.getReference(artistIdNew.getClass(), artistIdNew.getArtistId());
                artistAlbums.setArtistId(artistIdNew);
            }
            artistAlbums = em.merge(artistAlbums);
            if (albumNumberOld != null && !albumNumberOld.equals(albumNumberNew)) {
                albumNumberOld.getArtistAlbumsList().remove(artistAlbums);
                albumNumberOld = em.merge(albumNumberOld);
            }
            if (albumNumberNew != null && !albumNumberNew.equals(albumNumberOld)) {
                albumNumberNew.getArtistAlbumsList().add(artistAlbums);
                albumNumberNew = em.merge(albumNumberNew);
            }
            if (artistIdOld != null && !artistIdOld.equals(artistIdNew)) {
                artistIdOld.getArtistAlbumsList().remove(artistAlbums);
                artistIdOld = em.merge(artistIdOld);
            }
            if (artistIdNew != null && !artistIdNew.equals(artistIdOld)) {
                artistIdNew.getArtistAlbumsList().add(artistAlbums);
                artistIdNew = em.merge(artistIdNew);
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
                Integer id = artistAlbums.getTablekey();
                if (findArtistAlbums(id) == null) {
                    throw new NonexistentEntityException("The artistAlbum with id " + id + " no longer exists.");
                }
            }
            throw ex;
        }
    }

    public void destroy(Integer id) throws com.beatchamber.exceptions.NonexistentEntityException, NotSupportedException, SystemException, RollbackFailureException, RollbackException, HeuristicMixedException, HeuristicRollbackException, NonexistentEntityException {

        try {
            utx.begin();
            ArtistAlbums artistAlbums;
            try {
                artistAlbums = em.getReference(ArtistAlbums.class, id);
                artistAlbums.getTablekey();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The artistAlbums with id " + id + " no longer exists.", enfe);
            }
            Albums albumNumber = artistAlbums.getAlbumNumber();
            if (albumNumber != null) {
                albumNumber.getArtistAlbumsList().remove(artistAlbums);
                albumNumber = em.merge(albumNumber);
            }
            Artists artistId = artistAlbums.getArtistId();
            if (artistId != null) {
                artistId.getArtistAlbumsList().remove(artistAlbums);
                artistId = em.merge(artistId);
            }
            em.remove(artistAlbums);
            utx.commit();

        } catch (NotSupportedException | SystemException | com.beatchamber.exceptions.NonexistentEntityException | RollbackException | HeuristicMixedException | HeuristicRollbackException | SecurityException | IllegalStateException ex) {
            try {
                utx.rollback();
            } catch (IllegalStateException | SecurityException | SystemException re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            throw ex;
        }
    }

    public List<ArtistAlbums> findArtistAlbumsEntities() {
        return findArtistAlbumsEntities(true, -1, -1);
    }

    public List<ArtistAlbums> findArtistAlbumsEntities(int maxResults, int firstResult) {
        return findArtistAlbumsEntities(false, maxResults, firstResult);
    }

    private List<ArtistAlbums> findArtistAlbumsEntities(boolean all, int maxResults, int firstResult) {

        CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
        cq.select(cq.from(ArtistAlbums.class));
        Query q = em.createQuery(cq);
        if (!all) {
            q.setMaxResults(maxResults);
            q.setFirstResult(firstResult);
        }
        return q.getResultList();
    }

    public ArtistAlbums findArtistAlbums(Integer id) {

        return em.find(ArtistAlbums.class, id);
    }

    public int getArtistAlbumsCount() {

        CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
        Root<ArtistAlbums> rt = cq.from(ArtistAlbums.class);
        cq.select(em.getCriteriaBuilder().count(rt));
        Query q = em.createQuery(cq);
        return ((Long) q.getSingleResult()).intValue();
    }

}
