package com.beatchamber.beans.filter;

import com.beatchamber.beans.LoginRegisterBean;
import java.io.IOException;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.annotation.WebFilter;

import javax.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Filter checks if LoginRegisterBackingBean has loginIn property set to true. If it is not set
 * then request is being redirected to the login.xhml page.
 *
 * @author 1733570 Yan Tang
 */
@WebFilter(filterName = "ManagerLoginFilter", urlPatterns = {"/management_pages/*"})
public class ManagerLoginFilter implements Filter {

    private final static Logger LOG = LoggerFactory.getLogger(ManagerLoginFilter.class);

    @Inject
    private LoginRegisterBean loginRegisterBean; // As an instance variable

    /**
     * Checks if user is logged in. If not it redirects to the login.xhtml page.
     *
     * @param request
     * @param response
     * @param chain
     * @throws java.io.IOException
     * @throws javax.servlet.ServletException
     */
    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
            FilterChain chain) throws IOException, ServletException {
        // Get the loginRegisterBackingBean from session attribute

        LOG.info("In the filter");

        // For the first application request there is no loginRegisterBackingBean in the
        // session so user needs to log in
        // For other requests loginRegisterBackingBean is present but we need to check if user
        // has logged in successfully
        if (loginRegisterBean == null || !loginRegisterBean.isLoggedIn() || !loginRegisterBean.isManager()) {
            LOG.info("User not logged in");

            String contextPath = ((HttpServletRequest) request)
                    .getContextPath();
            ((HttpServletResponse) response).sendRedirect(contextPath
                    + "/login.xhtml");
        } else {
            LOG.info("User logged in: "
                    + loginRegisterBean.getUsername());
            chain.doFilter(request, response);
        }
    }

    @Override
    public void destroy() {
        // Nothing to do here!
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // Nothing to do here but must be overloaded
    }

}
