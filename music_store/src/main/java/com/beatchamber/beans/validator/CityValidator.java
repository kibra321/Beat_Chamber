/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.beatchamber.beans.validator;

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
@FacesValidator("cityValidator")
public class CityValidator implements Validator {
    private final static Logger LOG = LoggerFactory.getLogger(AddressValidator.class);

    @Override
    public void validate(FacesContext context, UIComponent component, Object value) throws ValidatorException {
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
        
        if(checkIfContainsNumber(strValue.toLowerCase())){
            FacesMessage message = com.beatchamber.util.Messages.getMessage("com.beatchamber.bundles.messages","CityNoNumber",null);
            message.setSeverity(FacesMessage.SEVERITY_ERROR);
            throw new ValidatorException(message);
        }
        
    }
    
    private boolean checkIfContainsNumber(String value){
        char[] valueChar = value.toCharArray();

        
        for(char item:valueChar){
            if(Character.isDigit(item)){
                return true;
            }
        }
        return false;
    }

    
}
