package com.beatchamber.beans;

import com.beatchamber.entities.Albums;
import com.beatchamber.entities.Artists;
import com.beatchamber.jpacontroller.AlbumsJpaController;
import java.io.Serializable;
import java.util.List;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;
import org.slf4j.LoggerFactory;

/**
 * Class is a wrapper for an Album entity
 * Display purposes on the browse music tab
 * @author Korjon Chang-Jones
 */
@Named("albumWrapper")
@SessionScoped
public class AlbumBackingBean implements Serializable, MusicComponent {

    private final static org.slf4j.Logger LOG = LoggerFactory.getLogger(LoginRegisterBean.class);

    private Albums album;
    private List<Artists> artists;
    private String coverPath;
    private double price;

    @Inject
    AlbumsJpaController albumController;

    @Inject
    AlbumBean albumBean;

    public AlbumBackingBean() {

        this.album = new Albums();
    }

    public AlbumBackingBean(Albums album) {

        this.album = album;
    }

    @Override
    public String getTitle() {

        return album.getAlbumTitle();
    }

    @Override
    public String getArtists() {

        StringBuilder artistNames = new StringBuilder();

        for (int i = 0; i < artists.size(); i++) {

            if (i < artists.size() - 1) {

                artistNames.append(artists.get(i).getArtistName() + ", ");
            } else {

                artistNames.append(artists.get(i).getArtistName());

            }
        }

        return artistNames.toString();
    }

    public void setArtists(List<Artists> artists) {
        this.artists = artists;
    }

    @Override
    public String getCoverPath() {

        return coverPath;
    }

    @Override
    public void setCoverPath(String coverPath) {
        this.coverPath = coverPath;
    }

    @Override
    public int getId() {

        return album.getAlbumNumber();
    }

    @Override
    public double getPrice() {
        
        return album.getListPrice();
    }

}
