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

import com.enonic.wem.admin.rest.resource.AbstractResource;
import com.enonic.wem.admin.rest.resource.schema.mixin.model.AbstractMixinJson;
import com.enonic.wem.admin.rest.resource.schema.mixin.model.MixinConfigJson;
import com.enonic.wem.admin.rest.resource.schema.mixin.model.MixinCreateOrUpdateJson;
import com.enonic.wem.admin.rest.resource.schema.mixin.model.MixinCreateOrUpdateParams;
import com.enonic.wem.admin.rest.resource.schema.mixin.model.MixinDeleteJson;
import com.enonic.wem.admin.rest.resource.schema.mixin.model.MixinDeleteParams;
import com.enonic.wem.admin.rest.resource.schema.mixin.model.MixinGetJson;
import com.enonic.wem.admin.rest.resource.schema.mixin.model.MixinListJson;
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
public class MixinResource extends AbstractResource
{
    public static final String FORMAT_XML = "XML";

    public static final String FORMAT_JSON = "JSON";

    private final MixinXmlSerializer mixinXmlSerializer = new MixinXmlSerializer();

    private UploadService uploadService;

    @Inject
    public void setUploadService( final UploadService uploadService )
    {
        this.uploadService = uploadService;
    }

    @GET
    public AbstractMixinJson get(@QueryParam("mixin") final String name,
                                 @QueryParam("format") final String format)
    {
        final QualifiedMixinName qualifiedMixinName = new QualifiedMixinName(name);
        final Mixin mixin = fetchMixin(qualifiedMixinName);

        if ( mixin == null )
        {
            String message = String.format("Mixin [%s] was not found.", qualifiedMixinName);
            throw new WebApplicationException( Response.serverError().entity( message ).type( MediaType.TEXT_PLAIN_TYPE ).build() );
        }

        if ( FORMAT_JSON.equalsIgnoreCase( format ))
        {
            return new MixinGetJson( mixin );
        }
        else if ( FORMAT_XML.equalsIgnoreCase( format ))
        {
            return new MixinConfigJson( mixin );
        }
        else
        {
            String message = String.format("Response format [%s] doesn't exist.", format);
            throw new WebApplicationException( Response.serverError().entity( message ).type( MediaType.TEXT_PLAIN_TYPE ).build() );
        }
    }

    @GET
    @Path( "list" )
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
    @Path( "create" )
    @Consumes( MediaType.APPLICATION_JSON )
    public MixinCreateOrUpdateJson create( MixinCreateOrUpdateParams params )
    {
        final Mixin mixin = parseXml( params.getMixin() );
        final Icon icon = fetchIcon( params.getIconReference() );

        if ( fetchMixin( mixin.getQualifiedName() ) != null )
        {
            throw new WebApplicationException(
                Response.serverError().entity( "Mixin already exists." ).type( MediaType.TEXT_PLAIN_TYPE ).build() );
        }

        final CreateMixin createCommand = mixin().create()
            .displayName( mixin.getDisplayName() )
            .formItem( mixin.getFormItem() )
            .moduleName( mixin.getModuleName() )
            .icon( icon );

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
    @Path( "update" )
    @Consumes( MediaType.APPLICATION_JSON )
    public MixinCreateOrUpdateJson update( MixinCreateOrUpdateParams params )
    {
        final Mixin mixin = parseXml( params.getMixin() );
        final Icon icon = fetchIcon( params.getIconReference() );

        if ( fetchMixin( mixin.getQualifiedName() ) == null )
        {
            throw new WebApplicationException(
                Response.serverError().entity( "Mixin doesn't exist." ).type( MediaType.TEXT_PLAIN_TYPE ).build() );
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
    @Path( "delete" )
    @Consumes(MediaType.APPLICATION_JSON)
    public MixinDeleteJson delete( MixinDeleteParams params )
    {
        final QualifiedMixinNames qualifiedMixinNames = QualifiedMixinNames.from( params.getQualifiedMixinNames().toArray( new String[0] ) );

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

    private Mixin parseXml(String mixinXml)
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
