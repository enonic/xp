package com.enonic.xp.portal.impl.resource.render;

import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentConstants;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.page.Page;
import com.enonic.xp.page.PageTemplate;
import com.enonic.xp.portal.impl.resource.underscore.UnderscoreResource;
import com.enonic.xp.schema.content.ContentTypeForms;
import com.enonic.xp.util.Reference;

public final class PageResource
    extends RenderResource
{
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
            if ( resource.pageTemplate == null )
            {
                throw notFound( "No template found for content" );
            }
        }
        else
        {
            final Page page = getPage( resource.content );
            resource.pageTemplate = getPageTemplate( page );
        }

        if ( resource.pageTemplate.getController() != null )
        {
            resource.pageDescriptor = getPageDescriptor( resource.pageTemplate );
        }

        if ( resource.pageDescriptor != null )
        {
            resource.moduleKey = resource.pageDescriptor.getKey().getModuleKey();
        }

        final Page effectivePage = new EffectivePageResolver( resource.content, resource.pageTemplate ).resolve();
        final Content effectiveContent = Content.newContent( resource.content ).
            page( effectivePage ).
            build();

        resource.content = effectiveContent;
        resource.renderer = this.services.getRendererFactory().getRenderer( effectiveContent );
        return resource;
    }

    private ShortcutControllerResource shortcut( final Content content )
    {
        final Reference shortcutTarget = content.getData().
            getProperty( ContentTypeForms.SHORTCUT_TARGET_PROPERTY ).getReference();
        if ( shortcutTarget == null || shortcutTarget.getNodeId() == null )
        {
            throw notFound( "Missing shortcut target" );
        }
        final ContentId target = ContentId.from( shortcutTarget );
        return initResource( new ShortcutControllerResource( target ) );
    }
}
