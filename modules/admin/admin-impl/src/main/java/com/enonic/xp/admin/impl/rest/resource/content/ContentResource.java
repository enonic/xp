package com.enonic.xp.admin.impl.rest.resource.content;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
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
import javax.ws.rs.core.Response;

import com.enonic.xp.query.expr.*;
import org.apache.commons.lang.StringUtils;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.common.io.ByteSource;

import io.swagger.annotations.ApiOperation;

import com.enonic.xp.admin.impl.json.content.AbstractContentListJson;
import com.enonic.xp.admin.impl.json.content.CompareContentResultsJson;
import com.enonic.xp.admin.impl.json.content.ContentIdJson;
import com.enonic.xp.admin.impl.json.content.ContentIdListJson;
import com.enonic.xp.admin.impl.json.content.ContentJson;
import com.enonic.xp.admin.impl.json.content.ContentListJson;
import com.enonic.xp.admin.impl.json.content.ContentSummaryJson;
import com.enonic.xp.admin.impl.json.content.ContentSummaryListJson;
import com.enonic.xp.admin.impl.json.content.GetActiveContentVersionsResultJson;
import com.enonic.xp.admin.impl.json.content.GetContentVersionsForViewResultJson;
import com.enonic.xp.admin.impl.json.content.GetContentVersionsResultJson;
import com.enonic.xp.admin.impl.json.content.ReorderChildrenResultJson;
import com.enonic.xp.admin.impl.json.content.RootPermissionsJson;
import com.enonic.xp.admin.impl.json.content.attachment.AttachmentJson;
import com.enonic.xp.admin.impl.json.content.attachment.AttachmentListJson;
import com.enonic.xp.admin.impl.rest.resource.ResourceConstants;
import com.enonic.xp.admin.impl.rest.resource.content.json.AbstractContentQueryResultJson;
import com.enonic.xp.admin.impl.rest.resource.content.json.ApplyContentPermissionsJson;
import com.enonic.xp.admin.impl.rest.resource.content.json.BatchContentJson;
import com.enonic.xp.admin.impl.rest.resource.content.json.CompareContentsJson;
import com.enonic.xp.admin.impl.rest.resource.content.json.ContentPublishItemJson;
import com.enonic.xp.admin.impl.rest.resource.content.json.ContentQueryJson;
import com.enonic.xp.admin.impl.rest.resource.content.json.ContentSelectorQueryJson;
import com.enonic.xp.admin.impl.rest.resource.content.json.CountItemsWithChildrenJson;
import com.enonic.xp.admin.impl.rest.resource.content.json.CreateContentJson;
import com.enonic.xp.admin.impl.rest.resource.content.json.DeleteAttachmentJson;
import com.enonic.xp.admin.impl.rest.resource.content.json.DeleteContentJson;
import com.enonic.xp.admin.impl.rest.resource.content.json.DeleteContentResultJson;
import com.enonic.xp.admin.impl.rest.resource.content.json.DuplicateContentJson;
import com.enonic.xp.admin.impl.rest.resource.content.json.EffectivePermissionAccessJson;
import com.enonic.xp.admin.impl.rest.resource.content.json.EffectivePermissionJson;
import com.enonic.xp.admin.impl.rest.resource.content.json.EffectivePermissionMemberJson;
import com.enonic.xp.admin.impl.rest.resource.content.json.GetContentVersionsJson;
import com.enonic.xp.admin.impl.rest.resource.content.json.LocaleListJson;
import com.enonic.xp.admin.impl.rest.resource.content.json.MoveContentJson;
import com.enonic.xp.admin.impl.rest.resource.content.json.MoveContentResultJson;
import com.enonic.xp.admin.impl.rest.resource.content.json.PublishContentJson;
import com.enonic.xp.admin.impl.rest.resource.content.json.PublishContentResultJson;
import com.enonic.xp.admin.impl.rest.resource.content.json.ReorderChildJson;
import com.enonic.xp.admin.impl.rest.resource.content.json.ReorderChildrenJson;
import com.enonic.xp.admin.impl.rest.resource.content.json.ResolvePublishContentResultJson;
import com.enonic.xp.admin.impl.rest.resource.content.json.ResolvePublishDependenciesJson;
import com.enonic.xp.admin.impl.rest.resource.content.json.SetActiveVersionJson;
import com.enonic.xp.admin.impl.rest.resource.content.json.SetChildOrderJson;
import com.enonic.xp.admin.impl.rest.resource.content.json.UpdateContentJson;
import com.enonic.xp.attachment.Attachment;
import com.enonic.xp.attachment.AttachmentNames;
import com.enonic.xp.attachment.CreateAttachment;
import com.enonic.xp.attachment.CreateAttachments;
import com.enonic.xp.branch.Branches;
import com.enonic.xp.content.ApplyContentPermissionsParams;
import com.enonic.xp.content.CompareContentResult;
import com.enonic.xp.content.CompareContentResults;
import com.enonic.xp.content.CompareContentsParams;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentAlreadyExistsException;
import com.enonic.xp.content.ContentConstants;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentIds;
import com.enonic.xp.content.ContentListMetaData;
import com.enonic.xp.content.ContentNotFoundException;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.ContentPaths;
import com.enonic.xp.content.ContentQuery;
import com.enonic.xp.content.ContentService;
import com.enonic.xp.content.ContentState;
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
import com.enonic.xp.content.MoveContentException;
import com.enonic.xp.content.MoveContentParams;
import com.enonic.xp.content.PushContentParams;
import com.enonic.xp.content.PushContentsResult;
import com.enonic.xp.content.RenameContentParams;
import com.enonic.xp.content.ReorderChildContentsParams;
import com.enonic.xp.content.ReorderChildContentsResult;
import com.enonic.xp.content.ReorderChildParams;
import com.enonic.xp.content.ResolvePublishDependenciesParams;
import com.enonic.xp.content.ResolvePublishDependenciesResult;
import com.enonic.xp.content.SetActiveContentVersionResult;
import com.enonic.xp.content.SetContentChildOrderParams;
import com.enonic.xp.content.UpdateContentParams;
import com.enonic.xp.content.UpdateMediaParams;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.index.ChildOrder;
import com.enonic.xp.jaxrs.JaxRsComponent;
import com.enonic.xp.jaxrs.JaxRsExceptions;
import com.enonic.xp.schema.content.ContentTypeService;
import com.enonic.xp.schema.relationship.RelationshipTypeService;
import com.enonic.xp.security.Principal;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.PrincipalKeys;
import com.enonic.xp.security.PrincipalQuery;
import com.enonic.xp.security.PrincipalQueryResult;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.SecurityService;
import com.enonic.xp.security.acl.AccessControlEntry;
import com.enonic.xp.security.acl.AccessControlList;
import com.enonic.xp.security.auth.AuthenticationInfo;
import com.enonic.xp.web.multipart.MultipartForm;
import com.enonic.xp.web.multipart.MultipartItem;

import static java.lang.Math.toIntExact;
import static org.apache.commons.lang.StringUtils.containsIgnoreCase;
import static org.apache.commons.lang.StringUtils.isBlank;

@SuppressWarnings("UnusedDeclaration")
@Path(ResourceConstants.REST_ROOT + "content")
@Produces(MediaType.APPLICATION_JSON)
@RolesAllowed(RoleKeys.ADMIN_LOGIN_ID)
@Component(immediate = true)
public final class ContentResource
    implements JaxRsComponent
{
    public static final String DEFAULT_SORT_FIELD = "modifiedTime";

    private static final String DEFAULT_FROM_PARAM = "0";

    private static final String DEFAULT_SIZE_PARAM = "500";

    private final static String EXPAND_FULL = "full";

    private final static String EXPAND_SUMMARY = "summary";

    private final static String EXPAND_NONE = "none";

    private static final int MAX_EFFECTIVE_PERMISSIONS_PRINCIPALS = 10;

    private final static Logger LOG = LoggerFactory.getLogger( ContentResource.class );

    private ContentService contentService;

    private ContentPrincipalsResolver principalsResolver;

    private SecurityService securityService;

    private RelationshipTypeService relationshipTypeService;

    private ContentIconUrlResolver contentIconUrlResolver;

    @POST
    @Path("create")
    public ContentJson create( final CreateContentJson params )
    {
        final Content persistedContent = contentService.create( params.getCreateContent() );
        return new ContentJson( persistedContent, contentIconUrlResolver, principalsResolver );
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

        final MultipartItem mediaFile = form.get( "file" );
        createMediaParams.name( form.getAsString( "name" ) ).
            mimeType( mediaFile.getContentType().toString() ).
            byteSource( getFileItemByteSource( mediaFile ) );

        final String focalX = form.getAsString( "focalX" );
        final String focalY = form.getAsString( "focalY" );

        if ( StringUtils.isNotBlank( focalX ) )
        {
            createMediaParams.focalX( Double.valueOf( focalX ) );
        }
        if ( StringUtils.isNotBlank( focalY ) )
        {
            createMediaParams.focalY( Double.valueOf( focalY ) );
        }

        persistedContent = contentService.create( createMediaParams );

        return new ContentJson( persistedContent, contentIconUrlResolver, principalsResolver );
    }

    @POST
    @Path("updateMedia")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public ContentJson updateMedia( final MultipartForm form )
    {
        final Content persistedContent;
        final UpdateMediaParams params = new UpdateMediaParams().
            content( ContentId.from( form.getAsString( "content" ) ) ).
            name( form.getAsString( "name" ) );

        final String focalX = form.getAsString( "focalX" );
        final String focalY = form.getAsString( "focalY" );

        if ( StringUtils.isNotBlank( focalX ) )
        {
            params.focalX( Double.valueOf( focalX ) );
        }
        if ( StringUtils.isNotBlank( focalY ) )
        {
            params.focalY( Double.valueOf( focalY ) );
        }

        final MultipartItem mediaFile = form.get( "file" );
        params.mimeType( mediaFile.getContentType().toString() );
        params.byteSource( getFileItemByteSource( mediaFile ) );
        persistedContent = contentService.update( params );

        return new ContentJson( persistedContent, contentIconUrlResolver, principalsResolver );
    }

    @POST
    @Path("updateThumbnail")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public ContentJson updateThumbnail( final MultipartForm form )
    {
        final Content persistedContent = this.doCreateAttachment( AttachmentNames.THUMBNAIL, form );

        return new ContentJson( persistedContent, contentIconUrlResolver, principalsResolver );
    }

    @POST
    @Path("createAttachment")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public AttachmentJson createAttachment( final MultipartForm form )
    {

        final MultipartItem mediaFile = form.get( "file" );
        final String attachmentName = mediaFile.getFileName();

        final Content persistedContent = this.doCreateAttachment( attachmentName, form );

        return new AttachmentJson( persistedContent.getAttachments().byName( attachmentName ) );

    }

    @POST
    @Path("deleteAttachment")
    public ContentJson deleteAttachment( final DeleteAttachmentJson json )
    {
        final UpdateContentParams params = new UpdateContentParams().
            contentId( json.getContentId() ).
            removeAttachments( json.getAttachmentReferences() );

        final Content content = contentService.update( params );
        return new ContentJson( content, contentIconUrlResolver, principalsResolver );
    }

    @POST
    @Path("duplicate")
    public ContentJson duplicate( final DuplicateContentJson params )
    {
        final Content duplicatedContent = contentService.duplicate( new DuplicateContentParams( params.getContentId() ) );

        return new ContentJson( duplicatedContent, contentIconUrlResolver, principalsResolver );
    }

    @POST
    @Path("move")
    public MoveContentResultJson move( final MoveContentJson params )
    {
        final MoveContentResultJson resultJson = new MoveContentResultJson();

        for ( ContentId contentId : ContentIds.from( params.getContentIds() ) )
        {
            try
            {
                final Content movedContent = contentService.move( new MoveContentParams( contentId, params.getParentContentPath() ) );
                resultJson.addSuccess( movedContent != null ? movedContent.getDisplayName() : contentId.toString() );
            }
            catch ( MoveContentException e )
            {
                try
                {
                    final Content failedContent = contentService.getById( contentId );
                    resultJson.addFailure( failedContent != null ? failedContent.getDisplayName() : contentId.toString(), e.getMessage() );
                }
                catch ( ContentNotFoundException cnEx )
                {
                    resultJson.addFailure( contentId.toString(), e.getMessage() );
                }
            }
        }

        return resultJson;
    }

    @POST
    @Path("update")
    public ContentJson update( final UpdateContentJson json )
    {
        if ( contentNameIsOccupied( json.getRenameContentParams() ) )
        {
            throw JaxRsExceptions.newException( Response.Status.CONFLICT,
                                                "Content [%s] could not be updated. A content with that name already exists",
                                                json.getRenameContentParams().getNewName().toString() );
        }

        final UpdateContentParams updateParams = json.getUpdateContentParams();

        final Content updatedContent = contentService.update( updateParams );
        if ( json.getContentName().equals( updatedContent.getName() ) )
        {
            return new ContentJson( updatedContent, contentIconUrlResolver, principalsResolver );
        }

        try
        {
            // in case content with same name and path was created in between content updated and renamed
            final RenameContentParams renameParams = json.getRenameContentParams();
            final Content renamedContent = contentService.rename( renameParams );
            return new ContentJson( renamedContent, contentIconUrlResolver, principalsResolver );
        }
        catch ( ContentAlreadyExistsException e )
        {
            // catching to throw exception with better message and other error code
            throw JaxRsExceptions.newException( Response.Status.CONFLICT,
                                                "Content could not be renamed to [%s]. A content with that name already exists",
                                                json.getRenameContentParams().getNewName().toString() );
        }
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
                Contents contents = contentService.delete( deleteContent );
                contents.forEach( ( content ) -> {
                    if ( ContentState.PENDING_DELETE.equals( content.getContentState() ) )
                    {
                        jsonResult.addPending( content.getId().toString(), content.getDisplayName() );
                    }
                    else
                    {
                        jsonResult.addSuccess( content.getId().toString(), content.getDisplayName(), content.getType().getLocalName() );
                    }

                } );

            }
            catch ( final Exception e )
            {
                try
                {
                    Content content = contentService.getByPath( contentToDelete );
                    if ( content != null )
                    {
                        jsonResult.addFailure( content.getId().toString(), content.getDisplayName(), content.getType().getLocalName(),
                                               e.getMessage() );
                    }
                }
                catch ( final Exception e2 )
                {
                    jsonResult.addFailure( null, deleteContent.getContentPath().toString(), null, e2.getMessage() );
                }

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
            includeChildren( params.isIncludeChildren() ).
            includeDependencies( true ).
            build() );

        return PublishContentResultJson.create().
            success( result.getPushedContents() ).
            deleted( result.getDeletedContents() ).
            failures( result.getFailedContents() ).
            build();
    }

    @POST
    @Path("resolvePublishContent")
    public ResolvePublishContentResultJson resolvePublishContent( final ResolvePublishDependenciesJson params )
    {
        //Resolved the requested ContentPublishItem
        final ContentIds requestedContentIds = ContentIds.from( params.getIds() );
        final List<ContentPublishItemJson> requestedContentPublishItemList = resolveContentPublishItems( requestedContentIds );

        //Resolves the publish dependencies
        final ResolvePublishDependenciesResult resolvePublishDependenciesResult =
            contentService.resolvePublishDependencies( ResolvePublishDependenciesParams.create().
                target( ContentConstants.BRANCH_MASTER ).
                contentIds( requestedContentIds ).
                includeChildren( params.includeChildren() ).
                build() );

        //Resolved the dependent ContentPublishItem
        final List<ContentId> dependentContentIdList = resolvePublishDependenciesResult.contentIds().
            stream().
            filter( contentId -> !requestedContentIds.contains( contentId ) ).
            collect( Collectors.toList() );
        final ContentIds dependentContentIds = ContentIds.from( dependentContentIdList );
        final List<ContentPublishItemJson> dependentContentPublishItemList = resolveContentPublishItems( dependentContentIds );

        //Returns the JSON result
        return ResolvePublishContentResultJson.create().
            setRequestedContents( requestedContentPublishItemList ).
            setDependentContents( dependentContentPublishItemList ).
            build();
    }

    private List<ContentPublishItemJson> resolveContentPublishItems( final ContentIds contentIds )
    {
        //Retrieves the contents
        final Contents contents = contentService.getByIds( new GetContentByIdsParams( contentIds ) );

        //Retrieves the compare contents
        final CompareContentResults compareContentResults =
            contentService.compare( new CompareContentsParams( contentIds, ContentConstants.BRANCH_MASTER ) );
        final Map<ContentId, CompareContentResult> compareContentResultsMap = compareContentResults.getCompareContentResultsMap();

        // Sorts the contents by path and for each
        return contents.stream().
            sorted( ( content1, content2 ) -> content1.getPath().compareTo( content2.getPath() ) ).
            map( content -> {
                //Creates a ContentPublishItem
                final CompareContentResult compareContentResult = compareContentResultsMap.get( content.getId() );
                return ContentPublishItemJson.create().
                    content( content ).
                    compareStatus( compareContentResult.getCompareStatus().name() ).
                    iconUrl( contentIconUrlResolver.resolve( content ) ).
                    build();
            } ).
            collect( Collectors.toList() );
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
            build() );

        return new ContentJson( updatedContent, contentIconUrlResolver, principalsResolver );
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
            silent( params.isSilent() ).
            build() );

        return new ContentJson( updatedContent, contentIconUrlResolver, principalsResolver );
    }

    @POST
    @Path("reorderChildren")
    public ReorderChildrenResultJson reorderChildContents( final ReorderChildrenJson params )
    {
        Content content = this.contentService.getById( ContentId.from( params.getContentId() ) );

        //If a initial sort is required before the manual reordering
        if ( params.getChildOrder() != null && !params.getChildOrder().getChildOrder().equals( content.getChildOrder() ) )
        {
            content = this.contentService.setChildOrder( SetContentChildOrderParams.create().
                childOrder( params.getChildOrder().getChildOrder() ).
                contentId( ContentId.from( params.getContentId() ) ).
                silent( true ).
                build() );
        }

        //If the content is not already manually ordered, sets it to manually ordered
        if ( !content.getChildOrder().isManualOrder() )
        {
            if ( params.isManualOrder() )
            {

                this.contentService.setChildOrder( SetContentChildOrderParams.create().
                    childOrder( ChildOrder.manualOrder() ).
                    contentId( ContentId.from( params.getContentId() ) ).
                    silent( true ).
                    build() );
            }
            else
            {
                throw JaxRsExceptions.badRequest( "Not allowed to reorder children manually, current parentOrder = [%s].",
                                                  content.getChildOrder().toString() );
            }
        }

        //Applies the manual movements
        final ReorderChildContentsParams.Builder builder =
            ReorderChildContentsParams.create().contentId( ContentId.from( params.getContentId() ) ).silent( params.isSilent() );

        for ( final ReorderChildJson reorderChildJson : params.getReorderChildren() )
        {
            final String moveBefore = reorderChildJson.getMoveBefore();
            builder.add( ReorderChildParams.create().
                contentToMove( ContentId.from( reorderChildJson.getContentId() ) ).
                contentToMoveBefore( isBlank( moveBefore ) ? null : ContentId.from( moveBefore ) ).
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
            throw JaxRsExceptions.notFound( String.format( "Content [%s] was not found", idParam ) );
        }
        else if ( EXPAND_NONE.equalsIgnoreCase( expandParam ) )
        {
            return new ContentIdJson( id );
        }
        else if ( EXPAND_SUMMARY.equalsIgnoreCase( expandParam ) )
        {
            return new ContentSummaryJson( content, contentIconUrlResolver );
        }
        else
        {
            return new ContentJson( content, contentIconUrlResolver, principalsResolver );
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
            throw JaxRsExceptions.notFound( String.format( "Content [%s] was not found", pathParam ) );
        }
        else if ( EXPAND_NONE.equalsIgnoreCase( expandParam ) )
        {
            return new ContentIdJson( content.getId() );
        }
        else if ( EXPAND_SUMMARY.equalsIgnoreCase( expandParam ) )
        {
            return new ContentSummaryJson( content, contentIconUrlResolver );
        }
        else
        {
            return new ContentJson( content, contentIconUrlResolver, principalsResolver );
        }
    }

    @GET
    @Path("contentPermissions")
    public RootPermissionsJson getPermissionsById( @QueryParam("id") final String contentId )
    {
        final AccessControlList permissions = contentService.getPermissionsById( ContentId.from( contentId ) );
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
            return new ContentJson( nearestSite, contentIconUrlResolver, principalsResolver );
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

        return new ContentSummaryListJson( contents, metaData, contentIconUrlResolver );
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
            return new ContentListJson( result.getContents(), metaData, contentIconUrlResolver, principalsResolver );
        }
        else
        {
            return new ContentSummaryListJson( result.getContents(), metaData, contentIconUrlResolver );
        }
    }

    @POST
    @Path("getDescendantsOfContents")
    public ContentSummaryListJson getDescendantsOfContents( final CountItemsWithChildrenJson json )
    {
        final ContentPaths contentsPaths = this.filterChildrenIfParentPresents( ContentPaths.from( json.getContentPaths() ) );

        return this.getDescendantsOfContents( contentsPaths );
    }

    @POST
    @Path("countContentsWithDescendants")
    public long countContentsWithDescendants( final CountItemsWithChildrenJson json )
    {
        final ContentPaths contentsPaths = this.filterChildrenIfParentPresents( ContentPaths.from( json.getContentPaths() ) );

        return this.countContentsAndTheirChildren( contentsPaths );
    }

    @POST
    @Path("query")
    @Consumes(MediaType.APPLICATION_JSON)
    public AbstractContentQueryResultJson query( final ContentQueryJson contentQueryJson )
    {
        //TODO: do we need this param? it does not seem to be checked at all
        final boolean getChildrenIds = !Expand.NONE.matches( contentQueryJson.getExpand() );

        final ContentIconUrlResolver iconUrlResolver = contentIconUrlResolver;
        final FindContentByQueryResult findResult = contentService.find( FindContentByQueryParams.create().
            populateChildren( getChildrenIds ).
            contentQuery( contentQueryJson.getContentQuery() ).
            build() );

        return FindContentByQuertResultJsonFactory.create( findResult, contentQueryJson.getExpand(), iconUrlResolver, principalsResolver );
    }

    @POST
    @Path("selectorQuery")
    @Consumes(MediaType.APPLICATION_JSON)
    public AbstractContentQueryResultJson selectorQuery( final ContentSelectorQueryJson contentQueryJson )
    {
        final ContentIconUrlResolver iconUrlResolver = contentIconUrlResolver;

        final ContentSelectorQueryJsonToContentQueryConverter selectorQueryProcessor =
            ContentSelectorQueryJsonToContentQueryConverter.create().
                contentQueryJson( contentQueryJson ).
                contentService( this.contentService ).
                relationshipTypeService( this.relationshipTypeService ).
                build();

        final FindContentByQueryResult findResult = contentService.find( FindContentByQueryParams.create().
            contentQuery( selectorQueryProcessor.createQuery() ).
            build() );

        return FindContentByQuertResultJsonFactory.create( findResult, contentQueryJson.getExpand(), iconUrlResolver, principalsResolver );
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

        return new GetContentVersionsResultJson( result, this.principalsResolver );
    }

    @GET
    @Path("getActiveVersions")
    public GetActiveContentVersionsResultJson getActiveVersions( @QueryParam("id") final String id )
    {
        final GetActiveContentVersionsResult result = contentService.getActiveVersions( GetActiveContentVersionsParams.create().
            branches( Branches.from( ContentConstants.BRANCH_DRAFT, ContentConstants.BRANCH_MASTER ) ).
            contentId( ContentId.from( id ) ).
            build() );

        return new GetActiveContentVersionsResultJson( result, this.principalsResolver );
    }

    @POST
    @Path("getVersionsForView")
    public GetContentVersionsForViewResultJson getContentVersionsForView( final GetContentVersionsJson params )
    {
        final ContentId contentId = ContentId.from( params.getContentId() );

        final FindContentVersionsResult allVersions = contentService.getVersions( FindContentVersionsParams.create().
            contentId( contentId ).
            from( params.getFrom() != null ? params.getFrom() : 0 ).
            size( params.getSize() != null ? params.getSize() : 50 ).
            build() );

        final GetActiveContentVersionsResult activeVersions = contentService.getActiveVersions( GetActiveContentVersionsParams.create().
            branches( Branches.from( ContentConstants.BRANCH_DRAFT, ContentConstants.BRANCH_MASTER ) ).
            contentId( contentId ).
            build() );

        return new GetContentVersionsForViewResultJson( allVersions, activeVersions, this.principalsResolver );
    }

    @GET
    @Path("getAttachments")
    public List<AttachmentJson> getAttachments( @QueryParam("id") final String idParam )
    {
        final ContentId id = ContentId.from( idParam );
        final Content content = contentService.getById( id );

        return AttachmentListJson.toJson( content.getAttachments() );
    }

    @POST
    @Path("setActiveVersion")
    public ContentIdJson setActiveVersion( final SetActiveVersionJson params )
    {
        final SetActiveContentVersionResult setActiveContentVersionResult =
            this.contentService.setActiveContentVersion( params.getContentId(), params.getVersionId() );

        return new ContentIdJson( setActiveContentVersionResult.getContentId() );
    }

    @GET
    @Path("locales")
    public LocaleListJson getLocales( @QueryParam("query") final String query )
    {
        Locale[] locales = Locale.getAvailableLocales();
        if ( StringUtils.isNotBlank( query ) )
        {
            String trimmedQuery = query.trim();
            locales = Arrays.stream( locales ).
                filter( ( locale ) -> containsIgnoreCase( locale.toLanguageTag(), trimmedQuery ) ||
                    containsIgnoreCase( locale.getDisplayName( locale ), trimmedQuery ) ||
                    containsIgnoreCase( locale.getLanguage(), trimmedQuery ) ||
                    containsIgnoreCase( locale.getDisplayLanguage( locale ), trimmedQuery ) ||
                    containsIgnoreCase( locale.getVariant(), trimmedQuery ) ||
                    containsIgnoreCase( locale.getDisplayVariant( locale ), trimmedQuery ) ||
                    containsIgnoreCase( locale.getCountry(), trimmedQuery ) ||
                    containsIgnoreCase( locale.getDisplayCountry( locale ), trimmedQuery ) ||
                    containsIgnoreCase( getFormattedDisplayName( locale ), trimmedQuery ) &&
                        StringUtils.isNotEmpty( locale.toLanguageTag() ) && StringUtils.isNotEmpty( locale.getDisplayName() ) ).
                toArray( Locale[]::new );
        }
        else
        {
            locales = Arrays.stream( locales ).
                filter(
                    ( locale ) -> StringUtils.isNotEmpty( locale.toLanguageTag() ) && StringUtils.isNotEmpty( locale.getDisplayName() ) ).
                toArray( Locale[]::new );
        }
        return new LocaleListJson( locales );
    }

    @GET
    @Path("effectivePermissions")
    public List<EffectivePermissionJson> getEffectivePermissions( @QueryParam("id") final String idParam )
    {
        final ContentId id = ContentId.from( idParam );
        final AccessControlList acl = contentService.getPermissionsById( id );

        final Multimap<Access, PrincipalKey> accessMembers = ArrayListMultimap.create();
        for ( AccessControlEntry ace : acl )
        {
            final Access access = Access.fromPermissions( ace.getAllowedPermissions() );
            accessMembers.put( access, ace.getPrincipal() );
        }

        final Map<Access, Integer> accessCount = new HashMap<>();
        final Map<Access, List<Principal>> accessPrincipals = new HashMap<>();

        final UserMembersResolver resolver = new UserMembersResolver( this.securityService );
        for ( Access access : Access.values() )
        {
            final Set<PrincipalKey> resolvedMembers = new HashSet<>();
            final Collection<PrincipalKey> permissionPrincipals = accessMembers.get( access );
            if ( permissionPrincipals.contains( RoleKeys.EVERYONE ) )
            {
                final PrincipalQueryResult totalUsersResult = this.getTotalUsers();
                accessCount.put( access, totalUsersResult.getTotalSize() );
                accessPrincipals.put( access, totalUsersResult.getPrincipals().getList() );
                continue;
            }

            for ( PrincipalKey principal : permissionPrincipals )
            {
                if ( principal.isUser() )
                {
                    resolvedMembers.add( principal );
                }
                else
                {
                    final PrincipalKeys members = resolver.getUserMembers( principal );
                    Iterables.addAll( resolvedMembers, members );
                }
            }

            accessCount.put( access, toIntExact( resolvedMembers.stream().filter( PrincipalKey::isUser ).count() ) );
            final List<Principal> principals = resolvedMembers.stream().
                filter( PrincipalKey::isUser ).
                map( ( key ) -> this.securityService.getUser( key ).orElse( null ) ).
                filter( Objects::nonNull ).
                limit( MAX_EFFECTIVE_PERMISSIONS_PRINCIPALS ).
                collect( Collectors.toList() );
            accessPrincipals.put( access, principals );
        }

        final List<EffectivePermissionJson> permissionsJson = Lists.newArrayList();
        for ( Access access : Access.values() )
        {
            final EffectivePermissionAccessJson accessJson = new EffectivePermissionAccessJson();
            accessJson.count = accessCount.get( access );
            accessJson.users = accessPrincipals.get( access ).
                stream().map( ( p ) -> new EffectivePermissionMemberJson( p.getKey().toString(), p.getDisplayName() ) ).
                toArray( EffectivePermissionMemberJson[]::new );

            permissionsJson.add( new EffectivePermissionJson( access.name(), accessJson ) );
        }
        return permissionsJson;
    }

    @POST
    @Path("reprocess")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @RolesAllowed(RoleKeys.ADMIN_ID)
    @ApiOperation("Reprocesses content")
    public ReprocessContentResultJson reprocess( final ReprocessContentRequestJson request )
    {
        final List<ContentPath> updated = new ArrayList<>();
        final List<String> errors = new ArrayList<>();

        final Content content = this.contentService.getByPath( request.getSourceBranchPath().getContentPath() );
        try
        {
            reprocessContent( content, request.isSkipChildren(), updated, errors );
        }
        catch ( Throwable t )
        {
            errors.add(
                String.format( "Content '%s' - %s: %s", content.getPath().toString(), t.getClass().getCanonicalName(), t.getMessage() ) );
            LOG.warn( "Error reprocessing content [" + content.getPath() + "]", t );
        }

        return new ReprocessContentResultJson( ContentPaths.from( updated ), errors );
    }

    private void reprocessContent( final Content content, final boolean skipChildren, final List<ContentPath> updated,
                                   final List<String> errors )
    {
        final Content reprocessedContent = this.contentService.reprocess( content.getId() );
        if ( !reprocessedContent.equals( content ) )
        {
            updated.add( content.getPath() );
        }
        if ( skipChildren )
        {
            return;
        }

        int from = 0;
        int resultCount;
        do
        {
            final FindContentByParentParams findParams = FindContentByParentParams.create().parentId( content.getId() ).
                from( from ).size( 5 ).build();
            final FindContentByParentResult results = this.contentService.findByParent( findParams );

            for ( Content child : results.getContents() )
            {
                try
                {
                    reprocessContent( child, false, updated, errors );
                }
                catch ( Throwable t )
                {
                    errors.add( String.format( "Content '%s' - %s: %s", child.getPath().toString(), t.getClass().getCanonicalName(),
                                               t.getMessage() ) );
                    LOG.warn( "Error reprocessing content [" + child.getPath() + "]", t );
                }
            }
            resultCount = Math.toIntExact( results.getHits() );
            from = from + resultCount;
        }
        while ( resultCount > 0 );
    }

    private Content doCreateAttachment( final String attachmentName, final MultipartForm form )
    {
        final MultipartItem mediaFile = form.get( "file" );

        final CreateAttachment attachment = CreateAttachment.create().
            name( attachmentName ).
            mimeType( mediaFile.getContentType().toString() ).
            byteSource( getFileItemByteSource( mediaFile ) ).
            build();

        final UpdateContentParams params = new UpdateContentParams().
            contentId( ContentId.from( form.getAsString( "id" ) ) ).
            createAttachments( CreateAttachments.from( attachment ) );

        return contentService.update( params );
    }

    private PrincipalQueryResult getTotalUsers()
    {
        final PrincipalQuery query = PrincipalQuery.create().includeUsers().size( MAX_EFFECTIVE_PERMISSIONS_PRINCIPALS ).build();
        return this.securityService.query( query );
    }

    private String getFormattedDisplayName( Locale locale )
    {
        return locale.getDisplayName( locale ) + " (" + locale.toLanguageTag() + ")";
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

    private ByteSource getFileItemByteSource( final MultipartItem item )
    {
        return item.getBytes();
    }

    private ContentPaths filterChildrenIfParentPresents( final ContentPaths sourceContentPaths )
    {
        ContentPaths filteredContentPaths = ContentPaths.empty();

        for ( ContentPath contentPath : sourceContentPaths )
        {
            boolean hasParent = sourceContentPaths.stream().anyMatch( contentPath::isChildOf );
            if ( !hasParent )
            {
                filteredContentPaths = filteredContentPaths.add( contentPath );
            }
        }

        return filteredContentPaths;
    }

    private long countContentsAndTheirChildren( final ContentPaths contentsPaths )
    {
        return contentsPaths.getSize() + ( contentsPaths.isEmpty() ? 0 : countChildren( contentsPaths ) );
    }

    private long countChildren( final ContentPaths contentsPaths )
    {
        FindContentByQueryResult result = this.contentService.find( FindContentByQueryParams.create().
            contentQuery( ContentQuery.create().size( 0 ).queryExpr( constructExprToFindChildren( contentsPaths ) ).build() ).
            build() );

        return result.getTotalHits();
    }

    private QueryExpr constructExprToFindChildren(final ContentPaths contentsPaths )
    {
        final FieldExpr fieldExpr = FieldExpr.from( "_path" );

        ConstraintExpr expr = CompareExpr.like( fieldExpr, ValueExpr.string( "/content" + contentsPaths.first() + "/*" ) );

        for ( ContentPath contentPath : contentsPaths )
        {
            if ( !contentPath.equals( contentsPaths.first() ) )
            {
                ConstraintExpr likeExpr =
                    CompareExpr.like( fieldExpr, ValueExpr.string( "/content" + contentPath + "/*" ) );
                expr = LogicalExpr.or( expr, likeExpr );
            }
        }

        return QueryExpr.from( expr, new FieldOrderExpr( fieldExpr, OrderExpr.Direction.ASC ) );
    }

    private ContentSummaryListJson getDescendantsOfContents( final ContentPaths contentsPaths )
    {
        FindContentByQueryResult result = this.contentService.find( FindContentByQueryParams.create().
                contentQuery( ContentQuery.create().size( Integer.MAX_VALUE ).queryExpr( constructExprToFindChildren( contentsPaths ) ).build() ).
                build() );

        final ContentListMetaData metaData = ContentListMetaData.create().
                totalHits( result.getTotalHits() ).
                hits( result.getHits() ).
                build();

        return new ContentSummaryListJson( result.getContents(), metaData, contentIconUrlResolver );
    }

    private boolean contentNameIsOccupied( final RenameContentParams renameParams )
    {
        Content content = contentService.getById( renameParams.getContentId() );
        if ( content.getName().equals( renameParams.getNewName() ) )
        {
            return false;
        }

        ContentPath newPath = ContentPath.from( content.getParentPath(), renameParams.getNewName().toString() );
        try
        {
            contentService.getByPath( newPath );
        }
        catch ( ContentNotFoundException e )
        {
            return false;
        }

        return true;
    }

    @Reference
    public void setContentService( final ContentService contentService )
    {
        this.contentService = contentService;
    }

    @Reference
    public void setContentTypeService( final ContentTypeService contentTypeService )
    {
        this.contentIconUrlResolver = new ContentIconUrlResolver( contentTypeService );
    }

    @Reference
    public void setSecurityService( final SecurityService securityService )
    {
        this.principalsResolver = new ContentPrincipalsResolver( securityService );
        this.securityService = securityService;
    }

    @Reference
    public void setRelationshipTypeService( final RelationshipTypeService relationshipTypeService )
    {
        this.relationshipTypeService = relationshipTypeService;
    }
}
