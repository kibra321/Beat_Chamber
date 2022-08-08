package com.beatchamber.beans.manager;

import com.beatchamber.entities.CustomerReviews;
import com.beatchamber.jpacontroller.CustomerReviewsJpaController;
import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Massimo Di Girolamo
 */
@Named
@RequestScoped
public class ReviewBackingBean implements Serializable {

    private final static Logger LOG = LoggerFactory.getLogger(ReviewBackingBean.class);

    @Inject
    private CustomerReviewsJpaController customerReviewsJpaController;

    private List<CustomerReviews> customerReviewsList;

    private CustomerReviews selectedCustomerReview;

    private boolean approval_status;

    private List<CustomerReviews> filteredCustomerReviewList;

    /**
     * Initialization.
     */
    @PostConstruct
    public void init() {
        this.customerReviewsList = customerReviewsJpaController.findCustomerReviewsEntities();
    }

    /**
     * Constructor, creates the selected Customer Review
     */
    public ReviewBackingBean() {
        this.selectedCustomerReview = new CustomerReviews();
    }

    /**
     * Get all the customer reviews.
     *
     * @return a list of customer reviews in the database.
     */
    public List<CustomerReviews> getCustomerReviews() {

        return customerReviewsList;
    }

    /**
     * Get the selected the customer review.
     *
     * @return the selected customer review.
     */
    public CustomerReviews getSelectedCustomerReviews() {
        return selectedCustomerReview;
    }

    /**
     * Set the selected customer review.
     *
     * @param selectedCustomerReview the selected customer review.
     */
    public void setSelectedCustomerReviews(CustomerReviews selectedCustomerReview) {
        this.selectedCustomerReview = selectedCustomerReview;
    }

    /**
     * Get the filteredCustomerReviewList.
     *
     * @return a list of filteredCustomerReviewList.
     */
    public List<CustomerReviews> getFilteredCustomerReviewList() {
        return filteredCustomerReviewList;
    }

    /**
     * Set the filteredCustomerReviewList.
     *
     * @param filteredCustomerReviewList a list of filteredCustomerReviewList.
     */
    public void setFilteredCustomerReviewList(List<CustomerReviews> filteredCustomerReviewList) {
        this.filteredCustomerReviewList = filteredCustomerReviewList;
    }

    public boolean getApproval_Status() {
        return this.approval_status;
    }

    public void setApproval_Status(boolean approval_status) {
        this.approval_status = approval_status;
    }

//    /**
//     * Initialize the selectedCustomerReview field when opening the add new
//     * dialog.
//     */
//    public void openNew() {
//        this.selectedCustomerReview = new CustomerReviews();
//    }
    public void changeReviewApproval(CustomerReviews custRev) throws Exception {

        //Set the selected Customer Review
        this.selectedCustomerReview = custRev;

        LOG.debug("Listener is called");
        LOG.debug("the approval status was " + this.selectedCustomerReview.getApprovalStatus());

        this.approval_status = this.selectedCustomerReview.getApprovalStatus();
        //calls a method that will change the approval status of the property
        changeApprovalStatus();

        this.selectedCustomerReview.setApprovalStatus(this.approval_status);
        customerReviewsJpaController.edit(this.selectedCustomerReview);

        LOG.debug("the approval status has been changed to " + this.approval_status);

    }

    /**
     * This method changes the approval status when the commandbutton is clicked
     */
    private void changeApprovalStatus() {

        //if the approval status is true switch it
        if (this.approval_status) {
            this.approval_status = false;
        } else {
            this.approval_status = true;
        }
    }

    /**
     * This method pops up a dialog box that says that the approval status was
     * changed
     */
    public void showApprovalStatus() {
        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Message", "Approval Status Changed");
    }

    /**
     * This method refreshes the page when a review is approved
     *
     * @throws IOException
     */
    public void reloadManagerReviewPage() throws IOException {
        ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
        ec.redirect(((HttpServletRequest) ec.getRequest()).getRequestURI());
    }
}
