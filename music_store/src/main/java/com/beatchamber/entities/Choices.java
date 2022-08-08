/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.beatchamber.entities;

import java.io.Serializable;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 *
 * @author kibra
 */
@Entity
@Table(name = "choices", catalog = "CSgb1w21", schema = "")
@NamedQueries({
    @NamedQuery(name = "Choices.findAll", query = "SELECT c FROM Choices c"),
    @NamedQuery(name = "Choices.findByChoiceId", query = "SELECT c FROM Choices c WHERE c.choiceId = :choiceId"),
    @NamedQuery(name = "Choices.findByChoiceName", query = "SELECT c FROM Choices c WHERE c.choiceName = :choiceName"),
    @NamedQuery(name = "Choices.findByVotes", query = "SELECT c FROM Choices c WHERE c.votes = :votes")})
public class Choices implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "choice_id")
    private Integer choiceId;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 40)
    @Column(name = "choice_name")
    private String choiceName;
    @Basic(optional = false)
    @NotNull
    @Column(name = "votes")
    private int votes;
    @OneToMany(mappedBy = "choiceId")
    private List<SurveyToChoice> surveyToChoiceList;

    public Choices() {
    }

    public Choices(Integer choiceId) {
        this.choiceId = choiceId;
    }

    public Choices(Integer choiceId, String choiceName, int votes) {
        this.choiceId = choiceId;
        this.choiceName = choiceName;
        this.votes = votes;
    }

    public Integer getChoiceId() {
        return choiceId;
    }

    public void setChoiceId(Integer choiceId) {
        this.choiceId = choiceId;
    }

    public String getChoiceName() {
        return choiceName;
    }

    public void setChoiceName(String choiceName) {
        this.choiceName = choiceName;
    }

    public int getVotes() {
        return votes;
    }

    public void setVotes(int votes) {
        this.votes = votes;
    }

    public List<SurveyToChoice> getSurveyToChoiceList() {
        return surveyToChoiceList;
    }

    public void setSurveyToChoiceList(List<SurveyToChoice> surveyToChoiceList) {
        this.surveyToChoiceList = surveyToChoiceList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (choiceId != null ? choiceId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Choices)) {
            return false;
        }
        Choices other = (Choices) object;
        if ((this.choiceId == null && other.choiceId != null) || (this.choiceId != null && !this.choiceId.equals(other.choiceId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.beatchamber.entities.Choices[ choiceId=" + choiceId + " ]";
    }
    
}
