/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.beatchamber.beans.validator;

import com.beatchamber.beans.CreditCard;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author kibra
 */
@FacesValidator("CvvValidation")
public class CvvValidation implements Validator {

    private final static Logger LOG = LoggerFactory.getLogger(CvvValidation.class);

    @Override
    public void validate(FacesContext context, UIComponent component,
            Object value) {

        if (value == null) {
            return;
        }
        
        String strValue="";
        
        if(value instanceof String){
            strValue = (String)value;
        }
        else{
            strValue = value.toString();
        }
        
        if(checkIfContainsLetters(strValue.toLowerCase())){
            FacesMessage message = com.beatchamber.util.Messages.getMessage("com.beatchamber.bundles.messages","cvvNoLetter",null);
            message.setSeverity(FacesMessage.SEVERITY_ERROR);
            throw new ValidatorException(message);
        }
        
        if(strValue.length()<3){
            FacesMessage message = com.beatchamber.util.Messages.getMessage("com.beatchamber.bundles.messages","cvvTooSmall",null);
            message.setSeverity(FacesMessage.SEVERITY_ERROR);
            throw new ValidatorException(message);
        }
        
        if(strValue.length()>4){
            FacesMessage message = com.beatchamber.util.Messages.getMessage("com.beatchamber.bundles.messages","cvvTooBig",null);
            message.setSeverity(FacesMessage.SEVERITY_ERROR);
            throw new ValidatorException(message);
        }

        
    }
    
    private boolean checkIfContainsLetters(String value){
        char[] valueChar = value.toCharArray();

        
        for(char item:valueChar){
            if(Character.isAlphabetic(item)){
                return true;
            }
        }
        return false;
    }

}