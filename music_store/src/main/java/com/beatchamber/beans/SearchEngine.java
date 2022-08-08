package com.beatchamber.beans;

import com.beatchamber.entities.Albums;
import com.beatchamber.entities.Tracks;
import com.beatchamber.jpacontroller.AlbumsJpaController;
import com.beatchamber.jpacontroller.TracksJpaController;
import java.io.Serializable;
import java.util.*;
import javax.enterprise.context.SessionScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import org.slf4j.LoggerFactory;

/**
 * Class contains all of the queries for searching for musical content (Tracks
 * and albums)
 *
 * @author Korjon Chang-Jones
 */
@Named("search")
@SessionScoped
public class SearchEngine implements Serializable {

    private final static org.slf4j.Logger LOG = LoggerFactory.getLogger(SearchEngine.class);

    //The dropdown selected item
    private String searchCondition;

    //The query String
    private String searchValue;
    

    //Data structure containing tracks
    private List<MusicComponent> trackComponents;
    
    //Data structure containing albums
    private List<MusicComponent> albumComponents;
    
    /**
     *Data structure containing the item
     *(album or track) that the user wishes
     *to see in more detail
     */
    private MusicComponent sendComponent;
    
    private Date startDate;
    
    private Date endDate;

    @Inject
    AlbumsJpaController albumController;
    
    @Inject
    TracksJpaController trackController;
    
    @Inject
    private TrackBean trackBean;
    
    @Inject
    private AlbumBean albumBean;
    
    public SearchEngine() {

        //default search Condition
        searchCondition = "albumTitle";
        
    }
    

    public String getSearchCondition() {
        return searchCondition;
    }

    public void setSearchCondition(String searchCondition) {
        this.searchCondition = searchCondition;
    }

    public String getSearchValue() {
        return this.searchValue;
    }

    public void setSearchValue(String searchValue) {
        this.searchValue = searchValue;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public MusicComponent getSendComponent() {
        return sendComponent;
    }

    public void setSendComponent(MusicComponent sendComponent) {
        this.sendComponent = sendComponent;
    }

    public List<MusicComponent> getTrackComponents() {
        return trackComponents;
    }

    public void setTrackComponents(List<MusicComponent> trackComponents) {
        this.trackComponents = trackComponents;
    }

    public List<MusicComponent> getAlbumComponents() {
        return albumComponents;
    }

    public void setAlbumComponents(List<MusicComponent> albumComponents) {
        this.albumComponents = albumComponents;
    }
      
   
    

    
    /**
     * Method checks if the selected search condition is based on the date added
     *
     * @return
     */
    public boolean getDateCondition() {

        return searchCondition.equals("dateAdded");
    }
    
    /**
     * Method finds Music content (albums and/or tracks)
     * based on a search condition
     * @return the browse music page
     */
    public String findMusicContent(){
        
        LOG.info("Calling finding music content");
        
        albumComponents = new ArrayList<>();
        trackComponents = new ArrayList<>();
          
        
        
        switch(searchCondition){
            
            
            case "trackName":
                
                LOG.info("Finding tracks");
                findTracks();
                break;
            
            case "dateAdded":
                LOG.info("Finding by date");
                findMusicContentByDate();
                break;
                
            case "artistName":
                LOG.info("finding by artist");
                findMusicContentByArtist();
                break;
                
                
            default:
                LOG.info("Finding by album");
                findAlbums();
        }
        
        return "browse_music.xhtml?faces-redirect=true";
    }
    
    /**
     * Method finds music content (albums and tracks)
     * entered in the inventory between certain dates
     */
    public void findMusicContentByDate(){
        
        LOG.info("LOOKING FOR MUSIC BY DATE");
           

        List<Tracks> tracks = trackController.findTracksByDate(startDate, endDate);
        List<Albums> albums = albumController.findAlbumsByDate(startDate, endDate);

        storeTracks(tracks);
        storeAlbums(albums);
     
    }
    
    /**
     * Method finds music content (albums and tracks)
     * made by a specific artist
     */
    private void findMusicContentByArtist(){
        
       List<Tracks> tracks = trackController.findTracksByArtist(searchValue);
       List<Albums> albums = albumController.findAlbumsByArtist(searchValue);
       
       storeTracks(tracks);
       storeAlbums(albums);
    }
    
    /**
     * Method finds tracks by track name
     */
    private void findTracks(){
        
        List<Tracks> tracks = trackController.findTracksByTitle(searchValue);
        storeTracks(tracks);
    }
    
    /**
     * Method stores tracks into a music component
     * @param tracks 
     */
    private void storeTracks(List<Tracks> tracks){
                
        for(var track : tracks){
            
            TrackBackingBean trackBackingBean = new TrackBackingBean(track);
            trackBackingBean.setCoverPath(albumController.getAlbumPath(track.getAlbumNumber().getAlbumNumber(), true));
            trackBackingBean.setArtists(trackController.findArtists(track.getTrackId()));
            
            trackComponents.add(trackBackingBean);
        }
    }
    
    /**
     * Method finds albums by album Name
     */
    private void findAlbums(){
        
        List<Albums> albums = albumController.findAlbumsByTitle(searchValue);
        storeAlbums(albums);
        
    }
    
    public String sendComponentPage(){


        if(sendComponent instanceof TrackBackingBean){
              
           return trackBean.sendTrack(sendComponent.getId(), sendComponent.getTitle());
        }

        return albumBean.sendAlbum(sendComponent.getId());
    }
        
               
    
    /**
     * Method stores albums into a music component
     * @param albums 
     */
    private void storeAlbums(List<Albums> albums){
        
        
        
        for(var album : albums){
            
            var albumBackingBean = new AlbumBackingBean(album);
            albumBackingBean.setCoverPath(albumController.getAlbumPath(album.getAlbumNumber(), true));
            albumBackingBean.setArtists(albumController.findArtists(album.getAlbumNumber()));
            
            albumComponents.add(albumBackingBean);
        }
        
    }
    
    
    /**
     * Method checks if there's any music content (tracks or albums) found
     * @return 
     */
    public boolean isEmptyList(){
                
        return albumComponents.size() <= 0 && trackComponents.size() <= 0;
    }
    
    
    /**
     * Method finds albums by genre
     * @return 
     */
    public String findAlbumsByGenre(){
        
        albumComponents = new ArrayList<>();
        trackComponents = new ArrayList<>();
        
        FacesContext fc = FacesContext.getCurrentInstance();
        Map<String,String> params = fc.getExternalContext().getRequestParameterMap();
        String genre = params.get("genre");
        List<Albums> albums = albumController.findAlbumsByGenre(genre);
        LOG.info("Album list size: " + albums.size());
        storeAlbums(albums);
        
        return "browse_music.xhtml?faces-redirect=true";
    }

}
