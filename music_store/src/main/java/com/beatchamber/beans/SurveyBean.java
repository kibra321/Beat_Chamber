package com.beatchamber.beans;

import com.beatchamber.entities.Choices;
import com.beatchamber.entities.Surveys;
import com.beatchamber.jpacontroller.ChoicesJpaController;
import com.beatchamber.jpacontroller.SurveysJpaController;
import java.io.Serializable;
import java.util.List;
import java.util.ResourceBundle;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.application.FacesMessage.Severity;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Bean for survey functionality
 * 
 * @author Susan Vuu - 1735488
 */
@Named
@SessionScoped
public class SurveyBean implements Serializable {
    
    private final static Logger LOG = LoggerFactory.getLogger(SurveyBean.class);

    //JPA controllers used for survey functionality
    @Inject
    SurveysJpaController surveysJpaController;
    
    @Inject
    ChoicesJpaController choicesJpaController;
    
    @Inject
    LocaleChanger localeChanger;
    
    //Stored values
    private Surveys survey;
    private List<Choices> surveyChoices;
    private String selectedChoice;

    /**
     * Default constructor
     */
    public SurveyBean() {
        this.survey = null;
        this.surveyChoices = null;
        this.selectedChoice = "";
    }
    
    /**
     * @return The choices of the survey
     */
    public List<Choices> getSurveyChoices() {
        return this.surveyChoices;
    }
    
    /**
     * @return The selected choice of the user
     */
    public String getSelectedChoice() {
        return selectedChoice;
    }

    /**
     * Set the selected choice of the user
     * @param selectedChoice 
     */
    public void setSelectedChoice(String selectedChoice) {
        this.selectedChoice = selectedChoice;
    }
    
    /**
     * @return The first approved survey. Nothing will display if none are approved
     */
    public Surveys getSurvey(){
        List<Surveys> allSurveys = this.surveysJpaController.findSurveysEntities();
        for (Surveys surveys : allSurveys) {
            if (surveys.getEnabled()) {
                this.survey = surveys;
                this.surveyChoices = surveysJpaController.getSurveyChoices(this.survey.getSurveyId());
                return surveys;
            }
        }
        return null;
    }
    
    /**
     * Voting process of the survey
     * 
     * @return Redirect to the index page if successful, nothing if not
     */
    public String voteSurvey() {
        //Get the locale to display with the right language
        ResourceBundle languageBundle = ResourceBundle.getBundle("com.beatchamber.bundles.messages", localeChanger.getLocale());
        
        //Inform the user if they didn't select a radio button
        if(this.selectedChoice == null || this.selectedChoice.isEmpty()) {
            displayMessage(FacesMessage.SEVERITY_WARN, languageBundle.getString("warning"), languageBundle.getString("noChoiceSelected"));
            return null;
        }
        
        LOG.debug("Choice selected:" + this.selectedChoice);
        
        //The choice value from the radio buttons is the choice converted with toString which only displays the choice's id
        //Using regex, we can get the single number that's added in that toString.
        int choiceId = Integer.parseInt(this.selectedChoice.replaceAll("[^0-9]", ""));
        LOG.debug("Choice id:" + choiceId);
        
        //Find the specific choice in the table using its id
        Choices foundChoice = choicesJpaController.findChoices(choiceId);
        int previousVotes = foundChoice.getVotes();
        
        //Add 1 to the choice's votes
        choicesJpaController.increaseChoiceVotes(foundChoice.getChoiceId());
        Choices changedChoice = choicesJpaController.findChoices(foundChoice.getChoiceId());
        
        //Check if the voting was successful
        if(changedChoice.getVotes() == previousVotes + 1) {
            return "index.xhtml?faces-redirect=true";
        }
        else {
            displayMessage(FacesMessage.SEVERITY_FATAL, languageBundle.getString("votingFailed"), languageBundle.getString("votingError"));
            return null;
        }
    }
    
    /**
     * Using p:growl to display a message
     * 
     * @param severity
     * @param title
     * @param message 
     */
    public void displayMessage(Severity severity, String title, String message) {
        FacesContext context = FacesContext.getCurrentInstance();
        context.addMessage(null, new FacesMessage(severity, title, message));
    }
}
