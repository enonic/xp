package com.enonic.wem.admin.rest.resource.schema.mixin;

import java.time.Instant;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.enonic.wem.admin.json.icon.ThumbnailJson;
import com.enonic.wem.admin.json.schema.mixin.MixinConfigJson;
import com.enonic.wem.admin.json.schema.mixin.MixinJson;
import com.enonic.wem.admin.json.schema.mixin.MixinListJson;
import com.enonic.wem.admin.rest.resource.schema.SchemaIconResolver;
import com.enonic.wem.admin.rest.resource.schema.SchemaIconUrlResolver;
import com.enonic.wem.admin.rest.resource.schema.json.CreateOrUpdateSchemaJsonResult;
import com.enonic.wem.admin.rest.resource.schema.json.SchemaDeleteJson;
import com.enonic.wem.admin.rest.resource.schema.json.SchemaDeleteParams;
import com.enonic.wem.api.Icon;
import com.enonic.wem.api.blob.Blob;
import com.enonic.wem.api.blob.BlobService;
import com.enonic.wem.api.schema.mixin.CreateMixinParams;
import com.enonic.wem.api.schema.mixin.DeleteMixinParams;
import com.enonic.wem.api.schema.mixin.GetMixinParams;
import com.enonic.wem.api.schema.mixin.Mixin;
import com.enonic.wem.api.schema.mixin.MixinName;
import com.enonic.wem.api.schema.mixin.MixinNames;
import com.enonic.wem.api.schema.mixin.MixinNotFoundException;
import com.enonic.wem.api.schema.mixin.MixinService;
import com.enonic.wem.api.schema.mixin.Mixins;
import com.enonic.wem.api.schema.mixin.UnableToDeleteMixinException;
import com.enonic.wem.api.schema.mixin.UpdateMixinParams;
import com.enonic.wem.api.schema.mixin.UpdateMixinResult;
import com.enonic.wem.api.schema.mixin.editor.MixinEditor;
import com.enonic.wem.core.schema.mixin.MixinXmlSerializer;

@Path("schema/mixin")
@Produces(MediaType.APPLICATION_JSON)
public class MixinResource
{
    private MixinService mixinService;

    private BlobService blobService;

    @GET
    public MixinJson get( @QueryParam("name") final String name )
    {
        final MixinName mixinName = MixinName.from( name );
        final Mixin mixin = fetchMixin( mixinName );

        if ( mixin == null )
        {
            String message = String.format( "Mixin [%s] was not found.", mixinName );
            throw new WebApplicationException( Response.status( Response.Status.NOT_FOUND ).
                entity( message ).type( MediaType.TEXT_PLAIN_TYPE ).build() );
        }

        return new MixinJson( mixin, newSchemaIconUrlResolver() );
    }

    @GET
    @Path("config")
    public MixinConfigJson getConfig( @QueryParam("name") final String name )
    {
        final MixinName mixinName = MixinName.from( name );
        final Mixin mixin = fetchMixin( mixinName );

        if ( mixin == null )
        {
            String message = String.format( "Mixin [%s] was not found.", mixinName );
            throw new WebApplicationException( Response.status( Response.Status.NOT_FOUND ).
                entity( message ).type( MediaType.TEXT_PLAIN_TYPE ).build() );
        }

        return new MixinConfigJson( mixin );
    }

    @GET
    @Path("list")
    public MixinListJson list()
    {
        final Mixins mixins = mixinService.getAll();

        return new MixinListJson( mixins, new SchemaIconUrlResolver( new SchemaIconResolver( mixinService ) ) );
    }

    @POST
    @Path("create")
    @Consumes(MediaType.APPLICATION_JSON)
    public CreateOrUpdateSchemaJsonResult create( MixinCreateJson params )
    {
        final Mixin mixin = new MixinXmlSerializer().
            overrideName( params.getName().toString() ).
            toMixin( params.getConfig() );
        final Icon schemaIcon = getSchemaIcon( params.getThumbnailJson() );

        final CreateMixinParams createParams = new CreateMixinParams().
            name( params.getName().toString() ).
            displayName( mixin.getDisplayName() ).
            description( mixin.getDescription() ).
            formItems( mixin.getFormItems() ).
            schemaIcon( schemaIcon );

        try
        {
            final Mixin createdMixin = mixinService.create( createParams );
            final MixinJson mixinJson = new MixinJson( createdMixin, newSchemaIconUrlResolver() );
            return CreateOrUpdateSchemaJsonResult.result( mixinJson );
        }
        catch ( Exception e )
        {
            return CreateOrUpdateSchemaJsonResult.error( e.getMessage() );
        }
    }

    @POST
    @Path("update")
    @Consumes(MediaType.APPLICATION_JSON)
    public CreateOrUpdateSchemaJsonResult update( final MixinUpdateJson params )
    {
        try
        {
            final Mixin parsed = new MixinXmlSerializer().
                overrideName( params.getName().toString() ).
                toMixin( params.getConfig() );
            final Icon schemaIcon = getSchemaIcon( params.getThumbnailJson() );

            final MixinEditor editor = new MixinEditor()
            {
                @Override
                public Mixin edit( final Mixin mixin )
                {
                    final Mixin.Builder builder = Mixin.newMixin( mixin );
                    builder.name( params.getName() );
                    builder.displayName( parsed.getDisplayName() );
                    builder.description( parsed.getDescription() );
                    builder.formItems( parsed.getFormItems() );
                    if ( schemaIcon != null )
                    {
                        builder.icon( schemaIcon );
                    }
                    return builder.build();
                }
            };

            final UpdateMixinParams updateParams = new UpdateMixinParams().
                name( params.getMixinToUpdate() ).
                editor( editor );

            final UpdateMixinResult result = mixinService.update( updateParams );
            final Mixin updatedMixin = result.getPersistedMixin();
            return CreateOrUpdateSchemaJsonResult.result( new MixinJson( updatedMixin, newSchemaIconUrlResolver() ) );
        }
        catch ( Exception e )
        {
            e.printStackTrace();
            return CreateOrUpdateSchemaJsonResult.error( e.getMessage() );
        }
    }

    @POST
    @Path("delete")
    @Consumes(MediaType.APPLICATION_JSON)
    public SchemaDeleteJson delete( SchemaDeleteParams params )
    {
        final MixinNames mixinNames = MixinNames.from( params.getNames().toArray( new String[params.getNames().size()] ) );

        final SchemaDeleteJson deletionResult = new SchemaDeleteJson();
        for ( MixinName mixinName : mixinNames )
        {
            final DeleteMixinParams deleteMixin = new DeleteMixinParams().name( mixinName );
            try
            {
                mixinService.delete( deleteMixin );
                deletionResult.success( mixinName );
            }
            catch ( MixinNotFoundException | UnableToDeleteMixinException e )
            {
                deletionResult.failure( mixinName, e.getMessage() );
            }
        }

        return deletionResult;
    }

    private Mixin fetchMixin( final MixinName name )
    {
        return mixinService.getByName( new GetMixinParams( name ) );
    }

    private Icon getSchemaIcon( final ThumbnailJson thumbnailJson )
    {
        if ( thumbnailJson != null )
        {
            final Blob blob = blobService.get( thumbnailJson.getThumbnail().getBlobKey() );
            return blob == null ? null : Icon.from( blob.getStream(), thumbnailJson.getMimeType(), Instant.now() );
        }
        return null;
    }

    private SchemaIconUrlResolver newSchemaIconUrlResolver()
    {
        return new SchemaIconUrlResolver( new SchemaIconResolver( mixinService ) );
    }

    @Inject
    public void setMixinService( final MixinService mixinService )
    {
        this.mixinService = mixinService;
    }

    @Inject
    public void setBlobService( final BlobService blobService )
    {
        this.blobService = blobService;
    }
}
