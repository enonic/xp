package com.enonic.wem.portal.content;

import javax.inject.Inject;

import com.enonic.wem.api.content.Content;
import com.enonic.wem.portal.AbstractResource;

public class PageResource
    extends AbstractResource
{
    private ContentService contentService;

    public String getPage()
    {
        contentService.getContent( getPortalRequest() );

        final Content content = contentService.getContent( getPortalRequest() );

        if ( content != null )
        {
            return content.toString();
        }

        throw new ContentNotFoundException(
            "Content with path " + getPortalRequest().getPortalRequestPath().getPathAsString() + " not found" );
    }


    @Inject
    public void setContentService( final ContentService contentService )
    {
        this.contentService = contentService;
    }

}
