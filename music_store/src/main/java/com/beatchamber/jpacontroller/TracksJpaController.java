package com.beatchamber.jpacontroller;

import com.beatchamber.beans.CookieManager;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.*;
import com.beatchamber.entities.CustomerReviews;
import com.beatchamber.entities.GenreToTracks;
import com.beatchamber.entities.Tracks;
import com.beatchamber.entities.Albums;
import com.beatchamber.entities.Artists;
import com.beatchamber.entities.Genres;
import com.beatchamber.exceptions.IllegalOrphanException;
import com.beatchamber.exceptions.NonexistentEntityException;
import com.beatchamber.exceptions.RollbackFailureException;
import javax.annotation.Resource;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Join;
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
@Named("tracksController")
@SessionScoped
public class TracksJpaController implements Serializable {

    private final static Logger LOG = LoggerFactory.getLogger(TracksJpaController.class);

    @Resource
    private UserTransaction utx;

    @PersistenceContext(unitName = "music_store_persistence")
    private EntityManager em;

    private ArrayList<Tracks> listOfTracksInTheCart = new ArrayList<Tracks>();
    
    @Inject
    private OrdersJpaController orderController;
    
    @Inject 
    private OrderTrackJpaController trackOrderController;

    public TracksJpaController() {
    }

    public void create(Tracks tracks) throws RollbackFailureException {
        if (tracks.getCustomerReviewsList() == null) {
            tracks.setCustomerReviewsList(new ArrayList<CustomerReviews>());
        }
        if (tracks.getGenreToTracksList() == null) {
            tracks.setGenreToTracksList(new ArrayList<GenreToTracks>());
        }

        try {

            utx.begin();
            Albums albumNumber = tracks.getAlbumNumber();
            if (albumNumber != null) {
                albumNumber = em.getReference(albumNumber.getClass(), albumNumber.getAlbumNumber());
                tracks.setAlbumNumber(albumNumber);
            }
            List<CustomerReviews> attachedCustomerReviewsList = new ArrayList<CustomerReviews>();
            for (CustomerReviews customerReviewsListCustomerReviewsToAttach : tracks.getCustomerReviewsList()) {
                customerReviewsListCustomerReviewsToAttach = em.getReference(customerReviewsListCustomerReviewsToAttach.getClass(), customerReviewsListCustomerReviewsToAttach.getReviewNumber());
                attachedCustomerReviewsList.add(customerReviewsListCustomerReviewsToAttach);
            }
            tracks.setCustomerReviewsList(attachedCustomerReviewsList);
            List<GenreToTracks> attachedGenreToTracksList = new ArrayList<GenreToTracks>();
            for (GenreToTracks genreToTracksListGenreToTracksToAttach : tracks.getGenreToTracksList()) {
                genreToTracksListGenreToTracksToAttach = em.getReference(genreToTracksListGenreToTracksToAttach.getClass(), genreToTracksListGenreToTracksToAttach.getTablekey());
                attachedGenreToTracksList.add(genreToTracksListGenreToTracksToAttach);
            }
            tracks.setGenreToTracksList(attachedGenreToTracksList);
            em.persist(tracks);
            if (albumNumber != null) {
                albumNumber.getTracksList().add(tracks);
                albumNumber = em.merge(albumNumber);
            }
            for (CustomerReviews customerReviewsListCustomerReviews : tracks.getCustomerReviewsList()) {
                Tracks oldTrackIdOfCustomerReviewsListCustomerReviews = customerReviewsListCustomerReviews.getTrackId();
                customerReviewsListCustomerReviews.setTrackId(tracks);
                customerReviewsListCustomerReviews = em.merge(customerReviewsListCustomerReviews);
                if (oldTrackIdOfCustomerReviewsListCustomerReviews != null) {
                    oldTrackIdOfCustomerReviewsListCustomerReviews.getCustomerReviewsList().remove(customerReviewsListCustomerReviews);
                    oldTrackIdOfCustomerReviewsListCustomerReviews = em.merge(oldTrackIdOfCustomerReviewsListCustomerReviews);
                }
            }
            for (GenreToTracks genreToTracksListGenreToTracks : tracks.getGenreToTracksList()) {
                Tracks oldTrackIdOfGenreToTracksListGenreToTracks = genreToTracksListGenreToTracks.getTrackId();
                genreToTracksListGenreToTracks.setTrackId(tracks);
                genreToTracksListGenreToTracks = em.merge(genreToTracksListGenreToTracks);
                if (oldTrackIdOfGenreToTracksListGenreToTracks != null) {
                    oldTrackIdOfGenreToTracksListGenreToTracks.getGenreToTracksList().remove(genreToTracksListGenreToTracks);
                    oldTrackIdOfGenreToTracksListGenreToTracks = em.merge(oldTrackIdOfGenreToTracksListGenreToTracks);
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

    public void edit(Tracks tracks) throws IllegalOrphanException, NonexistentEntityException, Exception {

        try {
            utx.begin();
            Tracks persistentTracks = em.find(Tracks.class, tracks.getTrackId());
            Albums albumNumberOld = persistentTracks.getAlbumNumber();
            Albums albumNumberNew = tracks.getAlbumNumber();
            List<CustomerReviews> customerReviewsListOld = persistentTracks.getCustomerReviewsList();
            List<CustomerReviews> customerReviewsListNew = tracks.getCustomerReviewsList();
            List<GenreToTracks> genreToTracksListOld = persistentTracks.getGenreToTracksList();
            List<GenreToTracks> genreToTracksListNew = tracks.getGenreToTracksList();
            List<String> illegalOrphanMessages = null;
            
            for (CustomerReviews customerReviewsListOldCustomerReviews : customerReviewsListOld) {
                if (!customerReviewsListNew.contains(customerReviewsListOldCustomerReviews)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain CustomerReviews " + customerReviewsListOldCustomerReviews + " since its trackId field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (albumNumberNew != null) {
                albumNumberNew = em.getReference(albumNumberNew.getClass(), albumNumberNew.getAlbumNumber());
                tracks.setAlbumNumber(albumNumberNew);
            }
            List<CustomerReviews> attachedCustomerReviewsListNew = new ArrayList<CustomerReviews>();
            for (CustomerReviews customerReviewsListNewCustomerReviewsToAttach : customerReviewsListNew) {
                customerReviewsListNewCustomerReviewsToAttach = em.getReference(customerReviewsListNewCustomerReviewsToAttach.getClass(), customerReviewsListNewCustomerReviewsToAttach.getReviewNumber());
                attachedCustomerReviewsListNew.add(customerReviewsListNewCustomerReviewsToAttach);
            }
            customerReviewsListNew = attachedCustomerReviewsListNew;
            tracks.setCustomerReviewsList(customerReviewsListNew);
            List<GenreToTracks> attachedGenreToTracksListNew = new ArrayList<GenreToTracks>();
            for (GenreToTracks genreToTracksListNewGenreToTracksToAttach : genreToTracksListNew) {
                genreToTracksListNewGenreToTracksToAttach = em.getReference(genreToTracksListNewGenreToTracksToAttach.getClass(), genreToTracksListNewGenreToTracksToAttach.getTablekey());
                attachedGenreToTracksListNew.add(genreToTracksListNewGenreToTracksToAttach);
            }
            genreToTracksListNew = attachedGenreToTracksListNew;
            tracks.setGenreToTracksList(genreToTracksListNew);
            tracks = em.merge(tracks);
            if (albumNumberOld != null && !albumNumberOld.equals(albumNumberNew)) {
                albumNumberOld.getTracksList().remove(tracks);
                albumNumberOld = em.merge(albumNumberOld);
            }
            if (albumNumberNew != null && !albumNumberNew.equals(albumNumberOld)) {
                albumNumberNew.getTracksList().add(tracks);
                albumNumberNew = em.merge(albumNumberNew);
            }
            for (CustomerReviews customerReviewsListNewCustomerReviews : customerReviewsListNew) {
                if (!customerReviewsListOld.contains(customerReviewsListNewCustomerReviews)) {
                    Tracks oldTrackIdOfCustomerReviewsListNewCustomerReviews = customerReviewsListNewCustomerReviews.getTrackId();
                    customerReviewsListNewCustomerReviews.setTrackId(tracks);
                    customerReviewsListNewCustomerReviews = em.merge(customerReviewsListNewCustomerReviews);
                    if (oldTrackIdOfCustomerReviewsListNewCustomerReviews != null && !oldTrackIdOfCustomerReviewsListNewCustomerReviews.equals(tracks)) {
                        oldTrackIdOfCustomerReviewsListNewCustomerReviews.getCustomerReviewsList().remove(customerReviewsListNewCustomerReviews);
                        oldTrackIdOfCustomerReviewsListNewCustomerReviews = em.merge(oldTrackIdOfCustomerReviewsListNewCustomerReviews);
                    }
                }
            }
            for (GenreToTracks genreToTracksListOldGenreToTracks : genreToTracksListOld) {
                if (!genreToTracksListNew.contains(genreToTracksListOldGenreToTracks)) {
                    genreToTracksListOldGenreToTracks.setTrackId(null);
                    genreToTracksListOldGenreToTracks = em.merge(genreToTracksListOldGenreToTracks);
                }
            }
            for (GenreToTracks genreToTracksListNewGenreToTracks : genreToTracksListNew) {
                if (!genreToTracksListOld.contains(genreToTracksListNewGenreToTracks)) {
                    Tracks oldTrackIdOfGenreToTracksListNewGenreToTracks = genreToTracksListNewGenreToTracks.getTrackId();
                    genreToTracksListNewGenreToTracks.setTrackId(tracks);
                    genreToTracksListNewGenreToTracks = em.merge(genreToTracksListNewGenreToTracks);
                    if (oldTrackIdOfGenreToTracksListNewGenreToTracks != null && !oldTrackIdOfGenreToTracksListNewGenreToTracks.equals(tracks)) {
                        oldTrackIdOfGenreToTracksListNewGenreToTracks.getGenreToTracksList().remove(genreToTracksListNewGenreToTracks);
                        oldTrackIdOfGenreToTracksListNewGenreToTracks = em.merge(oldTrackIdOfGenreToTracksListNewGenreToTracks);
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
                Integer id = tracks.getTrackId();
                if (findTracks(id) == null) {
                    throw new NonexistentEntityException("The track with id " + id + " no longer exists.");
                }
            }
            throw ex;
        }
    }

    public void destroy(Integer id) throws IllegalOrphanException, NonexistentEntityException, RollbackFailureException, NotSupportedException, SystemException, RollbackException, HeuristicMixedException, HeuristicMixedException, HeuristicRollbackException {

        try {
            utx.begin();
            Tracks tracks;
            try {
                tracks = em.getReference(Tracks.class, id);
                tracks.getTrackId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The tracks with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            
            List<CustomerReviews> customerReviewsListOrphanCheck = tracks.getCustomerReviewsList();
            for (CustomerReviews customerReviewsListOrphanCheckCustomerReviews : customerReviewsListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Tracks (" + tracks + ") cannot be destroyed since the CustomerReviews " + customerReviewsListOrphanCheckCustomerReviews + " in its customerReviewsList field has a non-nullable trackId field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Albums albumNumber = tracks.getAlbumNumber();
            if (albumNumber != null) {
                albumNumber.getTracksList().remove(tracks);
                albumNumber = em.merge(albumNumber);
            }
            List<GenreToTracks> genreToTracksList = tracks.getGenreToTracksList();
            for (GenreToTracks genreToTracksListGenreToTracks : genreToTracksList) {
                genreToTracksListGenreToTracks.setTrackId(null);
                genreToTracksListGenreToTracks = em.merge(genreToTracksListGenreToTracks);
            }
            em.remove(tracks);
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

    public List<Tracks> findTracksEntities() {
        return findTracksEntities(true, -1, -1);
    }

    public List<Tracks> findTracksEntities(int maxResults, int firstResult) {
        return findTracksEntities(false, maxResults, firstResult);
    }

    private List<Tracks> findTracksEntities(boolean all, int maxResults, int firstResult) {

        CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
        cq.select(cq.from(Tracks.class));
        Query q = em.createQuery(cq);
        if (!all) {
            q.setMaxResults(maxResults);
            q.setFirstResult(firstResult);
        }
        return q.getResultList();

    }

    /**
     * Written by Korjon Chang-Jones Method finds all Tracks based on a specific
     * album
     *
     * @param albumId
     * @return tracks from the same album
     * @author Korjon Chang-Jones
     */
    public List<Tracks> findTracksByAlbum(Integer albumId) {

        CriteriaBuilder cb = em.getCriteriaBuilder();

        CriteriaQuery<Tracks> cq = cb.createQuery(Tracks.class);
        Root<Tracks> rt = cq.from(Tracks.class);
        cq.where(cb.equal(rt.get("albumNumber").get("albumNumber"), albumId));
        cq.select(rt);
        Query q = em.createQuery(cq);

        return q.getResultList();

    }
    
    /**
     * Method finds Tracks by their date entered
     * in the inventory. 
     * @param startDate Lower bound of the date range
     * @param endDate Upper bound of the date range
     * @return 
     * 
     * @author Korjon Chang-Jones
     */
    public List<Tracks> findTracksByDate(Date startDate, Date endDate){
        
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Tracks> cq = cb.createQuery(Tracks.class);
        Root<Tracks> rt = cq.from(Tracks.class);
        cq.where(cb.between(rt.get("entryDate"), startDate, endDate));
        TypedQuery<Tracks> query = em.createQuery(cq);

        return query.getResultList();
        

    }

    /**
     * Method finds tracks based on the title that is given. Title does not have
     * to be exact string
     *
     * @param title
     * @return list of albums containing title string
     * @author Korjon Chang-Jones
     */
    public List<Tracks> findTracksByTitle(String title) {

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Tracks> cq = cb.createQuery(Tracks.class);
        Root<Tracks> rt = cq.from(Tracks.class);
        cq.where(cb.like(rt.get("trackTitle"), "%" + title + "%"));
        TypedQuery<Tracks> query = em.createQuery(cq);

        return query.getResultList();
    }
    
    /*
    public List<Tracks>findTracksByArtist(String artistName){
        
        
    }
    
    
    /**
     * Method finds Artists on a specific track
     * @param id
     * @return The list of artists on the track
     *
     * @author Korjon Chang-Jones
     */
    public List<Artists> findArtists(Integer id){
        
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Artists> cq = cb.createQuery(Artists.class);
        Root <Artists> rt = cq.from(Artists.class);
        Join artistToTracks = rt.join("artistsToTracksList");
        cq.where(cb.equal(artistToTracks.get("trackId").get("trackId"), id));
        TypedQuery<Artists> query = em.createQuery(cq);
        
        return query.getResultList();
    }
    
    
    public Tracks findTracks(Integer id) {

        return em.find(Tracks.class, id);

    }

    public Tracks findTracks(Integer id, EntityManager em2) {

        return em2.find(Tracks.class, id);

    }

    /**
     * Method finds one of the genres for a specific track based on its ID
     *
     * @param id
     * @return The genre of the track
     * @author Korjon Chang-Jones
     */
    public Genres findGenre(Integer id) {

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Genres> cq = cb.createQuery(Genres.class);
        Root<Genres> rt = cq.from(Genres.class);
        Join genreToTracks = rt.join("genreToTracksList");
        cq.where(cb.equal(genreToTracks.get("trackId").get("trackId"), id));
        TypedQuery<Genres> query = em.createQuery(cq);

        return query.getSingleResult();

    }
    
    /**
     * Method finds tracks based on the artist on
     * the track
     * @param artistName
     * @return 
     */
    public List<Tracks> findTracksByArtist(String artistName){
        
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Tracks> cq = cb.createQuery(Tracks.class);
        Root<Tracks> rt = cq.from(Tracks.class);
        Join artistToTracks = rt.join("artistsToTracksCollection");
        Join artists = artistToTracks.join("artistId");
        cq.where(cb.like(artists.get("artistName"), "%" + artistName + "%"));
        TypedQuery<Tracks> query = em.createQuery(cq);
        
        return query.getResultList();
    }

    public int getTracksCount() {

        CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
        Root<Tracks> rt = cq.from(Tracks.class);
        cq.select(em.getCriteriaBuilder().count(rt));
        Query q = em.createQuery(cq);
        return ((Long) q.getSingleResult()).intValue();

    }

    private void SetListOfItems() {
        listOfTracksInTheCart.clear();
        String[] data = getAllIdFromCart();
        for (String item : data) {
            if (item.length() > 0) {
                if (!item.toLowerCase().contains("a")) {
                    listOfTracksInTheCart.add(findTracks(parseStringToInt(item)));
                }
            }
        }
    }

    /**
     * This method will allow us to get the list of tracks that are in the cart
     *
     * @return ArrayList of tracks
     * @author Ibrahim
     */
    public ArrayList<Tracks> retrieveAllTracksInTheCart() {
        SetListOfItems();
        return this.listOfTracksInTheCart;
    }

    /**
     * This method will return an array of the id from tracks the cart
     *
     * @return String[]
     * @author Ibrahim
     */
    private String[] getAllIdFromCart() {
        CookieManager cookies = new CookieManager();
        String dataResult = cookies.findValue(com.beatchamber.util.Messages.getMessage("com.beatchamber.bundles.messages", "cartKey", null).getDetail());
        return dataResult.split(",");
    }

    /**
     * This method will return the total price of the tracks that are in the
     * cart
     *
     * @return String
     * @author Ibrahim
     */
    public String getTotalPrice() {
        SetListOfItems();
        double total = 0;
        for (Tracks item : listOfTracksInTheCart) {
            total = total + item.getListPrice();
            System.out.println(total + "   ------------------------");
        }
        return total + "";
    }

    /**
     * This method will return the int value of the string
     *
     * @param strToParse
     * @return int
     * @author Ibrahim
     */
    private int parseStringToInt(String strToParse) {
        return Integer.parseInt(strToParse);
    }
    

    
}
