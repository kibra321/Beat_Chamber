package com.beatchamber.jpacontroller;

import com.beatchamber.entities.Choices;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import com.beatchamber.entities.SurveyToChoice;
import com.beatchamber.exceptions.NonexistentEntityException;
import com.beatchamber.exceptions.RollbackFailureException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import javax.annotation.Resource;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceContext;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Massimo Di Girolamo
 */
@Named
@SessionScoped
public class ChoicesJpaController implements Serializable {

    private final static Logger LOG = LoggerFactory.getLogger(ChoicesJpaController.class);

    @Resource
    private UserTransaction utx;

    @PersistenceContext(unitName = "music_store_persistence")
    private EntityManager em;

    public ChoicesJpaController() {
    }

    public void create(Choices choices) throws RollbackFailureException {
        
        if (choices.getSurveyToChoiceList() == null) {
            choices.setSurveyToChoiceList(new ArrayList<SurveyToChoice>());
        }

        try {

            utx.begin();
            List<SurveyToChoice> attachedSurveyToChoiceList = new ArrayList<SurveyToChoice>();
            for (SurveyToChoice surveyToChoiceListSurveyToChoiceToAttach : choices.getSurveyToChoiceList()) {
                surveyToChoiceListSurveyToChoiceToAttach = em.getReference(surveyToChoiceListSurveyToChoiceToAttach.getClass(), surveyToChoiceListSurveyToChoiceToAttach.getTablekey());
                attachedSurveyToChoiceList.add(surveyToChoiceListSurveyToChoiceToAttach);
            }
            choices.setSurveyToChoiceList(attachedSurveyToChoiceList);
            em.persist(choices);
            for (SurveyToChoice surveyToChoiceListSurveyToChoice : choices.getSurveyToChoiceList()) {
                Choices oldChoiceIdOfSurveyToChoiceListSurveyToChoice = surveyToChoiceListSurveyToChoice.getChoiceId();
                surveyToChoiceListSurveyToChoice.setChoiceId(choices);
                surveyToChoiceListSurveyToChoice = em.merge(surveyToChoiceListSurveyToChoice);
                if (oldChoiceIdOfSurveyToChoiceListSurveyToChoice != null) {
                    oldChoiceIdOfSurveyToChoiceListSurveyToChoice.getSurveyToChoiceList().remove(surveyToChoiceListSurveyToChoice);
                    oldChoiceIdOfSurveyToChoiceListSurveyToChoice = em.merge(oldChoiceIdOfSurveyToChoiceListSurveyToChoice);
                }
            }
            utx.commit();
        } catch (NotSupportedException | SystemException | RollbackException | HeuristicMixedException | HeuristicRollbackException | SecurityException | IllegalStateException ex) {
            try {
                utx.rollback();
                LOG.error("Rollback");
            } catch (IllegalStateException | SecurityException | SystemException re) {
                LOG.error("Rollback2");

                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
        }
    }

    public void edit(Choices choices) throws NonexistentEntityException, Exception {

        try {

            utx.begin();
            Choices persistentChoices = em.find(Choices.class, choices.getChoiceId());
            List<SurveyToChoice> surveyToChoiceListOld = persistentChoices.getSurveyToChoiceList();
            List<SurveyToChoice> surveyToChoiceListNew = choices.getSurveyToChoiceList();
            List<SurveyToChoice> attachedSurveyToChoiceListNew = new ArrayList<SurveyToChoice>();
            for (SurveyToChoice surveyToChoiceListNewSurveyToChoiceToAttach : surveyToChoiceListNew) {
                surveyToChoiceListNewSurveyToChoiceToAttach = em.getReference(surveyToChoiceListNewSurveyToChoiceToAttach.getClass(), surveyToChoiceListNewSurveyToChoiceToAttach.getTablekey());
                attachedSurveyToChoiceListNew.add(surveyToChoiceListNewSurveyToChoiceToAttach);
            }
            surveyToChoiceListNew = attachedSurveyToChoiceListNew;
            choices.setSurveyToChoiceList(surveyToChoiceListNew);
            choices = em.merge(choices);
            for (SurveyToChoice surveyToChoiceListOldSurveyToChoice : surveyToChoiceListOld) {
                if (!surveyToChoiceListNew.contains(surveyToChoiceListOldSurveyToChoice)) {
                    surveyToChoiceListOldSurveyToChoice.setChoiceId(null);
                    surveyToChoiceListOldSurveyToChoice = em.merge(surveyToChoiceListOldSurveyToChoice);
                }
            }
            for (SurveyToChoice surveyToChoiceListNewSurveyToChoice : surveyToChoiceListNew) {
                if (!surveyToChoiceListOld.contains(surveyToChoiceListNewSurveyToChoice)) {
                    Choices oldChoiceIdOfSurveyToChoiceListNewSurveyToChoice = surveyToChoiceListNewSurveyToChoice.getChoiceId();
                    surveyToChoiceListNewSurveyToChoice.setChoiceId(choices);
                    surveyToChoiceListNewSurveyToChoice = em.merge(surveyToChoiceListNewSurveyToChoice);
                    if (oldChoiceIdOfSurveyToChoiceListNewSurveyToChoice != null && !oldChoiceIdOfSurveyToChoiceListNewSurveyToChoice.equals(choices)) {
                        oldChoiceIdOfSurveyToChoiceListNewSurveyToChoice.getSurveyToChoiceList().remove(surveyToChoiceListNewSurveyToChoice);
                        oldChoiceIdOfSurveyToChoiceListNewSurveyToChoice = em.merge(oldChoiceIdOfSurveyToChoiceListNewSurveyToChoice);
                    }
                }
            }
            utx.commit();
        } catch (NotSupportedException | SystemException | RollbackException | HeuristicMixedException | HeuristicRollbackException | SecurityException | IllegalStateException ex) {
            try {
                utx.rollback();
            } catch (IllegalStateException | SecurityException | SystemException re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = choices.getChoiceId();
                if (findChoices(id) == null) {
                    throw new NonexistentEntityException("The choices with id " + id + " no longer exists.");
                }
            }
            throw ex;
        }
    }

    public void destroy(Integer id) throws NonexistentEntityException, NotSupportedException, SystemException, RollbackException, RollbackFailureException, HeuristicMixedException, HeuristicRollbackException {
        
        try {
            utx.begin();
            Choices choices;
            try {
                choices = em.getReference(Choices.class, id);
                choices.getChoiceId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The choices with id " + id + " no longer exists.", enfe);
            }
            List<SurveyToChoice> surveyToChoiceList = choices.getSurveyToChoiceList();
            for (SurveyToChoice surveyToChoiceListSurveyToChoice : surveyToChoiceList) {
                surveyToChoiceListSurveyToChoice.setChoiceId(null);
                surveyToChoiceListSurveyToChoice = em.merge(surveyToChoiceListSurveyToChoice);
            }
            em.remove(choices);
            utx.commit();
        } catch (NotSupportedException | SystemException | NonexistentEntityException | RollbackException | HeuristicMixedException | HeuristicRollbackException | SecurityException | IllegalStateException ex) {
            try {
                utx.rollback();
            } catch (IllegalStateException | SecurityException | SystemException re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            throw ex;
        }
    }

    public List<Choices> findChoicesEntities() {
        return findChoicesEntities(true, -1, -1);
    }

    public List<Choices> findChoicesEntities(int maxResults, int firstResult) {
        return findChoicesEntities(false, maxResults, firstResult);
    }

    private List<Choices> findChoicesEntities(boolean all, int maxResults, int firstResult) {
        CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
        cq.select(cq.from(Choices.class));
        Query q = em.createQuery(cq);
        if (!all) {
            q.setMaxResults(maxResults);
            q.setFirstResult(firstResult);
        }
        return q.getResultList();
    }

    public Choices findChoices(Integer id) {
        return em.find(Choices.class, id);
    }

    public int getChoicesCount() {
        CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
        Root<Choices> rt = cq.from(Choices.class);
        cq.select(em.getCriteriaBuilder().count(rt));
        Query q = em.createQuery(cq);
        return ((Long) q.getSingleResult()).intValue();
    }
    
    /**
     * Increase the number of votes of a choice
     * 
     * @param choiceId 
     */
    public void increaseChoiceVotes(int choiceId) {
        try {
            UserTransaction transaction = (UserTransaction)new InitialContext().lookup("java:comp/UserTransaction");
            transaction.begin();
            Choices choice = (Choices)em.find(Choices.class, choiceId);
            choice.setVotes(choice.getVotes() + 1);
            transaction.commit();
        } catch (NamingException | RollbackException | HeuristicMixedException | 
                HeuristicRollbackException | SecurityException | IllegalStateException | 
                SystemException | NotSupportedException ex) {
            displayMessage(FacesMessage.SEVERITY_ERROR, ex.getMessage(), ex.getCause().toString());
        }
    }
    
    /**
     * Using p:growl to display a message
     * 
     * @param severity
     * @param title
     * @param message 
     */
    public void displayMessage(FacesMessage.Severity severity, String title, String message) {
        FacesContext context = FacesContext.getCurrentInstance();
        context.addMessage(null, new FacesMessage(severity, title, message));
    }
}
