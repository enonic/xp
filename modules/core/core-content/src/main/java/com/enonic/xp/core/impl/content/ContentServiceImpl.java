package com.enonic.xp.core.impl.content;

import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Predicate;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.NullMarked;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;

import com.google.common.io.ByteSource;

import com.enonic.xp.archive.ArchiveContentParams;
import com.enonic.xp.archive.ArchiveContentsResult;
import com.enonic.xp.archive.RestoreContentParams;
import com.enonic.xp.archive.RestoreContentsResult;
import com.enonic.xp.content.ApplyContentPermissionsParams;
import com.enonic.xp.content.ApplyContentPermissionsResult;
import com.enonic.xp.content.CompareContentResults;
import com.enonic.xp.content.CompareContentsParams;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentConstants;
import com.enonic.xp.content.ContentDependencies;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentIds;
import com.enonic.xp.content.ContentName;
import com.enonic.xp.content.ContentNotFoundException;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.ContentPaths;
import com.enonic.xp.content.ContentPublishInfo;
import com.enonic.xp.content.ContentQuery;
import com.enonic.xp.content.ContentService;
import com.enonic.xp.content.ContentValidator;
import com.enonic.xp.content.ContentValidityParams;
import com.enonic.xp.content.ContentValidityResult;
import com.enonic.xp.content.ContentVersionId;
import com.enonic.xp.content.Contents;
import com.enonic.xp.content.CreateContentParams;
import com.enonic.xp.content.CreateMediaParams;
import com.enonic.xp.content.DeleteContentParams;
import com.enonic.xp.content.DeleteContentsResult;
import com.enonic.xp.content.DuplicateContentParams;
import com.enonic.xp.content.DuplicateContentsResult;
import com.enonic.xp.content.FindContentByParentParams;
import com.enonic.xp.content.FindContentByParentResult;
import com.enonic.xp.content.FindContentIdsByParentResult;
import com.enonic.xp.content.FindContentIdsByQueryResult;
import com.enonic.xp.content.FindContentPathsByQueryResult;
import com.enonic.xp.content.GetActiveContentVersionsParams;
import com.enonic.xp.content.GetActiveContentVersionsResult;
import com.enonic.xp.content.GetContentByIdsParams;
import com.enonic.xp.content.GetContentVersionsParams;
import com.enonic.xp.content.GetContentVersionsResult;
import com.enonic.xp.content.GetPublishStatusResult;
import com.enonic.xp.content.GetPublishStatusesParams;
import com.enonic.xp.content.GetPublishStatusesResult;
import com.enonic.xp.content.HasUnpublishedChildrenParams;
import com.enonic.xp.content.MoveContentParams;
import com.enonic.xp.content.MoveContentsResult;
import com.enonic.xp.content.PatchContentParams;
import com.enonic.xp.content.PatchContentResult;
import com.enonic.xp.content.PublishContentResult;
import com.enonic.xp.content.PublishStatus;
import com.enonic.xp.content.PushContentParams;
import com.enonic.xp.content.ResolvePublishDependenciesParams;
import com.enonic.xp.content.ResolveRequiredDependenciesParams;
import com.enonic.xp.content.SortContentParams;
import com.enonic.xp.content.SortContentResult;
import com.enonic.xp.content.UnpublishContentParams;
import com.enonic.xp.content.UnpublishContentsResult;
import com.enonic.xp.content.UpdateContentMetadataParams;
import com.enonic.xp.content.UpdateContentMetadataResult;
import com.enonic.xp.content.UpdateContentParams;
import com.enonic.xp.content.UpdateMediaParams;
import com.enonic.xp.content.UpdateWorkflowParams;
import com.enonic.xp.content.UpdateWorkflowResult;
import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.core.impl.content.processor.ContentProcessor;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.event.EventPublisher;
import com.enonic.xp.exception.ForbiddenAccessException;
import com.enonic.xp.media.MediaInfoService;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeService;
import com.enonic.xp.page.PageDescriptorService;
import com.enonic.xp.project.ProjectName;
import com.enonic.xp.project.ProjectService;
import com.enonic.xp.region.LayoutDescriptorService;
import com.enonic.xp.region.PartDescriptorService;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.schema.content.ContentTypeService;
import com.enonic.xp.schema.mixin.MixinService;
import com.enonic.xp.security.acl.AccessControlList;
import com.enonic.xp.security.auth.AuthenticationInfo;
import com.enonic.xp.site.CmsService;
import com.enonic.xp.site.MixinMappingService;
import com.enonic.xp.site.Site;
import com.enonic.xp.site.SiteConfigService;
import com.enonic.xp.trace.Tracer;
import com.enonic.xp.util.BinaryReference;

@Component(configurationPid = "com.enonic.xp.content")
public class ContentServiceImpl
    implements ContentService
{
    public static final ContentName TEMPLATES_FOLDER_NAME = ContentName.from( "_templates" );

    private static final String TEMPLATES_FOLDER_DISPLAY_NAME = "Templates";

    private ContentTypeService contentTypeService;

    private final NodeService nodeService;

    private EventPublisher eventPublisher;

    private MediaInfoService mediaInfoService;

    private MixinService mixinService;

    private MixinMappingService mixinMappingService;

    private CmsService cmsService;

    private final List<ContentProcessor> contentProcessors = new CopyOnWriteArrayList<>();

    private final List<ContentValidator> contentValidators = new CopyOnWriteArrayList<>();

    private final SiteConfigService siteConfigService;

    private final PageDescriptorService pageDescriptorService;

    private final PartDescriptorService partDescriptorService;

    private final LayoutDescriptorService layoutDescriptorService;

    private ContentAuditLogSupport contentAuditLogSupport;

    private final ContentConfig config;

    @Activate
    public ContentServiceImpl( @Reference final NodeService nodeService, @Reference final PageDescriptorService pageDescriptorService,
                               @Reference final PartDescriptorService partDescriptorService,
                               @Reference final LayoutDescriptorService layoutDescriptorService,
                               @Reference final SiteConfigService siteConfigService, ContentConfig config )
    {
        this.config = config;
        this.nodeService = nodeService;
        this.pageDescriptorService = pageDescriptorService;
        this.partDescriptorService = partDescriptorService;
        this.layoutDescriptorService = layoutDescriptorService;
        this.siteConfigService = siteConfigService;
    }

    @Override
    public Content create( final CreateContentParams params )
    {
        verifyDraftBranch();

        final Content content = CreateContentCommand.create()
            .nodeService( this.nodeService )
            .contentTypeService( this.contentTypeService )
            .eventPublisher( this.eventPublisher )
            .cmsService( this.cmsService )
            .mixinService( this.mixinService )
            .contentProcessors( this.contentProcessors )
            .contentValidators( this.contentValidators )
            .mixinMappingService( this.mixinMappingService )
            .siteConfigService( this.siteConfigService )
            .pageDescriptorService( this.pageDescriptorService )
            .partDescriptorService( this.partDescriptorService )
            .layoutDescriptorService( this.layoutDescriptorService )
            .allowUnsafeAttachmentNames( config.attachments_allowUnsafeNames() )
            .params( params )
            .build()
            .execute();

        if ( content instanceof Site )
        {
            this.create( CreateContentParams.create()
                             .owner( content.getOwner() )
                             .displayName( TEMPLATES_FOLDER_DISPLAY_NAME )
                             .name( TEMPLATES_FOLDER_NAME )
                             .parent( content.getPath() )
                             .type( ContentTypeName.templateFolder() )
                             .requireValid( true )
                             .contentData( new PropertyTree() )
                             .build() );

            return content;
        }

        contentAuditLogSupport.createContent( params, content );

        return content;
    }

    @Override
    public Content create( final CreateMediaParams params )
    {
        verifyDraftBranch();

        final Content content = CreateMediaCommand.create()
            .params( params )
            .nodeService( this.nodeService )
            .contentTypeService( this.contentTypeService )
            .eventPublisher( this.eventPublisher )
            .mediaInfoService( this.mediaInfoService )
            .cmsService( this.cmsService )
            .mixinService( this.mixinService )
            .mixinMappingService( this.mixinMappingService )
            .siteConfigService( this.siteConfigService )
            .contentProcessors( this.contentProcessors )
            .contentValidators( this.contentValidators )
            .pageDescriptorService( this.pageDescriptorService )
            .partDescriptorService( this.partDescriptorService )
            .layoutDescriptorService( this.layoutDescriptorService )
            .allowUnsafeAttachmentNames( config.attachments_allowUnsafeNames() )
            .build()
            .execute();

        contentAuditLogSupport.createMedia( params, content );

        return content;
    }

    @Override
    public Content update( final UpdateContentParams params )
    {
        verifyDraftBranch();

        final Content content = UpdateContentCommand.create( params )
            .nodeService( this.nodeService )
            .contentTypeService( this.contentTypeService )
            .eventPublisher( this.eventPublisher )
            .cmsService( this.cmsService )
            .mixinService( this.mixinService )
            .contentProcessors( this.contentProcessors )
            .contentValidators( this.contentValidators )
            .pageDescriptorService( this.pageDescriptorService )
            .partDescriptorService( this.partDescriptorService )
            .layoutDescriptorService( this.layoutDescriptorService )
            .mixinMappingService( this.mixinMappingService )
            .siteConfigService( this.siteConfigService )
            .allowUnsafeAttachmentNames( config.attachments_allowUnsafeNames() )
            .build()
            .execute();

        contentAuditLogSupport.update( params, content );

        return content;
    }

    @Override
    public Content update( final UpdateMediaParams params )
    {
        verifyDraftBranch();

        final Content content = UpdateMediaCommand.create( params )
            .nodeService( this.nodeService )
            .contentTypeService( this.contentTypeService )
            .eventPublisher( this.eventPublisher )
            .mediaInfoService( this.mediaInfoService )
            .pageDescriptorService( this.pageDescriptorService )
            .partDescriptorService( this.partDescriptorService )
            .layoutDescriptorService( this.layoutDescriptorService )
            .cmsService( this.cmsService )
            .mixinService( this.mixinService )
            .mixinMappingService( this.mixinMappingService )
            .siteConfigService( this.siteConfigService )
            .contentProcessors( this.contentProcessors )
            .contentValidators( this.contentValidators )
            .allowUnsafeAttachmentNames( config.attachments_allowUnsafeNames() )
            .build()
            .execute();

        contentAuditLogSupport.update( params, content );

        return content;
    }

    @Override
    public DeleteContentsResult delete( final DeleteContentParams params )
    {
        verifyDraftBranch();

        final DeleteContentsResult result = DeleteContentCommand.create()
            .nodeService( this.nodeService )
            .contentTypeService( this.contentTypeService )
            .eventPublisher( this.eventPublisher )
            .params( params )
            .build()
            .execute();

        contentAuditLogSupport.delete( params, result );

        return result;
    }

    @Override
    public PublishContentResult publish( final PushContentParams params )
    {
        verifyDraftBranch();

        final PublishContentResult result = PublishContentCommand.create()
            .nodeService( this.nodeService )
            .contentTypeService( this.contentTypeService )
            .eventPublisher( this.eventPublisher )
            .contentIds( params.getContentIds() )
            .excludedContentIds( params.getExcludedContentIds() )
            .publishFrom( params.getPublishFrom() )
            .publishTo( params.getPublishTo() )
            .excludeDescendantsOf( params.getExcludeDescendantsOf() )
            .includeDependencies( params.isIncludeDependencies() )
            .pushListener( params.getPublishContentListener() )
            .message( params.getMessage() )
            .build()
            .execute();

        contentAuditLogSupport.publish( params, result );

        return result;
    }

    @Override
    public CompareContentResults resolvePublishDependencies( ResolvePublishDependenciesParams params )
    {
        return ResolveContentsToBePublishedCommand.create()
            .nodeService( this.nodeService )
            .contentTypeService( this.contentTypeService )
            .eventPublisher( this.eventPublisher )
            .contentIds( params.getContentIds() )
            .excludedContentIds( params.getExcludedContentIds() )
            .excludeDescendantsOf( params.getExcludeDescendantsOf() )
            .build()
            .execute();
    }

    @Override
    public ContentIds resolveRequiredDependencies( ResolveRequiredDependenciesParams params )
    {
        return ResolveRequiredDependenciesCommand.create()
            .nodeService( this.nodeService )
            .contentTypeService( this.contentTypeService )
            .eventPublisher( this.eventPublisher )
            .contentIds( params.getContentIds() )
            .build()
            .execute();
    }

    @Override
    public ContentValidityResult getContentValidity( final ContentValidityParams params )
    {
        return CheckContentValidityCommand.create()
            .nodeService( this.nodeService )
            .eventPublisher( this.eventPublisher )
            .contentTypeService( this.contentTypeService )
            .contentIds( params.getContentIds() )
            .build()
            .execute();
    }

    @Override
    public UnpublishContentsResult unpublish( final UnpublishContentParams params )
    {
        final UnpublishContentsResult result = UnpublishContentCommand.create()
            .params( params )
            .nodeService( this.nodeService )
            .contentTypeService( this.contentTypeService )
            .eventPublisher( this.eventPublisher )
            .build()
            .execute();

        contentAuditLogSupport.unpublishContent( params, result );

        return result;
    }

    @Override
    public Content getById( final ContentId contentId )
    {
        requireReadAccess();
        
        return Tracer.trace( "content.getById", trace -> trace.put( "id", contentId ), () -> doGetById( contentId ),
                             ( trace, content ) -> trace.put( "path", content.getPath().toString() ) );
    }

    private Content doGetById( final ContentId contentId )
    {
        final Content content = GetContentByIdCommand.create( contentId )
            .nodeService( this.nodeService )
            .contentTypeService( this.contentTypeService )
            .eventPublisher( this.eventPublisher )
            .build()
            .execute();
        if ( content == null )
        {
            throw ContentNotFoundException.create()
                .contentId( contentId )
                .repositoryId( ContextAccessor.current().getRepositoryId() )
                .branch( ContextAccessor.current().getBranch() )
                .contentRoot( ContentNodeHelper.getContentRoot() )
                .build();
        }
        return content;
    }

    @Override
    public Site getNearestSite( final ContentId contentId )
    {
        requireReadAccess();
        
        final GetNearestSiteCommand command = GetNearestSiteCommand.create()
            .contentId( contentId )
            .nodeService( this.nodeService )
            .contentTypeService( this.contentTypeService )
            .eventPublisher( this.eventPublisher )
            .build();
        return Tracer.trace( "content.getNearestSite", trace -> trace.put( "id", contentId ), command::execute, ( trace, site ) -> {
            if ( site != null )
            {
                trace.put( "path", site.getPath().toString() );
            }
        } );
    }

    @Override
    public Site findNearestSiteByPath( final ContentPath contentPath )
    {
        requireReadAccess();
        
        return Tracer.trace( "content.findNearestSiteByPath", trace -> trace.put( "contentPath", contentPath ),
                             () -> (Site) doFindNearestByPath( contentPath, Content::isSite ), ( trace, site ) -> {
                if ( site != null )
                {
                    trace.put( "path", site.getPath().toString() );
                }
            } );
    }

    private Content doFindNearestByPath( final ContentPath contentPath, final Predicate<Content> predicate )
    {
        return FindNearestContentByPathCommand.create()
            .contentPath( contentPath )
            .predicate( predicate )
            .nodeService( this.nodeService )
            .contentTypeService( this.contentTypeService )
            .eventPublisher( this.eventPublisher )
            .build()
            .execute();
    }

    @Override
    public Contents getByIds( final GetContentByIdsParams params )
    {
        requireReadAccess();
        
        final GetContentByIdsCommand command = GetContentByIdsCommand.create( params )
            .nodeService( this.nodeService )
            .contentTypeService( this.contentTypeService )
            .eventPublisher( this.eventPublisher )
            .build();

        return Tracer.trace( "content.getByIds", trace -> trace.put( "id", params.getIds() ), command::execute );
    }

    @Override
    public @NonNull Content getByPath( final ContentPath path )
    {
        requireReadAccess();
        
        return Tracer.trace( "content.getByPath", trace -> trace.put( "path", path ), () -> doGetByPath( path ),
                             ( trace, content ) -> trace.put( "id", content.getId() ) );
    }

    private Content doGetByPath( final ContentPath path )
    {
        final Content content = executeGetByPath( path );
        if ( content == null )
        {
            throw ContentNotFoundException.create()
                .contentPath( path )
                .repositoryId( ContextAccessor.current().getRepositoryId() )
                .branch( ContextAccessor.current().getBranch() )
                .contentRoot( ContentNodeHelper.getContentRoot() )
                .build();
        }
        return content;
    }

    private Content executeGetByPath( final ContentPath path )
    {
        return GetContentByPathCommand.create( path )
            .nodeService( this.nodeService )
            .contentTypeService( this.contentTypeService )
            .eventPublisher( this.eventPublisher )
            .build()
            .execute();
    }

    @Override
    public Contents getByPaths( final ContentPaths paths )
    {
        requireReadAccess();
        
        final GetContentByPathsCommand command = GetContentByPathsCommand.create( paths )
            .nodeService( this.nodeService )
            .contentTypeService( this.contentTypeService )
            .eventPublisher( this.eventPublisher )
            .build();

        return Tracer.trace( "content.getByPaths", trace -> trace.put( "path", paths ), command::execute );
    }

    @Override
    public FindContentByParentResult findByParent( final FindContentByParentParams params )
    {
        requireReadAccess();
        
        final FindContentByParentCommand command = FindContentByParentCommand.create( params )
            .nodeService( this.nodeService )
            .contentTypeService( this.contentTypeService )
            .eventPublisher( this.eventPublisher )
            .build();

        return Tracer.trace( "content.findByParent", trace -> {
            trace.put( "query", params.getParentPath() != null ? params.getParentPath() : params.getParentId() );
            trace.put( "from", params.getFrom() );
            trace.put( "size", params.getSize() );
        }, command::execute, ( trace, result ) -> trace.put( "hits", result.getTotalHits() ) );
    }

    @Override
    public FindContentIdsByParentResult findIdsByParent( final FindContentByParentParams params )
    {
        requireReadAccess();
        
        final FindContentIdsByParentCommand command = FindContentIdsByParentCommand.create( params )
            .nodeService( this.nodeService )
            .contentTypeService( this.contentTypeService )
            .eventPublisher( this.eventPublisher )
            .build();

        return Tracer.trace( "content.findIdsByParent", trace -> {
            trace.put( "query", params.getParentPath() != null ? params.getParentPath() : params.getParentId() );
            trace.put( "from", params.getFrom() );
            trace.put( "size", params.getSize() );
        }, command::execute, ( trace, result ) -> trace.put( "hits", result.getTotalHits() ) );
    }

    @Override
    public DuplicateContentsResult duplicate( final DuplicateContentParams params )
    {
        verifyDraftBranch();

        final DuplicateContentsResult result = DuplicateContentCommand.create( params )
            .nodeService( this.nodeService )
            .contentTypeService( this.contentTypeService )
            .eventPublisher( this.eventPublisher )
            .build()
            .execute();

        contentAuditLogSupport.duplicate( params, result );

        return result;
    }

    @Override
    public MoveContentsResult move( final MoveContentParams params )
    {
        verifyDraftBranch();

        final MoveContentsResult result = MoveContentCommand.create( params )
            .nodeService( this.nodeService )
            .contentTypeService( this.contentTypeService )
            .eventPublisher( this.eventPublisher )
            .mixinService( this.mixinService )
            .contentValidators( this.contentValidators )
            .build()
            .execute();

        contentAuditLogSupport.move( params, result );

        return result;
    }

    @Override
    public ArchiveContentsResult archive( final ArchiveContentParams params )
    {
        verifyDraftBranch();

        final ArchiveContentsResult result = ArchiveContentCommand.create( params )
            .nodeService( nodeService )
            .eventPublisher( eventPublisher )
            .contentTypeService( contentTypeService )
            .build()
            .execute();

        contentAuditLogSupport.archive( params, result );

        return result;
    }

    @Override
    public RestoreContentsResult restore( final RestoreContentParams params )
    {
        verifyDraftBranch();

        final RestoreContentsResult result = RestoreContentCommand.create( params )
            .nodeService( nodeService )
            .eventPublisher( eventPublisher )
            .contentTypeService( contentTypeService )
            .build()
            .execute();

        contentAuditLogSupport.restore( params, result );

        return result;
    }

    @Override
    public FindContentIdsByQueryResult find( final ContentQuery query )
    {
        requireReadAccess();
        
        final FindContentIdsByQueryCommand command = FindContentIdsByQueryCommand.create()
            .query( query )
            .nodeService( this.nodeService )
            .contentTypeService( this.contentTypeService )
            .eventPublisher( this.eventPublisher )
            .build();

        return Tracer.trace( "content.find", trace -> {
            trace.put( "query", query.getQueryExpr() );
            trace.put( "filter", query.getQueryFilters() );
            trace.put( "from", query.getFrom() );
            trace.put( "size", query.getSize() );
        }, command::execute, ( trace, result ) -> trace.put( "hits", result.getTotalHits() ) );
    }

    @Override
    public FindContentPathsByQueryResult findPaths( ContentQuery query )
    {
        requireReadAccess();
        
        return FindContentPathsByQueryCommand.create()
            .contentQuery( query )
            .nodeService( this.nodeService )
            .contentTypeService( this.contentTypeService )
            .eventPublisher( this.eventPublisher )
            .build()
            .execute();
    }

    @Override
    public CompareContentResults compare( final CompareContentsParams params )
    {
        return CompareContentsCommand.create()
            .nodeService( this.nodeService )
            .contentIds( params.getContentIds() )
            .target( ContentConstants.BRANCH_MASTER )
            .build()
            .execute();
    }

    @Override
    public GetPublishStatusesResult getPublishStatuses( final GetPublishStatusesParams params )
    {
        final Instant now = Instant.now();

        final Contents contents = ContextBuilder.from( ContextAccessor.current() )
            .branch( Objects.requireNonNullElseGet( params.getTarget(), ContextAccessor.current()::getBranch ) )
            .attribute( "ignorePublishTimes", Boolean.TRUE )
            .build()
            .callWith( () -> this.getByIds( GetContentByIdsParams.create().contentIds( params.getContentIds() ).build() ) );

        final GetPublishStatusesResult.Builder getPublishStatusesResult = GetPublishStatusesResult.create();

        contents.stream().map( content -> {

            final ContentPublishInfo publishInfo = content.getPublishInfo();
            if ( publishInfo != null )
            {
                if ( publishInfo.to() != null && publishInfo.to().compareTo( now ) < 0 )
                {
                    return new GetPublishStatusResult( content.getId(), PublishStatus.EXPIRED );
                }

                if ( publishInfo.from() != null && publishInfo.from().compareTo( now ) > 0 )
                {
                    return new GetPublishStatusResult( content.getId(), PublishStatus.PENDING );
                }
            }
            return new GetPublishStatusResult( content.getId(), PublishStatus.ONLINE );
        } ).forEach( getPublishStatusesResult::add );

        return getPublishStatusesResult.build();
    }

    @Override
    public GetContentVersionsResult getVersions( final GetContentVersionsParams params )
    {
        requireAnyProjectRole();

        return GetContentVersionsCommand.create()
            .nodeService( this.nodeService )
            .contentTypeService( this.contentTypeService )
            .eventPublisher( this.eventPublisher )
            .params( params )
            .build()
            .execute();
    }

    @Override
    public GetActiveContentVersionsResult getActiveVersions( final GetActiveContentVersionsParams params )
    {
        requireAnyProjectRole();

        return GetActiveContentVersionsCommand.create()
            .nodeService( this.nodeService )
            .contentTypeService( this.contentTypeService )
            .eventPublisher( this.eventPublisher )
            .params( params )
            .build()
            .execute();
    }

    @Override
    public SortContentResult sort( final SortContentParams params )
    {
        verifyDraftBranch();

        final SortContentResult result = SortContentCommand.create( params )
            .nodeService( this.nodeService )
            .contentTypeService( this.contentTypeService )
            .eventPublisher( this.eventPublisher )
            .build()
            .execute();

        contentAuditLogSupport.sort( params, result );

        return result;
    }

    @Override
    public boolean hasUnpublishedChildren( final HasUnpublishedChildrenParams params )
    {
        return nodeService.hasUnpublishedChildren( NodeId.from( params.getContentId() ), ContentConstants.BRANCH_MASTER );
    }

    @Override
    public ApplyContentPermissionsResult applyPermissions( final ApplyContentPermissionsParams params )
    {
        verifyDraftBranch();

        final ApplyContentPermissionsResult result = ApplyContentPermissionsCommand.create( params )
            .nodeService( this.nodeService )
            .contentTypeService( this.contentTypeService )
            .eventPublisher( this.eventPublisher )
            .build()
            .execute();

        contentAuditLogSupport.applyPermissions( params, result );

        return result;
    }

    @Override
    public AccessControlList getRootPermissions()
    {
        final ContentPath rootContentPath = ContentPath.ROOT;
        final NodePath rootNodePath = ContentNodeHelper.translateContentPathToNodePath( rootContentPath );
        final Node rootNode = nodeService.getByPath( rootNodePath );
        return rootNode != null ? rootNode.getPermissions() : AccessControlList.empty();
    }

    @Override
    public ContentDependencies getDependencies( final ContentId id )
    {
        return new ContentDependenciesResolver( this ).resolve( id );
    }

    @Override
    public ContentIds getOutboundDependencies( final ContentId id )
    {
        return new ContentOutboundDependenciesIdsResolver( this ).resolve( id );
    }

    @Override
    public boolean contentExists( final ContentId contentId )
    {
        final ContentExistsCommand command = ContentExistsCommand.create( contentId )
            .nodeService( this.nodeService )
            .contentTypeService( this.contentTypeService )
            .eventPublisher( this.eventPublisher )
            .build();
        return Tracer.trace( "content.exists", trace -> trace.put( "id", contentId ), command::execute,
                             ( trace, exists ) -> trace.put( "exists", exists ) );
    }

    @Override
    public boolean contentExists( final ContentPath contentPath )
    {
        final ContentExistsCommand command = ContentExistsCommand.create( contentPath )
            .nodeService( this.nodeService )
            .contentTypeService( this.contentTypeService )
            .eventPublisher( this.eventPublisher )
            .build();
        return Tracer.trace( "content.exists", trace -> trace.put( "path", contentPath ), command::execute,
                             ( trace, exists ) -> trace.put( "exists", exists ) );
    }

    @Override
    public ByteSource getBinary( final ContentId contentId, final BinaryReference binaryReference )
    {
        final GetBinaryCommand command = GetBinaryCommand.create( contentId, binaryReference )
            .nodeService( this.nodeService )
            .contentTypeService( this.contentTypeService )
            .eventPublisher( this.eventPublisher )
            .build();
        return Tracer.trace( "content.getBinary", trace -> {
            trace.put( "id", contentId );
            trace.put( "reference", binaryReference );
        }, command::execute, ( trace, byteSource ) -> {
            if ( byteSource != null )
            {
                trace.put( "size", byteSource.sizeIfKnown().or( -1L ) );
            }
        } );
    }

    @Override
    public ByteSource getBinary( final ContentId contentId, final ContentVersionId contentVersionId, final BinaryReference binaryReference )
    {
        final GetBinaryByVersionCommand command = GetBinaryByVersionCommand.create( contentId, contentVersionId, binaryReference )
            .nodeService( this.nodeService )
            .contentTypeService( this.contentTypeService )
            .eventPublisher( this.eventPublisher )
            .build();
        return Tracer.trace( "content.getBinary", trace -> {
            trace.put( "id", contentId );
            trace.put( "versionId", contentVersionId );
            trace.put( "reference", binaryReference );
        }, command::execute, ( trace, byteSource ) -> {
            if ( byteSource != null )
            {
                trace.put( "size", byteSource.sizeIfKnown().or( -1L ) );
            }
        } );
    }

    @Override
    public String getBinaryKey( final ContentId contentId, final BinaryReference binaryReference )
    {
        final GetBinaryKeyCommand command = GetBinaryKeyCommand.create( contentId, binaryReference )
            .nodeService( this.nodeService )
            .contentTypeService( this.contentTypeService )
            .eventPublisher( this.eventPublisher )
            .build();

        return Tracer.trace( "content.getBinaryKey", trace -> {
            trace.put( "id", contentId );
            trace.put( "reference", binaryReference );
        }, command::execute, ( trace, binaryKey ) -> trace.put( "binaryKey", binaryKey ) );
    }

    @Override
    public Content getByIdAndVersionId( final ContentId contentId, final ContentVersionId versionId )
    {
        requireReadAccess();
        
        final GetContentByIdAndVersionIdCommand command = GetContentByIdAndVersionIdCommand.create()
            .contentId( contentId )
            .versionId( versionId )
            .nodeService( this.nodeService )
            .contentTypeService( this.contentTypeService )
            .eventPublisher( this.eventPublisher )
            .build();

        return Tracer.trace( "content.getByIdAndVersionId", trace -> {
            trace.put( "contentId", contentId );
            trace.put( "versionId", versionId );
        }, command::execute, ( trace, content ) -> trace.put( "path", content.getPath().toString() ) );
    }

    @Override
    public PatchContentResult patch( final PatchContentParams params )
    {
        verifyDraftBranch();
        requireAdminRole();

        final PatchContentResult result = PatchContentCommand.create( params )
            .nodeService( this.nodeService )
            .contentTypeService( this.contentTypeService )
            .eventPublisher( this.eventPublisher )
            .cmsService( this.cmsService )
            .mixinService( this.mixinService )
            .contentProcessors( this.contentProcessors )
            .contentValidators( this.contentValidators )
            .pageDescriptorService( this.pageDescriptorService )
            .partDescriptorService( this.partDescriptorService )
            .layoutDescriptorService( this.layoutDescriptorService )
            .allowUnsafeAttachmentNames( config.attachments_allowUnsafeNames() )
            .build()
            .execute();

        contentAuditLogSupport.patch( params, result );

        return result;
    }

    @Override
    @NullMarked
    public UpdateContentMetadataResult updateMetadata( final UpdateContentMetadataParams params )
    {
        verifyDraftBranch();

        final UpdateContentMetadataResult result = UpdateMetadataCommand.create( params )
            .nodeService( this.nodeService )
            .contentTypeService( this.contentTypeService )
            .eventPublisher( this.eventPublisher )
            .cmsService( this.cmsService )
            .mixinService( this.mixinService )
            .contentProcessors( this.contentProcessors )
            .contentValidators( this.contentValidators )
            .pageDescriptorService( this.pageDescriptorService )
            .partDescriptorService( this.partDescriptorService )
            .layoutDescriptorService( this.layoutDescriptorService )
            .allowUnsafeAttachmentNames( config.attachments_allowUnsafeNames() )
            .build()
            .execute();

        contentAuditLogSupport.updateMetadata( params, result );

        return result;
    }

    @Override
    @NullMarked
    public UpdateWorkflowResult updateWorkflow( final UpdateWorkflowParams params )
    {
        verifyDraftBranch();

        final UpdateWorkflowResult result = UpdateWorkflowCommand.create( params )
            .nodeService( this.nodeService )
            .contentTypeService( this.contentTypeService )
            .eventPublisher( this.eventPublisher )
            .cmsService( this.cmsService )
            .mixinService( this.mixinService )
            .contentProcessors( this.contentProcessors )
            .contentValidators( this.contentValidators )
            .pageDescriptorService( this.pageDescriptorService )
            .partDescriptorService( this.partDescriptorService )
            .layoutDescriptorService( this.layoutDescriptorService )
            .allowUnsafeAttachmentNames( config.attachments_allowUnsafeNames() )
            .build()
            .execute();

        contentAuditLogSupport.updateWorkflow( params, result );

        return result;
    }

    private static void verifyDraftBranch()
    {
        final Context ctx = ContextAccessor.current();
        if ( ProjectName.from( ctx.getRepositoryId() ) == null )
        {
            throw new IllegalStateException( "Repository must be a project repository" );
        }
        if ( !ContentConstants.BRANCH_DRAFT.equals( ctx.getBranch() ) )
        {
            throw new IllegalStateException( String.format( "Branch must be %s", ContentConstants.BRANCH_DRAFT ) );
        }
    }

    private static void requireAdminRole()
    {
        final AuthenticationInfo authInfo = ContextAccessor.current().getAuthInfo();
        if ( !ProjectAccessHelper.hasAdminAccess( authInfo ) )
        {
            throw new ForbiddenAccessException( authInfo.getUser() );
        }
    }

    private static void requireAnyProjectRole()
    {
        final Context ctx = ContextAccessor.current();

        final AuthenticationInfo authInfo = ctx.getAuthInfo();
        if ( !ProjectAccessHelper.hasAnyAccess( authInfo, ProjectName.from( ctx.getRepositoryId() ) ) )
        {
            throw new ForbiddenAccessException( authInfo.getUser() );
        }
    }

    private static void requireReadAccess()
    {
        final Context ctx = ContextAccessor.current();

        if ( ContentConstants.BRANCH_DRAFT.equals( ctx.getBranch() ) )
        {
            requireAnyProjectRole();
        }
    }

    @Reference
    public void setContentTypeService( final ContentTypeService contentTypeService )
    {
        this.contentTypeService = contentTypeService;
    }

    @Reference
    public void setEventPublisher( final EventPublisher eventPublisher )
    {
        this.eventPublisher = eventPublisher;
    }

    @Reference
    public void setMediaInfoService( final MediaInfoService mediaInfoService )
    {
        this.mediaInfoService = mediaInfoService;
    }

    @Reference
    public void setMixinService( final MixinService mixinService )
    {
        this.mixinService = mixinService;
    }

    @Reference
    public void setMixinMappingService( final MixinMappingService mixinMappingService )
    {
        this.mixinMappingService = mixinMappingService;
    }

    @Reference
    public void setCmsService( final CmsService cmsService )
    {
        this.cmsService = cmsService;
    }

    @SuppressWarnings("unused")
    @Reference(cardinality = ReferenceCardinality.MULTIPLE, policy = ReferencePolicy.DYNAMIC)
    public void addContentProcessor( final ContentProcessor contentProcessor )
    {
        this.contentProcessors.add( contentProcessor );
    }

    public void removeContentProcessor( final ContentProcessor contentProcessor )
    {
        this.contentProcessors.remove( contentProcessor );
    }

    @Reference(cardinality = ReferenceCardinality.MULTIPLE, policy = ReferencePolicy.DYNAMIC)
    public void addContentValidator( final ContentValidator contentValidator )
    {
        this.contentValidators.add( contentValidator );
    }

    public void removeContentValidator( final ContentValidator contentValidator )
    {
        this.contentValidators.remove( contentValidator );
    }

    @Reference
    public void setContentAuditLogSupport( final ContentAuditLogSupport contentAuditLogSupport )
    {
        this.contentAuditLogSupport = contentAuditLogSupport;
    }

    @Reference
    public void setProjectService( final ProjectService projectService )
    {
        //Many starters depend on ContentService available only when default cms repo is fully initialized.
        // Starting from 7.3 Initialization happens in ProjectService, so we need a dependency.
    }

}
