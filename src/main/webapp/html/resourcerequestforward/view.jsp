<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ page import="javax.portlet.ResourceURL" %>

<portlet:defineObjects />
<% 

ResourceURL successURL = renderResponse.createResourceURL();
successURL.setParameter("action", "success");

ResourceURL failURL = renderResponse.createResourceURL();
failURL.setParameter("action", "fail");

ResourceURL redirectURL = renderResponse.createResourceURL();
redirectURL.setParameter("action", "redirect");

%>
This is the <b>Resource Request Forward</b> portlet in View mode.

<p>You can provoke the next call to <a href="<%=failURL%>">fail</a>, to succeed by <a href="<%=successURL%>">downloading a picture</a> or just make a <a href="<%=redirectURL%>">redirection</a>.

<p>Error message: ${errorMsg}
<p>Info message: ${infoMsg}
