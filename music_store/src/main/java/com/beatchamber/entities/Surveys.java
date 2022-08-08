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
@Table(name = "surveys", catalog = "CSgb1w21", schema = "")
@NamedQueries({
    @NamedQuery(name = "Surveys.findAll", query = "SELECT s FROM Surveys s"),
    @NamedQuery(name = "Surveys.findBySurveyId", query = "SELECT s FROM Surveys s WHERE s.surveyId = :surveyId"),
    @NamedQuery(name = "Surveys.findByTitle", query = "SELECT s FROM Surveys s WHERE s.title = :title"),
    @NamedQuery(name = "Surveys.findByEnabled", query = "SELECT s FROM Surveys s WHERE s.enabled = :enabled")})
public class Surveys implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "survey_id")
    private Integer surveyId;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 80)
    @Column(name = "title")
    private String title;
    @Column(name = "enabled")
    private boolean enabled;
    @OneToMany(mappedBy = "surveyId")
    private List<SurveyToChoice> surveyToChoiceList;

    public Surveys() {
    }

    public Surveys(Integer surveyId) {
        this.surveyId = surveyId;
    }

    public Surveys(Integer surveyId, String title) {
        this.surveyId = surveyId;
        this.title = title;
    }

    public Integer getSurveyId() {
        return surveyId;
    }

    public void setSurveyId(Integer surveyId) {
        this.surveyId = surveyId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
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
        hash += (surveyId != null ? surveyId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Surveys)) {
            return false;
        }
        Surveys other = (Surveys) object;
        if ((this.surveyId == null && other.surveyId != null) || (this.surveyId != null && !this.surveyId.equals(other.surveyId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.beatchamber.entities.Surveys[ surveyId=" + surveyId + " ]";
    }

}
