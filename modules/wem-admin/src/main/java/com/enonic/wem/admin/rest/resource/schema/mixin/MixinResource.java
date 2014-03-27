package com.enonic.wem.admin.rest.resource.schema.mixin;

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

import com.enonic.wem.admin.json.icon.IconJson;
import com.enonic.wem.admin.json.schema.mixin.MixinConfigJson;
import com.enonic.wem.admin.json.schema.mixin.MixinJson;
import com.enonic.wem.admin.json.schema.mixin.MixinListJson;
import com.enonic.wem.admin.rest.resource.AbstractResource;
import com.enonic.wem.admin.rest.resource.schema.json.CreateOrUpdateSchemaJsonResult;
import com.enonic.wem.admin.rest.resource.schema.json.SchemaDeleteJson;
import com.enonic.wem.admin.rest.resource.schema.json.SchemaDeleteParams;
import com.enonic.wem.api.blob.Blob;
import com.enonic.wem.api.command.content.blob.GetBlob;
import com.enonic.wem.api.command.schema.mixin.CreateMixinParams;
import com.enonic.wem.api.command.schema.mixin.DeleteMixinParams;
import com.enonic.wem.api.command.schema.mixin.GetMixinParams;
import com.enonic.wem.api.command.schema.mixin.MixinService;
import com.enonic.wem.api.command.schema.mixin.UpdateMixinParams;
import com.enonic.wem.api.command.schema.mixin.UpdateMixinResult;
import com.enonic.wem.api.schema.SchemaIcon;
import com.enonic.wem.api.schema.mixin.Mixin;
import com.enonic.wem.api.schema.mixin.MixinName;
import com.enonic.wem.api.schema.mixin.MixinNames;
import com.enonic.wem.api.schema.mixin.MixinNotFoundException;
import com.enonic.wem.api.schema.mixin.Mixins;
import com.enonic.wem.api.schema.mixin.UnableToDeleteMixinException;
import com.enonic.wem.api.schema.mixin.editor.MixinEditor;
import com.enonic.wem.core.schema.mixin.MixinXmlSerializer;

@Path("schema/mixin")
@Produces(MediaType.APPLICATION_JSON)
public class MixinResource
    extends AbstractResource
{
    private MixinService mixinService;

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

        return new MixinJson( mixin );
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

        return new MixinListJson( mixins );
    }

    @POST
    @Path("create")
    @Consumes(MediaType.APPLICATION_JSON)
    public CreateOrUpdateSchemaJsonResult create( MixinCreateJson params )
    {
        final Mixin mixin = new MixinXmlSerializer().
            overrideName( params.getName().toString() ).
            toMixin( params.getConfig() );
        final SchemaIcon schemaIcon = getSchemaIcon( params.getIconJson() );

        final CreateMixinParams createParams = new CreateMixinParams().
            name( params.getName().toString() ).
            displayName( mixin.getDisplayName() ).
            description( mixin.getDescription() ).
            formItems( mixin.getFormItems() ).
            schemaIcon( schemaIcon );

        try
        {
            final Mixin createdMixin = mixinService.create( createParams );
            final MixinJson mixinJson = new MixinJson( createdMixin );
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
            final SchemaIcon schemaIcon = getSchemaIcon( params.getIconJson() );

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
            return CreateOrUpdateSchemaJsonResult.result( new MixinJson( updatedMixin ) );
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

    private SchemaIcon getSchemaIcon( final IconJson iconJson )
    {
        if ( iconJson != null )
        {
            final Blob blob = client.execute( new GetBlob( iconJson.getThumbnail().getBlobKey() ) );
            return blob == null ? null : SchemaIcon.from( blob.getStream(), iconJson.getMimeType() );
        }
        return null;
    }

    @Inject
    public void setMixinService( final MixinService mixinService )
    {
        this.mixinService = mixinService;
    }
}
