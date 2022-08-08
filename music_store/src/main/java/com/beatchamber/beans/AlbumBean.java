package com.beatchamber.beans;

import com.beatchamber.entities.Albums;
import com.beatchamber.entities.CustomerReviews;
import com.beatchamber.entities.Genres;
import com.beatchamber.entities.Tracks;
import com.beatchamber.jpacontroller.AlbumsJpaController;
import com.beatchamber.jpacontroller.GenresJpaController;
import com.beatchamber.jpacontroller.TracksJpaController;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import javax.enterprise.context.SessionScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Managed bean for the Albums, passing to the album page
 * 
 * @author Susan Vuu - 1735488
 */
@Named("album")
@SessionScoped
public class AlbumBean implements Serializable {
    
    private final static Logger LOG = LoggerFactory.getLogger(AlbumsJpaController.class);
    
    private int albumId;
     
    private List<Albums> similarAlbums;
    private MusicComponent sendComponent;
    
    @Inject
    private AlbumsJpaController albumController;
    
    @Inject 
    private TracksJpaController trackController;
    
    @Inject
    private GenresJpaController genreController;
    
    @Inject
    private CookieManager cookies;

    /**
     * Default constructor
     */
    public AlbumBean() {
        this.albumId = 0;
    }
    
    /**
     * @return The album id
     */
    public int getAlbumId(){
        return this.albumId;
    }
    
    /**
     * Set the album id
     * @param albumId 
     */
    public void setAlbumId(Integer albumId){
        this.albumId = albumId;
    }
    
    /**
     * Method gets the first 3 similar albums to the
     * album being displayed
     * 
     * @return 
     * 
     * @author Korjon Chang-Jones
     */
    public List<Albums> getSimilarAlbums(){
        
        LOG.info("Similar albums amount in album bean: " + similarAlbums.size());
        return similarAlbums.subList(0, 3);
    }
    
    /**
     * Stores the sent param in albumId, and navigates to the album page
     * @return The album page
     * 
     * @author Susan Vuu - 1735488
     */
    public String sendAlbum(){
                
        FacesContext fc = FacesContext.getCurrentInstance();
        Map<String,String> params = fc.getExternalContext().getRequestParameterMap();
        this.albumId = Integer.parseInt(params.get("albumId"));
        
        cookies.addAlbumGenreToCookies(albumId);
        
        storeSimilarAlbums(albumController.findGenre(albumId));
        return "album_page.xhtml?faces-redirect=true";
    }
    
    
    public String sendAlbum(Albums album){
        
        LOG.info("send alum is called from browse music");
        this.albumId = album.getAlbumNumber();
        cookies.addAlbumGenreToCookies(albumId);

        storeSimilarAlbums(albumController.findGenre(albumId));
        return "album_page.xhtml?faces-redirect=true";
    }
    
    
    /**
     * Stores the sent param in albumId, and navigates to the album page
     * @param id
     * @return 
     * 
     * @author Korjon Chang-Jones
     */
    public String sendAlbum(int id){
        
        this.albumId = id;
        cookies.addAlbumGenreToCookies(albumId);

        storeSimilarAlbums(albumController.findGenre(albumId));
        
        return "album_page.xhtml?faces-redirect=true";
    }
    
    
    
    /**
     * Method gets albums that are similar to the contents
     * (album or track) being viewed.
     * Filtered by genre
     * @param genre
     * @author Korjon Chang-Jones
     */
    public void storeSimilarAlbums(Genres genre){
        
        LOG.info("" + albumController.toString());
        String title = albumController.findAlbums(albumId).getAlbumTitle();
        similarAlbums = albumController.findAlbumsByGenre(genre, title);
        
        //Used to randomize the album order so that they
        //don't always appear in the same order when displayed
        Collections.shuffle(similarAlbums);
    }
    
    /**
     * @return Recommended albums for the user
     * @author Susan Vuu - 1735488
     */
    public List<Albums> recommendAlbums() {
        CookieManager cookieManager = new CookieManager();
        String genreCookie = cookieManager.findValue("selectGenre");
        
        List<Albums> albumsGenre;
        //If it's the client's first visit, display a sample list of albums
        if(genreCookie.isEmpty()){
            albumsGenre = albumController.findAlbumsByGenre("Classical");
        }
        else{
            String lastViewedGenre = genreController.findGenres(Integer.parseInt(genreCookie)).getGenreName();
            LOG.debug(lastViewedGenre);
            albumsGenre = albumController.findAlbumsByGenre(lastViewedGenre);
        }
        
        //Collections.shuffle(albumsGenre);
        return albumsGenre;
    }
    
    
    /**
     * Method checks if the cart is empty
     * @return 
     * 
     * @author Korjon Chang-Jones
     */
    public boolean isEmptyCart(){
        
        return albumController.retrieveAllAlbumsInTheCart().size() <= 0 && trackController.retrieveAllTracksInTheCart().size() <= 0;
    }
    
    /**
     * @return The average rating of all tracks
     */
    public int getAlbumRating() {
        //Getting each average rating of each track in an ArrayList
        List<Tracks> albumTracks = trackController.findTracksByAlbum(this.albumId);
        List<Double> tracksAverageRatings = new ArrayList<>();
        //Using Korjon's method to get the average rating of a track
        albumTracks.forEach(track -> {
            tracksAverageRatings.add(track.getCustomerReviewsList().stream()
                    .filter(review -> review.getApprovalStatus() == true)
                    .mapToInt(review -> review.getRating()).average()
                    .orElse(0));
        });
        
        double averageRating = 0;
        int totalTracks = 0;
        
        //Number of tracks that have an average rating
        totalTracks = tracksAverageRatings.stream().filter(trackAverageRating -> (trackAverageRating > 0)).map(_item -> 1).reduce(totalTracks, Integer::sum);
        //Getting the average rating of all the rated tracks
        averageRating = tracksAverageRatings.stream().map(trackRating -> trackRating).reduce(averageRating, (accumulator, _item) -> accumulator + _item);
        return (int) averageRating / totalTracks;
    }

    public String toString(){
     
       return "ALBUM BEEN EXISTS";
    }
}

