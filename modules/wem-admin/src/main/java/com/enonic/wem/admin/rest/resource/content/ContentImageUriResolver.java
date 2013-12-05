package com.enonic.wem.admin.rest.resource.content;

import com.enonic.wem.api.content.Content;
import com.enonic.wem.web.servlet.ServletRequestUrlHelper;

public final class ContentImageUriResolver
{

    public static String resolve( final Content content )
    {
        final String contentId = content.getId().toString();
        return ServletRequestUrlHelper.createUrl( "/admin/rest/content/image/" + contentId );
    }

}
