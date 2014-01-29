package com.enonic.wem.portal.content;


import com.enonic.wem.api.content.ContentPath;

public class SiteNotFoundException
    extends RuntimeException
{
    public SiteNotFoundException( final ContentPath contentPath )
    {
        super( "Site not found for content: " + contentPath );
    }
}
