/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.beatchamber.beans.converter;

import java.io.Serializable;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.FacesConverter;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author 1733570 Yan Tang
 */
@FacesConverter("com.beatchamber.PostalCodeConverter")
public class PostalCodeConverter implements Converter, Serializable {

    private final static Logger LOG = LoggerFactory.getLogger(PostalCodeConverter.class);

    @Override
    public Object getAsObject(FacesContext context, UIComponent component,
            String stringValue) throws ConverterException {

        if (stringValue == null || stringValue.trim().length() == 0) {
            return null;
        }

        String rawCode = stringValue.replaceAll("\\s+", "").trim().toUpperCase();
        String postalCode = null;
        if (rawCode.length() != 6) {
            throw new ConverterException(new FacesMessage(
                    "PostalCode must be 6 characters"));
        } else {
            postalCode = rawCode.substring(0, 3) + " " + rawCode.substring(3, 6);
        }

        return postalCode;
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component,
            Object value) throws ConverterException {
        if (!(value instanceof String)) {
            throw new ConverterException();
        }

        String stringValue = "";

        if ((value instanceof String)) {
            String postalCodeStr = (String) value;
            if (!postalCodeStr.equals("")) {
                String rawStr = postalCodeStr.replaceAll("\\s+", "").trim().toUpperCase();
                stringValue = rawStr.substring(0, 3) + " " + rawStr.substring(3, 6);
            }
        }

        return stringValue;
    }
}
