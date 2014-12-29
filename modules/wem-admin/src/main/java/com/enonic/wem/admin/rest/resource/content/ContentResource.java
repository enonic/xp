package com.enonic.wem.admin.rest.resource.content;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.apache.commons.fileupload.disk.DiskFileItem;
import org.apache.commons.lang.StringUtils;

import com.google.common.collect.Lists;
import com.google.common.io.Files;

import com.enonic.wem.admin.json.content.AbstractContentListJson;
import com.enonic.wem.admin.json.content.CompareContentResultsJson;
import com.enonic.wem.admin.json.content.ContentIdJson;
import com.enonic.wem.admin.json.content.ContentIdListJson;
import com.enonic.wem.admin.json.content.ContentJson;
import com.enonic.wem.admin.json.content.ContentListJson;
import com.enonic.wem.admin.json.content.ContentSummaryJson;
import com.enonic.wem.admin.json.content.ContentSummaryListJson;
import com.enonic.wem.admin.json.content.GetActiveContentVersionsResultJson;
import com.enonic.wem.admin.json.content.GetContentVersionsResultJson;
import com.enonic.wem.admin.json.content.ReorderChildrenResultJson;
import com.enonic.wem.admin.json.content.attachment.AttachmentJson;
import com.enonic.wem.admin.rest.exception.NotFoundWebException;
import com.enonic.wem.admin.rest.multipart.MultipartForm;
import com.enonic.wem.admin.rest.resource.ResourceConstants;
import com.enonic.wem.admin.rest.resource.content.json.AbstractContentQueryResultJson;
import com.enonic.wem.admin.rest.resource.content.json.ApplyContentPermissionsJson;
import com.enonic.wem.admin.rest.resource.content.json.CompareContentsJson;
import com.enonic.wem.admin.rest.resource.content.json.ContentNameJson;
import com.enonic.wem.admin.rest.resource.content.json.ContentQueryJson;
import com.enonic.wem.admin.rest.resource.content.json.CreateContentJson;
import com.enonic.wem.admin.rest.resource.content.json.DeleteContentJson;
import com.enonic.wem.admin.rest.resource.content.json.DeleteContentResultJson;
import com.enonic.wem.admin.rest.resource.content.json.DuplicateContentJson;
import com.enonic.wem.admin.rest.resource.content.json.GetContentVersionsJson;
import com.enonic.wem.admin.rest.resource.content.json.PublishContentJson;
import com.enonic.wem.admin.rest.resource.content.json.ReorderChildJson;
import com.enonic.wem.admin.rest.resource.content.json.ReorderChildrenJson;
import com.enonic.wem.admin.rest.resource.content.json.SetChildOrderJson;
import com.enonic.wem.admin.rest.resource.content.json.UpdateContentJson;
import com.enonic.wem.api.content.ApplyContentPermissionsParams;
import com.enonic.wem.api.content.CompareContentResults;
import com.enonic.wem.api.content.CompareContentsParams;
import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentConstants;
import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.ContentIds;
import com.enonic.wem.api.content.ContentListMetaData;
import com.enonic.wem.api.content.ContentNotFoundException;
import com.enonic.wem.api.content.ContentPath;
import com.enonic.wem.api.content.ContentPaths;
import com.enonic.wem.api.content.ContentService;
import com.enonic.wem.api.content.CreateMediaParams;
import com.enonic.wem.api.content.DeleteContentParams;
import com.enonic.wem.api.content.DuplicateContentParams;
import com.enonic.wem.api.content.FindContentByParentParams;
import com.enonic.wem.api.content.FindContentByParentResult;
import com.enonic.wem.api.content.FindContentByQueryParams;
import com.enonic.wem.api.content.FindContentByQueryResult;
import com.enonic.wem.api.content.FindContentVersionsParams;
import com.enonic.wem.api.content.FindContentVersionsResult;
import com.enonic.wem.api.content.GetActiveContentVersionsParams;
import com.enonic.wem.api.content.GetActiveContentVersionsResult;
import com.enonic.wem.api.content.PushContentParams;
import com.enonic.wem.api.content.RenameContentParams;
import com.enonic.wem.api.content.ReorderChildContentsParams;
import com.enonic.wem.api.content.ReorderChildContentsResult;
import com.enonic.wem.api.content.ReorderChildParams;
import com.enonic.wem.api.content.SetContentChildOrderParams;
import com.enonic.wem.api.content.UnableToDeleteContentException;
import com.enonic.wem.api.content.UpdateContentParams;
import com.enonic.wem.api.content.UpdateMediaParams;
import com.enonic.wem.api.content.attachment.Attachment;
import com.enonic.wem.api.content.attachment.AttachmentNames;
import com.enonic.wem.api.content.attachment.CreateAttachment;
import com.enonic.wem.api.content.attachment.CreateAttachments;
import com.enonic.wem.api.context.ContextAccessor;
import com.enonic.wem.api.form.MixinReferencesToFormItemsTransformer;
import com.enonic.wem.api.index.ChildOrder;
import com.enonic.wem.api.schema.content.ContentTypeService;
import com.enonic.wem.api.schema.mixin.MixinService;
import com.enonic.wem.api.security.PrincipalKey;
import com.enonic.wem.api.security.SecurityService;
import com.enonic.wem.api.security.auth.AuthenticationInfo;
import com.enonic.wem.api.workspace.Workspaces;
import com.enonic.xp.web.jaxrs.JaxRsComponent;

@SuppressWarnings("UnusedDeclaration")
@Path(ResourceConstants.REST_ROOT + "content")
@Produces(MediaType.APPLICATION_JSON)
public final class ContentResource
    implements JaxRsComponent
{
    public static final String DEFAULT_SORT_FIELD = "modifiedTime";

    private static final String DEFAULT_FROM_PARAM = "0";

    private static final String DEFAULT_SIZE_PARAM = "500";

    private final static String EXPAND_FULL = "full";

    private final static String EXPAND_SUMMARY = "summary";

    private final static String EXPAND_NONE = "none";

    private ContentService contentService;

    private ContentTypeService contentTypeService;

    private MixinReferencesToFormItemsTransformer mixinReferencesToFormItemsTransformer;

    private ContentPrincipalsResolver principalsResolver;

    private SecurityService securityService;

    @POST
    @Path("create")
    public ContentJson create( final CreateContentJson params )
    {
        final Content persistedContent = contentService.create( params.getCreateContent() );
        return new ContentJson( persistedContent, newContentIconUrlResolver(), mixinReferencesToFormItemsTransformer, principalsResolver );
    }

    @POST
    @Path("createMedia")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public ContentJson createMedia( final MultipartForm form )
    {
        final Content persistedContent;
        final CreateMediaParams createMediaParams = new CreateMediaParams();
        createMediaParams.parent( ContentPath.from( form.get( "parent" ).getString() ) );
        createMediaParams.name( form.get( "name" ).getString() );

        final DiskFileItem mediaFile = (DiskFileItem) form.get( "file" );
        createMediaParams.mimeType( mediaFile.getContentType() );
        createMediaParams.byteSource( Files.asByteSource( mediaFile.getStoreLocation() ) );
        persistedContent = contentService.create( createMediaParams );

        return new ContentJson( persistedContent, newContentIconUrlResolver(), mixinReferencesToFormItemsTransformer, principalsResolver );
    }

    @POST
    @Path("updateMedia")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public ContentJson updateMedia( final MultipartForm form )
    {
        final Content persistedContent;
        final UpdateMediaParams params = new UpdateMediaParams();
        params.content( ContentId.from( form.get( "content" ).getString() ) );
        params.name( form.get( "name" ).getString() );

        final DiskFileItem mediaFile = (DiskFileItem) form.get( "file" );
        params.mimeType( mediaFile.getContentType() );
        params.byteSource( Files.asByteSource( mediaFile.getStoreLocation() ) );
        persistedContent = contentService.update( params );

        return new ContentJson( persistedContent, newContentIconUrlResolver(), mixinReferencesToFormItemsTransformer, principalsResolver );
    }

    @POST
    @Path("updateThumbnail")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public ContentJson updateThumbnail( final MultipartForm form )
    {
        final DiskFileItem mediaFile = (DiskFileItem) form.get( "file" );

        final CreateAttachment thumbnailAttachment = CreateAttachment.create().
            name( AttachmentNames.THUMBNAIL ).
            mimeType( mediaFile.getContentType() ).
            byteSource( Files.asByteSource( mediaFile.getStoreLocation() ) ).
            build();

        final UpdateContentParams params = new UpdateContentParams();
        params.contentId( ContentId.from( form.get( "id" ).getString() ) );
        params.createAttachments( CreateAttachments.from( thumbnailAttachment ) );

        final Content persistedContent = contentService.update( params );

        return new ContentJson( persistedContent, newContentIconUrlResolver(), mixinReferencesToFormItemsTransformer, principalsResolver );
    }

    @POST
    @Path("duplicate")
    public ContentJson duplicate( final DuplicateContentJson params )
    {
        final Content duplicatedContent = contentService.duplicate( new DuplicateContentParams( params.getContentId() ) );

        return new ContentJson( duplicatedContent, newContentIconUrlResolver(), mixinReferencesToFormItemsTransformer, principalsResolver );
    }

    @POST
    @Path("update")
    public ContentJson update( final UpdateContentJson json )
    {
        final UpdateContentParams updateParams = json.getUpdateContentParams();

        final Content updatedContent = contentService.update( updateParams );
        if ( json.getContentName().equals( updatedContent.getName() ) )
        {
            return new ContentJson( updatedContent, newContentIconUrlResolver(), mixinReferencesToFormItemsTransformer,
                                    principalsResolver );
        }

        final RenameContentParams renameParams = json.getRenameContentParams();
        final Content renamedContent = contentService.rename( renameParams );
        return new ContentJson( renamedContent, newContentIconUrlResolver(), mixinReferencesToFormItemsTransformer, principalsResolver );
    }


    @POST
    @Path("delete")
    public DeleteContentResultJson delete( final DeleteContentJson json )
    {
        final ContentPaths contentsToDelete = ContentPaths.from( json.getContentPaths() );

        //sort contents by nesting order to avoid removing parent content before child.
        List<ContentPath> contentsToDeleteList = Lists.newArrayList( contentsToDelete.getSet() );
        Collections.sort( contentsToDeleteList, ( ContentPath contentPath1, ContentPath contentPath2 ) -> ( contentPath2.elementCount() -
            contentPath1.elementCount() ) );

        final DeleteContentResultJson jsonResult = new DeleteContentResultJson();

        for ( final ContentPath contentToDelete : contentsToDeleteList )
        {
            final DeleteContentParams deleteContent = new DeleteContentParams();
            deleteContent.deleter( PrincipalKey.ofAnonymous() );
            deleteContent.contentPath( contentToDelete );

            try
            {
                contentService.delete( deleteContent );
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
    @Path("publish")
    public PublishContentResultJson publish( final PublishContentJson params )
    {
        final ContentIds contentIds = ContentIds.from(params.getIds());

        final PublishContentResultJson jsonResult = new PublishContentResultJson();

        for (ContentId contentId : contentIds) {
            try
            {
                final Content publishedContent =
                        contentService.push( new PushContentParams( ContentConstants.WORKSPACE_PROD, contentId ) );

                final String displayName = publishedContent.getDisplayName().toString();

                jsonResult.addSuccess( contentId, displayName );
            }
            catch ( ContentNotFoundException e )
            {
                jsonResult.addFailure( contentId, e.getMessage() );
            }
        }

        return jsonResult;
    }

    @POST
    @Path("applyPermissions")
    public ContentJson applyPermissions( final ApplyContentPermissionsJson jsonParams )
    {
        final AuthenticationInfo authInfo = ContextAccessor.current().getAuthInfo();
        final PrincipalKey modifier =
            authInfo != null && authInfo.isAuthenticated() ? authInfo.getUser().getKey() : PrincipalKey.ofAnonymous();

        final UpdateContentParams updatePermissionsParams = jsonParams.getUpdateContentParams().modifier( modifier );
        final Content updatedContent = contentService.update( updatePermissionsParams );

        contentService.applyPermissions( ApplyContentPermissionsParams.create().
            contentId( updatedContent.getId() ).
            overwriteChildPermissions( jsonParams.isOverwriteChildPermissions() ).
            modifier( modifier ).
            build() );

        return new ContentJson( updatedContent, newContentIconUrlResolver(), mixinReferencesToFormItemsTransformer, principalsResolver );
    }

    @POST
    @Path("setChildOrder")
    public ContentJson setChildOrder( final SetChildOrderJson params )
    {
        final Content updatedContent = this.contentService.setChildOrder( SetContentChildOrderParams.create().
            childOrder( params.getChildOrder().getChildOrder() ).
            contentId( ContentId.from( params.getContentId() ) ).
            build() );

        return new ContentJson( updatedContent, newContentIconUrlResolver(), mixinReferencesToFormItemsTransformer, principalsResolver );
    }

    @POST
    @Path("reorderChildren")
    public ReorderChildrenResultJson reorderChildContents( final ReorderChildrenJson params )
    {
        final ReorderChildContentsParams.Builder builder = ReorderChildContentsParams.create();

        for ( final ReorderChildJson reorderChildJson : params.getReorderChildren() )
        {
            builder.add( ReorderChildParams.create().
                contentToMove( ContentId.from( reorderChildJson.getContentId() ) ).
                contentToMoveBefore( ContentId.from( reorderChildJson.getMoveBefore() ) ).
                build() );
        }

        final ReorderChildContentsResult result = this.contentService.reorderChildren( builder.build() );

        return new ReorderChildrenResultJson( result );
    }

    @GET
    public ContentIdJson getById( @QueryParam("id") final String idParam,
                                  @QueryParam("expand") @DefaultValue(EXPAND_FULL) final String expandParam )
    {

        final ContentId id = ContentId.from( idParam );
        final Content content = contentService.getById( id );

        if ( content == null )
        {
            throw new NotFoundWebException( String.format( "Content [%s] was not found", idParam ) );
        }
        else if ( EXPAND_NONE.equalsIgnoreCase( expandParam ) )
        {
            return new ContentIdJson( id );
        }
        else if ( EXPAND_SUMMARY.equalsIgnoreCase( expandParam ) )
        {
            return new ContentSummaryJson( content, newContentIconUrlResolver() );
        }
        else
        {
            return new ContentJson( content, newContentIconUrlResolver(), mixinReferencesToFormItemsTransformer, principalsResolver );
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
            throw new NotFoundWebException( String.format( "Content [%s] was not found", pathParam ) );
        }
        else if ( EXPAND_NONE.equalsIgnoreCase( expandParam ) )
        {
            return new ContentIdJson( content.getId() );
        }
        else if ( EXPAND_SUMMARY.equalsIgnoreCase( expandParam ) )
        {
            return new ContentSummaryJson( content, newContentIconUrlResolver() );
        }
        else
        {
            return new ContentJson( content, newContentIconUrlResolver(), mixinReferencesToFormItemsTransformer, principalsResolver );
        }
    }

    @POST
    @Path("nearestSite")
    @Consumes(MediaType.APPLICATION_JSON)
    public ContentJson getNearest( final GetNearestSiteJson params )
    {
        final ContentId contentId = params.getGetNearestSiteByContentId();
        final Content nearestSite = this.contentService.getNearestSite( contentId );
        if ( nearestSite != null )
        {
            return new ContentJson( nearestSite, newContentIconUrlResolver(), mixinReferencesToFormItemsTransformer, principalsResolver );
        }
        else
        {
            return null;
        }
    }

    @GET
    @Path("list")
    public AbstractContentListJson listById( @QueryParam("parentId") @DefaultValue("") final String parentIdParam,
                                             @QueryParam("expand") @DefaultValue(EXPAND_SUMMARY) final String expandParam,
                                             @QueryParam("from") @DefaultValue(DEFAULT_FROM_PARAM) final Integer fromParam,
                                             @QueryParam("size") @DefaultValue(DEFAULT_SIZE_PARAM) final Integer sizeParam,
                                             @QueryParam("childOrder") @DefaultValue("") final String childOrder )
    {
        final ContentPath parentContentPath;

        if ( StringUtils.isEmpty( parentIdParam ) )
        {
            parentContentPath = null;
        }
        else
        {
            final Content parentContent = contentService.getById( ContentId.from( parentIdParam ) );

            parentContentPath = parentContent.getPath();
        }

        final FindContentByParentParams params = FindContentByParentParams.create().
            from( fromParam ).
            size( sizeParam ).
            parentPath( parentContentPath ).
            childOrder( ChildOrder.from( childOrder ) ).
            build();

        return doGetByParentPath( expandParam, params, parentContentPath );
    }

    @GET
    @Path("list/bypath")
    public AbstractContentListJson listByPath( @QueryParam("parentPath") @DefaultValue("") final String parentPathParam,
                                               @QueryParam("expand") @DefaultValue(EXPAND_SUMMARY) final String expandParam,
                                               @QueryParam("from") @DefaultValue(DEFAULT_FROM_PARAM) final Integer fromParam,
                                               @QueryParam("size") @DefaultValue(DEFAULT_SIZE_PARAM) final Integer sizeParam,
                                               @QueryParam("childOrder") @DefaultValue("") final String childOrder )
    {
        final ContentPath parentContentPath;

        if ( StringUtils.isEmpty( parentPathParam ) )
        {
            parentContentPath = null;
        }
        else
        {
            parentContentPath = ContentPath.from( parentPathParam );
        }

        final FindContentByParentParams params = FindContentByParentParams.create().
            from( fromParam ).
            size( sizeParam ).
            parentPath( parentContentPath ).
            childOrder( ChildOrder.from( childOrder ) ).
            build();

        return doGetByParentPath( expandParam, params, parentContentPath );
    }

    private AbstractContentListJson doGetByParentPath( final String expandParam, final FindContentByParentParams params,
                                                       final ContentPath parentContentPath )
    {
        final FindContentByParentResult result = contentService.findByParent( params );

        final ContentListMetaData metaData = ContentListMetaData.create().
            totalHits( result.getTotalHits() ).
            hits( result.getHits() ).
            build();

        if ( EXPAND_NONE.equalsIgnoreCase( expandParam ) )
        {
            return new ContentIdListJson( result.getContents(), metaData );
        }
        else if ( EXPAND_FULL.equalsIgnoreCase( expandParam ) )
        {
            return new ContentListJson( result.getContents(), metaData, newContentIconUrlResolver(), mixinReferencesToFormItemsTransformer,
                                        principalsResolver );
        }
        else
        {
            return new ContentSummaryListJson( result.getContents(), metaData, newContentIconUrlResolver() );
        }
    }

    @POST
    @Path("query")
    @Consumes(MediaType.APPLICATION_JSON)
    public AbstractContentQueryResultJson query( final ContentQueryJson contentQueryJson )
    {
        final boolean getChildrenIds = !Expand.NONE.matches( contentQueryJson.getExpand() );

        final ContentIconUrlResolver iconUrlResolver = newContentIconUrlResolver();
        final FindContentByQueryResult findResult = contentService.find( FindContentByQueryParams.create().
            populateChildren( getChildrenIds ).
            contentQuery( contentQueryJson.getContentQuery() ).
            build() );

        return FindContentByQuertResultJsonFactory.create( findResult, contentQueryJson.getExpand(), iconUrlResolver,
                                                           mixinReferencesToFormItemsTransformer, principalsResolver );
    }

    @GET
    @Path("generateName")
    public ContentNameJson generateName( @QueryParam("displayName") final String displayNameParam )
    {
        final String generatedContentName = contentService.generateContentName( displayNameParam );

        return new ContentNameJson( generatedContentName );
    }

    @POST
    @Path("compare")
    public CompareContentResultsJson compare( final CompareContentsJson params )
    {
        final ContentIds contentIds = ContentIds.from( params.getIds() );
        final CompareContentResults compareResults =
            contentService.compare( new CompareContentsParams( contentIds, ContentConstants.WORKSPACE_PROD ) );

        return new CompareContentResultsJson( compareResults );
    }

    @POST
    @Path("getVersions")
    public GetContentVersionsResultJson getContentVersions( final GetContentVersionsJson params )
    {
        final ContentId contentId = ContentId.from( params.getContentId() );

        final FindContentVersionsResult result = contentService.getVersions( FindContentVersionsParams.create().
            contentId( contentId ).
            from( params.getFrom() != null ? params.getFrom() : 0 ).
            size( params.getSize() != null ? params.getSize() : 10 ).
            build() );

        return new GetContentVersionsResultJson( result );
    }

    @GET
    @Path("getActiveVersions")
    public GetActiveContentVersionsResultJson getActiveVersions( @QueryParam("id") final String id )
    {
        final GetActiveContentVersionsResult result = contentService.getActiveVersions( GetActiveContentVersionsParams.create().
            workspaces( Workspaces.from( ContentConstants.WORKSPACE_STAGE, ContentConstants.WORKSPACE_PROD ) ).
            contentId( ContentId.from( id ) ).
            build() );

        return new GetActiveContentVersionsResultJson( result );
    }

    private List<Attachment> parseAttachments( final List<AttachmentJson> attachmentJsonList )
    {
        List<Attachment> attachments = new ArrayList<>();
        if ( attachmentJsonList != null )
        {
            attachments.addAll( attachmentJsonList.stream().map( AttachmentJson::getAttachment ).collect( Collectors.toList() ) );
        }
        return attachments;
    }

    private ContentIconUrlResolver newContentIconUrlResolver()
    {
        return new ContentIconUrlResolver( this.contentTypeService );
    }

    public void setContentService( final ContentService contentService )
    {
        this.contentService = contentService;
    }

    public void setContentTypeService( final ContentTypeService contentTypeService )
    {
        this.contentTypeService = contentTypeService;
    }

    public void setMixinService( final MixinService mixinService )
    {
        this.mixinReferencesToFormItemsTransformer = new MixinReferencesToFormItemsTransformer( mixinService );
    }

    public void setSecurityService( final SecurityService securityService )
    {
        this.securityService = securityService;
        this.principalsResolver = new ContentPrincipalsResolver( securityService );
    }
}
