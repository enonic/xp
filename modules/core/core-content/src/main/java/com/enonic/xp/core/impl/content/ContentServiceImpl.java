package com.enonic.xp.core.impl.content;

import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Predicate;

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
import com.enonic.xp.branch.Branch;
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
import com.enonic.xp.content.FindContentVersionsParams;
import com.enonic.xp.content.FindContentVersionsResult;
import com.enonic.xp.content.GetContentByIdsParams;
import com.enonic.xp.content.GetPublishStatusResult;
import com.enonic.xp.content.GetPublishStatusesParams;
import com.enonic.xp.content.GetPublishStatusesResult;
import com.enonic.xp.content.HasUnpublishedChildrenParams;
import com.enonic.xp.content.ImportContentParams;
import com.enonic.xp.content.ImportContentResult;
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
import com.enonic.xp.content.UpdateContentParams;
import com.enonic.xp.content.UpdateMediaParams;
import com.enonic.xp.content.XDataDefaultValuesProcessor;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.core.impl.content.processor.ContentProcessor;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.event.EventPublisher;
import com.enonic.xp.exception.ForbiddenAccessException;
import com.enonic.xp.form.FormDefaultValuesProcessor;
import com.enonic.xp.media.MediaInfoService;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeService;
import com.enonic.xp.page.PageDefaultValuesProcessor;
import com.enonic.xp.page.PageDescriptorService;
import com.enonic.xp.project.ProjectService;
import com.enonic.xp.region.LayoutDescriptorService;
import com.enonic.xp.region.PartDescriptorService;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.schema.content.ContentTypeService;
import com.enonic.xp.schema.xdata.XDataService;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.acl.AccessControlList;
import com.enonic.xp.security.auth.AuthenticationInfo;
import com.enonic.xp.site.Site;
import com.enonic.xp.site.SiteConfigService;
import com.enonic.xp.site.SiteService;
import com.enonic.xp.site.XDataMappingService;
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

    private XDataService xDataService;

    private XDataMappingService xDataMappingService;

    private final XDataDefaultValuesProcessor xDataDefaultValuesProcessor;

    private SiteService siteService;

    private final List<ContentProcessor> contentProcessors = new CopyOnWriteArrayList<>();

    private final List<ContentValidator> contentValidators = new CopyOnWriteArrayList<>();

    private final FormDefaultValuesProcessor formDefaultValuesProcessor;

    private final PageDefaultValuesProcessor pageFormDefaultValuesProcessor;

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
                               @Reference final SiteConfigService siteConfigService,
                               @Reference final FormDefaultValuesProcessor formDefaultValuesProcessor,
                               @Reference final PageDefaultValuesProcessor pageFormDefaultValuesProcessor,
                               @Reference final XDataDefaultValuesProcessor xDataDefaultValuesProcessor, ContentConfig config )
    {
        this.config = config;
        this.nodeService = nodeService;
        this.pageDescriptorService = pageDescriptorService;
        this.partDescriptorService = partDescriptorService;
        this.layoutDescriptorService = layoutDescriptorService;
        this.siteConfigService = siteConfigService;

        this.formDefaultValuesProcessor = formDefaultValuesProcessor;
        this.pageFormDefaultValuesProcessor = pageFormDefaultValuesProcessor;
        this.xDataDefaultValuesProcessor = xDataDefaultValuesProcessor;
    }

    @Override
    public Content create( final CreateContentParams params )
    {
        verifyContextBranch( ContentConstants.BRANCH_DRAFT );

        final Content content = CreateContentCommand.create()
            .nodeService( this.nodeService )
            .contentTypeService( this.contentTypeService )
            .eventPublisher( this.eventPublisher )
            .siteService( this.siteService )
            .xDataService( this.xDataService )
            .contentProcessors( this.contentProcessors )
            .contentValidators( this.contentValidators )
            .formDefaultValuesProcessor( this.formDefaultValuesProcessor )
            .pageFormDefaultValuesProcessor( this.pageFormDefaultValuesProcessor )
            .xDataDefaultValuesProcessor( this.xDataDefaultValuesProcessor )
            .xDataMappingService( this.xDataMappingService )
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
        verifyContextBranch( ContentConstants.BRANCH_DRAFT );

        final Content content = CreateMediaCommand.create()
            .params( params )
            .nodeService( this.nodeService )
            .contentTypeService( this.contentTypeService )
            .eventPublisher( this.eventPublisher )
            .mediaInfoService( this.mediaInfoService )
            .siteService( this.siteService )
            .xDataService( this.xDataService )
            .xDataMappingService( this.xDataMappingService )
            .siteConfigService( this.siteConfigService )
            .contentProcessors( this.contentProcessors )
            .contentValidators( this.contentValidators )
            .formDefaultValuesProcessor( this.formDefaultValuesProcessor )
            .pageFormDefaultValuesProcessor( this.pageFormDefaultValuesProcessor )
            .xDataDefaultValuesProcessor( this.xDataDefaultValuesProcessor )
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
        verifyContextBranch( ContentConstants.BRANCH_DRAFT );

        final Content content = UpdateContentCommand.create( params )
            .nodeService( this.nodeService )
            .contentTypeService( this.contentTypeService )
            .eventPublisher( this.eventPublisher )
            .siteService( this.siteService )
            .xDataService( this.xDataService )
            .contentProcessors( this.contentProcessors )
            .contentValidators( this.contentValidators )
            .pageDescriptorService( this.pageDescriptorService )
            .partDescriptorService( this.partDescriptorService )
            .layoutDescriptorService( this.layoutDescriptorService )
            .xDataMappingService( this.xDataMappingService )
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
        verifyContextBranch( ContentConstants.BRANCH_DRAFT );

        final Content content = UpdateMediaCommand.create( params )
            .nodeService( this.nodeService )
            .contentTypeService( this.contentTypeService )
            .eventPublisher( this.eventPublisher )
            .mediaInfoService( this.mediaInfoService )
            .pageDescriptorService( this.pageDescriptorService )
            .partDescriptorService( this.partDescriptorService )
            .layoutDescriptorService( this.layoutDescriptorService )
            .siteService( this.siteService )
            .xDataService( this.xDataService ).xDataMappingService( this.xDataMappingService ).siteConfigService( this.siteConfigService )
            .contentProcessors( this.contentProcessors )
            .contentValidators( this.contentValidators )
            .allowUnsafeAttachmentNames( config.attachments_allowUnsafeNames() )
            .build()
            .execute();

        contentAuditLogSupport.update( params, content );

        return content;
    }

    private void requireAdminRole()
    {
        final AuthenticationInfo authInfo = ContextAccessor.current().getAuthInfo();

        if ( !( authInfo.hasRole( RoleKeys.ADMIN ) || authInfo.hasRole( RoleKeys.CONTENT_MANAGER_ADMIN ) ) )
        {
            throw new ForbiddenAccessException( authInfo.getUser() );
        }
    }

    @Override
    public DeleteContentsResult delete( final DeleteContentParams params )
    {
        verifyContextBranch( ContentConstants.BRANCH_DRAFT );

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
        verifyContextBranch( ContentConstants.BRANCH_DRAFT );

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
        final GetContentByIdsCommand command = GetContentByIdsCommand.create( params )
            .nodeService( this.nodeService )
            .contentTypeService( this.contentTypeService )
            .eventPublisher( this.eventPublisher )
            .build();

        return Tracer.trace( "content.getByIds", trace -> trace.put( "id", params.getIds() ), command::execute );
    }

    @Override
    public Content getByPath( final ContentPath path )
    {
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
        verifyContextBranch( ContentConstants.BRANCH_DRAFT );

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
        verifyContextBranch( ContentConstants.BRANCH_DRAFT );

        final MoveContentsResult result = MoveContentCommand.create( params )
            .nodeService( this.nodeService )
            .contentTypeService( this.contentTypeService )
            .eventPublisher( this.eventPublisher )
            .xDataService( this.xDataService )
            .contentValidators( this.contentValidators  )
            .build()
            .execute();

        contentAuditLogSupport.move( params, result );

        return result;
    }

    @Override
    public ArchiveContentsResult archive( final ArchiveContentParams params )
    {
        verifyContextBranch( ContentConstants.BRANCH_DRAFT );

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
        verifyContextBranch( ContentConstants.BRANCH_DRAFT );

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
                if ( publishInfo.getTo() != null && publishInfo.getTo().compareTo( now ) < 0 )
                {
                    return new GetPublishStatusResult( content.getId(), PublishStatus.EXPIRED );
                }

                if ( publishInfo.getFrom() != null && publishInfo.getFrom().compareTo( now ) > 0 )
                {
                    return new GetPublishStatusResult( content.getId(), PublishStatus.PENDING );
                }
            }
            return new GetPublishStatusResult( content.getId(), PublishStatus.ONLINE );
        } ).forEach( getPublishStatusesResult::add );

        return getPublishStatusesResult.build();
    }

    @Override
    public FindContentVersionsResult getVersions( final FindContentVersionsParams params )
    {
        return FindContentVersionsCommand.create()
            .nodeService( this.nodeService )
            .contentTypeService( this.contentTypeService )
            .eventPublisher( this.eventPublisher )
            .contentId( params.getContentId() )
            .from( params.getFrom() )
            .size( params.getSize() )
            .build()
            .execute();
    }

    @Override
    public SortContentResult sort( final SortContentParams params )
    {
        verifyContextBranch( ContentConstants.BRANCH_DRAFT );

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
        verifyContextBranch( ContentConstants.BRANCH_DRAFT );

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
    public ImportContentResult importContent( final ImportContentParams params )
    {
        verifyContextBranch( ContentConstants.BRANCH_DRAFT );

        return ImportContentCommand.create()
            .params( params )
            .nodeService( nodeService )
            .contentTypeService( contentTypeService )
            .eventPublisher( eventPublisher )
            .build()
            .execute();
    }

    @Override
    public PatchContentResult patch( final PatchContentParams params )
    {
        requireAdminRole();

        verifyContextBranch( ContentConstants.BRANCH_DRAFT );

        final PatchContentResult result = PatchContentCommand.create( params )
            .nodeService( this.nodeService )
            .contentTypeService( this.contentTypeService )
            .eventPublisher( this.eventPublisher )
            .siteService( this.siteService )
            .xDataService( this.xDataService )
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

    private static void verifyContextBranch( final Branch branch )
    {
        final Branch contextBranch = ContextAccessor.current().getBranch();

        if ( !branch.equals( contextBranch ) )
        {
            throw new IllegalStateException( String.format( "Branch must be %s", branch ) );
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
    public void setxDataService( final XDataService xDataService )
    {
        this.xDataService = xDataService;
    }

    @Reference
    public void setXDataMappingService( final XDataMappingService xDataMappingService )
    {
        this.xDataMappingService = xDataMappingService;
    }

    @Reference
    public void setSiteService( final SiteService siteService )
    {
        this.siteService = siteService;
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
