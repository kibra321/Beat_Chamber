package com.beatchamber.entities;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

/**
 *
 * @author kibra
 */
@Entity
@Table(name = "survey_to_choice", catalog = "CSgb1w21", schema = "")
@NamedQueries({
    @NamedQuery(name = "SurveyToChoice.findAll", query = "SELECT s FROM SurveyToChoice s"),
    @NamedQuery(name = "SurveyToChoice.findByTablekey", query = "SELECT s FROM SurveyToChoice s WHERE s.tablekey = :tablekey")})
public class SurveyToChoice implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "tablekey")
    private Integer tablekey;
    @JoinColumn(name = "choice_id", referencedColumnName = "choice_id")
    @ManyToOne
    private Choices choiceId;
    @JoinColumn(name = "survey_id", referencedColumnName = "survey_id")
    @ManyToOne
    private Surveys surveyId;

    public SurveyToChoice() {
    }

    public SurveyToChoice(Integer tablekey) {
        this.tablekey = tablekey;
    }

    public Integer getTablekey() {
        return tablekey;
    }

    public void setTablekey(Integer tablekey) {
        this.tablekey = tablekey;
    }

    public Choices getChoiceId() {
        return choiceId;
    }

    public void setChoiceId(Choices choiceId) {
        this.choiceId = choiceId;
    }

    public Surveys getSurveyId() {
        return surveyId;
    }

    public void setSurveyId(Surveys surveyId) {
        this.surveyId = surveyId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (tablekey != null ? tablekey.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof SurveyToChoice)) {
            return false;
        }
        SurveyToChoice other = (SurveyToChoice) object;
        if ((this.tablekey == null && other.tablekey != null) || (this.tablekey != null && !this.tablekey.equals(other.tablekey))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.beatchamber.entities.SurveyToChoice[ tablekey=" + tablekey + " ]";
    }
    
}
