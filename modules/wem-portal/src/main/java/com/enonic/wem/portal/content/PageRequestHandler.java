package com.enonic.wem.portal.content;

import com.enonic.wem.portal.AbstractResource;

public class PageRequestHandler
    extends AbstractResource
{
    /*
    private ContentService contentService;

    public String getPage()
    {

        final Content content = contentService.getContent( new PageRequest( getPortalRequest() ) );

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
    */
}
