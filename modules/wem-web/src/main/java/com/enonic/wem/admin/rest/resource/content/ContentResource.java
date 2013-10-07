package com.enonic.wem.admin.rest.resource.content;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.apache.commons.lang.StringUtils;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.sun.jersey.api.NotFoundException;

import com.enonic.wem.admin.json.content.AbstractContentListJson;
import com.enonic.wem.admin.json.content.ContentIdJson;
import com.enonic.wem.admin.json.content.ContentIdListJson;
import com.enonic.wem.admin.json.content.ContentJson;
import com.enonic.wem.admin.json.content.ContentListJson;
import com.enonic.wem.admin.json.content.ContentSummaryJson;
import com.enonic.wem.admin.json.content.ContentSummaryListJson;
import com.enonic.wem.admin.rest.resource.AbstractResource;
import com.enonic.wem.admin.rest.resource.content.json.AbstractFacetedContentListJson;
import com.enonic.wem.admin.rest.resource.content.json.AttachmentParams;
import com.enonic.wem.admin.rest.resource.content.json.ContentFindParams;
import com.enonic.wem.admin.rest.resource.content.json.ContentNameJson;
import com.enonic.wem.admin.rest.resource.content.json.CreateContentJson;
import com.enonic.wem.admin.rest.resource.content.json.CreateContentParams;
import com.enonic.wem.admin.rest.resource.content.json.DeleteContentJson;
import com.enonic.wem.admin.rest.resource.content.json.DeleteContentParams;
import com.enonic.wem.admin.rest.resource.content.json.FacetedContentIdListJson;
import com.enonic.wem.admin.rest.resource.content.json.FacetedContentListJson;
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
import com.enonic.wem.api.command.content.FindContent;
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
import com.enonic.wem.api.content.ContentSelector;
import com.enonic.wem.api.content.ContentSelectors;
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
import com.enonic.wem.core.content.serializer.ContentDataJsonSerializer;

@Path("content")
@Produces(MediaType.APPLICATION_JSON)
public class ContentResource
    extends AbstractResource
{
    private UploadService uploadService;

    private final String EXPAND_FULL = "full";

    private final String EXPAND_SUMMARY = "summary";

    private final String EXPAND_NONE = "none";

    private ContentDataJsonSerializer contentDataJsonSerializer = new ContentDataJsonSerializer();

    @GET
    public ContentIdJson getById( @QueryParam("id") final String idParam, @QueryParam("version") final Long versionParam,
                                  @QueryParam("expand") @DefaultValue(EXPAND_FULL) final String expandParam )
    {
        final ContentId id = ContentId.from( idParam );
        final Content content;
        if ( versionParam == null )
        {
            content = doGetContent( id );
        }
        else
        {
            content = doGetVersionOfContent( id, ContentVersionId.of( versionParam ) );
        }

        if ( content == null )
        {
            throw new NotFoundException( String.format( "Content [%s] was not found", idParam ) );
        }
        else if ( EXPAND_NONE.equalsIgnoreCase( expandParam ) )
        {
            return new ContentIdJson( id );
        }
        else if ( EXPAND_SUMMARY.equalsIgnoreCase( expandParam ) )
        {
            return new ContentSummaryJson( content );
        }
        else
        {
            return new ContentJson( content );
        }
    }

    private Content doGetContent( final ContentSelector contentSelector )
    {
        final ContentSelectors contentSelectors;
        if ( contentSelector instanceof ContentId )
        {
            contentSelectors = ContentIds.from( (ContentId) contentSelector );
        }
        else
        {
            contentSelectors = ContentPaths.from( (ContentPath) contentSelector );
        }
        final GetContents getContents = Commands.content().get().selectors( contentSelectors );
        final Contents contents = client.execute( getContents );
        return contents.isNotEmpty() ? contents.first() : null;
    }

    private Content doGetVersionOfContent( final ContentSelector contentSelector, final ContentVersionId versionId )
    {
        final GetContentVersion getContentVersion = Commands.content().getVersion().
            selector( contentSelector ).
            version( versionId );

        return client.execute( getContentVersion );
    }

    @GET
    @Path("bypath")
    public ContentIdJson getByPath( @QueryParam("path") final String pathParam, @QueryParam("version") final Long versionParam,
                                    @QueryParam("expand") @DefaultValue(EXPAND_FULL) final String expandParam )
    {
        final Content content;
        final ContentPath path = ContentPath.from( pathParam );
        if ( versionParam == null )
        {
            content = doGetContent( path );
        }
        else
        {
            content = doGetVersionOfContent( ContentPath.from( pathParam ), ContentVersionId.of( versionParam ) );
        }

        if ( content == null )
        {
            throw new NotFoundException( String.format( "Content [%s] was not found", pathParam ) );
        }
        else if ( EXPAND_NONE.equalsIgnoreCase( expandParam ) )
        {
            return new ContentIdJson( content.getId() );
        }
        else if ( EXPAND_SUMMARY.equalsIgnoreCase( expandParam ) )
        {
            return new ContentSummaryJson( content );
        }
        else
        {
            return new ContentJson( content );
        }
    }

    @GET
    @Path("list")
    public AbstractContentListJson listById( @QueryParam("parentId") final String parentIdParam,
                                             @QueryParam("expand") @DefaultValue(EXPAND_SUMMARY) final String expandParam )
    {
        Contents contents;
        if ( StringUtils.isEmpty( parentIdParam ) )
        {
            contents = client.execute( Commands.content().getRoots() );
        }
        else
        {
            final GetContents getContent = Commands.content().get().selectors( ContentIds.from( parentIdParam ) );
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

        if ( EXPAND_NONE.equalsIgnoreCase( expandParam ) )
        {
            return new ContentIdListJson( contents );
        }
        else if ( EXPAND_FULL.equalsIgnoreCase( expandParam ) )
        {
            return new ContentListJson( contents );
        }
        else
        {
            return new ContentSummaryListJson( contents );
        }
    }

    @GET
    @Path("list/bypath")
    public AbstractContentListJson listByPath( @QueryParam("parentPath") final String parentPathParam,
                                               @QueryParam("expand") @DefaultValue(EXPAND_SUMMARY) final String expandParam )
    {
        final Contents contents;
        if ( StringUtils.isEmpty( parentPathParam ) )
        {
            contents = client.execute( Commands.content().getRoots() );
        }
        else
        {
            final GetChildContent getChildContent = Commands.content().getChildren().parentPath( ContentPath.from( parentPathParam ) );
            contents = client.execute( getChildContent );
        }

        if ( EXPAND_NONE.equalsIgnoreCase( expandParam ) )
        {
            return new ContentIdListJson( contents );
        }
        else if ( EXPAND_FULL.equalsIgnoreCase( expandParam ) )
        {
            return new ContentListJson( contents );
        }
        else
        {
            return new ContentSummaryListJson( contents );
        }
    }

    @POST
    @Path("find")
    @Consumes(MediaType.APPLICATION_JSON)
    public AbstractFacetedContentListJson find( final ContentFindParams params )
    {
        final ContentIndexQuery contentIndexQuery = new ContentIndexQuery();
        contentIndexQuery.setFullTextSearchString( params.getFulltext() );
        contentIndexQuery.setIncludeFacets( params.isIncludeFacets() );
        contentIndexQuery.setContentTypeNames( QualifiedContentTypeNames.from( params.getContentTypes() ) );
        contentIndexQuery.setSpaceNames( SpaceNames.from( params.getSpaces() ) );

        Set<ContentFindParams.Range> ranges = params.getRanges();
        if ( ranges != null && ranges.size() > 0 )
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

        final FindContent findContent = Commands.content().find().query( contentIndexQuery );
        final ContentIndexQueryResult contentIndexQueryResult = this.client.execute( findContent );

        final GetContents getContents = Commands.content().get().selectors( ContentIds.from( contentIndexQueryResult.getContentIds() ) );
        final Contents contents = this.client.execute( getContents );

        if ( EXPAND_FULL.equalsIgnoreCase( params.getExpand() ) )
        {
            return new FacetedContentListJson( contents, params.isIncludeFacets() ? contentIndexQueryResult.getFacets() : null );
        }
        else if ( EXPAND_SUMMARY.equalsIgnoreCase( params.getExpand() ) )
        {
            return new FacetedContentSummaryListJson( contents, params.isIncludeFacets() ? contentIndexQueryResult.getFacets() : null );
        }
        else
        {
            return new FacetedContentIdListJson( contents, params.isIncludeFacets() ? contentIndexQueryResult.getFacets() : null );
        }
    }

    @GET
    @Path("generateName")
    public ContentNameJson generateName( @QueryParam("displayName") final String displayNameParam )
    {
        final GenerateContentName generateContentName = Commands.content().generateContentName().displayName( displayNameParam );

        final String generatedContentName = client.execute( generateContentName );

        return new ContentNameJson( generatedContentName );
    }

    @POST
    @Path("validate")
    public ValidateContentJson validate( final ValidateContentParams params )
    {
        final QualifiedContentTypeName qualifiedContentTypeName = QualifiedContentTypeName.from( params.getQualifiedContentTypeName() );

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
        return contentDataJsonSerializer.parse( contentData );
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
