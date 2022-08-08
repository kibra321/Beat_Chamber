package com.beatchamber.jpacontroller;

import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import com.beatchamber.entities.Albums;
import com.beatchamber.entities.GenreToAlbum;
import com.beatchamber.entities.Genres;
import com.beatchamber.exceptions.IllegalOrphanException;
import com.beatchamber.exceptions.RollbackFailureException;
import com.beatchamber.exceptions.NonexistentEntityException;
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
public class GenreToAlbumJpaController implements Serializable {

    private final static Logger LOG = LoggerFactory.getLogger(GenreToAlbumJpaController.class);

    @Resource
    private UserTransaction utx;

    @PersistenceContext(unitName = "music_store_persistence")
    private EntityManager em;

    public GenreToAlbumJpaController() {
    }

    public void create(GenreToAlbum genreToAlbum) throws RollbackFailureException {

        try {
            utx.begin();
            Albums albumNumber = genreToAlbum.getAlbumNumber();
            if (albumNumber != null) {
                albumNumber = em.getReference(albumNumber.getClass(), albumNumber.getAlbumNumber());
                genreToAlbum.setAlbumNumber(albumNumber);
            }
            Genres genreId = genreToAlbum.getGenreId();
            if (genreId != null) {
                genreId = em.getReference(genreId.getClass(), genreId.getGenreId());
                genreToAlbum.setGenreId(genreId);
            }
            em.persist(genreToAlbum);
            if (albumNumber != null) {
                albumNumber.getGenreToAlbumList().add(genreToAlbum);
                albumNumber = em.merge(albumNumber);
            }
            if (genreId != null) {
                genreId.getGenreToAlbumList().add(genreToAlbum);
                genreId = em.merge(genreId);
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

    public void edit(GenreToAlbum genreToAlbum) throws NonexistentEntityException, Exception {

        try {
            utx.begin();
            GenreToAlbum persistentGenreToAlbum = em.find(GenreToAlbum.class, genreToAlbum.getTablekey());
            Albums albumNumberOld = persistentGenreToAlbum.getAlbumNumber();
            Albums albumNumberNew = genreToAlbum.getAlbumNumber();
            Genres genreIdOld = persistentGenreToAlbum.getGenreId();
            Genres genreIdNew = genreToAlbum.getGenreId();
            if (albumNumberNew != null) {
                albumNumberNew = em.getReference(albumNumberNew.getClass(), albumNumberNew.getAlbumNumber());
                genreToAlbum.setAlbumNumber(albumNumberNew);
            }
            if (genreIdNew != null) {
                genreIdNew = em.getReference(genreIdNew.getClass(), genreIdNew.getGenreId());
                genreToAlbum.setGenreId(genreIdNew);
            }
            genreToAlbum = em.merge(genreToAlbum);
            if (albumNumberOld != null && !albumNumberOld.equals(albumNumberNew)) {
                albumNumberOld.getGenreToAlbumList().remove(genreToAlbum);
                albumNumberOld = em.merge(albumNumberOld);
            }
            if (albumNumberNew != null && !albumNumberNew.equals(albumNumberOld)) {
                albumNumberNew.getGenreToAlbumList().add(genreToAlbum);
                albumNumberNew = em.merge(albumNumberNew);
            }
            if (genreIdOld != null && !genreIdOld.equals(genreIdNew)) {
                genreIdOld.getGenreToAlbumList().remove(genreToAlbum);
                genreIdOld = em.merge(genreIdOld);
            }
            if (genreIdNew != null && !genreIdNew.equals(genreIdOld)) {
                genreIdNew.getGenreToAlbumList().add(genreToAlbum);
                genreIdNew = em.merge(genreIdNew);
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
                Integer id = genreToAlbum.getTablekey();
                if (findGenreToAlbum(id) == null) {
                    throw new NonexistentEntityException("The genreToAlbum with id " + id + " no longer exists.");
                }
            }
            throw ex;
        }
    }

    public void destroy(Integer id) throws IllegalOrphanException, com.beatchamber.exceptions.NonexistentEntityException, NotSupportedException, SystemException, RollbackFailureException, RollbackException, HeuristicMixedException, HeuristicRollbackException {

        try {

            utx.begin();
            GenreToAlbum genreToAlbum;
            try {
                genreToAlbum = em.getReference(GenreToAlbum.class, id);
                genreToAlbum.getTablekey();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The genreToAlbum with id " + id + " no longer exists.", enfe);
            }
            Albums albumNumber = genreToAlbum.getAlbumNumber();
            if (albumNumber != null) {
                albumNumber.getGenreToAlbumList().remove(genreToAlbum);
                albumNumber = em.merge(albumNumber);
            }
            Genres genreId = genreToAlbum.getGenreId();
            if (genreId != null) {
                genreId.getGenreToAlbumList().remove(genreToAlbum);
                genreId = em.merge(genreId);
            }
            em.remove(genreToAlbum);
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

    public List<GenreToAlbum> findGenreToAlbumEntities() {
        return findGenreToAlbumEntities(true, -1, -1);
    }

    public List<GenreToAlbum> findGenreToAlbumEntities(int maxResults, int firstResult) {
        return findGenreToAlbumEntities(false, maxResults, firstResult);
    }

    private List<GenreToAlbum> findGenreToAlbumEntities(boolean all, int maxResults, int firstResult) {

        CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
        cq.select(cq.from(GenreToAlbum.class));
        Query q = em.createQuery(cq);
        if (!all) {
            q.setMaxResults(maxResults);
            q.setFirstResult(firstResult);
        }
        return q.getResultList();
    }

    public GenreToAlbum findGenreToAlbum(Integer id) {

        return em.find(GenreToAlbum.class, id);
    }

    public int getGenreToAlbumCount() {

        CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
        Root<GenreToAlbum> rt = cq.from(GenreToAlbum.class);
        cq.select(em.getCriteriaBuilder().count(rt));
        Query q = em.createQuery(cq);
        return ((Long) q.getSingleResult()).intValue();
    }

}
