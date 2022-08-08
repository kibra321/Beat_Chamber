/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.beatchamber.beans.manager;

import com.beatchamber.entities.Choices;
import com.beatchamber.entities.SurveyToChoice;
import com.beatchamber.entities.Surveys;
import com.beatchamber.exceptions.IllegalOrphanException;
import com.beatchamber.exceptions.NonexistentEntityException;
import com.beatchamber.exceptions.RollbackFailureException;
import com.beatchamber.jpacontroller.ChoicesJpaController;
import com.beatchamber.jpacontroller.SurveyToChoiceJpaController;
import com.beatchamber.jpacontroller.SurveysJpaController;
import java.io.Serializable;
import java.util.List;
import java.util.logging.Level;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.inject.Inject;
import javax.inject.Named;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;
import org.primefaces.PrimeFaces;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;

/**
 * This backing bean is used for survey management page
 * 
 * @author 1733570 Yan Tang
 */
@Named("theSurveys")
@ViewScoped
public class SurveysBackingBean implements Serializable {

    private final static Logger LOG = LoggerFactory.getLogger(SurveysBackingBean.class);

    @Inject
    private SurveysJpaController surveysJpaController;

    @Inject
    private SurveyToChoiceJpaController surveyToChoiceJpaController;

    @Inject
    private ChoicesJpaController choicesJpaController;

    private List<Surveys> surveys;
    private Surveys selectedSurvey;

    private Choices choice1;
    private Choices choice2;
    private Choices choice3;
    private Choices choice4;
    private Choices choice5;

    private String numOfChoices = "5";
    private boolean renderable3 = true;
    private boolean renderable4 = true;
    private boolean renderable5 = true;
    
    /**
     * Initialization
     */
    @PostConstruct
    public void init() {
        this.surveys = surveysJpaController.findSurveysEntities();
    }

    /**
     * Get selected Survey info.
     * @return the selected survey
     */
    public Surveys getSelectedSurvey() {
        if(selectedSurvey == null){
            //get new survey
            this.selectedSurvey = new Surveys();
        }
        return selectedSurvey;
    }

    /**
     * Set selected survey.
     * @param selectedSurvey 
     */
    public void setSelectedSurvey(Surveys selectedSurvey) {
        this.selectedSurvey = selectedSurvey;
        
        //set number of choices
        this.numOfChoices = "" + this.selectedSurvey.getSurveyToChoiceList().size();
        this.setBooleans();
        
        // set all the choices' value based on the number of choices
        this.choice1 = this.selectedSurvey.getSurveyToChoiceList().get(0).getChoiceId();
        this.choice2 = null;
        this.choice3 = null;
        this.choice4 = null;
        this.choice5 = null;
        if (this.selectedSurvey.getSurveyToChoiceList().size() > 1) {
            this.choice2 = this.selectedSurvey.getSurveyToChoiceList().get(1).getChoiceId();
            if (this.selectedSurvey.getSurveyToChoiceList().size() > 2) {
                this.choice3 = this.selectedSurvey.getSurveyToChoiceList().get(2).getChoiceId();
                if (this.selectedSurvey.getSurveyToChoiceList().size() > 3) {
                    this.choice4 = this.selectedSurvey.getSurveyToChoiceList().get(3).getChoiceId();
                    if (this.selectedSurvey.getSurveyToChoiceList().size() > 4) {
                        this.choice5 = this.selectedSurvey.getSurveyToChoiceList().get(4).getChoiceId();
                    }
                }
            }
        }
    }
    
    /**
     * Get all the surveys.
     * @return a list of all the surveys
     */
    public List<Surveys> getSurveys() {
        return surveys;
    }
    
    /**
     * Get Choice1.
     * @return choice1
     */
    public Choices getChoice1() {
        if(this.choice1 == null){
            this.choice1 = new Choices();
        this.choice1.setChoiceName("");
        }
        return this.choice1;
    }

    /**
     * Get Choice2.
     * @return choice2
     */
    public Choices getChoice2() {
        if(this.choice2 == null){
            this.choice2 = new Choices();
        this.choice2.setChoiceName("");
        }
        return this.choice2;
    }

    /**
     * Get Choice3.
     * @return choice3
     */
    public Choices getChoice3() {
        if(this.choice3 == null){
            this.choice3 = new Choices();
        this.choice3.setChoiceName("");
        }
        return this.choice3;
    }

    /**
     * Get Choice4.
     * @return choice4
     */
    public Choices getChoice4() {
        if(this.choice4 == null){
            this.choice4 = new Choices();
        this.choice4.setChoiceName("");
        }
        return this.choice4;
    }

    /**
     * Get Choice5.
     * @return choice5
     */
    public Choices getChoice5() {
        if(this.choice5 == null){
            this.choice5 = new Choices();
        this.choice5.setChoiceName("");
        }
        return this.choice5;
    }

    
     /**
     * Get number of the choices .
     * @return the number of choices.
     */
    public String getNumOfChoices(){
        return this.numOfChoices;
    }

    /**
     * Set the Number of Choices.
     * @param numOfChoices the number of choices.
     */
    public void setNumOfChoices(String numOfChoices) {
        this.numOfChoices = numOfChoices;
        this.setBooleans();
        PrimeFaces.current().ajax().update("form:messages", "form:manage-options-content");
    }
    
     /**
     * Get if Choice3 is available.
     * @return true when Choice3 is available, otherwise false.
     */
    public boolean getRenderable3() {
        return this.renderable3;
    }

    /**
     * Get if Choice4 is available.
     * @return true when Choice4 is available, otherwise false.
     */
    public boolean getRenderable4() {
        return this.renderable4;
    }

    /**
     * Get if Choice5 is available.
     * @return true when Choice5 is available, otherwise false.
     */
    public boolean getRenderable5() {
        return this.renderable5;
    }

    /**
     * Used for valueChangeListener of the Number of choices.
     */
    public void setNumOfChoicesAction() {
        //no action needed here. it is for ajax purpose only
    }


    /**
     * Initialize all the fields when opening the add new survey dialog.
     */
    public void openNew() {
        this.selectedSurvey = new Surveys();
        this.initializeChoices();
        this.numOfChoices = "2";
        this.setBooleans();
    }

    /**
     * Add or save all the changes of the survey and the choices.
     */
    public void saveSurvey() {
        try {
            if (this.selectedSurvey.getSurveyId() == null) {
                //when adding a new survey, add the survey and choices to the database 
                this.surveys.add(this.selectedSurvey);
                this.addChoicesDataToDB();
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Survey Added"));
            } else {
                //delete choice 3, 4, and 5 that are not needed anymore
                this.deleteChoices(); 
                //when editing a survey, save the changes to the database 
                this.surveysJpaController.edit(selectedSurvey);
                this.updateChoicesDataToDB();
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Survey Updated"));
            }
            //we need update the this.surveys busing the database data
            this.surveys = surveysJpaController.findSurveysEntities();
        } catch (RollbackFailureException ex) {
            java.util.logging.Logger.getLogger(ClientsBackingBean.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            java.util.logging.Logger.getLogger(ClientsBackingBean.class.getName()).log(Level.SEVERE, null, ex);
        }

        PrimeFaces.current().executeScript("PF('manageSurveyDialog').hide()");
        PrimeFaces.current().ajax().update("form:messages", "form:dt-surveys");
    }

    /**
     * Delete the survey and all the choices when delete button is clicked.
     */
    public void deleteSurvey() {
        try {
            //Delete the choice if the choice is available
            if (this.choice1 != null && this.choice1.getChoiceId() != null) {
                this.choicesJpaController.destroy(this.choice1.getChoiceId());
            }
            if (this.choice2 != null && this.choice2.getChoiceId() != null) {
                this.choicesJpaController.destroy(this.choice2.getChoiceId());
            }
            if (this.choice3 != null && this.choice3.getChoiceId() != null) {
                this.choicesJpaController.destroy(this.choice3.getChoiceId());
            }
            if (this.choice4 != null && this.choice4.getChoiceId() != null) {
                this.choicesJpaController.destroy(this.choice4.getChoiceId());
            }
            if (this.choice5 != null && this.choice5.getChoiceId() != null) {
                this.choicesJpaController.destroy(this.choice5.getChoiceId());
            }
            
            //Delete all the related item from surveyToChoice table
            List<SurveyToChoice> surveyToChoice = this.selectedSurvey.getSurveyToChoiceList();
            surveyToChoice.forEach(item -> {
                try {
                    this.surveyToChoiceJpaController.destroy(item.getTablekey());
                } catch (IllegalOrphanException | NonexistentEntityException | NotSupportedException | SystemException | RollbackFailureException | RollbackException | HeuristicMixedException | HeuristicRollbackException ex) {
                    java.util.logging.Logger.getLogger(SurveysBackingBean.class.getName()).log(Level.SEVERE, null, ex);
                }
            });
            
            //Delete the Surevey item from survey table
            this.surveysJpaController.destroy(this.selectedSurvey.getSurveyId());
        } catch (IllegalOrphanException | NonexistentEntityException | NotSupportedException | SystemException | RollbackFailureException | RollbackException | HeuristicMixedException | HeuristicRollbackException ex) {
            java.util.logging.Logger.getLogger(SurveysBackingBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        //Update the surveys List
        this.surveys.remove(this.selectedSurvey);
        this.selectedSurvey = null;
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Survey Removed"));
        PrimeFaces.current().ajax().update("form:messages", "form:dt-surveys");
    }

    
    /**
     * Add the choices to database when adding a new survey.
     */
    public void addChoicesDataToDB() {
        try {
            //this statement must be executed before adding it to the surveyToChoice otherwise the survey id is null
            this.surveysJpaController.create(selectedSurvey);
            if (choice1 != null && !choice1.getChoiceName().equals("")) {
                this.addChoiceAndSurveyToChoice(choice1);
            }
            if (choice2 != null && !choice2.getChoiceName().equals("")) {
                this.addChoiceAndSurveyToChoice(choice2);
            }
            if (choice3 != null && !choice3.getChoiceName().equals("")  && Integer.parseInt(this.numOfChoices) > 2) {
                this.addChoiceAndSurveyToChoice(choice3);
            }
            if (choice4 != null && !choice4.getChoiceName().equals("")  && Integer.parseInt(this.numOfChoices) > 3) {
                this.addChoiceAndSurveyToChoice(choice4);
            }
            if (choice5 != null && !choice5.getChoiceName().equals("")  && Integer.parseInt(this.numOfChoices) >4) {
                this.addChoiceAndSurveyToChoice(choice5);
            }
        } catch (RollbackFailureException ex) {
            java.util.logging.Logger.getLogger(SurveysBackingBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Update the Choices when editing.
     */
    public void updateChoicesDataToDB() {
        try {
            if (choice1 != null && !choice1.getChoiceName().equals("")) {
                if (choice1.getChoiceId() == null) {
                    addChoiceAndSurveyToChoice(choice1);
                } else {
                    this.choicesJpaController.edit(choice1);
                }
            }
            if (choice2 != null && !choice2.getChoiceName().equals("")) {
                if (choice1.getChoiceId() == null) {
                    addChoiceAndSurveyToChoice(choice2);
                } else {
                    this.choicesJpaController.edit(choice2);
                }
            }
            if (choice3 != null && !choice3.getChoiceName().equals("")) {
                if (this.selectedSurvey.getSurveyToChoiceList().size() < 3 && Integer.parseInt(numOfChoices) > 2) {
                    //newly added choice
                    addChoiceAndSurveyToChoice(choice3);
                } else {
                    this.choicesJpaController.edit(choice3);
                }
            }
            if (choice4 != null && !choice4.getChoiceName().equals("")) {
                if (this.selectedSurvey.getSurveyToChoiceList().size() < 4 && Integer.parseInt(numOfChoices) > 3) {
                    addChoiceAndSurveyToChoice(choice4);
                } else {
                    this.choicesJpaController.edit(choice4);
                }
            }
            if (choice5 != null && !choice5.getChoiceName().equals("")) {
                if (this.selectedSurvey.getSurveyToChoiceList().size() < 5 && Integer.parseInt(numOfChoices) > 4) {
                    addChoiceAndSurveyToChoice(choice5);
                } else {
                    this.choicesJpaController.edit(choice5);
                }
            }
        } catch (Exception ex) {
            java.util.logging.Logger.getLogger(SurveysBackingBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Add the choices to the choices table and surveyToChoice table.
     * @param choice 
     */
    private void addChoiceAndSurveyToChoice(Choices choice) {
        try {
            this.choicesJpaController.create(choice);
            SurveyToChoice surveyToChoice = new SurveyToChoice();
            surveyToChoice.setChoiceId(choice);
            surveyToChoice.setSurveyId(this.selectedSurvey);
            this.surveyToChoiceJpaController.create(surveyToChoice);
        } catch (RollbackFailureException ex) {
            java.util.logging.Logger.getLogger(SurveysBackingBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Initialize all the choices.
     */
    private void initializeChoices() {
        this.choice1 = new Choices();
        this.choice1.setChoiceName("");
        this.choice1.setVotes(0);
        this.choice2 = new Choices();
        this.choice2.setChoiceName("");
        this.choice2.setVotes(0);
        this.choice3 = new Choices();
        this.choice3.setChoiceName("");
        this.choice3.setVotes(0);
        this.choice4 = new Choices();
        this.choice4.setChoiceName("");
        this.choice4.setVotes(0);
        this.choice5 = new Choices();
        this.choice5.setChoiceName("");
        this.choice5.setVotes(0);
    }

   

    /**
     * Set the flag to identify how many options are available.
     */
    private void setBooleans() {
        switch (this.numOfChoices) {
            case "2":
                this.renderable3 = false;
                this.renderable4 = false;
                this.renderable5 = false;
                break;
            case "3":
                this.renderable3 = true;
                this.renderable4 = false;
                this.renderable5 = false;
                break;
            case "4":
                this.renderable3 = true;
                this.renderable4 = true;
                this.renderable5 = false;
                break;
            case "5":
                this.renderable3 = true;
                this.renderable4 = true;
                this.renderable5 = true;
                break;
            default:
                break;
        }
    }

   
    /**
     * Delete the choices if the choices are not need when editing.
     */
    private void deleteChoices() {
        try {
            
            if (this.selectedSurvey.getSurveyToChoiceList().size() > Integer.parseInt(numOfChoices)) {
                //
                List<SurveyToChoice> temp = this.selectedSurvey.getSurveyToChoiceList();
                switch (this.numOfChoices) {
                    case "2":
                        for (int i = 2; i < temp.size(); i++) {
                            this.surveyToChoiceJpaController.destroy(temp.get(i).getTablekey());
                        }
                        if (this.choice3.getChoiceId() != null) {
                            this.choicesJpaController.destroy(this.choice3.getChoiceId());
                        }
                        if (this.choice4.getChoiceId() != null) {
                            this.choicesJpaController.destroy(this.choice4.getChoiceId());
                        }
                        if (this.choice5.getChoiceId() != null) {
                            this.choicesJpaController.destroy(this.choice5.getChoiceId());
                        }
                        break;
                    case "3":
                        for (int i = 3; i < temp.size(); i++) {
                            this.surveyToChoiceJpaController.destroy(temp.get(i).getTablekey());
                        }
                        if (this.choice4.getChoiceId() != null) {
                            this.choicesJpaController.destroy(this.choice4.getChoiceId());
                        }
                        if (this.choice5.getChoiceId() != null) {
                            this.choicesJpaController.destroy(this.choice5.getChoiceId());
                        }
                        break;
                    case "4":
                        for (int i = 4; i < temp.size(); i++) {
                            this.surveyToChoiceJpaController.destroy(temp.get(i).getTablekey());
                        }
                        if (this.choice5.getChoiceId() != null) {
                            this.choicesJpaController.destroy(this.choice5.getChoiceId());
                        }
                        break;
                    default:
                        break;
                }
            }
        } catch (IllegalOrphanException | NonexistentEntityException | NotSupportedException | SystemException | RollbackException | RollbackFailureException | HeuristicMixedException | HeuristicRollbackException ex) {
            java.util.logging.Logger.getLogger(SurveysBackingBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * Initialize all the field when cancel a operation.
     */
    public void cancelAction(){
        this.openNew();
    }
    
    public void updateEnabled(Surveys survey) {
        try {
            this.surveysJpaController.edit(survey);
        } catch (RollbackFailureException | IllegalOrphanException | NonexistentEntityException ex) {
            java.util.logging.Logger.getLogger(SurveysBackingBean.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            java.util.logging.Logger.getLogger(SurveysBackingBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (survey.getEnabled()) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("This Survey Enabled"));
        } else {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("This Survey Disabled"));
        }
        PrimeFaces.current().ajax().update("form:messages", "form:dt-surveys");
    }
}
