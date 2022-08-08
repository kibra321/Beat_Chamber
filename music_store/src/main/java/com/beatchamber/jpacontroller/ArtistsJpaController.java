package com.beatchamber.jpacontroller;

import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import com.beatchamber.entities.ArtistAlbums;
import com.beatchamber.entities.Artists;
import java.util.ArrayList;
import java.util.List;
import com.beatchamber.entities.ArtistsToTracks;
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
public class ArtistsJpaController implements Serializable {

    private final static Logger LOG = LoggerFactory.getLogger(ArtistsJpaController.class);

    @Resource
    private UserTransaction utx;

    @PersistenceContext(unitName = "music_store_persistence")
    private EntityManager em;

    public ArtistsJpaController() {
    }

    public void create(Artists artists) throws RollbackFailureException {
        if (artists.getArtistAlbumsList() == null) {
            artists.setArtistAlbumsList(new ArrayList<ArtistAlbums>());
        }
        if (artists.getArtistsToTracksList() == null) {
            artists.setArtistsToTracksList(new ArrayList<ArtistsToTracks>());
        }

        try {

            utx.begin();
            List<ArtistAlbums> attachedArtistAlbumsList = new ArrayList<ArtistAlbums>();
            for (ArtistAlbums artistAlbumsListArtistAlbumsToAttach : artists.getArtistAlbumsList()) {
                artistAlbumsListArtistAlbumsToAttach = em.getReference(artistAlbumsListArtistAlbumsToAttach.getClass(), artistAlbumsListArtistAlbumsToAttach.getTablekey());
                attachedArtistAlbumsList.add(artistAlbumsListArtistAlbumsToAttach);
            }
            artists.setArtistAlbumsList(attachedArtistAlbumsList);
            List<ArtistsToTracks> attachedArtistsToTracksList = new ArrayList<ArtistsToTracks>();
            for (ArtistsToTracks artistsToTracksListArtistsToTracksToAttach : artists.getArtistsToTracksList()) {
                artistsToTracksListArtistsToTracksToAttach = em.getReference(artistsToTracksListArtistsToTracksToAttach.getClass(), artistsToTracksListArtistsToTracksToAttach.getTablekey());
                attachedArtistsToTracksList.add(artistsToTracksListArtistsToTracksToAttach);
            }
            artists.setArtistsToTracksList(attachedArtistsToTracksList);
            em.persist(artists);
            for (ArtistAlbums artistAlbumsListArtistAlbums : artists.getArtistAlbumsList()) {
                Artists oldArtistIdOfArtistAlbumsListArtistAlbums = artistAlbumsListArtistAlbums.getArtistId();
                artistAlbumsListArtistAlbums.setArtistId(artists);
                artistAlbumsListArtistAlbums = em.merge(artistAlbumsListArtistAlbums);
                if (oldArtistIdOfArtistAlbumsListArtistAlbums != null) {
                    oldArtistIdOfArtistAlbumsListArtistAlbums.getArtistAlbumsList().remove(artistAlbumsListArtistAlbums);
                    oldArtistIdOfArtistAlbumsListArtistAlbums = em.merge(oldArtistIdOfArtistAlbumsListArtistAlbums);
                }
            }
            for (ArtistsToTracks artistsToTracksListArtistsToTracks : artists.getArtistsToTracksList()) {
                Artists oldArtistIdOfArtistsToTracksListArtistsToTracks = artistsToTracksListArtistsToTracks.getArtistId();
                artistsToTracksListArtistsToTracks.setArtistId(artists);
                artistsToTracksListArtistsToTracks = em.merge(artistsToTracksListArtistsToTracks);
                if (oldArtistIdOfArtistsToTracksListArtistsToTracks != null) {
                    oldArtistIdOfArtistsToTracksListArtistsToTracks.getArtistsToTracksList().remove(artistsToTracksListArtistsToTracks);
                    oldArtistIdOfArtistsToTracksListArtistsToTracks = em.merge(oldArtistIdOfArtistsToTracksListArtistsToTracks);
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

    public void edit(Artists artists) throws IllegalOrphanException, NonexistentEntityException, Exception {

        try {

            utx.begin();
            Artists persistentArtists = em.find(Artists.class, artists.getArtistId());
            List<ArtistAlbums> artistAlbumsListOld = persistentArtists.getArtistAlbumsList();
            List<ArtistAlbums> artistAlbumsListNew = artists.getArtistAlbumsList();
            List<ArtistsToTracks> artistsToTracksListOld = persistentArtists.getArtistsToTracksList();
            List<ArtistsToTracks> artistsToTracksListNew = artists.getArtistsToTracksList();
            List<String> illegalOrphanMessages = null;
            for (ArtistAlbums artistAlbumsListOldArtistAlbums : artistAlbumsListOld) {
                if (!artistAlbumsListNew.contains(artistAlbumsListOldArtistAlbums)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain ArtistAlbums " + artistAlbumsListOldArtistAlbums + " since its artistId field is not nullable.");
                }
            }
            for (ArtistsToTracks artistsToTracksListOldArtistsToTracks : artistsToTracksListOld) {
                if (!artistsToTracksListNew.contains(artistsToTracksListOldArtistsToTracks)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain ArtistsToTracks " + artistsToTracksListOldArtistsToTracks + " since its artistId field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            List<ArtistAlbums> attachedArtistAlbumsListNew = new ArrayList<ArtistAlbums>();
            for (ArtistAlbums artistAlbumsListNewArtistAlbumsToAttach : artistAlbumsListNew) {
                artistAlbumsListNewArtistAlbumsToAttach = em.getReference(artistAlbumsListNewArtistAlbumsToAttach.getClass(), artistAlbumsListNewArtistAlbumsToAttach.getTablekey());
                attachedArtistAlbumsListNew.add(artistAlbumsListNewArtistAlbumsToAttach);
            }
            artistAlbumsListNew = attachedArtistAlbumsListNew;
            artists.setArtistAlbumsList(artistAlbumsListNew);
            List<ArtistsToTracks> attachedArtistsToTracksListNew = new ArrayList<ArtistsToTracks>();
            for (ArtistsToTracks artistsToTracksListNewArtistsToTracksToAttach : artistsToTracksListNew) {
                artistsToTracksListNewArtistsToTracksToAttach = em.getReference(artistsToTracksListNewArtistsToTracksToAttach.getClass(), artistsToTracksListNewArtistsToTracksToAttach.getTablekey());
                attachedArtistsToTracksListNew.add(artistsToTracksListNewArtistsToTracksToAttach);
            }
            artistsToTracksListNew = attachedArtistsToTracksListNew;
            artists.setArtistsToTracksList(artistsToTracksListNew);
            artists = em.merge(artists);
            for (ArtistAlbums artistAlbumsListNewArtistAlbums : artistAlbumsListNew) {
                if (!artistAlbumsListOld.contains(artistAlbumsListNewArtistAlbums)) {
                    Artists oldArtistIdOfArtistAlbumsListNewArtistAlbums = artistAlbumsListNewArtistAlbums.getArtistId();
                    artistAlbumsListNewArtistAlbums.setArtistId(artists);
                    artistAlbumsListNewArtistAlbums = em.merge(artistAlbumsListNewArtistAlbums);
                    if (oldArtistIdOfArtistAlbumsListNewArtistAlbums != null && !oldArtistIdOfArtistAlbumsListNewArtistAlbums.equals(artists)) {
                        oldArtistIdOfArtistAlbumsListNewArtistAlbums.getArtistAlbumsList().remove(artistAlbumsListNewArtistAlbums);
                        oldArtistIdOfArtistAlbumsListNewArtistAlbums = em.merge(oldArtistIdOfArtistAlbumsListNewArtistAlbums);
                    }
                }
            }
            for (ArtistsToTracks artistsToTracksListNewArtistsToTracks : artistsToTracksListNew) {
                if (!artistsToTracksListOld.contains(artistsToTracksListNewArtistsToTracks)) {
                    Artists oldArtistIdOfArtistsToTracksListNewArtistsToTracks = artistsToTracksListNewArtistsToTracks.getArtistId();
                    artistsToTracksListNewArtistsToTracks.setArtistId(artists);
                    artistsToTracksListNewArtistsToTracks = em.merge(artistsToTracksListNewArtistsToTracks);
                    if (oldArtistIdOfArtistsToTracksListNewArtistsToTracks != null && !oldArtistIdOfArtistsToTracksListNewArtistsToTracks.equals(artists)) {
                        oldArtistIdOfArtistsToTracksListNewArtistsToTracks.getArtistsToTracksList().remove(artistsToTracksListNewArtistsToTracks);
                        oldArtistIdOfArtistsToTracksListNewArtistsToTracks = em.merge(oldArtistIdOfArtistsToTracksListNewArtistsToTracks);
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
                Integer id = artists.getArtistId();
                if (findArtists(id) == null) {
                    throw new NonexistentEntityException("The artists with id " + id + " no longer exists.");
                }
            }
            throw ex;
        }
    }

    public void destroy(Integer id) throws IllegalOrphanException, NonexistentEntityException, RollbackFailureException, NotSupportedException, SystemException, RollbackException, HeuristicMixedException, HeuristicRollbackException {

        try {

            utx.begin();
            Artists artists;
            try {
                artists = em.getReference(Artists.class, id);
                artists.getArtistId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The artists with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<ArtistAlbums> artistAlbumsListOrphanCheck = artists.getArtistAlbumsList();
            for (ArtistAlbums artistAlbumsListOrphanCheckArtistAlbums : artistAlbumsListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Artists (" + artists + ") cannot be destroyed since the ArtistAlbums " + artistAlbumsListOrphanCheckArtistAlbums + " in its artistAlbumsList field has a non-nullable artistId field.");
            }
            List<ArtistsToTracks> artistsToTracksListOrphanCheck = artists.getArtistsToTracksList();
            for (ArtistsToTracks artistsToTracksListOrphanCheckArtistsToTracks : artistsToTracksListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Artists (" + artists + ") cannot be destroyed since the ArtistsToTracks " + artistsToTracksListOrphanCheckArtistsToTracks + " in its artistsToTracksList field has a non-nullable artistId field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            em.remove(artists);
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

    public List<Artists> findArtistsEntities() {
        return findArtistsEntities(true, -1, -1);
    }

    public List<Artists> findArtistsEntities(int maxResults, int firstResult) {
        return findArtistsEntities(false, maxResults, firstResult);
    }

    private List<Artists> findArtistsEntities(boolean all, int maxResults, int firstResult) {

            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Artists.class));
            Query q = em.createQuery(cq);
            if (!all) {
                q.setMaxResults(maxResults);
                q.setFirstResult(firstResult);
            }
            return q.getResultList();

    }

    public Artists findArtists(Integer id) {

            return em.find(Artists.class, id);

    }

    public int getArtistsCount() {

            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Artists> rt = cq.from(Artists.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();

    }
    
    
    
}
