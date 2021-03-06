package com.atlassian.labs.speakeasy.ui;

import com.atlassian.labs.speakeasy.SpeakeasyManager;
import com.atlassian.labs.speakeasy.UnauthorizedAccessException;
import com.atlassian.labs.speakeasy.product.ProductAccessor;
import com.atlassian.sal.api.ApplicationProperties;
import com.atlassian.sal.api.user.UserManager;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 *
 */
public class UserOptOutServlet extends HttpServlet
{
    private final SpeakeasyManager speakeasyManager;
    private final UserManager userManager;
    private final ProductAccessor productAccessor;
    private final ApplicationProperties applicationProperties;

    public UserOptOutServlet(SpeakeasyManager speakeasyManager, UserManager userManager, ProductAccessor productAccessor, ApplicationProperties applicationProperties)
    {
        this.speakeasyManager = speakeasyManager;
        this.userManager = userManager;
        this.productAccessor = productAccessor;
        this.applicationProperties = applicationProperties;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
    {
        String user = userManager.getRemoteUsername();
        if (user != null)
        {
            try
            {
                speakeasyManager.disallowAllUserAccess(user);
            }
            catch (UnauthorizedAccessException e)
            {
                resp.sendError(403, e.getMessage());
                return;
            }
        }
        String userPage = applicationProperties.getBaseUrl() + productAccessor.getProfilePath();
        resp.setContentType("text/html");
        resp.getWriter().append("<html><head><meta http-equiv=\"refresh\" content=\"2;url=" + userPage + "\"><body class=\"success\">Unsubscribed</body></html>");
        resp.getWriter().close();
    }
}
