package com.enonic.wem.admin.rest.resource.schema.content;

import java.io.IOException;
import java.util.List;

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

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.sun.jersey.api.NotFoundException;

import com.enonic.wem.admin.jsonrpc.JsonRpcException;
import com.enonic.wem.admin.rest.resource.AbstractResource;
import com.enonic.wem.admin.rest.resource.schema.content.model.ContentTypeConfigListJson;
import com.enonic.wem.admin.rest.resource.schema.content.model.ContentTypeList;
import com.enonic.wem.admin.rest.resource.schema.content.model.ContentTypeListJson;
import com.enonic.wem.admin.rest.resource.schema.content.model.ContentTypeSummaryListJson;
import com.enonic.wem.admin.rest.resource.schema.content.model.ContentTypeTreeJson;
import com.enonic.wem.admin.rest.resource.schema.content.model.ValidateContentTypeJson;
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
import com.enonic.wem.api.schema.content.ContentTypes;
import com.enonic.wem.api.schema.content.QualifiedContentTypeName;
import com.enonic.wem.api.schema.content.QualifiedContentTypeNames;
import com.enonic.wem.api.schema.content.editor.ContentTypeEditor;
import com.enonic.wem.api.schema.content.validator.ContentTypeValidationResult;
import com.enonic.wem.api.support.tree.Tree;
import com.enonic.wem.core.schema.content.serializer.ContentTypeXmlSerializer;
import com.enonic.wem.core.support.serializer.XmlParsingException;

import static com.enonic.wem.api.command.Commands.contentType;
import static com.enonic.wem.api.schema.content.ContentType.newContentType;
import static com.enonic.wem.api.schema.content.editor.SetContentTypeEditor.newSetContentTypeEditor;

@Path("schema/content")
public class ContentTypeResource
    extends AbstractResource
{
    public static final String FORMAT_XML = "XML";

    public static final String FORMAT_JSON = "JSON";

    private UploadService uploadService;

    @GET
    @Produces("application/json")
    public ContentTypeList get( @QueryParam("qualifiedNames") final List<String> qualifiedNamesAsStrings,
                                @QueryParam("format") final String format,
                                @QueryParam("mixinReferencesToFormItems") final Boolean mixinReferencesToFormItems )
    {
        final QualifiedContentTypeNames qualifiedNames = QualifiedContentTypeNames.from( qualifiedNamesAsStrings );
        final GetContentTypes getContentTypes = Commands.contentType().get().
            qualifiedNames( qualifiedNames ).
            mixinReferencesToFormItems( mixinReferencesToFormItems );

        final ContentTypes contentTypes = client.execute( getContentTypes );

        if ( qualifiedNames.getSize() == contentTypes.getSize() )
        {
            if ( format.equalsIgnoreCase( FORMAT_JSON ) )
            {
                return new ContentTypeListJson( contentTypes );
            }
            else if ( format.equalsIgnoreCase( FORMAT_XML ) )
            {
                return new ContentTypeConfigListJson( contentTypes );
            }
            else
            {
                throw new IllegalArgumentException( "Unknown format: " + format );
            }
        }

        final ImmutableSet<QualifiedContentTypeName> found = contentTypes.getNames();
        final String[] notFound = FluentIterable.
            from( qualifiedNames ).
            filter( new Predicate<QualifiedContentTypeName>()
            {
                public boolean apply( final QualifiedContentTypeName requested )
                {
                    return !found.contains( requested );
                }
            } ).
            transform( new Function<QualifiedContentTypeName, String>()
            {
                public String apply( final QualifiedContentTypeName qualifiedContentTypeName )
                {
                    return qualifiedContentTypeName.toString();
                }
            } ).
            toArray( String.class );

        final String missing = Joiner.on( "," ).join( notFound );

        throw new NotFoundException( String.format( "ContentTypes [%s] not found", missing ) );
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
    public void delete( @FormParam("qualifiedContentTypeNames") final List<String> names )
    {
        final QualifiedContentTypeNames contentTypeNames = QualifiedContentTypeNames.from( names );

        final List<String> notFound = Lists.newArrayList();
        final List<String> unableDelete = Lists.newArrayList();

        for ( QualifiedContentTypeName contentTypeName : contentTypeNames )
        {
            final DeleteContentType deleteContentType = Commands.contentType().delete().name( contentTypeName );
            final DeleteContentTypeResult deleteResult = client.execute( deleteContentType );
            switch ( deleteResult )
            {
                case SUCCESS:
                    break;

                case NOT_FOUND:
                    notFound.add( contentTypeName.toString() );
                    break;

                case UNABLE_TO_DELETE:
                    unableDelete.add( contentTypeName.toString() );
                    break;
            }
        }

        if ( !notFound.isEmpty() )
        {
            final String nameNotDeleted = Joiner.on( ", " ).join( notFound );
            throw new NotFoundException( String.format( "ContentType [%s] was not found", nameNotDeleted ) );
        }

        if ( !unableDelete.isEmpty() )
        {
            final String nameNotDeleted = Joiner.on( ", " ).join( unableDelete );
            throw new NotFoundException(
                String.format( "Unable to delete content type [%s]: Content type is being used", nameNotDeleted ) );
        }
    }

    @POST
    @Path("update")
    @Consumes(MediaType.APPLICATION_JSON)
    public void update( @FormParam("contentType") final String contentTypeXml, @FormParam("iconReference") final String iconReference )
    {
        ContentType contentType;
        try
        {
            contentType = new ContentTypeXmlSerializer().toContentType( contentTypeXml );
        }
        catch ( XmlParsingException e )
        {
            throw new WebApplicationException( e );
        }

        final Icon icon;
        try
        {
            icon = new UploadedIconFetcher( uploadService ).getUploadedIcon( iconReference );
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

    @POST
    @Path("create")
    @Consumes(MediaType.APPLICATION_JSON)
    public void create( @FormParam("contentType") final String contentTypeXml, @FormParam("iconReference") final String iconReference )
    {
        ContentType contentType;
        try
        {
            contentType = new ContentTypeXmlSerializer().toContentType( contentTypeXml );
        }
        catch ( XmlParsingException e )
        {
            throw new WebApplicationException( e );
        }

        final Icon icon;
        try
        {
            icon = new UploadedIconFetcher( uploadService ).getUploadedIcon( iconReference );
        }
        catch ( JsonRpcException | IOException e )
        {
            throw new WebApplicationException( e );
        }

        if ( icon != null )
        {
            contentType = newContentType( contentType ).icon( icon ).build();
        }

        if ( !contentTypeExists( contentType.getQualifiedName() ) )
        {
            createContentType( contentType );
        }
        else
        {
            updateContentType( contentType );
        }
    }

    private boolean contentTypeExists( final QualifiedContentTypeName qualifiedName )
    {
        final GetContentTypes getContentTypes = contentType().get().qualifiedNames( QualifiedContentTypeNames.from( qualifiedName ) );
        return !client.execute( getContentTypes ).isEmpty();
    }

    private void createContentType( final ContentType contentType )
    {
        final CreateContentType createCommand = contentType().create().
            name( contentType.getName() ).
            displayName( contentType.getDisplayName() ).
            superType( contentType.getSuperType() ).
            setAbstract( contentType.isAbstract() ).
            setFinal( contentType.isFinal() ).
            moduleName( contentType.getModuleName() ).
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

    @Inject
    public void setUploadService( final UploadService uploadService )
    {
        this.uploadService = uploadService;
    }
}
