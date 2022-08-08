
package com.beatchamber.beans;

import com.beatchamber.entities.GenreToAlbum;
import com.beatchamber.entities.OrderAlbum;
import com.beatchamber.entities.OrderTrack;
import com.beatchamber.entities.Orders;
import com.beatchamber.entities.Tracks;
import com.beatchamber.jpacontroller.AlbumsJpaController;
import com.beatchamber.jpacontroller.GenreToAlbumJpaController;
import com.beatchamber.jpacontroller.OrderAlbumJpaController;
import com.beatchamber.jpacontroller.OrderTrackJpaController;
import com.beatchamber.jpacontroller.OrdersJpaController;
import com.beatchamber.jpacontroller.TracksJpaController;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.context.RequestScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

/**
 *
 * @author kibra
 */
@Named("cookies")
@RequestScoped



public class CookieManager {
    private String cartKey = "CartItemsKey";
    FacesContext context = FacesContext.getCurrentInstance();
    Map<String,Object> cookies = new HashMap();
    
    
    @Inject
    private OrdersJpaController orderController;
    
    @Inject
    private OrderAlbumJpaController orderAlbumController;
    
    @Inject 
    private OrderTrackJpaController orderTrackController;
    
    @Inject
    private TracksJpaController trackJpaController;
    
    @Inject
    private AlbumsJpaController albumJpaController;
    
        @Inject
    private GenreToAlbumJpaController genreToAlbumController;
    
    
    

    
    /**
     * This method will return the number of Cookies that are made
     * @return String
     * @author Ibrahim
     */
    public String getCount(){
        Map result = FacesContext.getCurrentInstance().getExternalContext().getRequestCookieMap();
        return result.size()+"";
    }
    
    /**
     * This method will find the value of the cookies from the given key
     * @param key
     * @return String
     * @author Ibrahim
     */
    public String findValue(String key){
        Object my_cookie = context.getExternalContext().getRequestCookieMap().get(key);
        String val="";
        if(my_cookie != null){
            val = ((Cookie)my_cookie).getValue();
        }
        return val;
    }
    
    /**
     * This method will create a cookie with the given key and value
     * @param key
     * @param value 
     * @author Ibrahim
     */
    public void createCookie(String key,String value){
        FacesContext context = 	FacesContext.getCurrentInstance();
        context.getExternalContext().addResponseCookie(key, value,null);
    }
    
    /**
     * Adds the id to the cart if the id is not already in the cart
     * @param id 
     * @author Ibrahim
     */
    public void addItemToCart(String idOfItem) throws InterruptedException{
        if(!isItemInCart(idOfItem)){
            if(isCartEmpty()){
                createCookie(cartKey,idOfItem);
            }
            else{
                //I know it is an album if it contains an 'a'
                if(idOfItem.toLowerCase().contains("a")){
                    List<String> idOfTRacksToRemove= getListOfTrackIdFromAlbumId(Integer.parseInt(idOfItem.replace("a", "")));
                    /*for(String item:idOfTRacksToRemove){
                        System.out.println(item + "--------------------------------------------");
                        removeItemFromCart(item);
                    }*/
                    removeItemFromCart(idOfTRacksToRemove, idOfItem);
                    //createCookie(cartKey,findValue(cartKey)+","+idOfItem);
                }
                createCookie(cartKey,findValue(cartKey)+","+idOfItem);
            }
        }
    }
    
    /**
     * (checks for the albums)
     * This method will be used in the cart page it will check if the id is in the cart and if it is it wil return none so the div containing the information will not be shown
     * (This method was made because it takes some time for the cookies to be updated and be reflected on he page so this is a shortcut so that the user will not need to wait for the page to be updated after the cookies has been changed)
     * @param idToCheck
     * @return String 
     * @author Ibrahim
     */
    public String checkIfAlbumIsInCart(String idToCheck){
        for(String item:getAllIdFromCart()){
            if(item.equals("a"+idToCheck)){
                return "";
            }
            
        }
        return "none";
    }
    
    /**
     * (checks for the tracks)
     * This method will be used in the cart page it will check if the id is in the cart and if it is it wil return none so the div containing the information will not be shown
     * (This method was made because it takes some time for the cookies to be updated and be reflected on he page so this is a shortcut so that the user will not need to wait for the page to be updated after the cookies has been changed)
     * @param idToCheck
     * @return String
     * @author Ibrahim
     */
    public String checkIfTrackIsInCart(String idToCheck){
        for(String item:getAllIdFromCart()){
            if(item.equals(idToCheck)){
                return "";
            }
            
        }
        return "none";
    }
    
    /**
     * This method will format text depending on if the the tracks are in the cart or have been ordered
     * @param buttonText
     * @param albumId
     * @param clientID
     * @return String
     */
    public String formatViewTracks(String buttonText,int albumId,int clientID){
        
        if(checkIfAllTracksHaveBeenBought(albumId,clientID)){
            return com.beatchamber.util.Messages.getMessage("com.beatchamber.bundles.messages","Tracks_Alredy_Ordered",null).getDetail();
        }
        if(isAlbumInOrders(clientID,albumId)){
            return com.beatchamber.util.Messages.getMessage("com.beatchamber.bundles.messages","Tracks_Alredy_Ordered",null).getDetail();
        }
        /*if(areAllTrackInTheCart(albumId)){
            return com.beatchamber.util.Messages.getMessage("com.beatchamber.bundles.messages","All_Of_Tracks_In_Cart",null).getDetail();
        }*/
        else{
            return buttonText;
        }
        
    }
    
    /**
     * This method will return a list of all of ids of the tracks that are from a album from the album id given
     * @param albumId
     * @return List<String> 
     */
    private List<String> getListOfTrackIdFromAlbumId(int albumId){
        List<String> allTrackId = new ArrayList<String>();
        for(Tracks item:trackJpaController.findTracksEntities()){
            if(item.getAlbumNumber().getAlbumNumber() == albumId){
                allTrackId.add(item.getTrackId()+"");
            }
        }
        return allTrackId;
    }
    
    /**
     * Check is all of the tracks from an album are in the cart 
     * @param albumId
     * @return 
     */
    private boolean areAllTrackInTheCart(int albumId){
        
        //get all of the tracks that are from the album
        List<String> allTrackId = getListOfTrackIdFromAlbumId(albumId);
        
        //compare the cookies with the list of track from the album
        for(String item:getAllIdFromCart()){
            if(!allTrackId.contains(item)){
                return false;
            }
        }
        return true;
        
    }
    
    /**
     * This method will reload the page
     * @throws IOException 
     * @author Ibrahim
     */
    public void reload() throws IOException {
    ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
    ec.redirect(((HttpServletRequest) ec.getRequest()).getRequestURI());
}
    /**
     * This method will add the add the album to the cart cookie (it takes the string of the albums database id so not a15 but just 15)
     * @param albumId 
     * @author Ibrahim
     */
    public void addAlbumToCart(String albumId) {
        try {
            addItemToCart("a"+albumId);
        } catch (InterruptedException ex) {
            Logger.getLogger(CookieManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            reload();
        } catch (IOException ex) {
            Logger.getLogger(CookieManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * This method will add the add the tracks to the cart cookie 
     * @param albumId 
     * @author Ibrahim
     */
    public void addTrackToCart(String trackId) throws InterruptedException{
        addItemToCart(trackId);
        try {
            reload();
        } catch (IOException ex) {
            Logger.getLogger(CookieManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * This method returns true if the id given is in the cart
     * @param id
     * @return Boolean
     * @author Ibrahim
     */
    private Boolean isIdInCart(String id){
        for(String item:getAllIdFromCart()){
            if(item.equals(id)){
                return true;
            }
        }
        return false;
    }
    
    /**
     * This method will return a string that represents if the button should be disabled because it alredy is in the cart
     * @param albumId
     * @return String
     * @author Ibrahim
     */
    public String shouldTheAddToCartBeDisabled(int albumId,int clientId){
        if(checkIfAllTracksHaveBeenBought(albumId,clientId)){
         return "true";
        }
     if(isAlbumInOrders(clientId,albumId)){
         return "true";
     }
     if(isIdInCart("a"+albumId)){
         return "true";
     }
     else{
         return "false";
     }
    }
    
    /**
     * Same as the method above but for tracks
     * @param trackId
     * @param clientId
     * @return String
     * @author Ibrahim
     */
    public String shouldTheAddToCartBeDisabledForTracks(int trackId,int clientId){
     if(isIdInCart("a"+trackJpaController.findTracks(trackId).getAlbumNumber().getAlbumNumber())){
         return "true";
     }
     if(isAlbumInOrders(clientId,trackJpaController.findTracks(trackId).getAlbumNumber().getAlbumNumber())){
         return "true";
     }
             
     if(isTrackInOrders(clientId,trackId)){
         return "true";
     }
     if(isIdInCart(trackId+"")){
         return "true";
     }
     else{
         return "false";
     }
    }
    
    /**
     * This method will check if an albums has been ordered by a specific client
     * @param clientID
     * @param albumId
     * @return Boolean
     */
    private Boolean isAlbumInOrders(int clientID,int albumId){
        List<Integer> listOfId = new ArrayList<Integer>();
        
        //check to see if the client has an order
        for(Orders item:orderController.findOrdersEntities()){
            if(item.getClientNumber().getClientNumber() == clientID){
                listOfId.add(item.getOrderId());
            }
        }
        //if the client has not order return false
        if(listOfId.size()<1){
            return false;
        }
        
        List<OrderAlbum> listOfALbumOrders = orderAlbumController.findOrderAlbumEntities();
        
        
        for(int item:listOfId){
            for(OrderAlbum items:listOfALbumOrders){
                if(items.getAlbumId().getAlbumNumber() == albumId && items.getOrderId().getOrderId() == item){
                    return true;
                }
            }  
        }
        
        return false;
    }
    
    
    /**
     * This method will check if an track has been ordered by a specific client
     * @param clientID
     * @param trackId
     * @return Boolean
     * @author Ibrahim
     */
    private Boolean isTrackInOrders(int clientID,int trackId){
        List<Integer> listOfId = new ArrayList<Integer>();
        
        if(isAlbumInOrders(clientID,trackJpaController.findTracks(trackId).getAlbumNumber().getAlbumNumber())){
            return true;
        }
        
        //check to see if the client has an order
        for(Orders item:orderController.findOrdersEntities()){
            if(item.getClientNumber().getClientNumber() == clientID){
                listOfId.add(item.getOrderId());
            }
        }
        //if the client has not order return false
        if(listOfId.size()<1){
            return false;
        }
        
        List<OrderTrack> listOfTrackOrders = orderTrackController.findOrderTrackEntities();
        
        for(int item:listOfId){
            for(OrderTrack items:listOfTrackOrders){
                if(items.getTrackId().getTrackId() == trackId && items.getOrderId().getOrderId() == item){
                    return true;
                }
            }  
        }
        
        return false;
    }
    
    /**
     * This method will check if all of the tracks from an album have been ordered by a client and return a boolean to say it they all are bought 
     * @param albumId
     * @param clientId
     * @return Boolean
     * @author Ibrahim
     */
    private Boolean checkIfAllTracksHaveBeenBought(int albumId,int clientId){
        List<Integer> listOfId = new ArrayList<Integer>();
        List<OrderTrack> lisofOfAllOrderedTracks = orderTrackController.findOrderTrackEntities();
        List<OrderTrack> allOrderedItemByClient =new ArrayList<OrderTrack>();
        
        //list of tracks from the given album
        List<Integer> listOfSelectedTracks = new ArrayList<Integer>();
        
        //get the total number of tracks for the album id given
        int countOfTracks=albumJpaController.findAlbums(albumId).getTotalTracks();
        
        //get the id of the of the orders that the client has had
        for(Orders item:orderController.findOrdersEntities()){
            if(item.getClientNumber().getClientNumber() == clientId){
                listOfId.add(item.getOrderId());
            }
        }
        //get all of the order tracks from the specific client
        for(OrderTrack item:lisofOfAllOrderedTracks){
            if(isItemInList(listOfId,item.getOrderId().getOrderId())){
                allOrderedItemByClient.add(item);
            }
        }

        //get all of the tracks from the given album id
        for(Tracks item:trackJpaController.findTracksEntities()){
            if(item.getAlbumNumber().getAlbumNumber() == albumId){
                listOfSelectedTracks.add(item.getTrackId());
            }
        }
        
        //find if all if the tracks are bought
        for(OrderTrack item:allOrderedItemByClient){
            if(isItemInList(listOfSelectedTracks,item.getTrackId().getTrackId())){
                countOfTracks--;
            }
        }
        
        
        if(countOfTracks <1){
            return true;
        }
        else{
            return false;
        }
        
        
    }
    
    /**
     * check to see if an int is in a list
     * @param list
     * @param id
     * @return boolean
     * @author Ibrahim
     */
    private boolean isItemInList(List<Integer> list,int id){
        for(int item:list){
            if(item==id){
                return true;
            }
        }
        return false;
    }
    
    /**
     * This method will display the default txt on the button if the album is not already in the cart or if the album is already in the cart
     * it will return a message on the button to say that the album is alredy in the cart
     * @param albumId
     * @param buttonText
     * @param clientID
     * @return String
     * @author Ibrahim
     */
    public String adjustDisplay(String albumId,String buttonText,int clientID){
        if(checkIfAllTracksHaveBeenBought(Integer.parseInt(albumId),clientID)){
            return com.beatchamber.util.Messages.getMessage("com.beatchamber.bundles.messages","Album_Alredy_Ordered",null).getDetail();
        }
        if(isAlbumInOrders(clientID,Integer.parseInt(albumId))){
            return com.beatchamber.util.Messages.getMessage("com.beatchamber.bundles.messages","Album_Alredy_Ordered",null).getDetail();
        }
        if(isIdInCart("a"+albumId)){
            return com.beatchamber.util.Messages.getMessage("com.beatchamber.bundles.messages","Album_Alredy_In_Cart",null).getDetail();
        }
        else{
            return buttonText;
        }
    }
    
    /**
     * This like the method just above but it will check for the tracks
     * @param trackId
     * @param buttonText
     * @param clientID
     * @return String
     * @author Ibrahim
     */
    public String adjustDisplayForTracks(String trackId,String buttonText,int clientID){
        if(isIdInCart("a"+trackJpaController.findTracks(Integer.parseInt(trackId)).getAlbumNumber().getAlbumNumber())){
         return com.beatchamber.util.Messages.getMessage("com.beatchamber.bundles.messages","Album_Alredy_In_Cart",null).getDetail();
        }
        if(isAlbumInOrders(clientID,trackJpaController.findTracks(Integer.parseInt(trackId)).getAlbumNumber().getAlbumNumber())){
         return com.beatchamber.util.Messages.getMessage("com.beatchamber.bundles.messages","The Album has already been ordered",null).getDetail();
        }
        if(isTrackInOrders(clientID,Integer.parseInt(trackId))){
            return com.beatchamber.util.Messages.getMessage("com.beatchamber.bundles.messages","Track_Alredy_Ordered",null).getDetail();
        }
        if(isIdInCart(trackId)){
            return com.beatchamber.util.Messages.getMessage("com.beatchamber.bundles.messages","Track_Alredy_In_Cart",null).getDetail();
        }
        else{
            return buttonText + " " +trackJpaController.findTracks(Integer.parseInt(trackId)).getListPrice()+"$";
        }
    }
    
    /**
     * This method will clear the items in the cart
     * @author Ibrahim
     */
    public void clearTheCart(){
        clearCookie(com.beatchamber.util.Messages.getMessage("com.beatchamber.bundles.messages","cartKey",null).getDetail());
        try {
            reload();
        } catch (IOException ex) {
            Logger.getLogger(CookieManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * This method will clear the items in the cart
     * @author Ibrahim
     */
    public void clearTheCartWithoutRefresh(){
        clearCookie(com.beatchamber.util.Messages.getMessage("com.beatchamber.bundles.messages","cartKey",null).getDetail());
    }
    
    /**
     * This method will remove the id from the cart
     * @param idToRemove
     * @author Ibrahim
     */
    public void removeItemFromCart(String idToRemove) {
        String newCookieList = "";
        if(checkIfArrayContains(getAllIdFromCart(),idToRemove)){
            if(!isCartEmpty()){
                try {
                    for (String item:getAllIdFromCart()) {
                        if(!item.equals(idToRemove)){
                            newCookieList = newCookieList + item + "," ;
                        }
                    }
                    createCookie(cartKey,newCookieList);
                    reload();
                } catch (IOException ex) {
                    Logger.getLogger(CookieManager.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        
    }
    
    public void removeItemFromCart(List<String> idsToRemove,String albumId) {
        String newCookieList = "";
        List<String> returnedIds = getAllIdFromCartInList();
        for(String item:idsToRemove){
            if(returnedIds.contains(item)){
                returnedIds.remove(item);
            }
        }
        if(returnedIds.isEmpty()){
            clearCookie(com.beatchamber.util.Messages.getMessage("com.beatchamber.bundles.messages","cartKey",null).getDetail());
        }
        
        for(String item:returnedIds){
            newCookieList = newCookieList+","+item;
        }
        newCookieList = newCookieList+","+albumId;
        createCookie(cartKey,newCookieList);
        try {
            reload();
            
            /*if(!isCartEmpty()){
            try {
            for (String item:getAllIdFromCart()) {
            if(!item.equals(idToRemove)){
            newCookieList = newCookieList + item + "," ;
            }
            }
            createCookie(cartKey,newCookieList);
            reload();
            } catch (IOException ex) {
            Logger.getLogger(CookieManager.class.getName()).log(Level.SEVERE, null, ex);
            }
            }*/
        } catch (IOException ex) {
            Logger.getLogger(CookieManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
    
    /**
     * This method will check if a string is in an array and return an appropriate boolean
     * @param arr
     * @param strToFind
     * @return boolean 
     */
    private boolean checkIfArrayContains(String[] arr,String strToFind){
        for(String item:arr){
            if(item.equals(strToFind)){
                return true;
            }
        }
        return false;
    }
    
    /**
     * Check if the id of the is already in the cart
     * @param id
     * @return Boolean
     * @author Ibrahim
     */
    public Boolean isItemInCart(String id){
     return findValue(cartKey).contains(id);
    }
    
    /**
     * Checks if the cart is empty
     * @return Boolean
     * @author Ibrahim
     */
    private Boolean isCartEmpty(){
        if(findValue(cartKey).length() < 1){
            return true;
        }
        else{
            return false;
        }
    }
    
    /**
     * This method will clear the value of the given cookie value
     * @param key 
     * @author Ibrahim
     */
    public void clearCookie(String key){
        createCookie(key,"");
    }
    
    /**
     * This method will return the number of items in the cart
     * @return String 
     * @author Ibrahim
     */
    public String findNumberOfItemsInCart(){
        if(isCartEmpty()){
            return "0";
        }
        int count=0;
        for(String item:getAllIdFromCart()){
            if(item.length() > 0){
                count++;
                
            }
        }
        
        return count+"";
    }
    
    /**
     * This method will return a string array of all of the ids of the items that are in the cart
     * @return String 
     * @author Ibrahim
     */
    private String[] getAllIdFromCart(){
        String dataResult = findValue(com.beatchamber.util.Messages.getMessage("com.beatchamber.bundles.messages","cartKey",null).getDetail());
        return dataResult.split(",");
    }
    
    /**
     * This method will return a string list of all of the ids of the items that are in the cart
     * @return list<String> 
     * @author Ibrahim
     */
    private List<String> getAllIdFromCartInList(){
        List<String> returnedList = new ArrayList<String>();
        String dataResult = findValue(com.beatchamber.util.Messages.getMessage("com.beatchamber.bundles.messages","cartKey",null).getDetail());
        for(String item:dataResult.split(",")){
            returnedList.add(item);
        }
        return returnedList;
    }
    
    /**
     * This method will remove the items that are already in the clients order
     * @param clientId 
     */
    public void removeItemsInCartThatAreAlreadyOrdered(int clientId){
        if(clientId > 0){

            List<String> allItemsInCart = getAllIdFromCartInList();
            List<String> ItemIdToRemove = new ArrayList<String>();

            for(String item:allItemsInCart){
                if(!item.equals("")){
                    //if it is an album
                    if(item.contains("a")){
                        //if it is in the orders of the client remove it
                        if(isAlbumInOrders(clientId,cleanIdToMatchDatabaseId(item))){
                            ItemIdToRemove.add(item);
                        }
                    }
                    //if it is a track
                    else{
                        //if it is in the orders of the client remove it
                        if(isTrackInOrders(clientId,cleanIdToMatchDatabaseId(item))){
                            ItemIdToRemove.add(item);
                        }
                    }
                }
            }
            //these next line will remove the item in the list from the cart
            String newCookieList = "";
            List<String> returnedIds = getAllIdFromCartInList();
            //remove the items from the list
            for(String item:ItemIdToRemove){
                if(allItemsInCart.contains(item)){
                    allItemsInCart.remove(item);
                }
            }
            //clear the cart if nothing is left
            if(allItemsInCart.isEmpty()){
                clearCookie(com.beatchamber.util.Messages.getMessage("com.beatchamber.bundles.messages","cartKey",null).getDetail());
            }

            //create the new list with the items removed
            for(String item:allItemsInCart){
                newCookieList = newCookieList+","+item;
            }
            createCookie(cartKey,newCookieList);
        }
        
    }
    
    /**
     * This method will convert the string used to store the items in the cookies to the id that the database will use
     * @param Id
     * @return int
     */
    private int cleanIdToMatchDatabaseId(String id){
        String retunedStr = id.replace("a", "");
        return Integer.parseInt(retunedStr);
    } 
    
    
    /**
     * This method will updated and remove the items in the cart if they were ordered
     * @param clientId 
     */
    public void cleanCart(int clientId){
        List<String> returnedIds = getAllIdFromCartInList();
        List<String> ItemIdToRemove = new ArrayList<String>();
        
        for(String item:returnedIds){
                if(!item.equals("")){
                    //if it is an album
                    if(item.contains("a")){
                        //if it is in the orders of the client remove it
                        if(isAlbumInOrders(clientId,cleanIdToMatchDatabaseId(item))){
                            ItemIdToRemove.add(item);
                        }
                    }
                    //if it is a track
                    else{
                        //if tracks are in the orders of the client remove it
                        if(isTrackInOrders(clientId,cleanIdToMatchDatabaseId(item)) || isAlbumInOrders(clientId,trackJpaController.findTracks(Integer.parseInt(item)).getAlbumNumber().getAlbumNumber() )){
                            ItemIdToRemove.add(item);
                        }
                    }
                }
            }
        
        String newCookieList = "";
        for(String item:ItemIdToRemove){
            if(returnedIds.contains(item)){
                returnedIds.remove(item);
            }
        }
        if(returnedIds.isEmpty()){
            clearCookie(com.beatchamber.util.Messages.getMessage("com.beatchamber.bundles.messages","cartKey",null).getDetail());
        }
        
        for(String item:returnedIds){
            newCookieList = newCookieList+","+item;
        }
        createCookie(cartKey,newCookieList);
        
    }
    
    /**
     * This method will find the genre id that matches the genre of the album
     * @param albumId 
     */
    public void addAlbumGenreToCookies(int albumId){
        List<GenreToAlbum> listOfGenreToAlbum = genreToAlbumController.findGenreToAlbumEntities();
        GenreToAlbum foundConnection = new GenreToAlbum();
        
        for(GenreToAlbum item:listOfGenreToAlbum){
            if(item.getAlbumNumber().getAlbumNumber() == albumId){
                foundConnection = item;
            }
        }
        
        createCookie("selectGenre",foundConnection.getGenreId().getGenreId()+"");
        
    }
    
    
    
}
