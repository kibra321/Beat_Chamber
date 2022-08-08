package com.beatchamber.beans;

import java.io.Serializable;
import java.util.Date;
import javax.enterprise.context.RequestScoped;

import javax.inject.Named;

/**
 * The backing bean. Credit Card is an embedded class for use by the converter
 *
 * @author Ken Fogel
 */
@Named("payment")
@RequestScoped
public class PaymentBean implements Serializable {

    private double amount;
    private CreditCard card = new CreditCard("");
    private Date date = new Date();
    private String name;

    public void setAmount(double newValue) {
        amount = newValue;
    }

    public double getAmount() {
        return amount;
    }

    public void setCard(CreditCard newValue) {
        card = newValue;
    }

    public CreditCard getCard() {
        return card;
    }

    public void setDate(Date newValue) {
        date = newValue;
    }

    public Date getDate() {
        return date;
    }
    
        public void setName(String Name) {
        this.name = Name;
    }

    public String getName() {
        return this.name;
    }
}
