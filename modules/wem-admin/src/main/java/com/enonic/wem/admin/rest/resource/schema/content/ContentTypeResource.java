package com.enonic.wem.admin.rest.resource.schema.content;

import java.io.IOException;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;

import com.sun.jersey.api.NotFoundException;

import com.enonic.wem.admin.json.schema.ContentTypeTreeJson;
import com.enonic.wem.admin.json.schema.content.ContentTypeConfigJson;
import com.enonic.wem.admin.json.schema.content.ContentTypeJson;
import com.enonic.wem.admin.json.schema.content.ContentTypeSummaryListJson;
import com.enonic.wem.admin.jsonrpc.JsonRpcException;
import com.enonic.wem.admin.rest.resource.AbstractResource;
import com.enonic.wem.admin.rest.resource.schema.content.json.ContentTypeCreateOrUpdateParams;
import com.enonic.wem.admin.rest.resource.schema.content.json.ValidateContentTypeJson;
import com.enonic.wem.admin.rest.resource.schema.json.SchemaDeleteJson;
import com.enonic.wem.admin.rest.resource.schema.json.SchemaDeleteParams;
import com.enonic.wem.admin.rest.service.upload.UploadService;
import com.enonic.wem.admin.rpc.UploadedIconFetcher;
import com.enonic.wem.api.Icon;
import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.command.schema.content.CreateContentType;
import com.enonic.wem.api.command.schema.content.DeleteContentType;
import com.enonic.wem.api.command.schema.content.DeleteContentTypeResult;
import com.enonic.wem.api.command.schema.content.GetContentTypes;
import com.enonic.wem.api.command.schema.content.UpdateContentType;
import com.enonic.wem.api.exception.BaseException;
import com.enonic.wem.api.schema.content.ContentType;
import com.enonic.wem.api.schema.content.ContentTypeName;
import com.enonic.wem.api.schema.content.ContentTypeNames;
import com.enonic.wem.api.schema.content.ContentTypes;
import com.enonic.wem.api.schema.content.editor.ContentTypeEditor;
import com.enonic.wem.api.schema.content.validator.ContentTypeValidationResult;
import com.enonic.wem.api.support.tree.Tree;
import com.enonic.wem.core.schema.content.serializer.ContentTypeXmlSerializer;
import com.enonic.wem.core.support.serializer.XmlParsingException;

import static com.enonic.wem.api.command.Commands.contentType;
import static com.enonic.wem.api.schema.content.ContentType.newContentType;
import static com.enonic.wem.api.schema.content.editor.SetContentTypeEditor.newSetContentTypeEditor;

@Path("schema/content")
@Produces("application/json")
public class ContentTypeResource
    extends AbstractResource
{
    private UploadService uploadService;

    private ContentTypeXmlSerializer contentTypeXmlSerializer = new ContentTypeXmlSerializer();

    @GET
    public ContentTypeJson get( @QueryParam("qualifiedName") final String qualifiedNameAsString,
                                @QueryParam("mixinReferencesToFormItems") final Boolean mixinReferencesToFormItems )
    {
        final ContentTypeName qualifiedName = ContentTypeName.from( qualifiedNameAsString );
        final GetContentTypes getContentTypes = Commands.contentType().get().
            qualifiedNames( ContentTypeNames.from( qualifiedName ) ).
            mixinReferencesToFormItems( mixinReferencesToFormItems );

        final ContentTypes contentTypes = client.execute( getContentTypes );
        if ( contentTypes.isEmpty() )
        {
            throw new NotFoundException( String.format( "ContentTypes [%s] not found", qualifiedName ) );
        }
        return new ContentTypeJson( contentTypes.first() );
    }

    @GET
    @Path("config")
    public ContentTypeConfigJson getConfig( @QueryParam("qualifiedName") final String qualifiedNameAsString )
    {
        final ContentTypeName qualifiedName = ContentTypeName.from( qualifiedNameAsString );
        final GetContentTypes getContentTypes = Commands.contentType().get().
            qualifiedNames( ContentTypeNames.from( qualifiedName ) ).
            mixinReferencesToFormItems( false );

        final ContentTypes contentTypes = client.execute( getContentTypes );

        if ( contentTypes.isEmpty() )
        {
            throw new NotFoundException( String.format( "ContentTypes [%s] not found", qualifiedName ) );
        }

        return new ContentTypeConfigJson( contentTypes.first() );
    }

    @GET
    @Path("list")
    public ContentTypeSummaryListJson list()
    {
        final ContentTypes contentTypes = client.execute( Commands.contentType().get().all() );
        return new ContentTypeSummaryListJson( contentTypes );
    }

    @POST
    @Path("delete")
    @Consumes(MediaType.APPLICATION_JSON)
    public SchemaDeleteJson delete( SchemaDeleteParams params )
    {
        final ContentTypeNames contentTypeNames = ContentTypeNames.from( params.getQualifiedNames().toArray( new String[0] ) );

        final SchemaDeleteJson deletionResult = new SchemaDeleteJson();
        for ( ContentTypeName contentTypeName : contentTypeNames )
        {
            final DeleteContentType deleteContentType = Commands.contentType().delete().name( contentTypeName );
            final DeleteContentTypeResult result = client.execute( deleteContentType );
            switch ( result )
            {
                case SUCCESS:
                    deletionResult.success( contentTypeName );
                    break;

                case NOT_FOUND:
                    deletionResult.failure( contentTypeName,
                                            String.format( "ContentType [%s] was not found", contentTypeName.toString() ) );

                    break;

                case UNABLE_TO_DELETE:
                    deletionResult.failure( contentTypeName,
                                            String.format( "Unable to delete ContentType [%s]", contentTypeName.toString() ) );
                    break;
            }
        }

        return deletionResult;
    }

    @POST
    @Path("create")
    public ContentTypeJson create( ContentTypeCreateOrUpdateParams params )
    {
        ContentType contentType;
        try
        {
            contentType = contentTypeXmlSerializer.toContentType( params.getContentType() );
        }
        catch ( XmlParsingException e )
        {
            throw new WebApplicationException( e );
        }

        if ( contentTypeExists( contentType.getQualifiedName() ) )
        {
            throw new IllegalArgumentException(
                "ContentType already exists [" + contentType.getQualifiedName().toString() + "] TODO: make form reload" );
        }

        final Icon icon;
        try
        {
            icon = new UploadedIconFetcher( uploadService ).getUploadedIcon( params.getIconReference() );
        }
        catch ( JsonRpcException | IOException e )
        {
            throw new WebApplicationException( e );
        }

        if ( icon != null )
        {
            contentType = newContentType( contentType ).icon( icon ).build();
        }

        createContentType( contentType );

        return new ContentTypeJson( contentType );
    }

    private void createContentType( final ContentType contentType )
    {
        final CreateContentType createCommand = contentType().create().
            name( contentType.getName() ).
            displayName( contentType.getDisplayName() ).
            superType( contentType.getSuperType() ).
            setAbstract( contentType.isAbstract() ).
            setFinal( contentType.isFinal() ).
            form( contentType.form() ).
            icon( contentType.getIcon() ).
            contentDisplayNameScript( contentType.getContentDisplayNameScript() );
        try
        {
            client.execute( createCommand );
        }
        catch ( BaseException e )
        {
            throw new WebApplicationException( e );
        }
    }

    @POST
    @Path("update")
    public ContentTypeJson update( ContentTypeCreateOrUpdateParams params )
    {
        ContentType contentType;
        try
        {
            contentType = new ContentTypeXmlSerializer().toContentType( params.getContentType() );
        }
        catch ( XmlParsingException e )
        {
            throw new WebApplicationException( e );
        }

        final Icon icon;
        try
        {
            icon = new UploadedIconFetcher( uploadService ).getUploadedIcon( params.getIconReference() );
        }
        catch ( JsonRpcException | IOException e )
        {
            throw new WebApplicationException( e );
        }

        if ( icon != null )
        {
            contentType = newContentType( contentType ).icon( icon ).build();
        }

        updateContentType( contentType );

        return new ContentTypeJson( contentType );
    }

    private void updateContentType( final ContentType contentType )
    {
        final ContentTypeEditor editor = newSetContentTypeEditor().
            displayName( contentType.getDisplayName() ).
            icon( contentType.getIcon() ).
            superType( contentType.getSuperType() ).
            setAbstract( contentType.isAbstract() ).
            setFinal( contentType.isFinal() ).
            contentDisplayNameScript( contentType.getContentDisplayNameScript() ).
            form( contentType.form() ).
            build();
        final UpdateContentType updateCommand = contentType().update().qualifiedName( contentType.getQualifiedName() ).editor( editor );

        try
        {
            client.execute( updateCommand );
        }
        catch ( BaseException e )
        {
            throw new WebApplicationException( e );
        }
    }

    @GET
    @Path("tree")
    public ContentTypeTreeJson getTree()
    {
        final Tree<ContentType> contentTypeTree = client.execute( contentType().getTree() );
        return new ContentTypeTreeJson( contentTypeTree );
    }

    @POST
    @Path("validate")
    public ValidateContentTypeJson validate( @FormParam("contentType") final String contentTypeXml )
    {
        final ContentType contentType;
        try
        {
            contentType = new ContentTypeXmlSerializer().toContentType( contentTypeXml );
        }
        catch ( XmlParsingException e )
        {
            throw new WebApplicationException( e );
        }

        final ContentTypeValidationResult validationResult = client.execute( contentType().validate().contentType( contentType ) );

        return new ValidateContentTypeJson( validationResult, contentType );
    }

    private boolean contentTypeExists( final ContentTypeName qualifiedName )
    {
        final GetContentTypes getContentTypes = contentType().get().qualifiedNames( ContentTypeNames.from( qualifiedName ) );
        return !client.execute( getContentTypes ).isEmpty();
    }


    @Inject
    public void setUploadService( final UploadService uploadService )
    {
        this.uploadService = uploadService;
    }
}
