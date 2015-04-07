package com.enonic.xp.admin.impl.rest.resource.content;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import javax.annotation.security.RolesAllowed;
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
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.google.common.collect.Lists;
import com.google.common.io.ByteSource;
import com.google.common.io.Files;

import com.enonic.xp.admin.impl.AdminResource;
import com.enonic.xp.admin.impl.json.content.AbstractContentListJson;
import com.enonic.xp.admin.impl.json.content.CompareContentResultsJson;
import com.enonic.xp.admin.impl.json.content.ContentIdJson;
import com.enonic.xp.admin.impl.json.content.ContentIdListJson;
import com.enonic.xp.admin.impl.json.content.ContentJson;
import com.enonic.xp.admin.impl.json.content.ContentListJson;
import com.enonic.xp.admin.impl.json.content.ContentSummaryJson;
import com.enonic.xp.admin.impl.json.content.ContentSummaryListJson;
import com.enonic.xp.admin.impl.json.content.GetActiveContentVersionsResultJson;
import com.enonic.xp.admin.impl.json.content.GetContentVersionsResultJson;
import com.enonic.xp.admin.impl.json.content.ReorderChildrenResultJson;
import com.enonic.xp.admin.impl.json.content.RootPermissionsJson;
import com.enonic.xp.admin.impl.json.content.attachment.AttachmentJson;
import com.enonic.xp.admin.impl.rest.exception.NotFoundWebException;
import com.enonic.xp.admin.impl.rest.multipart.MultipartForm;
import com.enonic.xp.admin.impl.rest.resource.ResourceConstants;
import com.enonic.xp.admin.impl.rest.resource.content.json.AbstractContentQueryResultJson;
import com.enonic.xp.admin.impl.rest.resource.content.json.ApplyContentPermissionsJson;
import com.enonic.xp.admin.impl.rest.resource.content.json.BatchContentJson;
import com.enonic.xp.admin.impl.rest.resource.content.json.CompareContentsJson;
import com.enonic.xp.admin.impl.rest.resource.content.json.ContentNameJson;
import com.enonic.xp.admin.impl.rest.resource.content.json.ContentQueryJson;
import com.enonic.xp.admin.impl.rest.resource.content.json.CountItemsWithChildrenJson;
import com.enonic.xp.admin.impl.rest.resource.content.json.CreateContentJson;
import com.enonic.xp.admin.impl.rest.resource.content.json.DeleteContentJson;
import com.enonic.xp.admin.impl.rest.resource.content.json.DeleteContentResultJson;
import com.enonic.xp.admin.impl.rest.resource.content.json.DuplicateContentJson;
import com.enonic.xp.admin.impl.rest.resource.content.json.GetContentVersionsJson;
import com.enonic.xp.admin.impl.rest.resource.content.json.LocaleListJson;
import com.enonic.xp.admin.impl.rest.resource.content.json.MoveContentJson;
import com.enonic.xp.admin.impl.rest.resource.content.json.PublishContentJson;
import com.enonic.xp.admin.impl.rest.resource.content.json.PublishContentResultJson;
import com.enonic.xp.admin.impl.rest.resource.content.json.ReorderChildJson;
import com.enonic.xp.admin.impl.rest.resource.content.json.ReorderChildrenJson;
import com.enonic.xp.admin.impl.rest.resource.content.json.SetAndReorderChildrenJson;
import com.enonic.xp.admin.impl.rest.resource.content.json.SetChildOrderJson;
import com.enonic.xp.admin.impl.rest.resource.content.json.UpdateContentJson;
import com.enonic.xp.branch.Branches;
import com.enonic.xp.content.ApplyContentPermissionsParams;
import com.enonic.xp.content.CompareContentResults;
import com.enonic.xp.content.CompareContentsParams;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentAccessException;
import com.enonic.xp.content.ContentConstants;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentIds;
import com.enonic.xp.content.ContentListMetaData;
import com.enonic.xp.content.ContentNotFoundException;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.ContentPaths;
import com.enonic.xp.content.ContentService;
import com.enonic.xp.content.Contents;
import com.enonic.xp.content.CreateMediaParams;
import com.enonic.xp.content.DeleteContentParams;
import com.enonic.xp.content.DuplicateContentParams;
import com.enonic.xp.content.FindContentByParentParams;
import com.enonic.xp.content.FindContentByParentResult;
import com.enonic.xp.content.FindContentByQueryParams;
import com.enonic.xp.content.FindContentByQueryResult;
import com.enonic.xp.content.FindContentVersionsParams;
import com.enonic.xp.content.FindContentVersionsResult;
import com.enonic.xp.content.GetActiveContentVersionsParams;
import com.enonic.xp.content.GetActiveContentVersionsResult;
import com.enonic.xp.content.GetContentByIdsParams;
import com.enonic.xp.content.MoveContentParams;
import com.enonic.xp.content.PushContentParams;
import com.enonic.xp.content.PushContentsResult;
import com.enonic.xp.content.RenameContentParams;
import com.enonic.xp.content.ReorderChildContentsParams;
import com.enonic.xp.content.ReorderChildContentsResult;
import com.enonic.xp.content.ReorderChildParams;
import com.enonic.xp.content.SetContentChildOrderParams;
import com.enonic.xp.content.SortContentParams;
import com.enonic.xp.content.UnableToDeleteContentException;
import com.enonic.xp.content.UpdateContentParams;
import com.enonic.xp.content.UpdateMediaParams;
import com.enonic.xp.content.attachment.Attachment;
import com.enonic.xp.content.attachment.AttachmentNames;
import com.enonic.xp.content.attachment.CreateAttachment;
import com.enonic.xp.content.attachment.CreateAttachments;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.form.InlineMixinsToFormItemsTransformer;
import com.enonic.xp.index.ChildOrder;
import com.enonic.xp.schema.content.ContentTypeService;
import com.enonic.xp.schema.mixin.MixinService;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.SecurityService;
import com.enonic.xp.security.acl.AccessControlList;
import com.enonic.xp.security.auth.AuthenticationInfo;

import static org.apache.commons.lang.StringUtils.containsIgnoreCase;
import static org.apache.commons.lang.StringUtils.isBlank;

@SuppressWarnings("UnusedDeclaration")
@Path(ResourceConstants.REST_ROOT + "content")
@Produces(MediaType.APPLICATION_JSON)
@RolesAllowed(RoleKeys.ADMIN_LOGIN_ID)
@Component(immediate = true)
public final class ContentResource
    implements AdminResource
{
    public static final String DEFAULT_SORT_FIELD = "modifiedTime";

    private static final String DEFAULT_FROM_PARAM = "0";

    private static final String DEFAULT_SIZE_PARAM = "500";

    private final static String EXPAND_FULL = "full";

    private final static String EXPAND_SUMMARY = "summary";

    private final static String EXPAND_NONE = "none";

    private ContentService contentService;

    private ContentTypeService contentTypeService;

    private InlineMixinsToFormItemsTransformer inlineMixinsToFormItemsTransformer;

    private ContentPrincipalsResolver principalsResolver;

    @POST
    @Path("create")
    public ContentJson create( final CreateContentJson params )
    {
        final Content persistedContent = contentService.create( params.getCreateContent() );
        return new ContentJson( persistedContent, newContentIconUrlResolver(), inlineMixinsToFormItemsTransformer, principalsResolver );
    }

    @POST
    @Path("createMedia")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public ContentJson createMedia( final MultipartForm form )
        throws Exception
    {
        final Content persistedContent;
        final CreateMediaParams createMediaParams = new CreateMediaParams();
        final String parentParam = form.getAsString( "parent" );
        if ( parentParam.startsWith( "/" ) )
        {
            createMediaParams.parent( ContentPath.from( parentParam ) );
        }
        else
        {
            final Content parentContent = contentService.getById( ContentId.from( parentParam ) );
            createMediaParams.parent( parentContent.getPath() );
        }
        createMediaParams.name( form.getAsString( "name" ) );

        final DiskFileItem mediaFile = (DiskFileItem) form.get( "file" );
        createMediaParams.mimeType( mediaFile.getContentType() );
        createMediaParams.byteSource( getFileItemByteSource( mediaFile ) );
        persistedContent = contentService.create( createMediaParams );

        return new ContentJson( persistedContent, newContentIconUrlResolver(), inlineMixinsToFormItemsTransformer, principalsResolver );
    }

    @POST
    @Path("updateMedia")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public ContentJson updateMedia( final MultipartForm form )
    {
        final Content persistedContent;
        final UpdateMediaParams params = new UpdateMediaParams();
        params.content( ContentId.from( form.getAsString( "content" ) ) );
        params.name( form.getAsString( "name" ) );

        final DiskFileItem mediaFile = (DiskFileItem) form.get( "file" );
        params.mimeType( mediaFile.getContentType() );
        params.byteSource( getFileItemByteSource( mediaFile ) );
        persistedContent = contentService.update( params );

        return new ContentJson( persistedContent, newContentIconUrlResolver(), inlineMixinsToFormItemsTransformer, principalsResolver );
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
            byteSource( getFileItemByteSource( mediaFile ) ).
            build();

        final UpdateContentParams params = new UpdateContentParams().
            contentId( ContentId.from( form.getAsString( "id" ) ) ).
            createAttachments( CreateAttachments.from( thumbnailAttachment ) );

        final Content persistedContent = contentService.update( params );

        return new ContentJson( persistedContent, newContentIconUrlResolver(), inlineMixinsToFormItemsTransformer, principalsResolver );
    }

    @POST
    @Path("duplicate")
    public ContentJson duplicate( final DuplicateContentJson params )
    {
        final Content duplicatedContent = contentService.duplicate( new DuplicateContentParams( params.getContentId() ) );

        return new ContentJson( duplicatedContent, newContentIconUrlResolver(), inlineMixinsToFormItemsTransformer, principalsResolver );
    }

    @POST
    @Path("move")
    public ContentSummaryListJson move( final MoveContentJson params )
    {
        final Contents contentForMove = this.contentService.getByIds( new GetContentByIdsParams( ContentIds.from( params.getContentIds() ) ) );
        final Contents movedContents = contentService.move( new MoveContentParams(ContentIds.from( params.getContentIds() ), params.getParentContentPath() ) );

        final ContentListMetaData metaData = ContentListMetaData.create().
            totalHits( movedContents.getSize() ).
            hits( movedContents.getSize() ).
            build();

        return new ContentSummaryListJson( movedContents, metaData, newContentIconUrlResolver() );
    }

    @POST
    @Path("update")
    public ContentJson update( final UpdateContentJson json )
    {
        final UpdateContentParams updateParams = json.getUpdateContentParams();

        final Content updatedContent = contentService.update( updateParams );
        if ( json.getContentName().equals( updatedContent.getName() ) )
        {
            return new ContentJson( updatedContent, newContentIconUrlResolver(), inlineMixinsToFormItemsTransformer, principalsResolver );
        }

        final RenameContentParams renameParams = json.getRenameContentParams();
        final Content renamedContent = contentService.rename( renameParams );
        return new ContentJson( renamedContent, newContentIconUrlResolver(), inlineMixinsToFormItemsTransformer, principalsResolver );
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
            final DeleteContentParams deleteContent = DeleteContentParams.create().
                contentPath( contentToDelete ).
                build();

            try
            {
                contentService.delete( deleteContent );
                jsonResult.addSuccess( contentToDelete );
            }
            catch ( ContentNotFoundException | UnableToDeleteContentException | ContentAccessException e )
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
        final ContentIds contentIds = ContentIds.from( params.getIds() );

        final PushContentsResult result = contentService.push( PushContentParams.create().
            target( ContentConstants.BRANCH_MASTER ).
            contentIds( contentIds ).
            includeChildren( true ).
            allowPublishOutsideSelection( true ).
            build() );

        return PublishContentResultJson.from( result );
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

        return new ContentJson( updatedContent, newContentIconUrlResolver(), inlineMixinsToFormItemsTransformer, principalsResolver );
    }

    @GET
    @Path("rootPermissions")
    public RootPermissionsJson getRootPermissions()
    {
        final AccessControlList rootPermissions = contentService.getRootPermissions();
        return new RootPermissionsJson( rootPermissions, principalsResolver );
    }

    @POST
    @Path("setChildOrder")
    public ContentJson setChildOrder( final SetChildOrderJson params )
    {
        final Content updatedContent = this.contentService.setChildOrder( SetContentChildOrderParams.create().
            childOrder( params.getChildOrder().getChildOrder() ).
            contentId( ContentId.from( params.getContentId() ) ).
            build() );

        if ( !params.isSilent() )
        {
            this.contentService.sort( new SortContentParams().contentId( ContentId.from( params.getContentId() ) ) );
        }

        return new ContentJson( updatedContent, newContentIconUrlResolver(), inlineMixinsToFormItemsTransformer, principalsResolver );
    }

    @POST
    @Path("reorderChildren")
    public ReorderChildrenResultJson reorderChildContents( final ReorderChildrenJson params )
    {
        final ReorderChildContentsParams.Builder builder = ReorderChildContentsParams.create();

        for ( final ReorderChildJson reorderChildJson : params.getReorderChildren() )
        {
            final String moveBefore = reorderChildJson.getMoveBefore();
            builder.add( ReorderChildParams.create().
                contentToMove( ContentId.from( reorderChildJson.getContentId() ) ).
                contentToMoveBefore( isBlank( moveBefore ) ? null : ContentId.from( moveBefore ) ).
                build() );
        }

        final ReorderChildContentsResult result = this.contentService.reorderChildren(builder.build());

        if ( !params.isSilent() )
        {
            this.contentService.sort( new SortContentParams().contentId( ContentId.from( params.getContentId() ) ) );
        }

        return new ReorderChildrenResultJson( result );
    }

    @POST
    @Path("setAndReorderChildren")
    public ContentJson setAndReorderChildContents( final SetAndReorderChildrenJson params )
    {

        final Content updatedContent = this.contentService.setChildOrder( SetContentChildOrderParams.create().
            childOrder( params.getChildOrder().getChildOrder() ).
            contentId( ContentId.from( params.getContentId() ) ).
            build() );

        final ReorderChildContentsParams.Builder builder = ReorderChildContentsParams.create();

        for ( final ReorderChildJson reorderChildJson : params.getReorderChildren() )
        {
            final String moveBefore = reorderChildJson.getMoveBefore();
            builder.add( ReorderChildParams.create().
                contentToMove( ContentId.from( reorderChildJson.getContentId() ) ).
                contentToMoveBefore( isBlank( moveBefore ) ? null : ContentId.from( moveBefore ) ).
                build() );
        }

        final ReorderChildContentsResult result = this.contentService.reorderChildren( builder.build() );

        if ( !params.isSilent() )
        {
            this.contentService.sort( new SortContentParams().contentId( ContentId.from( params.getContentId() ) ) );
        }

        return new ContentJson( updatedContent, newContentIconUrlResolver(), inlineMixinsToFormItemsTransformer, principalsResolver );
    }

    @GET
    public ContentIdJson getById( @QueryParam("id") final String idParam,
                                  @QueryParam("expand") @DefaultValue(EXPAND_FULL) final String expandParam )
    {

        final ContentId id = ContentId.from(idParam);
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
            return new ContentJson( content, newContentIconUrlResolver(), inlineMixinsToFormItemsTransformer, principalsResolver );
        }
    }

    @GET
    @Path("bypath")
    public ContentIdJson getByPath( @QueryParam("path") final String pathParam,
                                    @QueryParam("expand") @DefaultValue(EXPAND_FULL) final String expandParam )
    {
        final Content content = contentService.getByPath(ContentPath.from(pathParam));

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
            return new ContentJson( content, newContentIconUrlResolver(), inlineMixinsToFormItemsTransformer, principalsResolver );
        }
    }

    @GET
    @Path("contentPermissions")
    public RootPermissionsJson getPermissionsByPath( @QueryParam("path") final String pathParam )
    {
        final AccessControlList permissions = contentService.getPermissionsByPath( ContentPath.from( pathParam ) );
        return new RootPermissionsJson( permissions, principalsResolver );
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
            return new ContentJson( nearestSite, newContentIconUrlResolver(), inlineMixinsToFormItemsTransformer, principalsResolver );
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

    @POST
    @Path("batch")
    public ContentSummaryListJson listBatched( final BatchContentJson json )
    {
        final ContentPaths contentsToBatch = ContentPaths.from( json.getContentPaths() );

        final Contents contents = contentService.getByPaths( contentsToBatch );

        final ContentListMetaData metaData = ContentListMetaData.create().
            totalHits( contents.getSize() ).
            hits( contents.getSize() ).
            build();

        return new ContentSummaryListJson( contents, metaData, newContentIconUrlResolver() );
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
            return new ContentListJson( result.getContents(), metaData, newContentIconUrlResolver(), inlineMixinsToFormItemsTransformer,
                                        principalsResolver );
        }
        else
        {
            return new ContentSummaryListJson( result.getContents(), metaData, newContentIconUrlResolver() );
        }
    }

    @POST
    @Path("countContentsWithDescendants")
    public long countContentsWithDescendants( final CountItemsWithChildrenJson json )
    {
        final ContentPaths contentsPaths = this.filterChildrenIfParentPresents( ContentPaths.from( json.getContentPaths() ) );

        return new ContentChildrenCounter( contentService ).countContentsAndTheirChildren( contentsPaths );
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
                                                           inlineMixinsToFormItemsTransformer, principalsResolver );
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
            contentService.compare( new CompareContentsParams( contentIds, ContentConstants.BRANCH_MASTER ) );

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
            branches( Branches.from( ContentConstants.BRANCH_DRAFT, ContentConstants.BRANCH_MASTER ) ).
            contentId( ContentId.from( id ) ).
            build() );

        return new GetActiveContentVersionsResultJson( result );
    }


    @GET
    @Path("locales")
    public LocaleListJson getLocales( @QueryParam("query") final String query )
    {
        Locale[] locales = Locale.getAvailableLocales();
        if ( StringUtils.isNotBlank( query ) )
        {
            locales = Arrays.stream( locales ).
                filter( ( locale ) -> containsIgnoreCase( locale.toLanguageTag(), query ) ||
                    containsIgnoreCase( locale.getDisplayName( locale ), query ) ||
                    containsIgnoreCase( locale.getLanguage(), query ) ||
                    containsIgnoreCase( locale.getDisplayLanguage( locale ), query ) ||
                    containsIgnoreCase( locale.getVariant(), query ) ||
                    containsIgnoreCase( locale.getDisplayVariant( locale ), query ) ||
                    containsIgnoreCase( locale.getCountry(), query ) ||
                    containsIgnoreCase( locale.getDisplayCountry( locale ), query ) ).
                toArray( Locale[]::new );
        }
        return new LocaleListJson( locales );
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

    private ByteSource getFileItemByteSource( final DiskFileItem diskFileItem )
    {
        if ( diskFileItem.isInMemory() )
        {
            return ByteSource.wrap( diskFileItem.get() );
        }
        else
        {
            return Files.asByteSource( diskFileItem.getStoreLocation() );
        }
    }

    private ContentIconUrlResolver newContentIconUrlResolver()
    {
        return new ContentIconUrlResolver( this.contentTypeService );
    }

    private ContentPaths filterChildrenIfParentPresents( ContentPaths sourceContentPaths )
    {
        ContentPaths filteredContentPaths = ContentPaths.empty();

        for ( ContentPath contentPath : sourceContentPaths.getSet() )
        {
            boolean hasParent = sourceContentPaths.stream().anyMatch( ( possibleParentCP ) -> contentPath.isChildOf( possibleParentCP ) );
            if ( !hasParent )
            {
                filteredContentPaths = filteredContentPaths.add( contentPath );
            }
        }

        return filteredContentPaths;
    }

    @Reference
    public void setContentService( final ContentService contentService )
    {
        this.contentService = contentService;
    }

    @Reference
    public void setContentTypeService( final ContentTypeService contentTypeService )
    {
        this.contentTypeService = contentTypeService;
    }

    @Reference
    public void setMixinService( final MixinService mixinService )
    {
        this.inlineMixinsToFormItemsTransformer = new InlineMixinsToFormItemsTransformer( mixinService );
    }

    @Reference
    public void setSecurityService( final SecurityService securityService )
    {
        this.principalsResolver = new ContentPrincipalsResolver( securityService );
    }
}
