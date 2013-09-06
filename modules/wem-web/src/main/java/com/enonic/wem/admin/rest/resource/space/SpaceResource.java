package com.enonic.wem.admin.rest.resource.space;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.sun.jersey.api.NotFoundException;

import com.enonic.wem.admin.json.space.SpaceJson;
import com.enonic.wem.admin.json.space.SpaceSummaryListJson;
import com.enonic.wem.admin.rest.resource.AbstractResource;
import com.enonic.wem.admin.rest.resource.space.exception.DuplicatedSpaceException;
import com.enonic.wem.admin.rest.service.upload.UploadService;
import com.enonic.wem.admin.rpc.UploadedIconFetcher;
import com.enonic.wem.api.Icon;
import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.command.space.DeleteSpace;
import com.enonic.wem.api.command.space.UpdateSpace;
import com.enonic.wem.api.exception.SpaceNotFoundException;
import com.enonic.wem.api.space.Space;
import com.enonic.wem.api.space.SpaceName;
import com.enonic.wem.api.space.SpaceNames;
import com.enonic.wem.api.space.Spaces;
import com.enonic.wem.api.space.editor.SpaceEditor;

import static com.enonic.wem.api.command.Commands.space;
import static com.enonic.wem.api.space.editor.SpaceEditors.composite;
import static com.enonic.wem.api.space.editor.SpaceEditors.setDisplayName;
import static com.enonic.wem.api.space.editor.SpaceEditors.setIcon;

@Path("space")
@Produces(MediaType.APPLICATION_JSON)
public final class SpaceResource
    extends AbstractResource
{

    private UploadService uploadService;

    @GET
    public SpaceJson getDetails( @QueryParam("name") final String name )
    {
        final SpaceName spaceName = SpaceName.from( name );
        final Space space = this.client.execute( Commands.space().get().name( spaceName ) ).first();
        if ( space != null )
        {
            return new SpaceJson( space );
        }
        else
        {
            throw new NotFoundException();
        }
    }

    @GET
    @Path("list")
    public SpaceSummaryListJson list()
    {
        final Spaces spaces = client.execute( Commands.space().get().all() );
        final SpaceSummaryListJson result = new SpaceSummaryListJson( spaces );
        return result;
    }

    @POST
    @Path("create")
    @Consumes(MediaType.APPLICATION_JSON)
    public void create( @FormParam("spaceName") String name, @FormParam("displayName") String displayName,
                        @FormParam("iconReference") String iconReference )
    {
        final SpaceName spaceName = SpaceName.from( name );

        final Icon icon;
        try
        {
            icon = new UploadedIconFetcher( uploadService ).getUploadedIcon( iconReference );
        }
        catch ( Exception e )
        {
            throw new WebApplicationException( e );
        }

        if ( !spaceExists( spaceName ) )
        {
            client.execute( space().create().name( spaceName ).displayName( displayName ).icon( icon ) );
        }
        else
        {
            throw new DuplicatedSpaceException( spaceName );
        }
    }

    @POST
    @Path("delete")
    @Consumes(MediaType.APPLICATION_JSON)
    public void delete( final List<String> names )
    {
        final SpaceNames spaceNames = SpaceNames.from( names.toArray( new String[0] ) );
        final List<SpaceName> notDeleted = Lists.newArrayList();
        boolean success = true;
        for ( SpaceName spaceName : spaceNames )
        {
            final DeleteSpace deleteSpace = space().delete().name( spaceName );
            boolean deleted = client.execute( deleteSpace );
            if ( !deleted )
            {
                notDeleted.add( spaceName );
                success = false;
            }
        }

        if ( !success )
        {
            final String spacesNotDeleted = Joiner.on( ", " ).join( spaceNames );
            throw new NotFoundException( String.format( "Space [%s] not found", spacesNotDeleted ) );
        }
    }

    @POST
    @Path("update")
    @Consumes(MediaType.APPLICATION_JSON)
    public void update( @FormParam("spaceName") String name, @FormParam("displayName") String displayName,
                        @FormParam("iconReference") String iconReference, @FormParam("newName") String newName )
    {
        final SpaceName spaceName = SpaceName.from( name );

        final Icon icon;
        try
        {
            icon = new UploadedIconFetcher( uploadService ).getUploadedIcon( iconReference );
        }
        catch ( Exception e )
        {
            throw new WebApplicationException( e );
        }

        if ( spaceExists( spaceName ) )
        {
            final SpaceEditor editor;
            if ( icon == null )
            {
                editor = setDisplayName( displayName );
            }
            else
            {
                editor = composite( setDisplayName( displayName ), setIcon( icon ) );
            }
            final UpdateSpace updateCommand = space().update().name( spaceName ).editor( editor );
            client.execute( updateCommand );

            if ( newName != null )
            {
                client.execute( space().rename().space( spaceName ).newName( newName ) );
            }
        }
        else
        {
            throw new SpaceNotFoundException( spaceName );
        }
    }

    private boolean spaceExists( final SpaceName spaceName )
    {
        return !client.execute( space().get().name( spaceName ) ).isEmpty();
    }

    @Inject
    public void setUploadService( final UploadService uploadService )
    {
        this.uploadService = uploadService;
    }
}
