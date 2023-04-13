package com.enonic.xp.core.impl.content;

import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Predicate;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.io.ByteSource;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.archive.ArchiveContentParams;
import com.enonic.xp.archive.ArchiveContentsResult;
import com.enonic.xp.archive.RestoreContentParams;
import com.enonic.xp.archive.RestoreContentsResult;
import com.enonic.xp.branch.Branch;
import com.enonic.xp.branch.Branches;
import com.enonic.xp.content.ActiveContentVersionEntry;
import com.enonic.xp.content.ApplyContentPermissionsParams;
import com.enonic.xp.content.ApplyContentPermissionsResult;
import com.enonic.xp.content.CompareContentParams;
import com.enonic.xp.content.CompareContentResult;
import com.enonic.xp.content.CompareContentResults;
import com.enonic.xp.content.CompareContentsParams;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentAccessException;
import com.enonic.xp.content.ContentConstants;
import com.enonic.xp.content.ContentDependencies;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentIds;
import com.enonic.xp.content.ContentNotFoundException;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.ContentPaths;
import com.enonic.xp.content.ContentPropertyNames;
import com.enonic.xp.content.ContentPublishInfo;
import com.enonic.xp.content.ContentQuery;
import com.enonic.xp.content.ContentService;
import com.enonic.xp.content.ContentValidator;
import com.enonic.xp.content.ContentValidityParams;
import com.enonic.xp.content.ContentValidityResult;
import com.enonic.xp.content.ContentVersion;
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
import com.enonic.xp.content.FindContentByQueryParams;
import com.enonic.xp.content.FindContentByQueryResult;
import com.enonic.xp.content.FindContentIdsByParentResult;
import com.enonic.xp.content.FindContentIdsByQueryResult;
import com.enonic.xp.content.FindContentPathsByQueryParams;
import com.enonic.xp.content.FindContentPathsByQueryResult;
import com.enonic.xp.content.FindContentVersionsParams;
import com.enonic.xp.content.FindContentVersionsResult;
import com.enonic.xp.content.GetActiveContentVersionParams;
import com.enonic.xp.content.GetActiveContentVersionsParams;
import com.enonic.xp.content.GetActiveContentVersionsResult;
import com.enonic.xp.content.GetContentByIdsParams;
import com.enonic.xp.content.GetPublishStatusResult;
import com.enonic.xp.content.GetPublishStatusesParams;
import com.enonic.xp.content.GetPublishStatusesResult;
import com.enonic.xp.content.HasUnpublishedChildrenParams;
import com.enonic.xp.content.ImportContentParams;
import com.enonic.xp.content.ImportContentResult;
import com.enonic.xp.content.MoveContentParams;
import com.enonic.xp.content.MoveContentsResult;
import com.enonic.xp.content.PublishContentResult;
import com.enonic.xp.content.PublishStatus;
import com.enonic.xp.content.PushContentParams;
import com.enonic.xp.content.RenameContentParams;
import com.enonic.xp.content.ReorderChildContentsParams;
import com.enonic.xp.content.ReorderChildContentsResult;
import com.enonic.xp.content.ReorderChildParams;
import com.enonic.xp.content.ReprocessContentParams;
import com.enonic.xp.content.ResolvePublishDependenciesParams;
import com.enonic.xp.content.ResolveRequiredDependenciesParams;
import com.enonic.xp.content.SetActiveContentVersionResult;
import com.enonic.xp.content.SetContentChildOrderParams;
import com.enonic.xp.content.UndoPendingDeleteContentParams;
import com.enonic.xp.content.UnpublishContentParams;
import com.enonic.xp.content.UnpublishContentsResult;
import com.enonic.xp.content.UpdateContentParams;
import com.enonic.xp.content.UpdateMediaParams;
import com.enonic.xp.content.processor.ContentProcessor;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.event.EventPublisher;
import com.enonic.xp.form.FormDefaultValuesProcessor;
import com.enonic.xp.media.MediaInfoService;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeAccessException;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeService;
import com.enonic.xp.node.RefreshMode;
import com.enonic.xp.node.ReorderChildNodeParams;
import com.enonic.xp.node.ReorderChildNodesParams;
import com.enonic.xp.node.ReorderChildNodesResult;
import com.enonic.xp.node.SetNodeChildOrderParams;
import com.enonic.xp.page.PageDescriptorService;
import com.enonic.xp.project.ProjectService;
import com.enonic.xp.query.expr.CompareExpr;
import com.enonic.xp.query.expr.FieldExpr;
import com.enonic.xp.query.expr.QueryExpr;
import com.enonic.xp.query.expr.ValueExpr;
import com.enonic.xp.region.LayoutDescriptorService;
import com.enonic.xp.region.PartDescriptorService;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.schema.content.ContentTypeService;
import com.enonic.xp.schema.xdata.XDataService;
import com.enonic.xp.security.acl.AccessControlList;
import com.enonic.xp.site.CreateSiteParams;
import com.enonic.xp.site.Site;
import com.enonic.xp.site.SiteConfigsDataSerializer;
import com.enonic.xp.site.SiteService;
import com.enonic.xp.trace.Tracer;
import com.enonic.xp.util.BinaryReference;

@Component(configurationPid = "com.enonic.xp.content")
public class ContentServiceImpl
    implements ContentService
{
    public static final String TEMPLATES_FOLDER_NAME = "_templates";

    private static final Logger LOG = LoggerFactory.getLogger( ContentServiceImpl.class );

    private static final String TEMPLATES_FOLDER_DISPLAY_NAME = "Templates";

    private static final SiteConfigsDataSerializer SITE_CONFIGS_DATA_SERIALIZER = new SiteConfigsDataSerializer();

    private ContentTypeService contentTypeService;

    private final NodeService nodeService;

    private EventPublisher eventPublisher;

    private MediaInfoService mediaInfoService;

    private XDataService xDataService;

    private SiteService siteService;

    private final ContentNodeTranslator translator;

    private final List<ContentProcessor> contentProcessors = new CopyOnWriteArrayList<>();

    private final List<ContentValidator> contentValidators = new CopyOnWriteArrayList<>();

    private FormDefaultValuesProcessor formDefaultValuesProcessor;

    private final PageDescriptorService pageDescriptorService;

    private final PartDescriptorService partDescriptorService;

    private final LayoutDescriptorService layoutDescriptorService;

    private ContentAuditLogSupport contentAuditLogSupport;

    private volatile ContentConfig config;

    @Activate
    public ContentServiceImpl( @Reference final NodeService nodeService, @Reference final PageDescriptorService pageDescriptorService,
                               @Reference final PartDescriptorService partDescriptorService,
                               @Reference final LayoutDescriptorService layoutDescriptorService )
    {
        this.nodeService = nodeService;
        this.pageDescriptorService = pageDescriptorService;
        this.partDescriptorService = partDescriptorService;
        this.layoutDescriptorService = layoutDescriptorService;
        this.translator = new ContentNodeTranslator( nodeService );
    }

    @Activate
    @Modified
    public void initialize( final ContentConfig config )
    {
        this.config = config;
    }

    @Override
    public Site create( final CreateSiteParams params )
    {
        verifyContextBranch( ContentConstants.BRANCH_DRAFT );

        final PropertyTree data = new PropertyTree();
        data.setString( "description", params.getDescription() );

        SITE_CONFIGS_DATA_SERIALIZER.toProperties( params.getSiteConfigs(), data.getRoot() );

        final CreateContentParams createContentParams = CreateContentParams.create().
            type( ContentTypeName.site() ).
            parent( params.getParentContentPath() ).
            name( params.getName() ).
            displayName( params.getDisplayName() ).
            contentData( data ).
            requireValid( params.isRequireValid() ).
            build();

        final Site site = (Site) CreateContentCommand.create().
            nodeService( this.nodeService ).
            contentTypeService( this.contentTypeService ).
            translator( this.translator ).
            eventPublisher( this.eventPublisher ).
            siteService( this.siteService ).
            xDataService( this.xDataService ).
            contentProcessors( this.contentProcessors ).
            contentValidators( this.contentValidators ).
            formDefaultValuesProcessor( this.formDefaultValuesProcessor ).
            pageDescriptorService( this.pageDescriptorService ).
            partDescriptorService( this.partDescriptorService ).
            layoutDescriptorService( this.layoutDescriptorService ).
            allowUnsafeAttachmentNames( config.attachments_allowUnsafeNames() ).
            params( createContentParams ).
            build().
            execute();

        this.create( CreateContentParams.create().
            owner( site.getOwner() ).
            displayName( TEMPLATES_FOLDER_DISPLAY_NAME ).
            name( TEMPLATES_FOLDER_NAME ).
            inheritPermissions( true ).
            parent( site.getPath() ).
            type( ContentTypeName.templateFolder() ).
            requireValid( true ).
            contentData( new PropertyTree() ).
            build() );

        contentAuditLogSupport.createSite( params, site );

        return site;
    }

    @Override
    public Content create( final CreateContentParams params )
    {
        verifyContextBranch( ContentConstants.BRANCH_DRAFT );

        final Content content = CreateContentCommand.create().
            nodeService( this.nodeService ).
            contentTypeService( this.contentTypeService ).
            translator( this.translator ).
            eventPublisher( this.eventPublisher ).
            siteService( this.siteService ).
            xDataService( this.xDataService ).
            contentProcessors( this.contentProcessors ).
            contentValidators( this.contentValidators ).
            formDefaultValuesProcessor( this.formDefaultValuesProcessor ).
            pageDescriptorService( this.pageDescriptorService ).
            partDescriptorService( this.partDescriptorService ).
            layoutDescriptorService( this.layoutDescriptorService ).
            allowUnsafeAttachmentNames( config.attachments_allowUnsafeNames() ).
            params( params ).
            build().
            execute();

        if ( content instanceof Site )
        {
            this.create( CreateContentParams.create().
                owner( content.getOwner() ).
                displayName( TEMPLATES_FOLDER_DISPLAY_NAME ).
                name( TEMPLATES_FOLDER_NAME ).
                inheritPermissions( true ).
                parent( content.getPath() ).
                type( ContentTypeName.templateFolder() ).
                requireValid( true ).
                contentData( new PropertyTree() ).
                build() );

            return this.doGetById( content.getId() );
        }

        contentAuditLogSupport.createContent( params, content );

        return content;
    }

    @Override
    public Content create( final CreateMediaParams params )
    {
        verifyContextBranch( ContentConstants.BRANCH_DRAFT );

        final Content content = CreateMediaCommand.create().
            params( params ).
            nodeService( this.nodeService ).
            contentTypeService( this.contentTypeService ).
            translator( this.translator ).
            eventPublisher( this.eventPublisher ).
            mediaInfoService( this.mediaInfoService ).
            siteService( this.siteService ).
            xDataService( this.xDataService ).
            contentProcessors( this.contentProcessors ).
            contentValidators( this.contentValidators ).
            formDefaultValuesProcessor( this.formDefaultValuesProcessor ).
            pageDescriptorService( this.pageDescriptorService ).
            partDescriptorService( this.partDescriptorService ).
            layoutDescriptorService( this.layoutDescriptorService ).
            allowUnsafeAttachmentNames( config.attachments_allowUnsafeNames() ).
            build().
            execute();

        contentAuditLogSupport.createMedia( params, content );

        return content;
    }

    @Override
    public Content update( final UpdateContentParams params )
    {
        verifyContextBranch( ContentConstants.BRANCH_DRAFT );

        final Content content = UpdateContentCommand.create( params ).
            nodeService( this.nodeService ).
            contentTypeService( this.contentTypeService ).
            translator( this.translator ).
            eventPublisher( this.eventPublisher ).
            siteService( this.siteService ).
            xDataService( this.xDataService ).
            contentProcessors( this.contentProcessors ).
            contentValidators( this.contentValidators ).
            pageDescriptorService( this.pageDescriptorService ).
            partDescriptorService( this.partDescriptorService ).
            layoutDescriptorService( this.layoutDescriptorService ).
            allowUnsafeAttachmentNames( config.attachments_allowUnsafeNames() ).
            build().
            execute();

        contentAuditLogSupport.update( params, content );

        return content;
    }

    @Override
    public Content update( final UpdateMediaParams params )
    {
        verifyContextBranch( ContentConstants.BRANCH_DRAFT );

        final Content content = UpdateMediaCommand.create( params ).
            nodeService( this.nodeService ).
            contentTypeService( this.contentTypeService ).
            translator( this.translator ).
            eventPublisher( this.eventPublisher ).
            mediaInfoService( this.mediaInfoService ).
            pageDescriptorService( this.pageDescriptorService ).
            partDescriptorService( this.partDescriptorService ).
            layoutDescriptorService( this.layoutDescriptorService ).
            siteService( this.siteService ).
            xDataService( this.xDataService ).
            contentProcessors( this.contentProcessors ).
            contentValidators( this.contentValidators ).
            allowUnsafeAttachmentNames( config.attachments_allowUnsafeNames() ).
            build().
            execute();

        contentAuditLogSupport.update( params, content );

        return content;
    }

    @Override
    public DeleteContentsResult deleteWithoutFetch( final DeleteContentParams params )
    {
        verifyContextBranch( ContentConstants.BRANCH_DRAFT );

        final DeleteContentsResult result = DeleteContentCommand.create().
            nodeService( this.nodeService ).
            contentTypeService( this.contentTypeService ).
            translator( this.translator ).
            eventPublisher( this.eventPublisher ).
            params( params ).
            build().
            execute();

        contentAuditLogSupport.delete( params, result );

        return result;
    }

    @Override
    @Deprecated
    public int undoPendingDelete( final UndoPendingDeleteContentParams params )
    {
        return 0;
    }

    @Override
    public PublishContentResult publish( final PushContentParams params )
    {
        verifyContextBranch( ContentConstants.BRANCH_DRAFT );

        final PublishContentResult result = PublishContentCommand.create().
            nodeService( this.nodeService ).
            contentTypeService( this.contentTypeService ).
            translator( this.translator ).
            eventPublisher( this.eventPublisher ).
            contentIds( params.getContentIds() ).
            excludedContentIds( params.getExcludedContentIds() ).
            contentPublishInfo( params.getContentPublishInfo() ).
            excludeChildrenIds( params.getExcludeChildrenIds() ).
            includeDependencies( params.isIncludeDependencies() ).
            pushListener( params.getPublishContentListener() ).
            message( params.getMessage() ).
            build().
            execute();

        contentAuditLogSupport.publish( params, result );

        return result;
    }

    @Override
    public CompareContentResults resolvePublishDependencies( ResolvePublishDependenciesParams params )
    {
        return ResolveContentsToBePublishedCommand.create().
            nodeService( this.nodeService ).
            contentTypeService( this.contentTypeService ).
            translator( this.translator ).
            eventPublisher( this.eventPublisher ).
            contentIds( params.getContentIds() ).
            excludedContentIds( params.getExcludedContentIds() ).
            excludeChildrenIds( params.getExcludeChildrenIds() ).
            build().
            execute();
    }

    @Override
    public ContentIds resolveRequiredDependencies( ResolveRequiredDependenciesParams params )
    {
        return ResolveRequiredDependenciesCommand.create().
            nodeService( this.nodeService ).
            contentTypeService( this.contentTypeService ).
            translator( this.translator ).
            eventPublisher( this.eventPublisher ).
            contentIds( params.getContentIds() ).
            build().
            execute();
    }

    @Override
    @Deprecated
    public boolean isValidContent( ContentIds contentIds )
    {
        return getInvalidContent( contentIds ).isEmpty();
    }

    @Override
    @Deprecated
    public ContentIds getInvalidContent( ContentIds contentIds )
    {
        return getContentValidity( ContentValidityParams.create().contentIds( contentIds ).build() ).getNotValidContentIds();
    }

    @Override
    public ContentValidityResult getContentValidity( final ContentValidityParams params )
    {
        return CheckContentValidityCommand.create().
            translator( this.translator ).
            nodeService( this.nodeService ).
            eventPublisher( this.eventPublisher ).
            contentTypeService( this.contentTypeService ).
            contentIds( params.getContentIds() ).
            build().
            execute();
    }

    @Override
    public UnpublishContentsResult unpublishContent( final UnpublishContentParams params )
    {
        final UnpublishContentsResult result = UnpublishContentCommand.create().
            params( params ).
            nodeService( this.nodeService ).
            contentTypeService( this.contentTypeService ).
            translator( this.translator ).
            eventPublisher( this.eventPublisher ).
            build().
            execute();

        contentAuditLogSupport.unpublishContent( params, result );

        return result;
    }

    @Override
    public Content getById( final ContentId contentId )
    {
        return Tracer.trace( "content.getById", trace -> trace.put( "id", contentId ), () -> doGetById( contentId ),
                             ( trace, content ) -> trace.put( "path", content.getPath() ) );
    }

    private Content doGetById( final ContentId contentId )
    {
        final Content content = GetContentByIdCommand.create( contentId )
            .nodeService( this.nodeService )
            .contentTypeService( this.contentTypeService )
            .translator( this.translator )
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
            .translator( this.translator )
            .eventPublisher( this.eventPublisher ).build();
        return Tracer.trace( "content.getNearestSite", trace -> trace.put( "id", contentId ), command::execute, ( trace, site ) -> {
            if ( site != null )
            {
                trace.put( "path", site.getPath() );
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
                    trace.put( "path", site.getPath() );
                }
            } );
    }

    private Content doFindNearestByPath( final ContentPath contentPath, final Predicate<Content> predicate )
    {
        final Content content = executeGetByPath( contentPath );
        if ( content != null && predicate.test( content ) )
        {
            return content;
        }

        //Resolves the closest content, starting from the root.
        Content foundContent = null;
        ContentPath nextContentPath = ContentPath.ROOT;
        for ( int contentPathIndex = 0; contentPathIndex < contentPath.elementCount(); contentPathIndex++ )
        {
            final ContentPath currentContentPath = ContentPath.from( nextContentPath, contentPath.getElement( contentPathIndex ) );

            final Content childContent = executeGetByPath( currentContentPath );
            if ( childContent == null )
            {
                break;
            }
            if ( predicate.test( childContent ) )
            {
                foundContent = childContent;
            }
            nextContentPath = currentContentPath;
        }

        return foundContent;
    }

    @Override
    public Contents getByIds( final GetContentByIdsParams params )
    {
        final GetContentByIdsCommand command = GetContentByIdsCommand.create( params )
            .nodeService( this.nodeService )
            .contentTypeService( this.contentTypeService )
            .translator( this.translator )
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
            .translator( this.translator )
            .eventPublisher( this.eventPublisher )
            .build()
            .execute();
    }

    @Override
    @Deprecated
    public AccessControlList getPermissionsById( ContentId contentId )
    {
        Content content = doGetById( contentId );
        if ( content.getPermissions() != null )
        {
            return content.getPermissions();
        }
        return AccessControlList.empty();
    }

    @Override
    public Contents getByPaths( final ContentPaths paths )
    {
        final GetContentByPathsCommand command = GetContentByPathsCommand.create( paths )
            .nodeService( this.nodeService )
            .contentTypeService( this.contentTypeService )
            .translator( this.translator )
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
            .translator( this.translator )
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
        return FindContentIdsByParentCommand.create( params ).
            nodeService( this.nodeService ).
            contentTypeService( this.contentTypeService ).
            translator( this.translator ).
            eventPublisher( this.eventPublisher ).
            build().
            execute();
    }

    @Override
    public DuplicateContentsResult duplicate( final DuplicateContentParams params )
    {
        verifyContextBranch( ContentConstants.BRANCH_DRAFT );

        final DuplicateContentsResult result = DuplicateContentCommand.create( params ).
            nodeService( this.nodeService ).
            contentTypeService( this.contentTypeService ).
            translator( this.translator ).
            eventPublisher( this.eventPublisher ).
            duplicateListener( params.getDuplicateContentListener() ).
            build().
            execute();

        contentAuditLogSupport.duplicate( params, result );

        return result;
    }

    @Override
    public MoveContentsResult move( final MoveContentParams params )
    {
        verifyContextBranch( ContentConstants.BRANCH_DRAFT );

        final MoveContentsResult result = MoveContentCommand.create( params ).
            nodeService( this.nodeService ).
            contentTypeService( this.contentTypeService ).
            translator( this.translator ).
            eventPublisher( this.eventPublisher ).
            moveListener( params.getMoveContentListener() ).
            build().
            execute();

        contentAuditLogSupport.move( params, result );

        return result;
    }

    @Override
    public ArchiveContentsResult archive( final ArchiveContentParams params )
    {
        verifyContextBranch( ContentConstants.BRANCH_DRAFT );

        final ArchiveContentsResult result = ArchiveContentCommand.create( params )
            .nodeService( nodeService )
            .translator( translator )
            .eventPublisher( eventPublisher )
            .contentTypeService( contentTypeService )
            .archiveListener( params.getArchiveContentListener() )
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
            .translator( translator )
            .eventPublisher( eventPublisher )
            .contentTypeService( contentTypeService )
            .restoreListener( params.getRestoreContentListener() )
            .build()
            .execute();

        contentAuditLogSupport.restore( params, result );

        return result;
    }

    @Override
    public Content rename( final RenameContentParams params )
    {
        verifyContextBranch( ContentConstants.BRANCH_DRAFT );

        final Content content = RenameContentCommand.create( params ).
            nodeService( this.nodeService ).
            xDataService( this.xDataService ).
            siteService( this.siteService ).
            contentTypeService( this.contentTypeService ).
            translator( this.translator ).
            eventPublisher( this.eventPublisher ).
            contentProcessors( this.contentProcessors ).
            contentValidators( this.contentValidators ).
            pageDescriptorService( this.pageDescriptorService ).
            partDescriptorService( this.partDescriptorService ).
            layoutDescriptorService( this.layoutDescriptorService ).
            build().
            execute();

        contentAuditLogSupport.rename( params, content );

        return content;
    }

    @Override
    @Deprecated
    public FindContentByQueryResult find( final FindContentByQueryParams params )
    {
        return FindContentByQueryCommand.create().
            params( params ).
            nodeService( this.nodeService ).
            contentTypeService( this.contentTypeService ).
            translator( this.translator ).
            eventPublisher( this.eventPublisher ).
            build().
            execute();
    }

    @Override
    public FindContentIdsByQueryResult find( final ContentQuery query )
    {
        final FindContentIdsByQueryCommand command = FindContentIdsByQueryCommand.create()
            .query( query )
            .nodeService( this.nodeService )
            .contentTypeService( this.contentTypeService )
            .translator( this.translator )
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
    public Contents findByApplicationKey( final ApplicationKey key )
    {
        final String filedPath =
            String.join( ".", ContentPropertyNames.DATA, ContentPropertyNames.SITECONFIG, ContentPropertyNames.APPLICATION_KEY );
        final ContentQuery query = ContentQuery.create()
            .queryExpr( QueryExpr.from( CompareExpr.eq( FieldExpr.from( filedPath ), ValueExpr.string( key.toString() ) ) ) )
            .size( -1 )
            .build();

        return this.getByIds( new GetContentByIdsParams( this.find( query ).getContentIds() ) );
    }

    @Override
    @Deprecated
    public ContentPaths findContentPaths( ContentQuery query )
    {
        return FindContentPathsByQueryCommand.create().
            params( new FindContentPathsByQueryParams( query ) ).
            nodeService( this.nodeService ).
            contentTypeService( this.contentTypeService ).
            translator( this.translator ).
            eventPublisher( this.eventPublisher ).
            build().
            execute().
            getContentPaths();
    }

    @Override
    public FindContentPathsByQueryResult findPaths( ContentQuery query )
    {
        return FindContentPathsByQueryCommand.create().
            params( new FindContentPathsByQueryParams( query ) ).
            nodeService( this.nodeService ).
            contentTypeService( this.contentTypeService ).
            translator( this.translator ).
            eventPublisher( this.eventPublisher ).
            build().
            execute();
    }

    @Override
    @Deprecated
    public CompareContentResult compare( final CompareContentParams params )
    {
        return CompareContentsCommand.create()
            .nodeService( this.nodeService )
            .contentIds( ContentIds.from( params.getContentId() ) )
            .target( Objects.requireNonNullElse( params.getTarget(), ContentConstants.BRANCH_MASTER ) )
            .build()
            .execute()
            .iterator()
            .next();
    }

    @Override
    public CompareContentResults compare( final CompareContentsParams params )
    {
        return CompareContentsCommand.create().
            nodeService( this.nodeService ).
            contentIds( params.getContentIds() ).
            target( Objects.requireNonNullElse( params.getTarget(), ContentConstants.BRANCH_MASTER ) ).
            build().
            execute();
    }

    @Override
    public GetPublishStatusesResult getPublishStatuses( final GetPublishStatusesParams params )
    {
        final Instant now = Instant.now();

        final Contents contents = ContextBuilder.from( ContextAccessor.current() ).
            branch( Objects.requireNonNullElseGet( params.getTarget(), ContextAccessor.current()::getBranch ) ).
            attribute( "ignorePublishTimes", Boolean.TRUE ).
            build().
            callWith( () -> this.getByIds( new GetContentByIdsParams( params.getContentIds() ) ) );

        final GetPublishStatusesResult.Builder getPublishStatusesResult = GetPublishStatusesResult.create();

        contents.stream().
            map( content -> {

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
            } ).
            forEach( getPublishStatusesResult::add );

        return getPublishStatusesResult.build();
    }

    @Override
    public FindContentVersionsResult getVersions( final FindContentVersionsParams params )
    {
        return FindContentVersionsCommand.create().
            nodeService( this.nodeService ).
            contentTypeService( this.contentTypeService ).
            translator( this.translator ).
            eventPublisher( this.eventPublisher ).
            contentId( params.getContentId() ).
            from( params.getFrom() ).
            size( params.getSize() ).
            build().
            execute();
    }

    @Override
    public GetActiveContentVersionsResult getActiveVersions( final GetActiveContentVersionsParams params )
    {
        return GetActiveContentVersionsCommand.create().
            nodeService( this.nodeService ).
            contentTypeService( this.contentTypeService ).
            translator( this.translator ).
            eventPublisher( this.eventPublisher ).
            contentId( params.getContentId() ).
            branches( params.getBranches() ).
            build().
            execute();
    }

    @Override
    public ContentVersion getActiveVersion( final GetActiveContentVersionParams params )
    {
        final GetActiveContentVersionsParams versionsParams = GetActiveContentVersionsParams.create()
            .branches( Branches.from( Objects.requireNonNullElseGet( params.getBranch(), ContextAccessor.current()::getBranch ) ) )
            .contentId( params.getContentId() )
            .build();
        final List<ActiveContentVersionEntry> contentVersions = getActiveVersions( versionsParams ).getActiveContentVersions();

        return contentVersions.isEmpty() ? null : contentVersions.get( 0 ).getContentVersion();
    }

    @Override
    @Deprecated
    public SetActiveContentVersionResult setActiveContentVersion( final ContentId contentId, final ContentVersionId versionId )
    {
        throw new UnsupportedOperationException( "setActiveContentVersion is no longer supported" );
    }

    @Override
    public Content setChildOrder( final SetContentChildOrderParams params )
    {
        verifyContextBranch( ContentConstants.BRANCH_DRAFT );

        try
        {
            final SetNodeChildOrderParams.Builder builder = SetNodeChildOrderParams.create()
                .nodeId( NodeId.from( params.getContentId() ) )
                .refresh( RefreshMode.ALL )
                .childOrder( params.getChildOrder() );

            if ( params.stopInherit() )
            {
                builder.processor( new SetContentChildOrderProcessor() );
            }

            final Node node = nodeService.setChildOrder( builder.build() );

            final Content content = translator.fromNode( node, true );

            contentAuditLogSupport.setChildOrder( params, content );

            return content;
        }
        catch ( NodeAccessException e )
        {
            throw new ContentAccessException( e );
        }
    }

    @Override
    public ReorderChildContentsResult reorderChildren( final ReorderChildContentsParams params )
    {
        verifyContextBranch( ContentConstants.BRANCH_DRAFT );

        final ReorderChildNodesParams.Builder builder = ReorderChildNodesParams.create().refresh( RefreshMode.ALL );

        for ( final ReorderChildParams param : params )
        {
            builder.add( ReorderChildNodeParams.create()
                             .nodeId( NodeId.from( param.getContentToMove() ) )
                             .moveBefore( param.getContentToMoveBefore() == null ? null : NodeId.from( param.getContentToMoveBefore() ) )
                             .build() );
        }

        if ( params.stopInherit() )
        {
            builder.processor( new SetContentChildOrderProcessor() );
        }

        final ReorderChildNodesResult reorderChildNodesResult = this.nodeService.reorderChildren( builder.build() );

        final ReorderChildContentsResult result = new ReorderChildContentsResult( reorderChildNodesResult.getSize() );

        contentAuditLogSupport.reorderChildren( params, result );

        return result;
    }

    @Override
    public Boolean hasUnpublishedChildren( final HasUnpublishedChildrenParams params )
    {
        return nodeService.hasUnpublishedChildren( NodeId.from( params.getContentId() ), ContentConstants.BRANCH_MASTER );
    }

    @Override
    public ApplyContentPermissionsResult applyPermissions( final ApplyContentPermissionsParams params )
    {
        verifyContextBranch( ContentConstants.BRANCH_DRAFT );

        final ApplyContentPermissionsResult result = ApplyContentPermissionsCommand.create( params ).
            nodeService( this.nodeService ).
            contentTypeService( this.contentTypeService ).
            translator( this.translator ).
            eventPublisher( this.eventPublisher ).
            build().
            execute();

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
            .translator( this.translator )
            .eventPublisher( this.eventPublisher )
            .build();
        return Tracer.trace( "content.exists", trace -> trace.put( "id", contentId ), command::execute,
         (trace, exists) -> trace.put( "exists", exists ) );
    }

    @Override
    public boolean contentExists( final ContentPath contentPath )
    {
        final ContentExistsCommand command = ContentExistsCommand.create( contentPath ).
            nodeService( this.nodeService ).
            contentTypeService( this.contentTypeService ).
            translator( this.translator ).
            eventPublisher( this.eventPublisher ).
            build();
        return Tracer.trace( "content.exists", trace -> trace.put( "path", contentPath ), command::execute,
                             (trace, exists) -> trace.put( "exists", exists ) );
    }

    @Override
    public ByteSource getBinary( final ContentId contentId, final BinaryReference binaryReference )
    {
        final GetBinaryCommand command = GetBinaryCommand.create( contentId, binaryReference )
            .nodeService( this.nodeService )
            .contentTypeService( this.contentTypeService )
            .translator( this.translator )
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
        final GetBinaryByVersionCommand command = GetBinaryByVersionCommand.create( contentId, contentVersionId, binaryReference ).
            nodeService( this.nodeService ).
            contentTypeService( this.contentTypeService ).
            translator( this.translator ).
            eventPublisher( this.eventPublisher ).
            build();
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
            .translator( this.translator )
            .eventPublisher( this.eventPublisher )
            .build();

        return Tracer.trace( "content.getBinaryKey", trace -> {
            trace.put( "id", contentId );
            trace.put( "reference", binaryReference );
        }, command::execute, ( trace, binaryKey ) -> trace.put( "binaryKey", binaryKey ) );
    }

    @Override
    public Content reprocess( final ContentId contentId )
    {
        verifyContextBranch( ContentConstants.BRANCH_DRAFT );

        final Content content = ReprocessContentCommand.create( ReprocessContentParams.create().contentId( contentId ).build() ).
            nodeService( this.nodeService ).
            contentTypeService( this.contentTypeService ).
            translator( this.translator ).
            eventPublisher( this.eventPublisher ).
            mediaInfoService( this.mediaInfoService ).
            pageDescriptorService( this.pageDescriptorService ).
            partDescriptorService( this.partDescriptorService ).
            layoutDescriptorService( this.layoutDescriptorService ).
            siteService( this.siteService ).
            xDataService( this.xDataService ).
            contentProcessors( this.contentProcessors ).
            contentValidators( this.contentValidators ).
            build().
            execute();

        contentAuditLogSupport.reprocess( content );

        return content;
    }

    @Override
    public Content getByIdAndVersionId( final ContentId contentId, final ContentVersionId versionId )
    {
        final GetContentByIdAndVersionIdCommand command = GetContentByIdAndVersionIdCommand.create()
            .contentId( contentId )
            .versionId( versionId )
            .nodeService( this.nodeService )
            .contentTypeService( this.contentTypeService )
            .translator( this.translator )
            .eventPublisher( this.eventPublisher )
            .build();

        return Tracer.trace( "content.getByIdAndVersionId", trace -> {
            trace.put( "contentId", contentId );
            trace.put( "versionId", versionId );
        }, command::execute, ( trace, content ) -> trace.put( "path", content.getPath() ) );
    }

    @Override
    @Deprecated
    public Content getByPathAndVersionId( final ContentPath contentPath, final ContentVersionId versionId )
    {
        final GetContentByPathAndVersionIdCommand command = GetContentByPathAndVersionIdCommand.create()
            .contentPath( contentPath )
            .versionId( versionId )
            .nodeService( this.nodeService )
            .contentTypeService( this.contentTypeService )
            .translator( this.translator )
            .eventPublisher( this.eventPublisher )
            .build();

        return Tracer.trace( "content.getByPathAndVersionId", trace -> {
            trace.put( "path", contentPath );
            trace.put( "versionId", versionId );
        }, command::execute, ( trace, content ) -> trace.put( "contentId", content.getId() ) );
    }

    @Override
    public ImportContentResult importContent( final ImportContentParams params )
    {
        verifyContextBranch( ContentConstants.BRANCH_DRAFT );

        return ImportContentCommand.create().
            params( params ).
            nodeService( nodeService ).
            contentTypeService( contentTypeService ).
            eventPublisher( eventPublisher ).
            translator( translator ).
            build().
            execute();
    }

    @Override
    @Deprecated
    public InputStream getBinaryInputStream( final ContentId contentId, final BinaryReference binaryReference )
    {
        try
        {
            return nodeService.getBinary( NodeId.from( contentId ), binaryReference ).openStream();
        }
        catch ( IOException e )
        {
            return null;
        }
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
    public void setFormDefaultValuesProcessor( final FormDefaultValuesProcessor formDefaultValuesProcessor )
    {
        this.formDefaultValuesProcessor = formDefaultValuesProcessor;
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
