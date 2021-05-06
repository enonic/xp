package com.enonic.xp.admin.impl.rest.resource.schema;

import com.google.common.hash.Hashing;

import com.enonic.xp.icon.Icon;
import com.enonic.xp.web.servlet.ServletRequestUrlHelper;

public abstract class IconUrlResolver
{

    protected String generateIconUrl( final String baseUrl, final Icon icon )
    {
        final StringBuilder str = new StringBuilder( baseUrl );
        if ( icon != null )
        {
            final byte[] iconData = icon.toByteArray();
            if ( iconData.length > 0 )
            {
                str.append( "?hash=" ).append( Hashing.md5().hashBytes( iconData ).toString() );
            }
        }
        return ServletRequestUrlHelper.createUri( str.toString() );
    }

}
