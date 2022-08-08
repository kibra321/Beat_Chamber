package com.beatchamber.jpacontroller;

import com.beatchamber.beans.CookieManager;
import com.beatchamber.entities.Albums;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import com.beatchamber.entities.Artists;
import com.beatchamber.entities.ArtistAlbums;
import java.util.ArrayList;
import java.util.List;
import com.beatchamber.entities.Tracks;
import com.beatchamber.entities.Genres;
import com.beatchamber.entities.GenreToAlbum;
import com.beatchamber.exceptions.IllegalOrphanException;
import com.beatchamber.exceptions.NonexistentEntityException;
import com.beatchamber.exceptions.RollbackFailureException;
import java.util.Date;
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
@Named
@SessionScoped
public class AlbumsJpaController implements Serializable {

    private final static Logger LOG = LoggerFactory.getLogger(AlbumsJpaController.class);

    @Resource
    private UserTransaction utx;

    @PersistenceContext(unitName = "music_store_persistence")
    private EntityManager em;
    
    @Inject
    private TracksJpaController tracksJpaController;
    
    private ArrayList<Albums> listOfAlbumsInTheCart = new ArrayList<Albums>();

    public AlbumsJpaController() {
    }

    public void create(Albums albums) throws RollbackFailureException {
        if (albums.getArtistAlbumsList() == null) {
            albums.setArtistAlbumsList(new ArrayList<ArtistAlbums>());
        }
        if (albums.getTracksList() == null) {
            albums.setTracksList(new ArrayList<Tracks>());
        }
        if (albums.getGenreToAlbumList() == null) {
            albums.setGenreToAlbumList(new ArrayList<GenreToAlbum>());
        }

        try {
            utx.begin();
            List<ArtistAlbums> attachedArtistAlbumsList = new ArrayList<ArtistAlbums>();
            for (ArtistAlbums artistAlbumsListArtistAlbumsToAttach : albums.getArtistAlbumsList()) {
                artistAlbumsListArtistAlbumsToAttach = em.getReference(artistAlbumsListArtistAlbumsToAttach.getClass(), artistAlbumsListArtistAlbumsToAttach.getTablekey());
                attachedArtistAlbumsList.add(artistAlbumsListArtistAlbumsToAttach);
            }
            albums.setArtistAlbumsList(attachedArtistAlbumsList);
            List<Tracks> attachedTracksList = new ArrayList<Tracks>();
            for (Tracks tracksListTracksToAttach : albums.getTracksList()) {
                tracksListTracksToAttach = em.getReference(tracksListTracksToAttach.getClass(), tracksListTracksToAttach.getTrackId());
                attachedTracksList.add(tracksListTracksToAttach);
            }
            albums.setTracksList(attachedTracksList);
            List<GenreToAlbum> attachedGenreToAlbumList = new ArrayList<GenreToAlbum>();
            for (GenreToAlbum genreToAlbumListGenreToAlbumToAttach : albums.getGenreToAlbumList()) {
                genreToAlbumListGenreToAlbumToAttach = em.getReference(genreToAlbumListGenreToAlbumToAttach.getClass(), genreToAlbumListGenreToAlbumToAttach.getTablekey());
                attachedGenreToAlbumList.add(genreToAlbumListGenreToAlbumToAttach);
            }
            albums.setGenreToAlbumList(attachedGenreToAlbumList);
            em.persist(albums);
            for (ArtistAlbums artistAlbumsListArtistAlbums : albums.getArtistAlbumsList()) {
                Albums oldAlbumNumberOfArtistAlbumsListArtistAlbums = artistAlbumsListArtistAlbums.getAlbumNumber();
                artistAlbumsListArtistAlbums.setAlbumNumber(albums);
                artistAlbumsListArtistAlbums = em.merge(artistAlbumsListArtistAlbums);
                if (oldAlbumNumberOfArtistAlbumsListArtistAlbums != null) {
                    oldAlbumNumberOfArtistAlbumsListArtistAlbums.getArtistAlbumsList().remove(artistAlbumsListArtistAlbums);
                    oldAlbumNumberOfArtistAlbumsListArtistAlbums = em.merge(oldAlbumNumberOfArtistAlbumsListArtistAlbums);
                }
            }
            for (Tracks tracksListTracks : albums.getTracksList()) {
                Albums oldAlbumNumberOfTracksListTracks = tracksListTracks.getAlbumNumber();
                tracksListTracks.setAlbumNumber(albums);
                tracksListTracks = em.merge(tracksListTracks);
                if (oldAlbumNumberOfTracksListTracks != null) {
                    oldAlbumNumberOfTracksListTracks.getTracksList().remove(tracksListTracks);
                    oldAlbumNumberOfTracksListTracks = em.merge(oldAlbumNumberOfTracksListTracks);
                }
            }
            for (GenreToAlbum genreToAlbumListGenreToAlbum : albums.getGenreToAlbumList()) {
                Albums oldAlbumNumberOfGenreToAlbumListGenreToAlbum = genreToAlbumListGenreToAlbum.getAlbumNumber();
                genreToAlbumListGenreToAlbum.setAlbumNumber(albums);
                genreToAlbumListGenreToAlbum = em.merge(genreToAlbumListGenreToAlbum);
                if (oldAlbumNumberOfGenreToAlbumListGenreToAlbum != null) {
                    oldAlbumNumberOfGenreToAlbumListGenreToAlbum.getGenreToAlbumList().remove(genreToAlbumListGenreToAlbum);
                    oldAlbumNumberOfGenreToAlbumListGenreToAlbum = em.merge(oldAlbumNumberOfGenreToAlbumListGenreToAlbum);
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

    public void edit(Albums albums) throws IllegalOrphanException, NonexistentEntityException, Exception {

        try {
            utx.begin();
            Albums persistentAlbums = em.find(Albums.class, albums.getAlbumNumber());
            List<ArtistAlbums> artistAlbumsListOld = persistentAlbums.getArtistAlbumsList();
            List<ArtistAlbums> artistAlbumsListNew = albums.getArtistAlbumsList();
            List<Tracks> tracksListOld = persistentAlbums.getTracksList();
            List<Tracks> tracksListNew = albums.getTracksList();
            List<GenreToAlbum> genreToAlbumListOld = persistentAlbums.getGenreToAlbumList();
            List<GenreToAlbum> genreToAlbumListNew = albums.getGenreToAlbumList();
            List<String> illegalOrphanMessages = null;
            for (ArtistAlbums artistAlbumsListOldArtistAlbums : artistAlbumsListOld) {
                if (!artistAlbumsListNew.contains(artistAlbumsListOldArtistAlbums)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain ArtistAlbums " + artistAlbumsListOldArtistAlbums + " since its albumNumber field is not nullable.");
                }
            }
            for (Tracks tracksListOldTracks : tracksListOld) {
                if (!tracksListNew.contains(tracksListOldTracks)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Tracks " + tracksListOldTracks + " since its albumNumber field is not nullable.");
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
            albums.setArtistAlbumsList(artistAlbumsListNew);
            List<Tracks> attachedTracksListNew = new ArrayList<Tracks>();
            for (Tracks tracksListNewTracksToAttach : tracksListNew) {
                tracksListNewTracksToAttach = em.getReference(tracksListNewTracksToAttach.getClass(), tracksListNewTracksToAttach.getTrackId());
                attachedTracksListNew.add(tracksListNewTracksToAttach);
            }
            tracksListNew = attachedTracksListNew;
            albums.setTracksList(tracksListNew);
            List<GenreToAlbum> attachedGenreToAlbumListNew = new ArrayList<GenreToAlbum>();
            for (GenreToAlbum genreToAlbumListNewGenreToAlbumToAttach : genreToAlbumListNew) {
                genreToAlbumListNewGenreToAlbumToAttach = em.getReference(genreToAlbumListNewGenreToAlbumToAttach.getClass(), genreToAlbumListNewGenreToAlbumToAttach.getTablekey());
                attachedGenreToAlbumListNew.add(genreToAlbumListNewGenreToAlbumToAttach);
            }
            genreToAlbumListNew = attachedGenreToAlbumListNew;
            albums.setGenreToAlbumList(genreToAlbumListNew);
            albums = em.merge(albums);
            for (ArtistAlbums artistAlbumsListNewArtistAlbums : artistAlbumsListNew) {
                if (!artistAlbumsListOld.contains(artistAlbumsListNewArtistAlbums)) {
                    Albums oldAlbumNumberOfArtistAlbumsListNewArtistAlbums = artistAlbumsListNewArtistAlbums.getAlbumNumber();
                    artistAlbumsListNewArtistAlbums.setAlbumNumber(albums);
                    artistAlbumsListNewArtistAlbums = em.merge(artistAlbumsListNewArtistAlbums);
                    if (oldAlbumNumberOfArtistAlbumsListNewArtistAlbums != null && !oldAlbumNumberOfArtistAlbumsListNewArtistAlbums.equals(albums)) {
                        oldAlbumNumberOfArtistAlbumsListNewArtistAlbums.getArtistAlbumsList().remove(artistAlbumsListNewArtistAlbums);
                        oldAlbumNumberOfArtistAlbumsListNewArtistAlbums = em.merge(oldAlbumNumberOfArtistAlbumsListNewArtistAlbums);
                    }
                }
            }
            for (Tracks tracksListNewTracks : tracksListNew) {
                if (!tracksListOld.contains(tracksListNewTracks)) {
                    Albums oldAlbumNumberOfTracksListNewTracks = tracksListNewTracks.getAlbumNumber();
                    tracksListNewTracks.setAlbumNumber(albums);
                    tracksListNewTracks = em.merge(tracksListNewTracks);
                    if (oldAlbumNumberOfTracksListNewTracks != null && !oldAlbumNumberOfTracksListNewTracks.equals(albums)) {
                        oldAlbumNumberOfTracksListNewTracks.getTracksList().remove(tracksListNewTracks);
                        oldAlbumNumberOfTracksListNewTracks = em.merge(oldAlbumNumberOfTracksListNewTracks);
                    }
                }
            }
            for (GenreToAlbum genreToAlbumListOldGenreToAlbum : genreToAlbumListOld) {
                if (!genreToAlbumListNew.contains(genreToAlbumListOldGenreToAlbum)) {
                    genreToAlbumListOldGenreToAlbum.setAlbumNumber(null);
                    genreToAlbumListOldGenreToAlbum = em.merge(genreToAlbumListOldGenreToAlbum);
                }
            }
            for (GenreToAlbum genreToAlbumListNewGenreToAlbum : genreToAlbumListNew) {
                if (!genreToAlbumListOld.contains(genreToAlbumListNewGenreToAlbum)) {
                    Albums oldAlbumNumberOfGenreToAlbumListNewGenreToAlbum = genreToAlbumListNewGenreToAlbum.getAlbumNumber();
                    genreToAlbumListNewGenreToAlbum.setAlbumNumber(albums);
                    genreToAlbumListNewGenreToAlbum = em.merge(genreToAlbumListNewGenreToAlbum);
                    if (oldAlbumNumberOfGenreToAlbumListNewGenreToAlbum != null && !oldAlbumNumberOfGenreToAlbumListNewGenreToAlbum.equals(albums)) {
                        oldAlbumNumberOfGenreToAlbumListNewGenreToAlbum.getGenreToAlbumList().remove(genreToAlbumListNewGenreToAlbum);
                        oldAlbumNumberOfGenreToAlbumListNewGenreToAlbum = em.merge(oldAlbumNumberOfGenreToAlbumListNewGenreToAlbum);
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
                Integer id = albums.getAlbumNumber();
                if (findAlbums(id) == null) {
                    throw new NonexistentEntityException("The album with id " + id + " no longer exists.");
                }
            }
            throw ex;
        }
    }

    public void destroy(Integer id) throws IllegalOrphanException, NonexistentEntityException, NotSupportedException, SystemException, RollbackFailureException, RollbackException, HeuristicMixedException, HeuristicRollbackException {

        try {
            utx.begin();
            Albums albums;
            try {
                albums = em.getReference(Albums.class, id);
                albums.getAlbumNumber();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The albums with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<ArtistAlbums> artistAlbumsListOrphanCheck = albums.getArtistAlbumsList();
            for (ArtistAlbums artistAlbumsListOrphanCheckArtistAlbums : artistAlbumsListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Albums (" + albums + ") cannot be destroyed since the ArtistAlbums " + artistAlbumsListOrphanCheckArtistAlbums + " in its artistAlbumsList field has a non-nullable albumNumber field.");
            }
            List<Tracks> tracksListOrphanCheck = albums.getTracksList();
            for (Tracks tracksListOrphanCheckTracks : tracksListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Albums (" + albums + ") cannot be destroyed since the Tracks " + tracksListOrphanCheckTracks + " in its tracksList field has a non-nullable albumNumber field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            List<GenreToAlbum> genreToAlbumList = albums.getGenreToAlbumList();
            for (GenreToAlbum genreToAlbumListGenreToAlbum : genreToAlbumList) {
                genreToAlbumListGenreToAlbum.setAlbumNumber(null);
                genreToAlbumListGenreToAlbum = em.merge(genreToAlbumListGenreToAlbum);
            }
            em.remove(albums);
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

    public List<Albums> findAlbumsEntities() {
        return findAlbumsEntities(true, -1, -1);
    }

    public List<Albums> findAlbumsEntities(int maxResults, int firstResult) {
        return findAlbumsEntities(false, maxResults, firstResult);
    }

    private List<Albums> findAlbumsEntities(boolean all, int maxResults, int firstResult) {

        CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
        cq.select(cq.from(Albums.class));
        Query q = em.createQuery(cq);
        if (!all) {
            q.setMaxResults(maxResults);
            q.setFirstResult(firstResult);
        }
        return q.getResultList();
    }

    public Albums findAlbums(Integer id) {
        
        return em.find(Albums.class, id);
    }
    public Albums findAlbums(Integer id,EntityManager em2) {
        return em2.find(Albums.class, id);
    }

    public int getAlbumsCount() {
        CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
        Root<Albums> rt = cq.from(Albums.class);
        cq.select(em.getCriteriaBuilder().count(rt));
        Query q = em.createQuery(cq);
        return ((Long) q.getSingleResult()).intValue();
    }
    
    /**
     * @param id
     * @return The artist of the specific album
     * @author Susan Vuu
     */
    public Artists getAlbumArtist(Integer id){
        //Get the album first
        Albums foundAlbum = em.find(Albums.class, id);
        //Get the album's artist
        return foundAlbum.getArtistAlbumsList().get(0).getArtistId();
    }
    
    /**
     * @param id
     * @param isSmall
     * @return The path of the album's image, either big or small
     * @author Susan Vuu
     */
    public String getAlbumPath(Integer id, boolean isSmall){
        
        Albums foundAlbum = em.find(Albums.class, id);
        
        //Starting album path
        String startingPath = "albums/";
        //The album name included in the path
        String albumName = foundAlbum.getAlbumTitle().replace(" ", "_").toLowerCase();
        //Probably a better way with regex but unsure
        String trimmedAlbumName = albumName.replace(".", "").replace("'", "").replace(":", "");
        String albumPath = startingPath + trimmedAlbumName;
        //Rest of album path
        if(isSmall){
            return albumPath + "_small.jpg";
        }
        return albumPath + "_large.jpg";
    }
    
    /**
     * This method will allow us to get the list of albums that are in the cart
     * @return ArrayList of albums
     * @author Ibrahim
     */
    public ArrayList<Albums> retrieveAllAlbumsInTheCart(){
        SetListOfItems();
        return this.listOfAlbumsInTheCart;
    }
    
    /**
     * This method will return the total price of the items in the cart
     * @return String
     * @author Ibrahim
     */
    public String getTotalPrice(){
        SetListOfItems();
        double total=0;
        for(Albums item:listOfAlbumsInTheCart){
            total = total + item.getListPrice();
        }
        return total + "";
    }
    
    
    /**
     * This method will get all of the ids of the items in the cart
     * @return String[] 
     * @author Ibrahim
     */
    private String[] getAllIdFromCart(){
        CookieManager cookies = new CookieManager();
        String dataResult = cookies.findValue(com.beatchamber.util.Messages.getMessage("com.beatchamber.bundles.messages","cartKey",null).getDetail());
        return dataResult.split(",");
    }
    
    /**
     * This method will return the int value of the string
     * @param strToParse
     * @return int
     * @author Ibrahim
     */
    private int parseStringToInt(String strToParse){
        return Integer.parseInt(strToParse);
    }
    
    /**
     * This method will set the arrayList so that we would have the data from the cookies in them
     * @author Ibrahim
     */
    private void SetListOfItems(){
        listOfAlbumsInTheCart.clear();
        String[] data = getAllIdFromCart();
        for(String item:data){
            if(item.length()>0){
                if(item.toLowerCase().contains("a")){
                    listOfAlbumsInTheCart.add(findAlbums(parseStringToInt(item.replace("a", ""))));
                }
            }
        }
    }
    
     /**
     * Method finds Albums by their date entered
     * in the inventory. 
     * @param firstDate Lower bound of the date range
     * @param lastDate Upper bound of the date range
     * @return 
     * 
     * @author Korjon Chang-Jones
     */
    public List<Albums> findAlbumsByDate(Date firstDate, Date lastDate){
        
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Albums> cq = cb.createQuery(Albums.class);
        Root<Albums> rt = cq.from(Albums.class);
        cq.where(cb.between(rt.get("entryDate"), firstDate, lastDate));
        TypedQuery<Albums> query = em.createQuery(cq);

        return query.getResultList();
        
    }
    
    /**
     * Method finds albums based on the title
     * that is given. Title does not have
     * to be exact string
     * 
     * @param title
     * @return list of albums containing title string 
     * @author Korjon Chang-jones
     */
    public List<Albums> findAlbumsByTitle(String title){
        
        
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Albums>  cq = cb.createQuery(Albums.class);
        Root<Albums> rt = cq.from(Albums.class);
        cq.where(cb.like(rt.get("albumTitle"), "%" + title + "%"));
        TypedQuery<Albums> query = em.createQuery(cq);
        
        return query.getResultList();
    }
    
    
     /**
     * Method finds specific Albums based on its
     * genre Query omits the album that is being displayed
     * @param genre
     * @param title
     * @return the list of Albums in the same genre
     * 
     * @author Korjon Chang-jones and Ibrahim Kebe
     */
    public List<Albums> findAlbumsByGenre(Genres genre, String title){
        
        
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Albums>  cq = cb.createQuery(Albums.class);
        Root<Albums> rt = cq.from(Albums.class);
        Join genreToAlbums = rt.join("genreToAlbumList");
        Join genres = genreToAlbums.join("genreId");
        cq.where(cb.and(cb.equal(genres.get("genreName"), genre.getGenreName()), cb.notEqual(rt.get("albumTitle"), title)));
        TypedQuery<Albums> query = em.createQuery(cq);
        
        List<Albums> returnedData = query.getResultList();
        List<Albums> returnedList = new ArrayList<Albums>();
        
        //This one last verification to be sure that we got all of the albums that match the string given
        for (Albums item:returnedData) {
            if(!(item.getAlbumTitle().contains(title))){
                returnedList.add(item);
            }
        }
        
        return returnedList;
    }
    
    
    /**
     * Method finds specific Albums based on its
     * genre. 
     * @param genre
     * @return the list of Albums in the same genre
     * 
     * @author Korjon Chang-jones
     */
    public List<Albums> findAlbumsByGenre(String genre){
        
        
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Albums>  cq = cb.createQuery(Albums.class);
        Root<Albums> rt = cq.from(Albums.class);
        Join genreToAlbums = rt.join("genreToAlbumList");
        Join genres = genreToAlbums.join("genreId");
        cq.where(cb.equal(genres.get("genreName"), genre));
        TypedQuery<Albums> query = em.createQuery(cq);
        
        return query.getResultList();
       
    }
    
    
      /**
     * Method finds one of the genres for a specific Album
     * based on its ID
     * @param id
     * @return The genre of the Album
     * 
     * @author Korjon Chang-Jones
     */
    public Genres findGenre(Integer id){
        
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Genres>  cq = cb.createQuery(Genres.class);
        Root <Genres> rt = cq.from(Genres.class);
        Join genreToTracks = rt.join("genreToAlbumList");
        cq.where(cb.equal(genreToTracks.get("albumNumber").get("albumNumber"), id));
        TypedQuery<Genres> query = em.createQuery(cq);
        
        return query.getSingleResult();
        
    }
    
    /**
     * Method finds albums based on the artist on
     * the album
     * @param artistName
     * @return 
     * 
     * @author Korjon Chang-Jones
     */
    public List<Albums> findAlbumsByArtist(String artistName){
        
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Albums>  cq = cb.createQuery(Albums.class);
        Root<Albums> rt = cq.from(Albums.class);
        Join artistAlbums = rt.join("artistAlbumsList");
        Join artists = artistAlbums.join("artistId");
        cq.where(cb.like(artists.get("artistName"), "%" + artistName + "%"));
        TypedQuery<Albums> query = em.createQuery(cq);
        
        return query.getResultList();
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
        Join artistAlbums = rt.join("artistAlbumsList");
        cq.where(cb.equal(artistAlbums.get("albumNumber").get("albumNumber"), id));
        TypedQuery<Artists> query = em.createQuery(cq);
        
        return query.getResultList();
    }
    
    /**
     * Method finds albums based on the trackIds
     * In other words, it finds the album that
     * a track belongs to
     * @param trackId
     * @return 
     * 
     * @author Korjon Chang-Jones
     */
    public Albums findAlbumByTrackId(Integer trackId){
        
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Albums> cq = cb.createQuery(Albums.class);
        Root <Albums> rt = cq.from(Albums.class);
        Join tracksList = rt.join("tracksList");
        cq.where(cb.equal(tracksList.get("trackId"), trackId));
        TypedQuery<Albums> query = em.createQuery(cq);
        
        return query.getSingleResult();
    }
    
    /**
     * @param albumId
     * @return If the album is on sale
     * @author Susan Vuu
     */
    public boolean isAlbumOnSale(Integer albumId) {
        return findAlbums(albumId).getSalePrice() > 0;
    }
    
    /**
     * @param albumId
     * @return The album's price - the album's sale price
     */
    public Double getAlbumPrice(Integer albumId) {
        return findAlbums(albumId).getListPrice() - findAlbums(albumId).getSalePrice();
    }
}
