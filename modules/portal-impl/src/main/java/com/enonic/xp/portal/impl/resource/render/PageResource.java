package com.enonic.xp.portal.impl.resource.render;

import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentConstants;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.page.DescriptorKey;
import com.enonic.xp.page.Page;
import com.enonic.xp.page.PageDescriptor;
import com.enonic.xp.page.PageTemplate;
import com.enonic.xp.portal.impl.resource.underscore.UnderscoreResource;
import com.enonic.xp.util.Reference;

public final class PageResource
    extends RenderResource
{
    private static final String SHORTCUT_TARGET_PROPERTY = "target";

    @Path("_")
    public UnderscoreResource underscore()
    {
        return underscore( "/" );
    }

    @Path("{path:.*}/_")
    public UnderscoreResource underscore( @PathParam("path") final String path )
    {
        this.contentPath = ContentPath.from( "/" + path );
        return initResource( new UnderscoreResource() );
    }

    @Path("{path:.*}")
    public RendererControllerResource page( @PathParam("path") final String path )
    {
        this.contentPath = ContentPath.from( "/" + path );

        if ( ContentConstants.CONTENT_ROOT_PARENT.toString().equals( this.contentPath.toString() ) )
        {
            throw notFound( "Page [%s] not found", this.contentPath.toString() );
        }

        final PageControllerResource resource = initResource( new PageControllerResource() );

        resource.content = getContent( this.contentPath.toString() );
        if ( resource.content.getType().isShortcut() )
        {
            return shortcut( resource.content );
        }
        resource.site = getSite( resource.content );

        if ( resource.content instanceof PageTemplate )
        {
            resource.pageTemplate = (PageTemplate) resource.content;
        }
        else if ( !resource.content.hasPage() )
        {
            resource.pageTemplate = getDefaultPageTemplate( resource.content.getType(), resource.site );
        }
        else // hasPage
        {
            final Page page = getPage( resource.content );
            if ( page.hasTemplate() )
            {
                resource.pageTemplate = getPageTemplate( page );
            }
            else if ( page.hasController() )
            {
                resource.pageDescriptor = getPageDescriptor( page.getController() );
            }
        }

        if ( resource.pageTemplate != null && resource.pageTemplate.getController() != null )
        {
            resource.pageDescriptor = getPageDescriptor( resource.pageTemplate );
        }

        if ( resource.pageDescriptor != null )
        {
            resource.applicationKey = resource.pageDescriptor.getKey().getApplicationKey();
        }

        final Page effectivePage = new EffectivePageResolver( resource.content, resource.pageTemplate ).resolve();
        final Content effectiveContent = Content.create( resource.content ).
            page( effectivePage ).
            build();

        resource.content = effectiveContent;
        resource.renderer = this.services.getRendererFactory().getRenderer( effectiveContent );
        return resource;
    }

    private PageDescriptor getPageDescriptor( final DescriptorKey descriptorKey )
    {
        final PageDescriptor pageDescriptor = this.services.getPageDescriptorService().getByKey( descriptorKey );
        if ( pageDescriptor == null )
        {
            throw notFound( "Page descriptor [%s] not found", descriptorKey.getName() );
        }

        return pageDescriptor;
    }

    private ShortcutControllerResource shortcut( final Content content )
    {
        final Reference shortcutTarget = content.getData().
            getProperty( SHORTCUT_TARGET_PROPERTY ).getReference();
        if ( shortcutTarget == null || shortcutTarget.getNodeId() == null )
        {
            throw notFound( "Missing shortcut target" );
        }
        final ContentId target = ContentId.from( shortcutTarget );
        return initResource( new ShortcutControllerResource( target ) );
    }
}
