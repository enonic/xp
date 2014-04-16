package com.enonic.wem.admin.rest.resource.content;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.apache.commons.lang.StringUtils;

import com.sun.jersey.api.NotFoundException;

import com.enonic.wem.admin.json.content.AbstractContentListJson;
import com.enonic.wem.admin.json.content.ContentIdJson;
import com.enonic.wem.admin.json.content.ContentIdListJson;
import com.enonic.wem.admin.json.content.ContentJson;
import com.enonic.wem.admin.json.content.ContentListJson;
import com.enonic.wem.admin.json.content.ContentSummaryJson;
import com.enonic.wem.admin.json.content.ContentSummaryListJson;
import com.enonic.wem.admin.json.content.attachment.AttachmentJson;
import com.enonic.wem.admin.rest.resource.content.json.AbstractContentQueryResultJson;
import com.enonic.wem.admin.rest.resource.content.json.ContentNameJson;
import com.enonic.wem.admin.rest.resource.content.json.ContentQueryJson;
import com.enonic.wem.admin.rest.resource.content.json.CreateContentJson;
import com.enonic.wem.admin.rest.resource.content.json.DeleteContentJson;
import com.enonic.wem.admin.rest.resource.content.json.DeleteContentResultJson;
import com.enonic.wem.admin.rest.resource.content.json.UpdateAttachmentsJson;
import com.enonic.wem.admin.rest.resource.content.json.UpdateContentJson;
import com.enonic.wem.api.account.AccountKey;
import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.ContentIds;
import com.enonic.wem.api.content.ContentNotFoundException;
import com.enonic.wem.api.content.ContentPath;
import com.enonic.wem.api.content.ContentPaths;
import com.enonic.wem.api.content.ContentService;
import com.enonic.wem.api.content.Contents;
import com.enonic.wem.api.content.DeleteContentParams;
import com.enonic.wem.api.content.DeleteContentResult;
import com.enonic.wem.api.content.GetContentByIdsParams;
import com.enonic.wem.api.content.RenameContentParams;
import com.enonic.wem.api.content.UnableToDeleteContentException;
import com.enonic.wem.api.content.UpdateContentParams;
import com.enonic.wem.api.content.attachment.Attachment;
import com.enonic.wem.api.content.data.ContentData;
import com.enonic.wem.api.content.editor.ContentEditor;
import com.enonic.wem.api.content.query.ContentQueryResult;
import com.enonic.wem.core.data.json.DataJson;

import static com.enonic.wem.api.content.Content.editContent;

@SuppressWarnings("UnusedDeclaration")
@Path("content")
@Produces(MediaType.APPLICATION_JSON)
public class ContentResource
{
    private final String EXPAND_FULL = "full";

    private final String EXPAND_SUMMARY = "summary";

    private final String EXPAND_NONE = "none";

    private ContentService contentService;

    @GET
    public ContentIdJson getById( @QueryParam("id") final String idParam,
                                  @QueryParam("expand") @DefaultValue(EXPAND_FULL) final String expandParam )
    {

        final ContentId id = ContentId.from( idParam );
        final Content content = contentService.getById( id );

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
    public ContentIdJson getByPath( @QueryParam("path") final String pathParam,
                                    @QueryParam("expand") @DefaultValue(EXPAND_FULL) final String expandParam )
    {
        final Content content = contentService.getByPath( ContentPath.from( pathParam ) );

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
        final Contents contents;
        if ( StringUtils.isEmpty( parentIdParam ) )
        {
            contents = contentService.getRoots();
        }
        else
        {
            final GetContentByIdsParams params = new GetContentByIdsParams( ContentIds.from( parentIdParam ) );
            final Contents parentContents = contentService.getByIds( params );

            if ( parentContents.isNotEmpty() )
            {
                contents = contentService.getChildren( parentContents.first().getPath() );
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
            contents = contentService.getRoots();
        }
        else
        {
            contents = contentService.getChildren( ContentPath.from( parentPathParam ) );
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
    @Path("query")
    @Consumes(MediaType.APPLICATION_JSON)
    public AbstractContentQueryResultJson query( final ContentQueryJson contentQueryJson )
    {
        final ContentQueryResult contentQueryResult = contentService.find( contentQueryJson.getContentQuery() );

        final boolean getChildrenIds = !Expand.NONE.matches( contentQueryJson.getExpand() );
        final GetContentByIdsParams params = new GetContentByIdsParams( ContentIds.from( contentQueryResult.getContentIds() ) ).
            setGetChildrenIds( getChildrenIds );

        final Contents contents = contentService.getByIds( params );

        return ContentQueryResultJsonFactory.create( contentQueryResult, contents, contentQueryJson.getExpand() );
    }

    @GET
    @Path("generateName")
    public ContentNameJson generateName( @QueryParam("displayName") final String displayNameParam )
    {
        final String generatedContentName = contentService.generateContentName( displayNameParam );

        return new ContentNameJson( generatedContentName );
    }

    /* TODO: Re-introduce or remove when doing task: CMS-2256 Validate occurrences before save when user clicks Publish
    @POST
    @Path("validate")
    public Result validate( final ValidateContentParams params )
    {
        try
        {
            final ContentTypeName contentTypeName = ContentTypeName.from( params.getContentTypeName() );

            GetContentTypesParams getContentType =
                Commands.contentType().get().byNames().contentTypeNames( ContentTypeNames.from( contentTypeName ) );
            final ContentType contentType = client.execute( getContentType ).first();

            final ContentData contentData = new ContentDataParser( contentType ).parse( params.getContentData() );

            final DataValidationErrors validationErrors =
                client.execute( Commands.content().validate().contentData( contentData ).contentType( contentTypeName ) );

            return Result.result( new ValidateContentJson( validationErrors ) );
        }
        catch ( Exception e )
        {
            return Result.exception( e );
        }
    }*/

    @POST
    @Path("delete")
    public DeleteContentResultJson delete( final DeleteContentJson json )
    {
        final ContentPaths contentsToDelete = ContentPaths.from( json.getContentPaths() );

        final DeleteContentResultJson jsonResult = new DeleteContentResultJson();

        for ( final ContentPath contentToDelete : contentsToDelete )
        {
            final DeleteContentParams deleteContent = new DeleteContentParams();
            deleteContent.deleter( AccountKey.anonymous() );
            deleteContent.contentPath( contentToDelete );

            try
            {
                final DeleteContentResult deleteResult = contentService.delete( deleteContent );
                jsonResult.addSuccess( contentToDelete );
            }
            catch ( ContentNotFoundException | UnableToDeleteContentException e )
            {
                jsonResult.addFailure( deleteContent.getContentPath(), e.getMessage() );
            }
        }

        return jsonResult;
    }

    @POST
    @Path("create")
    public ContentJson create( final CreateContentJson params )
    {
        final Content persistedContent = contentService.create( params.getCreateContent() );
        return new ContentJson( persistedContent );
    }

    @POST
    @Path("update")
    public ContentJson update( final UpdateContentJson json )
    {
        final ContentData contentData = parseContentData( json.getContentData() );

        final UpdateAttachmentsJson updateAttachments = json.getUpdateAttachments();

        final UpdateContentParams updateParams = new UpdateContentParams().
            contentId( json.getContentId() ).
            modifier( AccountKey.anonymous() ).
            updateAttachments( json.getUpdateAttachments() != null ? json.getUpdateAttachments().getUpdateAttachments() : null ).
            editor( new ContentEditor()
            {
                @Override
                public Content.EditBuilder edit( final Content toBeEdited )
                {
                    Content.EditBuilder editContentBuilder = editContent( toBeEdited ).
                        form( json.getForm().getForm() ).
                        contentData( contentData ).
                        draft( json.isDraft() ).
                        displayName( json.getDisplayName() );
                    if ( json.getThumbnail() != null )
                    {
                        editContentBuilder = editContentBuilder.thumbnail( json.getThumbnail().getThumbnail() );
                    }
                    return editContentBuilder;
                }
            } );

        final Content updatedContent = contentService.update( updateParams );
        if ( json.getContentName().equals( updatedContent.getName() ) )
        {
            return new ContentJson( updatedContent );
        }

        final RenameContentParams renameParams = new RenameContentParams().
            contentId( json.getContentId() ).
            newName( json.getContentName() );
        final Content renamedContent = contentService.rename( renameParams );

        return new ContentJson( renamedContent );
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
    {
        List<Attachment> attachments = new ArrayList<>();
        if ( attachmentJsonList != null )
        {
            for ( AttachmentJson attachmentJson : attachmentJsonList )
            {
                attachments.add( attachmentJson.getAttachment() );
            }
        }
        return attachments;
    }

    @Inject
    public void setContentService( final ContentService contentService )
    {
        this.contentService = contentService;
    }
}
