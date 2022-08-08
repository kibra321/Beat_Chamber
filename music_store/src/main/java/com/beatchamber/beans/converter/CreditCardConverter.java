package com.beatchamber.beans.converter;

import com.beatchamber.beans.CreditCard;
import java.io.Serializable;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.convert.FacesConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A converter is a class that converts between strings and objects. A converter
 * must implement the Converter interface. The annotation connects this
 * converter with the CreditCard class.
 *
 * This converter inserts the spaces in the appropriate location for a credit
 * card number
 *
 */
@FacesConverter("com.beatChamber.CreditCardConverter")
public class CreditCardConverter implements Converter, Serializable {

    private final static Logger LOG = LoggerFactory.getLogger(CreditCardConverter.class);

    private String separator;

    public void setSeparator(String newValue) {
        separator = newValue;
    }

    /**
     * Used when inputing a value Accept the string and check that includes
     * digits or whitespace
     */
    public Object getAsObject(FacesContext context, UIComponent component,
            String newValue) throws ConverterException {
        StringBuilder builder = new StringBuilder(newValue);
        boolean foundInvalidCharacter = false;
        char invalidCharacter = '\0';
        int i = 0;
        while (i < builder.length() && !foundInvalidCharacter) {
            char ch = builder.charAt(i);
            if (Character.isDigit(ch)) {
                i++;
            } else if (Character.isWhitespace(ch)) {
                builder.deleteCharAt(i);
            } else {
                foundInvalidCharacter = true;
                invalidCharacter = ch;
            }
        }
        
        if (foundInvalidCharacter) {
            FacesMessage message = com.beatchamber.util.Messages.getMessage(
                    "com.beatchamber.bundles.messages", "badCreditCardCharacter",
                    new Object[]{new Character(invalidCharacter)});
            message.setSeverity(FacesMessage.SEVERITY_ERROR);
            throw new ConverterException(message);
        }

        return new CreditCard(builder.toString());
    }

    /**
     * Used when display the value Depending on the length of the string add
     * spaces where appropriate
     */
    public String getAsString(FacesContext context, UIComponent component,
            Object value) throws ConverterException {
        // length 13: xxxx xxx xxx xxx
        // length 14: xxxxx xxxx xxxxx
        // length 15: xxxx xxxxxx xxxxx
        // length 16: xxxx xxxx xxxx xxxx
        // length 22: xxxxxx xxxxxxxx xxxxxxxx
        if(!(value instanceof String)){
            if (!(value instanceof CreditCard)) {

                throw new ConverterException();
            }
        }
        String v = "";
        if((value instanceof String)){
            v = (String)value;
        }
        else{
            v = ((CreditCard) value).toString();
        }
        
        String sep = separator;
        if (sep == null) {
            sep = " ";
        }

        int[] boundaries = null;
        int length = v.length();
        if (length == 13) {
            boundaries = new int[]{4, 7, 10};
        } else if (length == 14) {
            boundaries = new int[]{5, 9};
        } else if (length == 15) {
            boundaries = new int[]{4, 10};
        } else if (length == 16) {
            boundaries = new int[]{4, 8, 12};
        } else if (length == 22) {
            boundaries = new int[]{6, 14};
        } else {
            return v;
        }
        StringBuilder result = new StringBuilder();
        int start = 0;
        for (int i = 0; i < boundaries.length; i++) {
            int end = boundaries[i];
            result.append(v.substring(start, end));
            result.append(sep);
            start = end;
        }
        result.append(v.substring(start));
        return result.toString();
    }
}
