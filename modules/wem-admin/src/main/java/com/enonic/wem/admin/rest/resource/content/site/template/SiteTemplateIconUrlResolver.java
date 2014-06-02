package com.enonic.wem.admin.rest.resource.content.site.template;

import com.google.common.hash.Hashing;

import com.enonic.wem.api.Icon;
import com.enonic.wem.api.content.site.SiteTemplate;
import com.enonic.wem.core.web.servlet.ServletRequestUrlHelper;

public final class SiteTemplateIconUrlResolver
{
    public static String resolve( final SiteTemplate siteTemplate )
    {
        final StringBuilder str = new StringBuilder( "/admin/rest/sitetemplate/image/" );
        str.append( siteTemplate.getKey().toString() );

        final Icon icon = siteTemplate.getIcon();
        if ( ( icon != null ) && ( icon.toByteArray() != null ) )
        {
            str.append( "?hash=" ).append( Hashing.md5().hashBytes( icon.toByteArray() ).toString() );
        }

        return ServletRequestUrlHelper.createUrl( str.toString() );
    }
}
