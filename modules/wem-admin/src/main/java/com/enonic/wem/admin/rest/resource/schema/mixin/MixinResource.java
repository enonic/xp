package com.enonic.wem.admin.rest.resource.schema.mixin;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.enonic.wem.admin.json.schema.mixin.MixinConfigJson;
import com.enonic.wem.admin.json.schema.mixin.MixinJson;
import com.enonic.wem.admin.json.schema.mixin.MixinListJson;
import com.enonic.wem.admin.rest.resource.AbstractResource;
import com.enonic.wem.admin.rest.resource.schema.json.CreateOrUpdateSchemaJsonResult;
import com.enonic.wem.admin.rest.resource.schema.json.SchemaDeleteJson;
import com.enonic.wem.admin.rest.resource.schema.json.SchemaDeleteParams;
import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.command.schema.mixin.CreateMixin;
import com.enonic.wem.api.command.schema.mixin.DeleteMixin;
import com.enonic.wem.api.command.schema.mixin.UpdateMixin;
import com.enonic.wem.api.command.schema.mixin.UpdateMixinResult;
import com.enonic.wem.api.schema.mixin.Mixin;
import com.enonic.wem.api.schema.mixin.MixinName;
import com.enonic.wem.api.schema.mixin.MixinNames;
import com.enonic.wem.api.schema.mixin.MixinNotFoundException;
import com.enonic.wem.api.schema.mixin.Mixins;
import com.enonic.wem.api.schema.mixin.UnableToDeleteMixinException;
import com.enonic.wem.api.schema.mixin.editor.MixinEditor;
import com.enonic.wem.core.schema.mixin.MixinXmlSerializer;

import static com.enonic.wem.api.command.Commands.mixin;

@Path("schema/mixin")
@Produces(MediaType.APPLICATION_JSON)
public class MixinResource
    extends AbstractResource
{
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
        final Mixins mixins = client.execute( Commands.mixin().get().all() );

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

        final CreateMixin createCommand = mixin().create().
            name( params.getName().toString() ).
            displayName( mixin.getDisplayName() ).
            formItems( mixin.getFormItems() ).
            icon( params.getIconJson() != null ? params.getIconJson().getIcon() : null );

        try
        {
            final Mixin createdMixin = client.execute( createCommand );
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

            final MixinEditor editor = new MixinEditor()
            {
                @Override
                public Mixin edit( final Mixin mixin )
                {
                    final Mixin.Builder builder = Mixin.newMixin( mixin );
                    builder.name( params.getName() );
                    builder.displayName( parsed.getDisplayName() );
                    builder.formItems( parsed.getFormItems() );
                    if ( params.getIconJson() != null )
                    {
                        builder.icon( params.getIconJson().getIcon() );
                    }
                    return builder.build();
                }
            };

            final UpdateMixin updateCommand = mixin().update().
                name( params.getMixinToUpdate() ).
                editor( editor );

            final UpdateMixinResult result = client.execute( updateCommand );
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
            final DeleteMixin deleteMixin = Commands.mixin().delete().name( mixinName );
            try
            {
                client.execute( deleteMixin );
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
        return client.execute( mixin().get().byName( name ) );
    }
}
