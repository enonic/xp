package com.enonic.wem.admin.rest.resource.content.site.template;

import com.google.common.hash.Hashing;

import com.enonic.wem.admin.rest.resource.schema.SchemaIconUrlResolver;
import com.enonic.wem.api.Icon;
import com.enonic.wem.api.content.site.SiteTemplate;
import com.enonic.wem.api.schema.content.ContentTypeName;
import com.enonic.wem.core.web.servlet.ServletRequestUrlHelper;

public final class SiteTemplateIconUrlResolver
{
    private SchemaIconUrlResolver schemaIconUrlResolver;

    public SiteTemplateIconUrlResolver( final SchemaIconUrlResolver schemaIconUrlResolver )
    {
        this.schemaIconUrlResolver = schemaIconUrlResolver;
    }

    public String resolve( final SiteTemplate siteTemplate )
    {
        if ( siteTemplate.getIcon() != null && siteTemplate.getIcon().toByteArray() != null )
        {
            final StringBuilder str = new StringBuilder( "/admin/rest/sitetemplate/icon/" );
            str.append( siteTemplate.getKey().toString() );
            final Icon icon = siteTemplate.getIcon();
            byte[] iconAsByteArray = icon.toByteArray();
            if ( iconAsByteArray != null )
            {
                str.append( "?hash=" ).append( Hashing.md5().hashBytes( iconAsByteArray ).toString() );
            }

            return ServletRequestUrlHelper.createUri( str.toString() );
        }
        else
        {
            return schemaIconUrlResolver.resolve( ContentTypeName.site() );
        }
    }
}
