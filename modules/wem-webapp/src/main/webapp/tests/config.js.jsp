<%@ taglib prefix="w" uri="uri:enonic.wem.taglib" %>
<w:helper var="helper"/>
window.CONFIG = {
  baseUrl: '<%= helper.getBaseUrl().substring( 0, helper.getBaseUrl().lastIndexOf( "/tests" ) )%>'
};