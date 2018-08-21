package com.enonic.xp.lib.portal.current;

import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentNotFoundException;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.ContentService;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.script.bean.BeanContext;
import com.enonic.xp.script.bean.ScriptBean;
import com.enonic.xp.site.Site;

public abstract class GetCurrentAbstractHandler
    implements ScriptBean
{
    protected PortalRequest request;

    protected ContentService contentService;

    protected Content getContent()
    {
        Content content = this.request.getContent();
        if ( content == null )
        {
            final ContentPath contentPath = this.request.getContentPath();
            if ( contentPath != null && !ContentPath.ROOT.equals( contentPath ) )
            {
                try
                {
                    content = this.contentService.getByPath( contentPath );
                }
                catch ( final ContentNotFoundException e )
                {
                }
            }
        }
        return content;
    }

    protected Site getSite()
    {
        Site site = this.request.getSite();
        if ( site == null )
        {
            final Content content = this.getContent();
            if ( content != null )
            {
                try
                {
                    site = this.contentService.getNearestSite( content.getId() );
                }
                catch ( final ContentNotFoundException e )
                {
                }
            }
        }
        return site;
    }


    @Override
    public void initialize( final BeanContext context )
    {
        this.request = context.getBinding( PortalRequest.class ).get();
        this.contentService = context.getService( ContentService.class ).get();
    }
}
