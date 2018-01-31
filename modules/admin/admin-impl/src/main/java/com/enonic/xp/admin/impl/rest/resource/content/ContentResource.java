package com.enonic.xp.admin.impl.rest.resource.content;

import com.enonic.xp.admin.impl.json.content.*;
import com.enonic.xp.admin.impl.json.content.attachment.AttachmentJson;
import com.enonic.xp.admin.impl.json.content.attachment.AttachmentListJson;
import com.enonic.xp.admin.impl.rest.resource.ResourceConstants;
import com.enonic.xp.admin.impl.rest.resource.content.json.*;
import com.enonic.xp.admin.impl.rest.resource.content.query.ContentQueryWithChildren;
import com.enonic.xp.admin.impl.rest.resource.content.task.*;
import com.enonic.xp.admin.impl.rest.resource.schema.content.ContentTypeIconResolver;
import com.enonic.xp.admin.impl.rest.resource.schema.content.ContentTypeIconUrlResolver;
import com.enonic.xp.attachment.Attachment;
import com.enonic.xp.attachment.AttachmentNames;
import com.enonic.xp.attachment.CreateAttachment;
import com.enonic.xp.attachment.CreateAttachments;
import com.enonic.xp.branch.Branches;
import com.enonic.xp.content.*;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.extractor.BinaryExtractor;
import com.enonic.xp.extractor.ExtractedData;
import com.enonic.xp.index.ChildOrder;
import com.enonic.xp.jaxrs.JaxRsComponent;
import com.enonic.xp.jaxrs.JaxRsExceptions;
import com.enonic.xp.query.parser.QueryParser;
import com.enonic.xp.schema.content.ContentTypeService;
import com.enonic.xp.schema.relationship.RelationshipTypeService;
import com.enonic.xp.security.*;
import com.enonic.xp.security.acl.AccessControlEntry;
import com.enonic.xp.security.acl.AccessControlList;
import com.enonic.xp.security.acl.Permission;
import com.enonic.xp.security.auth.AuthenticationInfo;
import com.enonic.xp.task.TaskService;
import com.enonic.xp.web.HttpStatus;
import com.enonic.xp.web.multipart.MultipartForm;
import com.enonic.xp.web.multipart.MultipartItem;
import com.google.common.collect.*;
import com.google.common.io.ByteSource;
import org.apache.commons.lang.StringUtils;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.time.Instant;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.lang.Math.toIntExact;
import static org.apache.commons.lang.StringUtils.containsIgnoreCase;
import static org.apache.commons.lang.StringUtils.isBlank;

@SuppressWarnings("UnusedDeclaration")
@Path(ResourceConstants.REST_ROOT + "content")
@Produces(MediaType.APPLICATION_JSON)
@RolesAllowed(RoleKeys.ADMIN_LOGIN_ID)
@Component(immediate = true, property = "group=admin")
public final class ContentResource
    implements JaxRsComponent
{
    public static final String DEFAULT_SORT_FIELD = "modifiedTime";

    public static final int GET_ALL_SIZE_FLAG = -1;

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

    private ContentTypeIconUrlResolver contentTypeIconUrlResolver;

    private BinaryExtractor extractor;

    private TaskService taskService;

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
    public TaskResultJson duplicate( final DuplicateContentJson params )
    {
        return DuplicateRunnableTask.create().
            params( params ).
            description( "Duplicate content" ).
            taskService( taskService ).
            contentService( contentService ).
            authInfo( ContextAccessor.current().getAuthInfo() ).
            build().
            createTaskResult();
    }

    @POST
    @Path("move")
    public TaskResultJson move( final MoveContentJson params )
    {
        return MoveRunnableTask.create().
            params( params ).
            description( "Move content" ).
            taskService( taskService ).
            contentService( contentService ).
            build().
            createTaskResult();
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
        validatePublishInfo( json );

        final UpdateContentParams updateParams = json.getUpdateContentParams();

        final AccessControlList permissionsBeforeSave = contentService.getPermissionsById( updateParams.getContentId() );

        final Content updatedContent = contentService.update( updateParams );

        if ( !permissionsBeforeSave.equals( updatedContent.getPermissions() ) )
        {
            this.contentService.applyPermissions( json.getApplyContentPermissionsParams() );
        }

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
    @Path("undoPendingDelete")
    public UndoPendingDeleteContentResultJson undoPendingDelete( final UndoPendingDeleteContentJson params )
    {
        UndoPendingDeleteContentResultJson result = new UndoPendingDeleteContentResultJson();
        int numberOfContents = this.contentService.undoPendingDelete( UndoPendingDeleteContentParams.create().
            contentIds( ContentIds.from( params.getContentIds() ) ).
            target( ContentConstants.BRANCH_MASTER ).
            build() );
        return result.setSuccess( numberOfContents );
    }

    @POST
    @Path("delete")
    public TaskResultJson delete( final DeleteContentJson params )
    {
        return DeleteRunnableTask.create().
            params( params ).
            description( "Delete content" ).
            taskService( taskService ).
            contentService( contentService ).
            build().
            createTaskResult();
    }

    @POST
    @Path("getDependencies")
    public GetDependenciesResultJson getDependencies( final ContentIdsJson params )
    {
        final Map<String, DependenciesJson> result = Maps.newHashMap();

        params.getContentIds().forEach( (id -> {
            final ContentDependencies dependencies = contentService.getDependencies( id );

            final List<DependenciesAggregationJson> inbound = dependencies.getInbound().stream().
                map( aggregation -> new DependenciesAggregationJson( aggregation, this.contentTypeIconUrlResolver ) ).collect(
                Collectors.toList() );

            final List<DependenciesAggregationJson> outbound = dependencies.getOutbound().stream().
                map( aggregation -> new DependenciesAggregationJson( aggregation, this.contentTypeIconUrlResolver ) ).collect(
                Collectors.toList() );

            result.put( id.toString(), new DependenciesJson( inbound, outbound ));
        }) );

        return new GetDependenciesResultJson(result);
    }

    @POST
    @Path("publish")
    public TaskResultJson publish( final PublishContentJson params )
    {
        return PublishRunnableTask.create().
            params( params ).
            description( "Publish content" ).
            taskService( taskService ).
            contentService( contentService ).
            build().
            createTaskResult();
    }

    @POST
    @Path("unpublish")
    public TaskResultJson unpublish( final UnpublishContentJson params )
    {
        return UnpublishRunnableTask.create().
            params( params ).
            description( "Unpublish content" ).
            taskService( taskService ).
            contentService( contentService ).
            build().
            createTaskResult();
    }

    @POST
    @Path("hasUnpublishedChildren")
    public HasUnpublishedChildrenResultJson hasUnpublishedChildren( final ContentIdsJson ids )
    {
        final HasUnpublishedChildrenResultJson.Builder result = HasUnpublishedChildrenResultJson.create();

        ids.getContentIds().forEach( contentId ->
                                     {
                                         final Boolean hasChildren = this.contentService.hasUnpublishedChildren(
                                             new HasUnpublishedChildrenParams( contentId, ContentConstants.BRANCH_MASTER ) );

                                         result.addHasChildren( contentId, hasChildren );
                                     } );

        return result.build();
    }

    @POST
    @Path("resolvePublishContent")
    public ResolvePublishContentResultJson resolvePublishContent( final ResolvePublishDependenciesJson params )
    {
        //Resolved the requested ContentPublishItem
        final ContentIds requestedContentIds = ContentIds.from( params.getIds() );
        final ContentIds excludeContentIds = ContentIds.from( params.getExcludedIds() );
        final ContentIds excludeChildrenIds = ContentIds.from( params.getExcludeChildrenIds() );

        //Resolves the publish dependencies
        final CompareContentResults compareResults = contentService.resolvePublishDependencies( ResolvePublishDependenciesParams.create().
            target( ContentConstants.BRANCH_MASTER ).
            contentIds( requestedContentIds ).
            excludedContentIds( excludeContentIds ).
            excludeChildrenIds( excludeChildrenIds ).
            build() );

        //Resolved the dependent ContentPublishItem
        final ContentIds dependentContentIds = ContentIds.from( compareResults.contentIds().
            stream().
            filter( contentId -> !requestedContentIds.contains( contentId ) ).
            collect( Collectors.toList() ) );

        final ContentIds fullPublishList = ContentIds.create().addAll( dependentContentIds ).addAll( requestedContentIds ).build();

        //Resolve required ids
        final ContentIds requiredIds = this.contentService.resolveRequiredDependencies( ResolveRequiredDependenciesParams.create().
            contentIds( fullPublishList ).
            target( ContentConstants.BRANCH_MASTER ).
            build() );

        final AuthenticationInfo authInfo = ContextAccessor.current().getAuthInfo();

        final Predicate<ContentId> publishAllowedCondition = id -> this.contentService.getPermissionsById( id ).isAllowedFor( ContextAccessor.current().getAuthInfo().getPrincipals(),
                                                                                                                        Permission.PUBLISH );
        //check if user has access to publish every content
        final Boolean isAllPublishable = authInfo.hasRole( RoleKeys.ADMIN ) ? true : fullPublishList.stream().allMatch(publishAllowedCondition);

        //filter required dependant ids
        final ContentIds requiredDependantIds = ContentIds.from( requiredIds.stream().
            filter( contentId -> !requestedContentIds.contains( contentId ) ).
            collect( Collectors.toList() ) );

        final ContentIds invalidContentIds = getInvalidContent( compareResults );

        //sort all dependant content ids
        final ContentIds sortedDependentContentIds =
            dependentContentIds.getSize() > 0 ? sortContentIds( dependentContentIds, "_path" ) : dependentContentIds;

        final ContentIds sortedInvalidContentIds =
            invalidContentIds.getSize() > 0 ? sortContentIds( invalidContentIds, "_path" ) : invalidContentIds;

        //Returns the JSON result
        return ResolvePublishContentResultJson.create().
            setRequestedContents( requestedContentIds ).
            setDependentContents( this.invalidDependantsOnTop( sortedDependentContentIds, requestedContentIds, sortedInvalidContentIds ) ).
            setRequiredContents( requiredDependantIds ).
            setAllPublishable( isAllPublishable ).
            setContainsInvalid( !invalidContentIds.isEmpty() ).
            build();
    }

    private ContentIds sortContentIds( final ContentIds contentIds, final String field )
    {
        if ( StringUtils.isBlank( field ) )
        {
            return contentIds;
        }

        return this.contentService.find( ContentQuery.create().
            filterContentIds( contentIds ).
            queryExpr( QueryParser.parse( "order by " + field ) ).
            size( -1 ).
            build() ).getContentIds();
    }

    private ContentIds invalidDependantsOnTop( final ContentIds dependentContentIdList, final ContentIds requestedContentIds,
                                               final ContentIds invalidContentIds )
    {
        return ContentIds.from( Stream.concat( invalidContentIds.stream().filter( ( e ) -> !requestedContentIds.contains( e ) ),
                                               dependentContentIdList.stream().filter(
                                                   ( e ) -> !invalidContentIds.contains( e ) && !requestedContentIds.contains( e ) ) ).
            collect( Collectors.toList() ) );
    }

    private ContentIds getInvalidContent( final CompareContentResults compareResults )
    {
        return contentService.getInvalidContent(
            ContentIds.from( compareResults.stream().filter( ( result ) -> result.getCompareStatus() != CompareStatus.PENDING_DELETE ).
                map( CompareContentResult::getContentId ).
                collect( Collectors.toList() ) ) );
    }

    private Boolean isAnyContentRemovableFromPublish( final ContentIds contentIds )
    {
        final CompareContentResults compareContentResults =
            contentService.compare( new CompareContentsParams( contentIds, ContentConstants.BRANCH_MASTER ) );

        return compareContentResults.getCompareContentResultsMap().values().stream().anyMatch(
            result -> CompareStatus.NEWER == result.getCompareStatus() );
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
            // sorted( ( content1, content2 ) -> content1.getPath().compareTo( content2.getPath() ) ).
                map( content ->
                     {
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

    @POST
    @Path("resolveByIds")
    public ContentSummaryListJson getByIds( final ContentIdsJson params )
    {
        final Contents contents = contentService.getByIds( new GetContentByIdsParams( params.getContentIds() ) );

        if ( contents == null )
        {
            throw JaxRsExceptions.notFound( String.format( "Contents [%s] was not found", params.getContentIds() ) );
        }
        final ContentListMetaData metaData = ContentListMetaData.create().
            totalHits( contents.getSize() ).
            hits( contents.getSize() ).
            build();

        return new ContentSummaryListJson( contents, metaData, contentIconUrlResolver );
    }

    @POST
    @Path("isReadOnlyContent")
    public List<String> checkContentsReadOnly( final ContentIdsJson params )
    {
        final Contents contents = contentService.getByIds( new GetContentByIdsParams( params.getContentIds() ) );

        final AuthenticationInfo authInfo = ContextAccessor.current().getAuthInfo();

        if ( authInfo.hasRole( RoleKeys.ADMIN ) )
        {
            return new ArrayList<>();
        }

        final List<String> result = new ArrayList<>();

        contents.stream().forEach( content ->
                                   {
                                       if ( !content.getPermissions().isAllowedFor( authInfo.getPrincipals(), Permission.MODIFY ) )
                                       {
                                           result.add( content.getId().toString() );
                                       }
                                   } );

        return result;
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
    @Path("contentPermissionsByIds")
    public List<ContentPermissionsJson> getPermissionsByIds( final ContentIdsJson params )
    {
        final List<ContentPermissionsJson> result = new ArrayList<>();
        for ( final ContentId contentId : params.getContentIds() )
        {
            final AccessControlList permissions = contentService.getPermissionsById( contentId );
            result.add( new ContentPermissionsJson( contentId.toString(), permissions, principalsResolver ) );
        }

        return result;
    }

    @POST
    @Path("contentsExist")
    public ContentsExistJson contentsExist( final ContentIdsJson params )
    {
        final ContentsExistJson result = new ContentsExistJson();
        for ( final ContentId contentId : params.getContentIds() )
        {
            result.add( contentId, contentService.contentExists( contentId ) );
        }

        return result;
    }

    @POST
    @Path("allowedActions")
    public List<String> getPermittedActions( final ContentIdsPermissionsJson params )
    {
        final List<Permission> permissions =
            params.getPermissions().size() > 0 ? params.getPermissions() : Arrays.asList( Permission.values() );

        final AuthenticationInfo authInfo = ContextAccessor.current().getAuthInfo();

        if ( authInfo.hasRole( RoleKeys.ADMIN ) )
        {
            return permissions.stream().map( p -> p.name() ).collect( Collectors.toList() );
        }

        final List<AccessControlList> contentsPermissions =
            params.getContentIds().getSize() > 0
                ? contentService.getByIds( new GetContentByIdsParams( params.getContentIds() ) ).
                stream().map( Content::getPermissions ).collect( Collectors.toList() )
                : Collections.singletonList( contentService.getRootPermissions() );

        final List<String> result = new ArrayList<>();

        permissions.forEach( permission ->
                             {
                                 if ( userHasPermission( authInfo, permission, contentsPermissions ) )
                                 {
                                     result.add( permission.name() );
                                 }
                             } );

        return result;
    }

    private boolean userHasPermission( final AuthenticationInfo authInfo, final Permission permission,
                                       final List<AccessControlList> contentsPermissions )
    {
        final PrincipalKeys authInfoPrincipals = authInfo.getPrincipals();

        return contentsPermissions.stream().
            allMatch( contentPermissions -> contentPermissions.isAllowedFor( authInfoPrincipals, permission ) );
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
    public List<ContentIdJson> getDescendantsOfContents( final GetDescendantsOfContents json )
    {
        final ContentPaths contentsPaths = ContentPaths.from( json.getContentPaths() );

        FindContentIdsByQueryResult result = ContentQueryWithChildren.create().
            contentService( this.contentService ).
            contentsPaths( contentsPaths ).
            size( GET_ALL_SIZE_FLAG ).
            build().
            find();

        final Boolean isFilterNeeded = json.getFilterStatuses() != null && json.getFilterStatuses().size() > 0;

        if ( isFilterNeeded )
        {
            return this.filterIdsByStatus( result.getContentIds(), json.getFilterStatuses() ).
                map( ContentIdJson::new ).
                collect( Collectors.toList() );
        }
        else
        {
            return result.getContentIds().stream().map( ContentIdJson::new ).collect( Collectors.toList() );
        }
    }

    private Stream<ContentId> filterIdsByStatus( final ContentIds ids, final Collection<CompareStatus> statuses )
    {
        final CompareContentResults compareResults =
            contentService.compare( new CompareContentsParams( ids, ContentConstants.BRANCH_MASTER ) );
        final Map<ContentId, CompareContentResult> compareResultMap = compareResults.getCompareContentResultsMap();

        return compareResultMap.entrySet().
            stream().
            filter( entry -> statuses.contains( entry.getValue().getCompareStatus() ) ).
            map( Map.Entry::getKey );
    }

    @POST
    @Path("countContentsWithDescendants")
    public long countContentsWithDescendants( final GetDescendantsOfContents json )
    {
        final ContentPaths paths = ContentPaths.from( json.getContentPaths() );
        List<ContentPath> filteredPaths =
            paths.stream().filter( contentPath -> paths.stream().noneMatch( contentPath::isChildOf ) ).collect( Collectors.toList() );
        return this.countContentsAndTheirChildren( ContentPaths.from( filteredPaths ) );
    }

    @POST
    @Path("query")
    @Consumes(MediaType.APPLICATION_JSON)
    public AbstractContentQueryResultJson query( final ContentQueryJson contentQueryJson )
    {
        //TODO: do we need this param? it does not seem to be checked at all
        final boolean getChildrenIds = !Expand.NONE.matches( contentQueryJson.getExpand() );

        final ContentQueryJsonToContentQueryConverter selectorQueryProcessor = ContentQueryJsonToContentQueryConverter.create().
            contentQueryJson( contentQueryJson ).
            contentService( this.contentService ).
            build();

        final ContentIconUrlResolver iconUrlResolver = contentIconUrlResolver;
        final FindContentIdsByQueryResult findResult = contentService.find( selectorQueryProcessor.createQuery() );

        return FindContentByQuertResultJsonFactory.create().
            contents( this.contentService.getByIds( new GetContentByIdsParams( findResult.getContentIds() ) ) ).
            aggregations( findResult.getAggregations() ).
            contentPrincipalsResolver( principalsResolver ).
            iconUrlResolver( iconUrlResolver ).
            expand( contentQueryJson.getExpand() ).
            hits( findResult.getHits() ).
            totalHits( findResult.getTotalHits() ).
            build().
            execute();
    }

    @POST
    @Path("selectorQuery")
    @Consumes(MediaType.APPLICATION_JSON)
    public AbstractContentQueryResultJson selectorQuery( final ContentSelectorQueryJson contentQueryJson )
    {
        final FindContentIdsByQueryResult findResult = findContentsBySelectorQuery( contentQueryJson );

        return FindContentByQuertResultJsonFactory.create().
            contents( this.contentService.getByIds( new GetContentByIdsParams( findResult.getContentIds() ) ) ).
            aggregations( findResult.getAggregations() ).
            contentPrincipalsResolver( principalsResolver ).
            iconUrlResolver( contentIconUrlResolver ).
            expand( contentQueryJson.getExpand() ).
            hits( findResult.getHits() ).
            totalHits( findResult.getTotalHits() ).
            build().
            execute();
    }

    @POST
    @Path("treeSelectorQuery")
    @Consumes(MediaType.APPLICATION_JSON)
    public ContentTreeSelectorListJson treeSelectorQuery( final ContentTreeSelectorQueryJson contentQueryJson )
    {
        final Integer from  = contentQueryJson.getFrom();
        contentQueryJson.setFrom( 0 );

        final Integer size = contentQueryJson.getSize();
        contentQueryJson.setSize( -1 );

        final ContentPaths targetContentPaths = findContentPathsBySelectorQuery( contentQueryJson );

        final ContentPath parentPath = contentQueryJson.getParentPath();
        final Integer parentPathSize = parentPath != null ? parentPath.elementCount() : 0;

        final Set<ContentPath> layerPaths = targetContentPaths.stream().
            filter( path -> parentPath != null ? path.isChildOf( parentPath ) : true ).
            map( path -> path.getAncestorPath( path.elementCount() - parentPathSize - 1 ) ).

            collect( Collectors.toSet() );

        if(layerPaths.size() == 0) {
            return ContentTreeSelectorListJson.empty();
        }

        final ChildOrder layerOrder = parentPath == null
            ? ContentConstants.DEFAULT_CONTENT_REPO_ROOT_ORDER
            : contentQueryJson.getChildOrder() != null ? contentQueryJson.getChildOrder() : ContentConstants.DEFAULT_CHILD_ORDER;

        final FindContentIdsByQueryResult findLayerContentsResult = ContentQueryWithChildren.create().
            contentService( this.contentService ).
            contentsPaths( ContentPaths.from( layerPaths ) ).
            order( layerOrder ).
            from( from ).
            size( size ).
            build().
            findOrdered();

        final List<ContentTreeSelectorJson> resultItems = contentService.getByIds( new GetContentByIdsParams( findLayerContentsResult.getContentIds() ) ).
            stream().
            map( content -> new ContentTreeSelectorJson( new ContentJson( content, contentIconUrlResolver, principalsResolver ),
                                                         targetContentPaths.contains( content.getPath().asRelative() ),
                                                         targetContentPaths.stream().anyMatch( path -> path.isChildOf( content.getPath() )) )).
            collect( Collectors.toList() );

        final ContentListMetaData metaData = ContentListMetaData.create().hits( findLayerContentsResult.getHits() ).totalHits(
            findLayerContentsResult.getTotalHits() ).build();

        return new ContentTreeSelectorListJson( resultItems, metaData );
    }

    private FindContentIdsByQueryResult findContentsBySelectorQuery( final ContentSelectorQueryJson contentQueryJson )
    {
        return contentService.find( makeConverterFromSelectorQuery( contentQueryJson ).createQuery() );
    }

    private ContentPaths findContentPathsBySelectorQuery( final ContentSelectorQueryJson contentQueryJson )
    {
        return contentService.findContentPaths( makeConverterFromSelectorQuery( contentQueryJson ).createQuery() );
    }

    private ContentSelectorQueryJsonToContentQueryConverter makeConverterFromSelectorQuery(
        final ContentSelectorQueryJson contentQueryJson )
    {
        return ContentSelectorQueryJsonToContentQueryConverter.create().
            contentQueryJson( contentQueryJson ).
            contentService( this.contentService ).
            relationshipTypeService( this.relationshipTypeService ).
            build();
    }

    @POST
    @Path("compare")
    public CompareContentResultsJson compare( final CompareContentsJson params )
    {
        final ContentIds contentIds = ContentIds.from( params.getIds() );
        final CompareContentResults compareResults =
            contentService.compare( new CompareContentsParams( contentIds, ContentConstants.BRANCH_MASTER ) );
        final GetPublishStatusesResult getPublishStatusesResult =
            contentService.getPublishStatuses( new GetPublishStatusesParams( contentIds, ContentConstants.BRANCH_DRAFT ) );
        return new CompareContentResultsJson( compareResults, getPublishStatusesResult );
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

    @GET
    @Path("listIds")
    public List<ContentIdJson> listChildrenIds( @QueryParam("parentId") final String parentId,
                                                @QueryParam("childOrder") @DefaultValue("") final String childOrder )
    {

        final FindContentByParentParams params = FindContentByParentParams.create().
            parentId( StringUtils.isNotEmpty( parentId ) ? ContentId.from( parentId ) : null ).
            childOrder( StringUtils.isNotEmpty( childOrder ) ? ChildOrder.from( childOrder ) : null ).
            build();

        final FindContentIdsByParentResult result = this.contentService.findIdsByParent( params );
        return result.getContentIds().stream().map( contentId -> new ContentIdJson( contentId ) ).collect( Collectors.toList() );
    }

    private Content doCreateAttachment( final String attachmentName, final MultipartForm form )
    {
        final MultipartItem mediaFile = form.get( "file" );

        final ExtractedData extractedData = this.extractor.extract( mediaFile.getBytes() );

        final CreateAttachment attachment = CreateAttachment.create().
            name( attachmentName ).
            mimeType( mediaFile.getContentType().toString() ).
            byteSource( getFileItemByteSource( mediaFile ) ).
            text( extractedData.getText() ).
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

    private long countContentsAndTheirChildren( final ContentPaths contentsPaths )
    {
        return contentsPaths.getSize() + ( contentsPaths.isEmpty() ? 0 : countChildren( contentsPaths ) );
    }

    private long countChildren( final ContentPaths contentsPaths )
    {
        return ContentQueryWithChildren.create().
            contentService( this.contentService ).
            contentsPaths( contentsPaths ).
            build().
            find().
            getTotalHits();
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

    private void validatePublishInfo( final UpdateContentJson updateContentJson )
    {

        final Instant publishToInstant = updateContentJson.getPublishToInstant();
        if ( publishToInstant != null )
        {
            final Instant publishFromInstant = updateContentJson.getPublishFromInstant();
            if ( publishFromInstant == null )
            {
                throw new WebApplicationException( "[Online to] date/time cannot be set without [Online from]",
                                                   HttpStatus.UNPROCESSABLE_ENTITY.value() );
            }
            if ( publishToInstant.compareTo( publishFromInstant ) < 0 )
            {
                throw new WebApplicationException( "[Online from] date/time must be earlier than [Online to]",
                                                   HttpStatus.UNPROCESSABLE_ENTITY.value() );
            }
        }
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
        this.contentTypeIconUrlResolver = new ContentTypeIconUrlResolver( new ContentTypeIconResolver( contentTypeService ) );
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

    @Reference
    public void setExtractor( final BinaryExtractor extractor )
    {
        this.extractor = extractor;
    }

    @Reference
    public void setTaskService( final TaskService taskService )
    {
        this.taskService = taskService;
    }
}
