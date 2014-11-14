package arm.requestforward;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.portlet.GenericPortlet;
import javax.portlet.PortletException;
import javax.portlet.PortletRequestDispatcher;
import javax.portlet.PortletURL;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.util.PortalUtil;

/**
 * Portlet implementation class ResourceRequestForwardPortlet
 */
public class ResourceRequestForwardPortlet extends GenericPortlet {

    public void init() {
        viewTemplate = getInitParameter("view-template");
    }

    public void serveResource(
            ResourceRequest request, ResourceResponse response)
        throws PortletException, IOException {

        
        String action = request.getParameter("action");
        if ("success".equals(action)) {
            
            //Everything should work and the image will be shown
            response.setContentType("image/png");
            
            InputStream is =ResourceRequestForwardPortlet.class.getResourceAsStream("/liferay-logo.png");

            byte[] logoBytes = inputStream2Bytes(is); 
            response.getPortletOutputStream().write(logoBytes);
            response.flushBuffer();
        } else if ("redirect".equals(action)) {

            //Suppose the image generation went wrong, and we do a redirection
            redirect ("Action: redirect", request, response);
        } else {

            // Suppose the image generation went wrong, and we try to do a forward which should work
            // like the redirection but without using the session to store "request"-scoped attributes
            request.setAttribute("error", "Unexpected action: " + action);
            forwardResourceRequest("Unexpected action: " + action, request, response);
        }
    }

    public void doView(RenderRequest renderRequest, RenderResponse renderResponse)
        throws IOException, PortletException {

        String error = (String)SessionErrors.get(renderRequest, "resourcerequest.error");
        SessionErrors.clear(renderRequest);
        
        String message = (String)SessionMessages.get(renderRequest, "resourcerequest.message");
        SessionMessages.clear(renderRequest);
        
        renderRequest.setAttribute("errorMsg", error);
        renderRequest.setAttribute("infoMsg", message);
        include(viewTemplate, renderRequest, renderResponse);
    }

    protected void forwardResourceRequest(String errorText, ResourceRequest portletRequest, ResourceResponse portletResponse)
        throws IOException, PortletException {


        HttpServletRequest request = PortalUtil.getOriginalServletRequest(PortalUtil.getHttpServletRequest(portletRequest));
        
        PortletURL renderURL = portletResponse.createRenderURL();
        
        try {
            request.setAttribute("error", errorText);            
            RequestDispatcher rd = request.getRequestDispatcher(renderURL.toString().replace("http://localhost:8080", ""));
            rd.forward(request, PortalUtil.getHttpServletResponse(portletResponse));
        } catch (ServletException e) {
            throw new PortletException(e);
        }
    }
    
    protected void redirect (String errorText, ResourceRequest resourceRequest,
            ResourceResponse resourceResponse) {
        
        SessionErrors.add(resourceRequest, "resourcerequest.error", "ERROR " + errorText);
        SessionMessages.add(resourceRequest, "resourcerequest.message", "MESSAGE " + errorText);
        resourceResponse.setProperty(ResourceResponse.HTTP_STATUS_CODE, "302");
        resourceResponse.addProperty("Location", resourceResponse.createRenderURL().toString());
    }
    
    protected void include(
            String path, RenderRequest renderRequest,
            RenderResponse renderResponse)
        throws IOException, PortletException {

        PortletRequestDispatcher portletRequestDispatcher =
            getPortletContext().getRequestDispatcher(path);

        if (portletRequestDispatcher == null) {
            _log.error(path + " is not a valid include");
        }
        else {
            portletRequestDispatcher.include(renderRequest, renderResponse);
        }
    }
 
    
    public static byte[] inputStream2Bytes(final InputStream is) throws IOException{
        try{
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buf = new byte[1024*8];
            int len = -1;
            while((len = is.read(buf))!=-1){
                baos.write(buf, 0, len);
            }
            return baos.toByteArray();
        }
        finally{
            is.close();
        }
    }
    
    protected String viewTemplate;

    private static Log _log = LogFactoryUtil.getLog(ResourceRequestForwardPortlet.class);

}
