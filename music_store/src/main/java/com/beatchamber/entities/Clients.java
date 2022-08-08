package com.beatchamber.entities;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 *
 * @author kibra
 */
@Entity
@Table(name = "clients", catalog = "CSgb1w21", schema = "")
@NamedQueries({
    @NamedQuery(name = "Clients.findAll", query = "SELECT c FROM Clients c"),
    @NamedQuery(name = "Clients.findByClientNumber", query = "SELECT c FROM Clients c WHERE c.clientNumber = :clientNumber"),
    @NamedQuery(name = "Clients.findByTitle", query = "SELECT c FROM Clients c WHERE c.title = :title"),
    @NamedQuery(name = "Clients.findByLastName", query = "SELECT c FROM Clients c WHERE c.lastName = :lastName"),
    @NamedQuery(name = "Clients.findByFirstName", query = "SELECT c FROM Clients c WHERE c.firstName = :firstName"),
    @NamedQuery(name = "Clients.findByCompanyName", query = "SELECT c FROM Clients c WHERE c.companyName = :companyName"),
    @NamedQuery(name = "Clients.findByAddress1", query = "SELECT c FROM Clients c WHERE c.address1 = :address1"),
    @NamedQuery(name = "Clients.findByAddress2", query = "SELECT c FROM Clients c WHERE c.address2 = :address2"),
    @NamedQuery(name = "Clients.findByCity", query = "SELECT c FROM Clients c WHERE c.city = :city"),
    @NamedQuery(name = "Clients.findByProvince", query = "SELECT c FROM Clients c WHERE c.province = :province"),
    @NamedQuery(name = "Clients.findByCountry", query = "SELECT c FROM Clients c WHERE c.country = :country"),
    @NamedQuery(name = "Clients.findByPostalCode", query = "SELECT c FROM Clients c WHERE c.postalCode = :postalCode"),
    @NamedQuery(name = "Clients.findByHomePhone", query = "SELECT c FROM Clients c WHERE c.homePhone = :homePhone"),
    @NamedQuery(name = "Clients.findByCellPhone", query = "SELECT c FROM Clients c WHERE c.cellPhone = :cellPhone"),
    @NamedQuery(name = "Clients.findByEmail", query = "SELECT c FROM Clients c WHERE c.email = :email"),
    @NamedQuery(name = "Clients.findByUsername", query = "SELECT c FROM Clients c WHERE c.username = :username"),
    @NamedQuery(name = "Clients.findBySalt", query = "SELECT c FROM Clients c WHERE c.salt = :salt"),
    @NamedQuery(name = "Clients.findByHash", query = "SELECT c FROM Clients c WHERE c.hash = :hash")})
public class Clients implements Serializable {

    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 40)
    @Column(name = "title")
    private String title;
    @Basic(optional = false)
    @NotNull()
    @Size(min = 1, max = 40)
    @Column(name = "last_name")
    private String lastName;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 40)
    @Column(name = "first_name")
    private String firstName;
    @Size(max = 40)
    @Column(name = "company_name")
    private String companyName;
    @Size(max = 40)
    @Column(name = "address1")
    private String address1;
    @Size(max = 40)
    @Column(name = "address2")
    private String address2;
    @Size(max = 20)
    @Column(name = "city")
    private String city;
    @Size(max = 20)
    @Column(name = "province")
    private String province;
    @Size(max = 40)
    @Column(name = "country")
    private String country;
    @Size(max = 20)
    @Column(name = "postal_code")
    private String postalCode;
    @Size(max = 20)
    @Column(name = "home_phone")
    private String homePhone;
    @Size(max = 20)
    @Column(name = "cell_phone")
    private String cellPhone;
    // @Pattern(regexp="[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?", message="Invalid email")//if the field contains email address consider using this annotation to enforce field validation
    // @Pattern(regexp="[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?", message="Invalid email")//if the field contains email address consider using this annotation to enforce field validation
    // @Pattern(regexp="[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?", message="Invalid email")//if the field contains email address consider using this annotation to enforce field validation
    // @Pattern(regexp="[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?", message="Invalid email")//if the field contains email address consider using this annotation to enforce field validation
    // @Pattern(regexp="[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?", message="Invalid email")//if the field contains email address consider using this annotation to enforce field validation
    // @Pattern(regexp="[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?", message="Invalid email")//if the field contains email address consider using this annotation to enforce field validation
    // @Pattern(regexp="[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?", message="Invalid email")//if the field contains email address consider using this annotation to enforce field validation
    @Basic(optional = false)
    @NotNull()
    @Size(min = 1, max = 40)
    @Column(name = "email")
    private String email;
    // @Pattern(regexp="[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?", message="Invalid email")//if the field contains email address consider using this annotation to enforce field validation
    /*@Size(max = 40)
    @Column(name = "genre_of_last_search")
    private String genreOfLastSearch;*/
    @Basic(optional = false)
    @NotNull()
    @Size(min = 1, max = 40)
    @Column(name = "username")
    private String username;
   /* @Basic(optional = false)
    @NotNull()
    @Size(min = 1, max = 40)
    @Column(name = "password")
    private String password;*/
    @Size(max = 32)
    @Column(name = "salt")
    private String salt;
    @Size(max = 32)
    @Column(name = "hash")
    private String hash;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "clientNumber")
    private Collection<Orders> ordersCollection;
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "client_number")
    private Integer clientNumber;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "clientNumber")
    private List<CustomerReviews> customerReviewsList;

    public Clients() {
    }

    public Clients(Integer clientNumber) {
        this.clientNumber = clientNumber;
    }

    public Clients(Integer clientNumber, String title, String lastName, String firstName, String email, String username) {
        this.clientNumber = clientNumber;
        this.title = title;
        this.lastName = lastName;
        this.firstName = firstName;
        this.email = email;
        this.username = username;
    }

    public Integer getClientNumber() {
        return clientNumber;
    }

    public void setClientNumber(Integer clientNumber) {
        this.clientNumber = clientNumber;
    }


    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }


    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getHomePhone() {
        return homePhone;
    }

    public void setHomePhone(String homePhone) {
        this.homePhone = homePhone;
    }

    public String getCellPhone() {
        return cellPhone;
    }

    public void setCellPhone(String cellPhone) {
        this.cellPhone = cellPhone;
    }
    
    public List<CustomerReviews> getCustomerReviewsList() {
        return customerReviewsList;
    }

    public void setCustomerReviewsList(List<CustomerReviews> customerReviewsList) {
        this.customerReviewsList = customerReviewsList;
    }


    @Override
    public int hashCode() {
        int hash = 0;
        hash += (clientNumber != null ? clientNumber.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Clients)) {
            return false;
        }
        Clients other = (Clients) object;
        if ((this.clientNumber == null && other.clientNumber != null) || (this.clientNumber != null && !this.clientNumber.equals(other.clientNumber))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.beatchamber.entities.Clients[ clientNumber=" + clientNumber + " ]";
    }
    public Collection<Orders> getOrdersCollection() {
        return ordersCollection;
    }
    public void setOrdersCollection(Collection<Orders> ordersCollection) {
        this.ordersCollection = ordersCollection;
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    /*public String getGenreOfLastSearch() {
        return genreOfLastSearch;
    }

    public void setGenreOfLastSearch(String genreOfLastSearch) {
        this.genreOfLastSearch = genreOfLastSearch;
    }*/


    /*public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }*/


    public String getAddress1() {
        return address1;
    }

    public void setAddress1(String address1) {
        this.address1 = address1;
    }

    public String getAddress2() {
        return address2;
    }

    public void setAddress2(String address2) {
        this.address2 = address2;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }
    
}
