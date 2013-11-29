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

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.inject.Inject;
import com.sun.jersey.api.NotFoundException;

import com.enonic.wem.admin.json.content.AbstractContentListJson;
import com.enonic.wem.admin.json.content.ContentIdJson;
import com.enonic.wem.admin.json.content.ContentIdListJson;
import com.enonic.wem.admin.json.content.ContentJson;
import com.enonic.wem.admin.json.content.ContentListJson;
import com.enonic.wem.admin.json.content.ContentSummaryJson;
import com.enonic.wem.admin.json.content.ContentSummaryListJson;
import com.enonic.wem.admin.json.data.DataJson;
import com.enonic.wem.admin.rest.resource.AbstractResource;
import com.enonic.wem.admin.rest.resource.content.json.AbstractFacetedContentListJson;
import com.enonic.wem.admin.rest.resource.content.json.AttachmentJson;
import com.enonic.wem.admin.rest.resource.content.json.ContentFindParams;
import com.enonic.wem.admin.rest.resource.content.json.ContentNameJson;
import com.enonic.wem.admin.rest.resource.content.json.CreateContentJson;
import com.enonic.wem.admin.rest.resource.content.json.CreateOrUpdateContentJsonResult;
import com.enonic.wem.admin.rest.resource.content.json.DeleteContentJson;
import com.enonic.wem.admin.rest.resource.content.json.DeleteContentParams;
import com.enonic.wem.admin.rest.resource.content.json.FacetedContentIdListJson;
import com.enonic.wem.admin.rest.resource.content.json.FacetedContentListJson;
import com.enonic.wem.admin.rest.resource.content.json.FacetedContentSummaryListJson;
import com.enonic.wem.admin.rest.resource.content.json.UpdateContentParams;
import com.enonic.wem.admin.rest.resource.content.json.ValidateContentJson;
import com.enonic.wem.admin.rest.resource.content.json.ValidateContentParams;
import com.enonic.wem.admin.rest.service.upload.UploadItem;
import com.enonic.wem.admin.rest.service.upload.UploadService;
import com.enonic.wem.api.account.AccountKey;
import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.command.content.CreateContent;
import com.enonic.wem.api.command.content.CreateContentResult;
import com.enonic.wem.api.command.content.DeleteContent;
import com.enonic.wem.api.command.content.FindContent;
import com.enonic.wem.api.command.content.GenerateContentName;
import com.enonic.wem.api.command.content.GetChildContent;
import com.enonic.wem.api.command.content.GetContentByIds;
import com.enonic.wem.api.command.content.GetContentVersion;
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
import com.enonic.wem.api.content.editor.ContentEditor;
import com.enonic.wem.api.content.query.ContentIndexQuery;
import com.enonic.wem.api.content.query.ContentIndexQueryResult;
import com.enonic.wem.api.content.versioning.ContentVersionId;
import com.enonic.wem.api.schema.content.ContentType;
import com.enonic.wem.api.schema.content.ContentTypeName;
import com.enonic.wem.api.schema.content.ContentTypeNames;
import com.enonic.wem.api.schema.content.validator.DataValidationErrors;

import static com.enonic.wem.api.content.Content.editContent;

@SuppressWarnings("UnusedDeclaration")
@Path("content")
@Produces(MediaType.APPLICATION_JSON)
public class ContentResource
    extends AbstractResource
{
    private UploadService uploadService;

    private final String EXPAND_FULL = "full";

    private final String EXPAND_SUMMARY = "summary";

    private final String EXPAND_NONE = "none";

    @GET
    public ContentIdJson getById( @QueryParam("id") final String idParam, @QueryParam("version") final Long versionParam,
                                  @QueryParam("expand") @DefaultValue(EXPAND_FULL) final String expandParam )
    {
        final ContentId id = ContentId.from( idParam );
        final Content content;
        if ( versionParam == null )
        {
            content = client.execute( Commands.content().get().byId( id ) );
        }
        else
        {
            final GetContentVersion getContentVersion = Commands.content().getVersion().
                contentId( id ).
                version( ContentVersionId.of( versionParam ) );

            content = client.execute( getContentVersion );
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

    @GET
    @Path("bypath")
    public ContentIdJson getByPath( @QueryParam("path") final String pathParam, @QueryParam("version") final Long versionParam,
                                    @QueryParam("expand") @DefaultValue(EXPAND_FULL) final String expandParam )
    {
        final Content content;
        final ContentPath path = ContentPath.from( pathParam );
        if ( versionParam == null )
        {
            content = client.execute( Commands.content().get().byPath( path ) );
        }
        else
        {
            final GetContentVersion getContentVersion = Commands.content().getVersion().
                contentPath( path ).
                version( ContentVersionId.of( versionParam ) );

            content = client.execute( getContentVersion );
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
            final Contents parentContents = client.execute( Commands.content().get().byIds( ContentIds.from( parentIdParam ) ) );

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
        contentIndexQuery.setContentTypeNames( ContentTypeNames.from( params.getContentTypes() ) );

        if ( params.getCount() >= 0 )
        {
            contentIndexQuery.setSize( params.getCount() );
        }

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

        final GetContentByIds getContents = Commands.content().get().byIds( ContentIds.from( contentIndexQueryResult.getContentIds() ) );
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
        final ContentTypeName qualifiedContentTypeName = ContentTypeName.from( params.getQualifiedContentTypeName() );

        GetContentTypes getContentType =
            Commands.contentType().get().byNames().contentTypeNames( ContentTypeNames.from( qualifiedContentTypeName ) );
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
            deleteContent.contentPath( contentToDelete );
            jsonResult.addResult( contentToDelete, client.execute( deleteContent ) );
        }

        return jsonResult;
    }

    @POST
    @Path("create")
    public CreateOrUpdateContentJsonResult create( final CreateContentJson params )
    {
        try
        {
            final CreateContent createContent = params.getCreateContent();
            final List<Attachment> attachments = parseAttachments( params.getAttachments() );
            createContent.attachments( attachments );

            final CreateContentResult createContentResult = client.execute( createContent );

            final Content content = client.execute( Commands.content().get().byId( createContentResult.getContentId() ) );

            return CreateOrUpdateContentJsonResult.result( new ContentJson( content ) );
        }
        catch ( CreateContentException | FileNotFoundException e )
        {
            return CreateOrUpdateContentJsonResult.error( e.getMessage() );
        }
    }

    @POST
    @Path("update")
    public CreateOrUpdateContentJsonResult update( final UpdateContentParams params )
    {
        try
        {
            final ContentData contentData = parseContentData( params.getContentData() );
            final List<Attachment> attachments = parseAttachments( params.getAttachments() );

            final UpdateContent updateContent = Commands.content().update().
                contentId( params.getContentId() ).
                modifier( AccountKey.anonymous() ).
                attachments( attachments ).
                editor( new ContentEditor()
                {
                    @Override
                    public Content.EditBuilder edit( final Content toBeEdited )
                    {
                        return editContent( toBeEdited ).
                            form( params.getForm().getForm() ).
                            contentData( contentData ).
                            displayName( params.getDisplayName() );
                    }
                } );

            final UpdateContentResult updateContentResult = client.execute( updateContent );

            if ( ( updateContentResult == null || updateContentResult == UpdateContentResult.SUCCESS ) &&
                !Strings.isNullOrEmpty( params.getContentName() ) )
            {
                // rename if the the update was successful or null (meaning nothing was edited) only
                client.execute( Commands.content().rename().contentId( params.getContentId() ).newName( params.getContentName() ) );
            }

            if ( updateContentResult == UpdateContentResult.SUCCESS || updateContentResult == null )
            {
                final Content content = client.execute( Commands.content().get().byId( params.getContentId() ) );
                return CreateOrUpdateContentJsonResult.result( new ContentJson( content ) );
            }
            else
            {
                return CreateOrUpdateContentJsonResult.error( updateContentResult.getMessage() );
            }

        }
        catch ( UpdateContentException | ContentNotFoundException | RenameContentException | FileNotFoundException e )
        {
            return CreateOrUpdateContentJsonResult.error( e.getMessage() );
        }
    }


    private ContentData parseContentData( final List<DataJson> dataJsonList )
    {
        final ContentData contentData = new ContentData();
        for ( DataJson dataJson : dataJsonList )
        {
            contentData.add( dataJson.getData() );
        }
        return contentData;
    }

    private List<Attachment> parseAttachments( final List<AttachmentJson> attachmentJsonList )
        throws FileNotFoundException
    {
        List<Attachment> attachments = new ArrayList<>();
        if ( attachmentJsonList != null )
        {
            for ( AttachmentJson attachmentJson : attachmentJsonList )
            {
                attachments.add( createAttachment( attachmentJson ) );
            }
        }
        return attachments;
    }

    private Attachment createAttachment( final AttachmentJson attachmentJson )
        throws FileNotFoundException
    {
        final UploadItem uploadItem = uploadService.getItem( attachmentJson.getUploadId() );

        Preconditions.checkArgument( uploadItem != null, "Uploaded file not found: [%s]", attachmentJson.getUploadId() );

        final Binary binary = Binary.from( new FileInputStream( uploadItem.getFile() ) );

        uploadService.removeItem( attachmentJson.getUploadId() );

        return Attachment.newAttachment().
            name( attachmentJson.getAttachmentName() ).
            mimeType( uploadItem.getMimeType() ).
            binary( binary ).
            build();
    }

    @Inject
    public void setUploadService( final UploadService uploadService )
    {
        this.uploadService = uploadService;
    }
}
