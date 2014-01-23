package com.enonic.wem.portal.content;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.OPTIONS;
import javax.ws.rs.POST;
import javax.ws.rs.core.Response;

import com.enonic.wem.api.Client;
import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.ContentNotFoundException;
import com.enonic.wem.api.content.ContentPath;
import com.enonic.wem.api.content.page.Page;
import com.enonic.wem.api.content.page.PageDescriptor;
import com.enonic.wem.api.content.page.PageDescriptorKey;
import com.enonic.wem.api.content.page.PageTemplate;
import com.enonic.wem.portal.controller.JsControllerFactory;
import com.enonic.wem.portal.exception.PortalWebException;

import static com.enonic.wem.api.command.Commands.content;
import static com.enonic.wem.api.command.Commands.page;


public abstract class RenderResource
{
    private static final String EDIT_MODE = "edit";

    @Inject
    protected Client client;

    @Inject
    protected JsControllerFactory controllerFactory;

    @GET
    public Response handleGet()
        throws Exception
    {
        return doHandle();
    }

    @POST
    public Response handlePost()
        throws Exception
    {
        return doHandle();
    }

    @OPTIONS
    public Response handleOptions()
        throws Exception
    {
        return doHandle();
    }

    abstract protected Response doHandle()
        throws Exception;

    protected Content getContent( final String contentSelector, final String mode )
    {
        final ContentPath contentPath = ContentPath.from( contentSelector );
        final Content content = getContentByPath( contentPath );
        if ( content != null )
        {
            return content;
        }

        final boolean inEditMode = EDIT_MODE.equals( mode );
        if ( inEditMode )
        {
            final ContentId contentId = ContentId.from( contentSelector );
            final Content contentById = getContentById( contentId );
            if ( contentById != null )
            {
                return contentById;
            }
        }
        throw PortalWebException.notFound().message( "Page [{0}] not found.", contentPath ).build();
    }

    protected Page getPage( final Content content )
    {
        if ( !content.isPage() )
        {
            throw PortalWebException.notFound().message( "Page not found." ).build();
        }
        return content.getPage();
    }

    protected PageDescriptor getPageDescriptor( final PageTemplate pageTemplate )
    {
        final PageDescriptorKey descriptorKey = pageTemplate.getDescriptor();
        final PageDescriptor pageDescriptor = this.client.execute( page().descriptor().page().getByKey( descriptorKey ) );
        if ( pageDescriptor == null )
        {
            throw PortalWebException.notFound().message( "Page descriptor for template [{0}] not found.", pageTemplate.getName() ).build();
        }
        return pageDescriptor;
    }

    protected PageTemplate getPageTemplate( final Page page )
    {
        final PageTemplate pageTemplate = this.client.execute( page().template().page().getByKey().key( page.getTemplate() ) );
        if ( pageTemplate == null )
        {
            throw PortalWebException.notFound().message( "Page template [{0}] not found.", page.getTemplate() ).build();
        }
        return pageTemplate;
    }

    private Content getContentByPath( final ContentPath contentPath )
    {
        try
        {
            return this.client.execute( content().get().byPath( contentPath ) );
        }
        catch ( ContentNotFoundException e )
        {
            return null;
        }
    }

    private Content getContentById( final ContentId contentId )
    {
        try
        {
            return this.client.execute( content().get().byId( contentId ) );
        }
        catch ( ContentNotFoundException e )
        {
            return null;
        }
    }

}
