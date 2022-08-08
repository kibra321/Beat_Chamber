package com.beatchamber.jpacontroller;

import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import com.beatchamber.entities.Choices;
import com.beatchamber.entities.SurveyToChoice;
import com.beatchamber.entities.Surveys;
import com.beatchamber.exceptions.IllegalOrphanException;
import com.beatchamber.exceptions.RollbackFailureException;
import com.beatchamber.exceptions.NonexistentEntityException;
import java.util.List;
import javax.annotation.Resource;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;
import javax.persistence.EntityManager;
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
public class SurveyToChoiceJpaController implements Serializable {

    private final static Logger LOG = LoggerFactory.getLogger(SurveyToChoiceJpaController.class);

    @Resource
    private UserTransaction utx;

    @PersistenceContext(unitName = "music_store_persistence")
    private EntityManager em;

    public SurveyToChoiceJpaController() {
    }

    public void create(SurveyToChoice surveyToChoice) throws RollbackFailureException {

        try {
            utx.begin();
            Choices choiceId = surveyToChoice.getChoiceId();
            if (choiceId != null) {
                choiceId = em.getReference(choiceId.getClass(), choiceId.getChoiceId());
                surveyToChoice.setChoiceId(choiceId);
            }
            Surveys surveyId = surveyToChoice.getSurveyId();
            if (surveyId != null) {
                surveyId = em.getReference(surveyId.getClass(), surveyId.getSurveyId());
                surveyToChoice.setSurveyId(surveyId);
            }
            em.persist(surveyToChoice);
            if (choiceId != null) {
                choiceId.getSurveyToChoiceList().add(surveyToChoice);
                choiceId = em.merge(choiceId);
            }
            if (surveyId != null) {
                surveyId.getSurveyToChoiceList().add(surveyToChoice);
                surveyId = em.merge(surveyId);
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

    public void edit(SurveyToChoice surveyToChoice) throws NonexistentEntityException, Exception {

        try {
            utx.begin();
            SurveyToChoice persistentSurveyToChoice = em.find(SurveyToChoice.class, surveyToChoice.getTablekey());
            Choices choiceIdOld = persistentSurveyToChoice.getChoiceId();
            Choices choiceIdNew = surveyToChoice.getChoiceId();
            Surveys surveyIdOld = persistentSurveyToChoice.getSurveyId();
            Surveys surveyIdNew = surveyToChoice.getSurveyId();
            if (choiceIdNew != null) {
                choiceIdNew = em.getReference(choiceIdNew.getClass(), choiceIdNew.getChoiceId());
                surveyToChoice.setChoiceId(choiceIdNew);
            }
            if (surveyIdNew != null) {
                surveyIdNew = em.getReference(surveyIdNew.getClass(), surveyIdNew.getSurveyId());
                surveyToChoice.setSurveyId(surveyIdNew);
            }
            surveyToChoice = em.merge(surveyToChoice);
            if (choiceIdOld != null && !choiceIdOld.equals(choiceIdNew)) {
                choiceIdOld.getSurveyToChoiceList().remove(surveyToChoice);
                choiceIdOld = em.merge(choiceIdOld);
            }
            if (choiceIdNew != null && !choiceIdNew.equals(choiceIdOld)) {
                choiceIdNew.getSurveyToChoiceList().add(surveyToChoice);
                choiceIdNew = em.merge(choiceIdNew);
            }
            if (surveyIdOld != null && !surveyIdOld.equals(surveyIdNew)) {
                surveyIdOld.getSurveyToChoiceList().remove(surveyToChoice);
                surveyIdOld = em.merge(surveyIdOld);
            }
            if (surveyIdNew != null && !surveyIdNew.equals(surveyIdOld)) {
                surveyIdNew.getSurveyToChoiceList().add(surveyToChoice);
                surveyIdNew = em.merge(surveyIdNew);
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
                Integer id = surveyToChoice.getTablekey();
                if (findSurveyToChoice(id) == null) {
                    throw new NonexistentEntityException("The surveyToChoice with id " + id + " no longer exists.");
                }
            }
            throw ex;
        }
    }

    public void destroy(Integer id) throws IllegalOrphanException, NonexistentEntityException, NotSupportedException, SystemException, RollbackFailureException, RollbackException, HeuristicMixedException, HeuristicRollbackException {

        try {
            utx.begin();
            SurveyToChoice surveyToChoice;
            try {
                surveyToChoice = em.getReference(SurveyToChoice.class, id);
                surveyToChoice.getTablekey();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The surveyToChoice with id " + id + " no longer exists.", enfe);
            }
            Choices choiceId = surveyToChoice.getChoiceId();
            if (choiceId != null) {
                choiceId.getSurveyToChoiceList().remove(surveyToChoice);
                choiceId = em.merge(choiceId);
            }
            Surveys surveyId = surveyToChoice.getSurveyId();
            if (surveyId != null) {
                surveyId.getSurveyToChoiceList().remove(surveyToChoice);
                surveyId = em.merge(surveyId);
            }
            em.remove(surveyToChoice);
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

    public List<SurveyToChoice> findSurveyToChoiceEntities() {
        return findSurveyToChoiceEntities(true, -1, -1);
    }

    public List<SurveyToChoice> findSurveyToChoiceEntities(int maxResults, int firstResult) {
        return findSurveyToChoiceEntities(false, maxResults, firstResult);
    }

    private List<SurveyToChoice> findSurveyToChoiceEntities(boolean all, int maxResults, int firstResult) {

        CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
        cq.select(cq.from(SurveyToChoice.class));
        Query q = em.createQuery(cq);
        if (!all) {
            q.setMaxResults(maxResults);
            q.setFirstResult(firstResult);
        }
        return q.getResultList();
    }

    public SurveyToChoice findSurveyToChoice(Integer id) {

        return em.find(SurveyToChoice.class, id);
    }

    public int getSurveyToChoiceCount() {

        CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
        Root<SurveyToChoice> rt = cq.from(SurveyToChoice.class);
        cq.select(em.getCriteriaBuilder().count(rt));
        Query q = em.createQuery(cq);
        return ((Long) q.getSingleResult()).intValue();
    }

}
