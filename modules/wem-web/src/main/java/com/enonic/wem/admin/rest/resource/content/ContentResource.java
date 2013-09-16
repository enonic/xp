package com.enonic.wem.admin.rest.resource.content;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.JsonNode;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.sun.jersey.api.NotFoundException;

import com.enonic.wem.admin.json.content.ContentJson;
import com.enonic.wem.admin.json.content.ContentSummaryListJson;
import com.enonic.wem.admin.rest.resource.AbstractResource;
import com.enonic.wem.admin.rest.resource.content.json.AttachmentParams;
import com.enonic.wem.admin.rest.resource.content.json.ContentFindParams;
import com.enonic.wem.admin.rest.resource.content.json.ContentNameJson;
import com.enonic.wem.admin.rest.resource.content.json.CreateContentJson;
import com.enonic.wem.admin.rest.resource.content.json.CreateContentParams;
import com.enonic.wem.admin.rest.resource.content.json.DeleteContentJson;
import com.enonic.wem.admin.rest.resource.content.json.DeleteContentParams;
import com.enonic.wem.admin.rest.resource.content.json.FacetedContentSummaryListJson;
import com.enonic.wem.admin.rest.resource.content.json.UpdateContentJson;
import com.enonic.wem.admin.rest.resource.content.json.UpdateContentParams;
import com.enonic.wem.admin.rest.resource.content.json.ValidateContentJson;
import com.enonic.wem.admin.rest.resource.content.json.ValidateContentParams;
import com.enonic.wem.admin.rest.service.upload.UploadItem;
import com.enonic.wem.admin.rest.service.upload.UploadService;
import com.enonic.wem.admin.rpc.content.ContentDataParser;
import com.enonic.wem.api.account.AccountKey;
import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.command.content.CreateContent;
import com.enonic.wem.api.command.content.CreateContentResult;
import com.enonic.wem.api.command.content.DeleteContent;
import com.enonic.wem.api.command.content.GenerateContentName;
import com.enonic.wem.api.command.content.GetChildContent;
import com.enonic.wem.api.command.content.GetContentVersion;
import com.enonic.wem.api.command.content.GetContents;
import com.enonic.wem.api.command.content.UpdateContent;
import com.enonic.wem.api.command.content.UpdateContentResult;
import com.enonic.wem.api.command.schema.content.GetContentTypes;
import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.ContentIds;
import com.enonic.wem.api.content.ContentNotFoundException;
import com.enonic.wem.api.content.ContentPath;
import com.enonic.wem.api.content.ContentPaths;
import com.enonic.wem.api.content.Contents;
import com.enonic.wem.api.content.CreateContentException;
import com.enonic.wem.api.content.RenameContentException;
import com.enonic.wem.api.content.UpdateContentException;
import com.enonic.wem.api.content.attachment.Attachment;
import com.enonic.wem.api.content.binary.Binary;
import com.enonic.wem.api.content.data.ContentData;
import com.enonic.wem.api.content.editor.ContentEditors;
import com.enonic.wem.api.content.query.ContentIndexQuery;
import com.enonic.wem.api.content.query.ContentIndexQueryResult;
import com.enonic.wem.api.content.versioning.ContentVersionId;
import com.enonic.wem.api.schema.content.ContentType;
import com.enonic.wem.api.schema.content.QualifiedContentTypeName;
import com.enonic.wem.api.schema.content.QualifiedContentTypeNames;
import com.enonic.wem.api.schema.content.validator.DataValidationErrors;
import com.enonic.wem.api.space.SpaceNames;

@Path("content")
@Produces(MediaType.APPLICATION_JSON)
public class ContentResource
    extends AbstractResource
{
    private UploadService uploadService;

    @GET
    public ContentJson getById( @QueryParam("id") final String id, @QueryParam("version") final Long version )
    {
        Content content = null;
        if ( version == null )
        {
            final GetContents getContents = Commands.content().get().selectors( ContentIds.from( id ) );
            final Contents contents = client.execute( getContents );

            if ( contents.isNotEmpty() )
            {
                content = contents.first();
            }
        }
        else
        {
            final GetContentVersion getContentVersion = Commands.content().getVersion().
                selector( ContentId.from( id ) ).
                version( ContentVersionId.of( version ) );

            content = client.execute( getContentVersion );
        }

        if ( content == null )
        {
            throw new NotFoundException( String.format( "Content [%s] was not found", id ) );
        }
        return new ContentJson( content );
    }

    @GET
    @Path("bypath")
    public ContentJson getByPath( @QueryParam("path") final String path, @QueryParam("version") final Long version )
    {
        Content content = null;
        if ( version == null )
        {
            final GetContents getContents = Commands.content().get().selectors( ContentPaths.from( path ) );
            final Contents contents = client.execute( getContents );

            if ( contents.isNotEmpty() )
            {
                content = contents.first();
            }
        }
        else
        {
            final GetContentVersion getContentVersion = Commands.content().getVersion().
                selector( ContentPath.from( path ) ).
                version( ContentVersionId.of( version ) );

            content = client.execute( getContentVersion );
        }

        if ( content == null )
        {
            throw new NotFoundException( String.format( "Content [%s] was not found", path ) );
        }
        return new ContentJson( content );
    }

    @GET
    @Path("list")
    public ContentSummaryListJson listById( @QueryParam("parentId") String parentId )
    {
        Contents contents;
        if ( StringUtils.isEmpty( parentId ) )
        {
            contents = client.execute( Commands.content().getRoots() );
        }
        else
        {
            final GetContents getContent = Commands.content().get().selectors( ContentIds.from( parentId ) );
            Contents parentContents = client.execute( getContent );

            if ( parentContents.isNotEmpty() )
            {
                final GetChildContent getChildContent = Commands.content().getChildren().parentPath( parentContents.first().getPath() );
                contents = client.execute( getChildContent );
            }
            else
            {
                contents = Contents.empty();
            }
        }
        return new ContentSummaryListJson( contents );
    }

    @GET
    @Path("list/bypath")
    public ContentSummaryListJson listByPath( @QueryParam("parentPath") String parentPath )
    {
        final Contents contents;
        if ( StringUtils.isEmpty( parentPath ) )
        {
            contents = client.execute( Commands.content().getRoots() );
        }
        else
        {
            final GetChildContent getChildContent = Commands.content().getChildren().parentPath( ContentPath.from( parentPath ) );
            contents = client.execute( getChildContent );
        }
        return new ContentSummaryListJson( contents );
    }

    @POST
    @Path("find")
    @Consumes(MediaType.APPLICATION_JSON)
    public FacetedContentSummaryListJson find( final ContentFindParams params )
    {
        final ContentIndexQuery contentIndexQuery = new ContentIndexQuery();
        contentIndexQuery.setFullTextSearchString( params.getFulltext() );
        contentIndexQuery.setIncludeFacets( params.isIncludeFacets() );
        contentIndexQuery.setContentTypeNames( QualifiedContentTypeNames.from( params.getContentTypes() ) );
        contentIndexQuery.setSpaceNames( SpaceNames.from( params.getSpaces() ) );

        Set<ContentFindParams.Range> ranges = params.getRanges();
        if ( ranges != null && !ranges.isEmpty() )
        {
            for ( ContentFindParams.Range range : ranges )
            {
                contentIndexQuery.addRange( range.getLower(), range.getUpper() );
            }
        }

        if ( params.isIncludeFacets() )
        {
            contentIndexQuery.setFacets( params.getFacets() );
        }

        final ContentIndexQueryResult contentIndexQueryResult = this.client.execute( Commands.content().find().query( contentIndexQuery ) );

        final Contents contents =
            this.client.execute( Commands.content().get().selectors( ContentIds.from( contentIndexQueryResult.getContentIds() ) ) );

        return new FacetedContentSummaryListJson( contents, contentIndexQueryResult.getFacets() );
    }

    @GET
    @Path("generateName")
    public ContentNameJson generateName( @QueryParam("displayName") final String displayName )
    {
        final GenerateContentName generateContentName = Commands.content().generateContentName().displayName( displayName );

        final String generatedContentName = client.execute( generateContentName );

        return new ContentNameJson( generatedContentName );
    }

    @POST
    @Path("validate")
    public ValidateContentJson validate( final ValidateContentParams params )
    {
        final QualifiedContentTypeName qualifiedContentTypeName = new QualifiedContentTypeName( params.getQualifiedContentTypeName() );

        GetContentTypes getContentType =
            Commands.contentType().get().qualifiedNames( QualifiedContentTypeNames.from( qualifiedContentTypeName ) );
        final ContentType contentType = client.execute( getContentType ).first();

        final ContentData contentData = new ContentDataParser( contentType ).parse( params.getContentData() );

        final DataValidationErrors validationErrors =
            client.execute( Commands.content().validate().contentData( contentData ).contentType( qualifiedContentTypeName ) );

        return new ValidateContentJson( validationErrors );
    }

    @POST
    @Path("delete")
    public DeleteContentJson delete( final DeleteContentParams params )
    {
        final ContentPaths contentsToDelete = ContentPaths.from( params.getContentPaths() );

        final DeleteContentJson jsonResult = new DeleteContentJson();

        for ( final ContentPath contentToDelete : contentsToDelete )
        {
            final DeleteContent deleteContent = Commands.content().delete();
            deleteContent.deleter( AccountKey.anonymous() );
            deleteContent.selector( contentToDelete );
            jsonResult.addResult( contentToDelete, client.execute( deleteContent ) );
        }

        return jsonResult;
    }

    @POST
    @Path("create")
    public CreateContentJson create( final CreateContentParams params )
    {
        try
        {
            ContentData contentData = parseContentData( params.getQualifiedContentTypeName(), params.getContentData() );
            final List<Attachment> attachments = parseAttachments( params.getAttachments() );

            final CreateContent createContent = Commands.content().create().
                parentContentPath( params.getParentContentPath() ).
                name( params.getContentName() ).
                contentType( params.getQualifiedContentTypeName() ).
                contentData( contentData ).
                displayName( params.getDisplayName() ).
                owner( AccountKey.anonymous() ).
                temporary( params.getTemporary() ).
                attachments( attachments );

            final CreateContentResult createContentResult = client.execute( createContent );

            return new CreateContentJson( createContentResult );
        }
        catch ( CreateContentException | FileNotFoundException e )
        {
            return new CreateContentJson( e );
        }
    }

    @POST
    @Path("update")
    public UpdateContentJson update( final UpdateContentParams params )
    {
        try
        {
            final ContentData contentData = parseContentData( params.getQualifiedContentTypeName(), params.getContentData() );
            final List<Attachment> attachments = parseAttachments( params.getAttachments() );

            final UpdateContent updateContent = Commands.content().update().
                selector( params.getContentId() ).
                modifier( AccountKey.anonymous() ).
                attachments( attachments ).
                editor( ContentEditors.composite( ContentEditors.setContentData( contentData ),
                                                  ContentEditors.setContentDisplayName( params.getDisplayName() ) ) );

            final UpdateContentResult updateContentResult = client.execute( updateContent );

            if ( ( updateContentResult == null || updateContentResult == UpdateContentResult.SUCCESS ) &&
                !Strings.isNullOrEmpty( params.getContentName() ) )
            {
                // rename if the the update was successful or null (meaning nothing was edited) only
                client.execute( Commands.content().rename().contentId( params.getContentId() ).newName( params.getContentName() ) );
            }

            return new UpdateContentJson( updateContentResult );
        }
        catch ( UpdateContentException | ContentNotFoundException | RenameContentException | FileNotFoundException e )
        {
            return new UpdateContentJson( e );
        }
    }


    private ContentData parseContentData( QualifiedContentTypeName qualifiedContentTypeName, JsonNode contentData )
    {

        final GetContentTypes getContentTypes = Commands.contentType().get().
            qualifiedNames( QualifiedContentTypeNames.from( qualifiedContentTypeName ) ).
            mixinReferencesToFormItems( true );

        final ContentType contentType = client.execute( getContentTypes ).first();

        Preconditions.checkArgument( contentType != null, "ContentType [%s] not found", qualifiedContentTypeName );

        return new ContentDataParser( contentType ).parse( contentData );
    }

    private List<Attachment> parseAttachments( final List<AttachmentParams> attachmentParamsList )
        throws FileNotFoundException
    {
        List<Attachment> attachments = new ArrayList<>();
        if ( attachmentParamsList != null )
        {
            for ( AttachmentParams attachmentParams : attachmentParamsList )
            {
                attachments.add( createAttachment( attachmentParams ) );
            }
        }
        return attachments;
    }

    private Attachment createAttachment( final AttachmentParams attachmentParams )
        throws FileNotFoundException
    {
        final UploadItem uploadItem = uploadService.getItem( attachmentParams.getUploadId() );

        Preconditions.checkArgument( uploadItem != null, "Uploaded file not found: [%s]", attachmentParams.getUploadId() );

        final Binary binary = Binary.from( new FileInputStream( uploadItem.getFile() ) );

        return Attachment.newAttachment().
            name( attachmentParams.getAttachmentName() ).
            mimeType( uploadItem.getMimeType() ).
            binary( binary ).
            build();
    }
}
