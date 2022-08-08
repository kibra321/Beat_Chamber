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
@FacesConverter("com.beatchamber.PhoneNumberConverter")
public class PhoneNumberConverter implements Converter, Serializable {

    private final static Logger LOG = LoggerFactory.getLogger(PhoneNumberConverter.class);

    @Override
    public Object getAsObject(FacesContext context, UIComponent component,
            String stringValue) throws ConverterException {

        if (stringValue == null || stringValue.trim().length() == 0) {
            return null;
        }

        String rawNumber = stringValue.replaceAll("[^0-9]", "");
        String phoneNumber = null;
        if (rawNumber.length() != 10) {
            throw new ConverterException(new FacesMessage(
                    "Phone number must be 10 numeric characters"));
        } else {
            phoneNumber = "(" + rawNumber.substring(0, 3) + ") "
                    + rawNumber.substring(3, 6) + "-" + rawNumber.substring(6, 10);
        }

        return phoneNumber;
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component,
            Object value) throws ConverterException {
        if (!(value instanceof String)) {
            throw new ConverterException();
        }

        String stringValue = "";

        if ((value instanceof String)) {
            String phoneNumberStr = (String) value;
            if (!phoneNumberStr.equals("")) {
                String rawStr = phoneNumberStr.replaceAll("[^0-9]+", " ").replaceAll("\\s+", "").trim();
                stringValue = "(" + rawStr.substring(0, 3) + ") "
                        + rawStr.substring(3, 6) + "-" + rawStr.substring(6, 10);
            }
        }

        return stringValue;
    }
}
