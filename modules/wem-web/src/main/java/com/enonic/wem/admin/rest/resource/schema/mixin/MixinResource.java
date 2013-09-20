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

import com.enonic.wem.admin.json.schema.mixin.MixinConfigJson;
import com.enonic.wem.admin.json.schema.mixin.MixinJson;
import com.enonic.wem.admin.json.schema.mixin.MixinListJson;
import com.enonic.wem.admin.rest.resource.AbstractResource;
import com.enonic.wem.admin.rest.resource.schema.mixin.json.MixinCreateOrUpdateJson;
import com.enonic.wem.admin.rest.resource.schema.mixin.json.MixinCreateOrUpdateParams;
import com.enonic.wem.admin.rest.resource.schema.mixin.json.MixinDeleteJson;
import com.enonic.wem.admin.rest.resource.schema.mixin.json.MixinDeleteParams;
import com.enonic.wem.admin.rest.service.upload.UploadService;
import com.enonic.wem.admin.rpc.UploadedIconFetcher;
import com.enonic.wem.api.Icon;
import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.command.schema.mixin.CreateMixin;
import com.enonic.wem.api.command.schema.mixin.DeleteMixin;
import com.enonic.wem.api.command.schema.mixin.DeleteMixinResult;
import com.enonic.wem.api.command.schema.mixin.UpdateMixin;
import com.enonic.wem.api.exception.BaseException;
import com.enonic.wem.api.schema.mixin.Mixin;
import com.enonic.wem.api.schema.mixin.Mixins;
import com.enonic.wem.api.schema.mixin.QualifiedMixinName;
import com.enonic.wem.api.schema.mixin.QualifiedMixinNames;
import com.enonic.wem.api.schema.mixin.editor.SetMixinEditor;
import com.enonic.wem.core.schema.mixin.MixinXmlSerializer;
import com.enonic.wem.core.support.serializer.ParsingException;

import static com.enonic.wem.api.command.Commands.mixin;
import static com.enonic.wem.api.schema.mixin.editor.SetMixinEditor.newSetMixinEditor;

@Path("schema/mixin")
@Produces(MediaType.APPLICATION_JSON)
public class MixinResource
    extends AbstractResource
{
    private final MixinXmlSerializer mixinXmlSerializer = new MixinXmlSerializer();

    private UploadService uploadService;

    @Inject
    public void setUploadService( final UploadService uploadService )
    {
        this.uploadService = uploadService;
    }

    @GET
    public MixinJson get( @QueryParam("qualifiedName") final String name )
    {
        final QualifiedMixinName qualifiedMixinName = new QualifiedMixinName( name );
        final Mixin mixin = fetchMixin( qualifiedMixinName );

        if ( mixin == null )
        {
            String message = String.format( "Mixin [%s] was not found.", qualifiedMixinName );
            throw new WebApplicationException( Response.status( Response.Status.NOT_FOUND ).
                entity( message ).type( MediaType.TEXT_PLAIN_TYPE ).build() );
        }

        return new MixinJson( mixin );
    }

    @GET
    @Path("config")
    public MixinConfigJson getConfig( @QueryParam("qualifiedName") final String name )
    {
        final QualifiedMixinName qualifiedMixinName = new QualifiedMixinName( name );
        final Mixin mixin = fetchMixin( qualifiedMixinName );

        if ( mixin == null )
        {
            String message = String.format( "Mixin [%s] was not found.", qualifiedMixinName );
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

    private Mixin fetchMixin( final QualifiedMixinName qualifiedName )
    {
        final QualifiedMixinNames qualifiedNames = QualifiedMixinNames.from( qualifiedName );
        final Mixins mixins = client.execute( mixin().get().names( qualifiedNames ) );
        return mixins.isEmpty() ? null : mixins.first();
    }

    @POST
    @Path("create")
    @Consumes(MediaType.APPLICATION_JSON)
    public MixinCreateOrUpdateJson create( MixinCreateOrUpdateParams params )
    {
        final Mixin mixin = parseXml( params.getMixin() );
        final Icon icon = fetchIcon( params.getIconReference() );

        if ( fetchMixin( mixin.getQualifiedName() ) != null )
        {
            String message = String.format( "Mixin [%s] already exists.", mixin.getQualifiedName() );
            throw new WebApplicationException(
                Response.status( Response.Status.CONFLICT ).entity( message ).type( MediaType.TEXT_PLAIN_TYPE ).build() );
        }

        final CreateMixin createCommand =
            mixin().create().displayName( mixin.getDisplayName() ).formItem( mixin.getFormItem() ).moduleName( mixin.getModuleName() ).icon(
                icon );

        try
        {
            client.execute( createCommand );
            return MixinCreateOrUpdateJson.created();
        }
        catch ( BaseException e )
        {
            throw new WebApplicationException( e );
        }
    }

    @POST
    @Path("update")
    @Consumes(MediaType.APPLICATION_JSON)
    public MixinCreateOrUpdateJson update( MixinCreateOrUpdateParams params )
    {
        final Mixin mixin = parseXml( params.getMixin() );
        final Icon icon = fetchIcon( params.getIconReference() );

        if ( fetchMixin( mixin.getQualifiedName() ) == null )
        {
            String message = String.format( "Mixin [%s] not found.", mixin.getQualifiedName() );
            throw new WebApplicationException(
                Response.status( Response.Status.NOT_FOUND ).entity( message ).type( MediaType.TEXT_PLAIN_TYPE ).build() );
        }

        final SetMixinEditor editor = newSetMixinEditor().
            displayName( mixin.getDisplayName() ).
            formItem( mixin.getFormItem() ).
            icon( icon ).
            build();
        final UpdateMixin updateCommand = mixin().update().
            qualifiedName( mixin.getQualifiedName() ).
            editor( editor );
        try
        {
            client.execute( updateCommand );
            return MixinCreateOrUpdateJson.updated();
        }
        catch ( BaseException e )
        {
            throw new WebApplicationException( e );
        }
    }

    @POST
    @Path("delete")
    @Consumes(MediaType.APPLICATION_JSON)
    public MixinDeleteJson delete( MixinDeleteParams params )
    {
        final QualifiedMixinNames qualifiedMixinNames =
            QualifiedMixinNames.from( params.getQualifiedMixinNames().toArray( new String[0] ) );

        final MixinDeleteJson deletionResult = new MixinDeleteJson();
        for ( QualifiedMixinName qualifiedMixinName : qualifiedMixinNames )
        {
            final DeleteMixin deleteMixin = Commands.mixin().delete().name( qualifiedMixinName );
            final DeleteMixinResult result = client.execute( deleteMixin );
            switch ( result )
            {
                case SUCCESS:
                    deletionResult.success( qualifiedMixinName );
                    break;

                case NOT_FOUND:
                    deletionResult.failure( qualifiedMixinName,
                                            String.format( "Mixin [%s] was not found", qualifiedMixinName.toString() ) );
                    break;

                case UNABLE_TO_DELETE:
                    deletionResult.failure( qualifiedMixinName,
                                            String.format( "Unable to delete Mixin [%s]", qualifiedMixinName.toString() ) );
                    break;
            }
        }

        return deletionResult;
    }

    private Mixin parseXml( String mixinXml )
    {
        try
        {
            return mixinXmlSerializer.toMixin( mixinXml );
        }
        catch ( ParsingException e )
        {
            throw new WebApplicationException( e );
        }
    }

    private Icon fetchIcon( String iconReference )
    {
        try
        {
            return new UploadedIconFetcher( uploadService ).getUploadedIcon( iconReference );
        }
        catch ( Exception e )
        {
            throw new WebApplicationException( e );
        }
    }
}
