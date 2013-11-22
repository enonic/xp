package com.enonic.wem.core.content;

import com.enonic.wem.api.content.Content;

public class ContentToNodeNameResolver
{

    static String resolve( final Content content )
    {
        if ( content.getName() == null )
        {
            // This is a space or root or something fishy, skip
            return null;
        }

        return content.getPath() == null ? content.getName() : content.getPath().getRelativePath();
    }

}
