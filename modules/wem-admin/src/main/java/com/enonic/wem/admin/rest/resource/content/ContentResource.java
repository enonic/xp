package com.enonic.wem.admin.rest.resource.content;

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
import com.enonic.wem.admin.rest.resource.Result;
import com.enonic.wem.admin.rest.resource.content.json.AttachmentJson;
import com.enonic.wem.admin.rest.resource.content.json.ContentFindParams;
import com.enonic.wem.admin.rest.resource.content.json.ContentNameJson;
import com.enonic.wem.admin.rest.resource.content.json.CreateContentJson;
import com.enonic.wem.admin.rest.resource.content.json.DeleteContentParams;
import com.enonic.wem.admin.rest.resource.content.json.DeleteContentResultJson;
import com.enonic.wem.admin.rest.resource.content.json.FacetedContentIdListJson;
import com.enonic.wem.admin.rest.resource.content.json.FacetedContentListJson;
import com.enonic.wem.admin.rest.resource.content.json.FacetedContentSummaryListJson;
import com.enonic.wem.admin.rest.resource.content.json.UpdateContentParams;
import com.enonic.wem.api.account.AccountKey;
import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.command.content.DeleteContent;
import com.enonic.wem.api.command.content.FindContent;
import com.enonic.wem.api.command.content.GenerateContentName;
import com.enonic.wem.api.command.content.GetChildContent;
import com.enonic.wem.api.command.content.GetContentByIds;
import com.enonic.wem.api.command.content.GetContentVersion;
import com.enonic.wem.api.command.content.UpdateContent;
import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.ContentIds;
import com.enonic.wem.api.content.ContentPath;
import com.enonic.wem.api.content.ContentPaths;
import com.enonic.wem.api.content.Contents;
import com.enonic.wem.api.content.attachment.Attachment;
import com.enonic.wem.api.content.data.ContentData;
import com.enonic.wem.api.content.editor.ContentEditor;
import com.enonic.wem.api.content.query.ContentIndexQuery;
import com.enonic.wem.api.content.query.ContentIndexQueryResult;
import com.enonic.wem.api.content.versioning.ContentVersionId;
import com.enonic.wem.api.schema.content.ContentTypeNames;

import static com.enonic.wem.api.content.Content.editContent;

@SuppressWarnings("UnusedDeclaration")
@Path("content")
@Produces(MediaType.APPLICATION_JSON)
public class ContentResource
    extends AbstractResource
{
    private final String EXPAND_FULL = "full";

    private final String EXPAND_SUMMARY = "summary";

    private final String EXPAND_NONE = "none";

    @GET
    public Result getById( @QueryParam("id") final String idParam, @QueryParam("version") final Long versionParam,
                           @QueryParam("expand") @DefaultValue(EXPAND_FULL) final String expandParam )
    {
        try
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
                return Result.result( new ContentIdJson( id ) );
            }
            else if ( EXPAND_SUMMARY.equalsIgnoreCase( expandParam ) )
            {
                return Result.result( new ContentSummaryJson( content ) );
            }
            else
            {
                return Result.result( new ContentJson( content ) );
            }
        }
        catch ( Exception e )
        {
            return Result.exception( e );
        }
    }

    @GET
    @Path("bypath")
    public Result getByPath( @QueryParam("path") final String pathParam, @QueryParam("version") final Long versionParam,
                             @QueryParam("expand") @DefaultValue(EXPAND_FULL) final String expandParam )
    {
        try
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
                return Result.result( new ContentIdJson( content.getId() ) );
            }
            else if ( EXPAND_SUMMARY.equalsIgnoreCase( expandParam ) )
            {
                return Result.result( new ContentSummaryJson( content ) );
            }
            else
            {
                return Result.result( new ContentJson( content ) );
            }
        }
        catch ( Exception e )
        {
            return Result.exception( e );
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
    public Result listByPath( @QueryParam("parentPath") final String parentPathParam,
                              @QueryParam("expand") @DefaultValue(EXPAND_SUMMARY) final String expandParam )
    {
        try
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
                return Result.result( new ContentIdListJson( contents ) );
            }
            else if ( EXPAND_FULL.equalsIgnoreCase( expandParam ) )
            {
                return Result.result( new ContentListJson( contents ) );
            }
            else
            {
                return Result.result( new ContentSummaryListJson( contents ) );
            }
        }
        catch ( Exception e )
        {
            return Result.exception( e );
        }
    }

    @POST
    @Path("find")
    @Consumes(MediaType.APPLICATION_JSON)
    public Result find( final ContentFindParams params )
    {
        try
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

            final GetContentByIds getContents =
                Commands.content().get().byIds( ContentIds.from( contentIndexQueryResult.getContentIds() ) );
            final Contents contents = this.client.execute( getContents );

            if ( EXPAND_FULL.equalsIgnoreCase( params.getExpand() ) )
            {
                return Result.result(
                    new FacetedContentListJson( contents, params.isIncludeFacets() ? contentIndexQueryResult.getFacets() : null ) );
            }
            else if ( EXPAND_SUMMARY.equalsIgnoreCase( params.getExpand() ) )
            {
                return Result.result(
                    new FacetedContentSummaryListJson( contents, params.isIncludeFacets() ? contentIndexQueryResult.getFacets() : null ) );
            }
            else
            {
                return Result.result(
                    new FacetedContentIdListJson( contents, params.isIncludeFacets() ? contentIndexQueryResult.getFacets() : null ) );
            }
        }
        catch ( Exception e )
        {
            return Result.exception( e );
        }
    }

    @GET
    @Path("generateName")
    public Result generateName( @QueryParam("displayName") final String displayNameParam )
    {
        try
        {
            final GenerateContentName generateContentName = Commands.content().generateContentName().displayName( displayNameParam );

            final String generatedContentName = client.execute( generateContentName );

            return Result.result( new ContentNameJson( generatedContentName ) );
        }
        catch ( Exception e )
        {
            return Result.exception( e );
        }
    }

    /* TODO: Re-introduce or remove when doing task: CMS-2256 Validate occurrences before save when user clicks Publish
    @POST
    @Path("validate")
    public Result validate( final ValidateContentParams params )
    {
        try
        {
            final ContentTypeName contentTypeName = ContentTypeName.from( params.getContentTypeName() );

            GetContentTypes getContentType =
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
    public Result delete( final DeleteContentParams params )
    {
        try
        {
            final ContentPaths contentsToDelete = ContentPaths.from( params.getContentPaths() );

            final DeleteContentResultJson jsonResult = new DeleteContentResultJson();

            for ( final ContentPath contentToDelete : contentsToDelete )
            {
                final DeleteContent deleteContent = Commands.content().delete();
                deleteContent.deleter( AccountKey.anonymous() );
                deleteContent.contentPath( contentToDelete );
                jsonResult.addResult( contentToDelete, client.execute( deleteContent ) );
            }

            return Result.result( jsonResult );
        }
        catch ( Exception e )
        {
            return Result.exception( e );
        }
    }

    @POST
    @Path("create")
    public ContentJson create( final CreateContentJson params )
    {
        final Content persistedContent = client.execute( params.getCreateContent() );
        return new ContentJson( persistedContent );
    }

    @POST
    @Path("update")
    public ContentJson update( final UpdateContentParams params )
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

        final Content updatedContent = client.execute( updateContent );
        if ( params.getContentName().equals( updatedContent.getName() ) )
        {
            return new ContentJson( updatedContent );
        }

        final Content renamedContent = client.execute( Commands.content().rename().
            contentId( params.getContentId() ).
            newName( params.getContentName() ) );
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

}
