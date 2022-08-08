package com.beatchamber.beans;

import com.beatchamber.entities.Artists;
import com.beatchamber.entities.Tracks;
import com.beatchamber.jpacontroller.AlbumsJpaController;
import com.beatchamber.jpacontroller.TracksJpaController;
import java.io.Serializable;
import java.util.List;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class is a wrapper for a Track entity.
 * Display purposes on the browse music tab
 * @author Korjon Chang-jones
 */
@Named("trackWrapper")
@SessionScoped
public class TrackBackingBean implements Serializable, MusicComponent {

    private final static Logger LOG = LoggerFactory.getLogger(AlbumsJpaController.class);

    private Tracks track;
    private List<Artists> artists;
    private String coverPath;
    private double price;

    @Inject
    private TrackBean trackBean;

    @Inject
    private AlbumsJpaController albumController;

    @Inject
    private TracksJpaController trackController;

    public TrackBackingBean() {

    }

    public TrackBackingBean(Tracks track) {

        this.track = track;
        trackBean = new TrackBean();
    }

    @Override
    public String getTitle() {

        return track.getTrackTitle();
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

    @Override
    public void setArtists(List<Artists> artists) {
        this.artists = artists;
    }

    @Override
    public void setCoverPath(String coverPath) {

        this.coverPath = coverPath;
    }

    @Override
    public String getCoverPath() {

        return coverPath;
    }

    @Override
    public int getId() {

        return track.getTrackId();
    }

    @Override
    public double getPrice() {
        return track.getListPrice();
    }

}
