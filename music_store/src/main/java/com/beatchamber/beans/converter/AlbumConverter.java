/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.beatchamber.beans.converter;

import com.beatchamber.entities.Albums;
import java.util.List;
import java.util.function.Predicate;
import javax.faces.component.UIComponent;
import javax.faces.component.UISelectItems;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

/**
 *
 * @author 1733570
 */
@FacesConverter(value = "AlbumConverter")
public class AlbumConverter implements Converter {

    @Override
    public Object getAsObject(FacesContext ctx, UIComponent comp, String value) {
        Object o = null;
        if (!(value == null || value.isEmpty())) {
            o = this.getSelectedItemAsEntity(comp, value);
        }
        return o;
    }

    @Override
    public String getAsString(FacesContext ctx, UIComponent comp, Object value) {
        String s = "";
        if (value != null && value != "") {
            s = ((Albums) value).getAlbumTitle();
        }
        return s;
    }

    private Albums getSelectedItemAsEntity(UIComponent comp, String value) {
        Albums item = null;

        List<Albums> selectItems = null;
        for (UIComponent uic : comp.getChildren()) {
            if (uic instanceof UISelectItems) {
                selectItems = (List<Albums>) ((UISelectItems) uic).getValue();

                if (value != null && selectItems != null && !selectItems.isEmpty()) {
                    Predicate<Albums> predicate = i -> i.getAlbumTitle().equals(value);
                    item = selectItems.stream().filter(predicate).findFirst().orElse(null);
                }
            }
        }

        return item;
    }
}
