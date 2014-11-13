package arm.requestforward;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.portlet.GenericPortlet;
import javax.portlet.PortletException;
import javax.portlet.PortletRequest;
import javax.portlet.PortletRequestDispatcher;
import javax.portlet.PortletResponse;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

/**
 * Portlet implementation class ResourceRequestForwardPortlet
 */
public class ResourceRequestForwardPortlet extends GenericPortlet {

    public void init() {
        viewTemplate = getInitParameter("view-template");
        errorTemplate = getInitParameter("error-template");
    }

    public void serveResource(
            ResourceRequest request, ResourceResponse response)
        throws PortletException, IOException {

        String action = request.getParameter("action");
        if ("success".equals(action)) {
            response.setContentType("image/png");
            
            InputStream is =ResourceRequestForwardPortlet.class.getResourceAsStream("/liferay-logo.png");

            byte[] logoBytes = inputStream2Bytes(is); 
            response.getPortletOutputStream().write(logoBytes);
            response.flushBuffer();
        } else {
            request.setAttribute("error", "Unexpected action: " + action);
            forward(errorTemplate, request, response);
        }
    }

    public void doView(
            RenderRequest renderRequest, RenderResponse renderResponse)
        throws IOException, PortletException {

        include(viewTemplate, renderRequest, renderResponse);
    }

    protected void forward(String path, PortletRequest renderRequest,
            PortletResponse renderResponse)
        throws IOException, PortletException {

        PortletRequestDispatcher portletRequestDispatcher =
            getPortletContext().getRequestDispatcher(path);

        if (portletRequestDispatcher == null) {
            _log.error(path + " is not a valid forward");
        }
        else {
            portletRequestDispatcher.forward(renderRequest, renderResponse);
        }
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
    protected String errorTemplate;

    private static Log _log = LogFactoryUtil.getLog(ResourceRequestForwardPortlet.class);

}
