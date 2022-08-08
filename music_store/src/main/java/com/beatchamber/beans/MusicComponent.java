package com.beatchamber.beans;

import com.beatchamber.entities.Artists;
import java.util.List;

/**
 * Interface for commmon needed functions for
 * musical items (albums and tracks)
 *
 * @author Korjon Chang-Jones
 */
public interface MusicComponent {

    String getTitle();

    void setArtists(List<Artists> artists);
    
    int getId();
        
    double getPrice();
    
    String getArtists();

    /**
     * Gets the file path for the song or album cover
     *
     * @return
     */
    String getCoverPath();
    
    void setCoverPath(String coverPath);

    /**
     * Sends the url of the page for either an album or a track
     *
     * @return webpage url String
     */
    //String sendComponentPage();

}
