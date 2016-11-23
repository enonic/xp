package com.enonic.xp.core.impl.content;

import com.enonic.xp.content.*;
import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.core.impl.content.processor.ContentProcessor;
import com.enonic.xp.core.impl.content.processor.ContentProcessors;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.event.EventPublisher;
import com.enonic.xp.form.FormDefaultValuesProcessor;
import com.enonic.xp.index.IndexService;
import com.enonic.xp.media.MediaInfoService;
import com.enonic.xp.node.*;
import com.enonic.xp.schema.content.ContentType;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.schema.content.ContentTypeService;
import com.enonic.xp.schema.content.GetContentTypeParams;
import com.enonic.xp.schema.mixin.MixinService;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.acl.AccessControlList;
import com.enonic.xp.security.auth.AuthenticationInfo;
import com.enonic.xp.site.CreateSiteParams;
import com.enonic.xp.site.Site;
import com.enonic.xp.site.SiteConfigsDataSerializer;
import com.enonic.xp.site.SiteService;
import com.enonic.xp.util.BinaryReference;
import com.google.common.io.ByteSource;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.osgi.service.component.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.concurrent.*;

@Component(immediate = true)
public class ContentServiceImpl
    implements ContentService
{
    public static final String TEMPLATES_FOLDER_NAME = "_templates";

    private final static Logger LOG = LoggerFactory.getLogger( ContentServiceImpl.class );

    private static final String TEMPLATES_FOLDER_DISPLAY_NAME = "Templates";

    private static final SiteConfigsDataSerializer SITE_CONFIGS_DATA_SERIALIZER = new SiteConfigsDataSerializer();

    private final ExecutorService applyPermissionsExecutor;

    private ContentTypeService contentTypeService;

    private NodeService nodeService;

    private EventPublisher eventPublisher;

    private MediaInfoService mediaInfoService;

    private MixinService mixinService;

    private SiteService siteService;

    private ContentNodeTranslator translator;

    private IndexService indexService;

    private ContentProcessors contentProcessors;

    private FormDefaultValuesProcessor formDefaultValuesProcessor;

    public ContentServiceImpl()
    {
        final ThreadFactory namedThreadFactory = new ThreadFactoryBuilder().
            setNameFormat( "apply-permissions-thread-%d" ).
            setUncaughtExceptionHandler( ( t, e ) -> LOG.error( "Apply Permissions failed", e ) ).
            build();
        this.applyPermissionsExecutor = Executors.newFixedThreadPool( 5, namedThreadFactory );
        this.contentProcessors = new ContentProcessors();
    }

    @Activate
    public void initialize()
    {
        if ( this.indexService.isMaster() )
        {
            new ContentInitializer( this.nodeService ).initialize();
        }
    }

    @Override
    public Site create( final CreateSiteParams params )
    {
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
            mixinService( this.mixinService ).
            contentProcessors( this.contentProcessors ).
            formDefaultValuesProcessor( this.formDefaultValuesProcessor ).
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

        return site;
    }

    @Override
    public Content create( final CreateContentParams params )
    {
        final Content content = CreateContentCommand.create().
            nodeService( this.nodeService ).
            contentTypeService( this.contentTypeService ).
            translator( this.translator ).
            eventPublisher( this.eventPublisher ).
            siteService( this.siteService ).
            mixinService( this.mixinService ).
            contentProcessors( this.contentProcessors ).
            formDefaultValuesProcessor( this.formDefaultValuesProcessor ).
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

            return this.getById( content.getId() );
        }

        return content;
    }

    @Override
    public Content create( final CreateMediaParams params )
    {
        return CreateMediaCommand.create().
            params( params ).
            nodeService( this.nodeService ).
            contentTypeService( this.contentTypeService ).
            translator( this.translator ).
            eventPublisher( this.eventPublisher ).
            mediaInfoService( this.mediaInfoService ).
            siteService( this.siteService ).
            mixinService( this.mixinService ).
            contentProcessors( this.contentProcessors ).
            formDefaultValuesProcessor( this.formDefaultValuesProcessor ).
            build().
            execute();
    }

    @Override
    public Content update( final UpdateContentParams params )
    {
        return UpdateContentCommand.create( params ).
            nodeService( this.nodeService ).
            contentTypeService( this.contentTypeService ).
            translator( this.translator ).
            eventPublisher( this.eventPublisher ).
            siteService( this.siteService ).
            mixinService( this.mixinService ).
            contentProcessors( this.contentProcessors ).
            build().
            execute();
    }

    @Override
    public Content update( final UpdateMediaParams params )
    {
        return UpdateMediaCommand.create( params ).
            nodeService( this.nodeService ).
            contentTypeService( this.contentTypeService ).
            translator( this.translator ).
            eventPublisher( this.eventPublisher ).
            mediaInfoService( this.mediaInfoService ).
            siteService( this.siteService ).
            mixinService( this.mixinService ).
            contentProcessors( this.contentProcessors ).
            build().
            execute();
    }

    @Override
    public Contents delete( final DeleteContentParams params )
    {
        return DeleteAndFetchContentCommand.create().
            nodeService( this.nodeService ).
            contentService( this ).
            contentTypeService( this.contentTypeService ).
            translator( this.translator ).
            eventPublisher( this.eventPublisher ).
            params( params ).
            build().
            execute();
    }


    @Override
    public DeleteContentsResult deleteWithoutFetch( final DeleteContentParams params )
    {
        return DeleteContentCommand.create().
            nodeService( this.nodeService ).
            contentTypeService( this.contentTypeService ).
            translator( this.translator ).
            eventPublisher( this.eventPublisher ).
            params( params ).
            build().
            execute();
    }

    @Override
    public PushContentsResult push( final PushContentParams params )
    {
        return PushContentCommand.create().
            nodeService( this.nodeService ).
            contentTypeService( this.contentTypeService ).
            translator( this.translator ).
            eventPublisher( this.eventPublisher ).
            contentIds( params.getContentIds() ).
            excludedContentIds( params.getExcludedContentIds() ).
            target( params.getTarget() ).
            includeChildren( params.isIncludeChildren() ).
            includeDependencies( params.isIncludeDependencies() ).
            pushListener( params.getPushContentListener() ).
            build().
            execute();
    }


    @Override
    public PublishContentResult publish( final PushContentParams params )
    {
        return PublishContentCommand.create().
            nodeService( this.nodeService ).
            contentTypeService( this.contentTypeService ).
            translator( this.translator ).
            eventPublisher( this.eventPublisher ).
            contentIds( params.getContentIds() ).
            excludedContentIds( params.getExcludedContentIds() ).
            target( params.getTarget() ).
            includeChildren( params.isIncludeChildren() ).
            includeDependencies( params.isIncludeDependencies() ).
            pushListener( params.getPushContentListener() ).
            build().
            execute();
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
            target( params.getTarget() ).
            includeChildren( params.isIncludeChildren() ).
            build().
            execute();
    }

    @Override
    public Content getById( final ContentId contentId )
    {
        return doGetById( contentId );
    }

    private Content doGetById( final ContentId contentId )
    {
        return GetContentByIdCommand.create( contentId ).
            nodeService( this.nodeService ).
            contentTypeService( this.contentTypeService ).
            translator( this.translator ).
            eventPublisher( this.eventPublisher ).
            build().
            execute();
    }

    @Override
    public Site getNearestSite( final ContentId contentId )
    {
        return GetNearestSiteCommand.create().
            contentService( this ).
            contentId( contentId ).
            build().
            execute();
    }

    @Override
    public Contents getByIds( final GetContentByIdsParams params )
    {
        return GetContentByIdsCommand.create( params ).
            nodeService( this.nodeService ).
            contentTypeService( this.contentTypeService ).
            translator( this.translator ).
            eventPublisher( this.eventPublisher ).
            build().
            execute();
    }

    @Override
    public Content getByPath( final ContentPath path )
    {
        return GetContentByPathCommand.create( path ).
            nodeService( this.nodeService ).
            contentTypeService( this.contentTypeService ).
            translator( this.translator ).
            eventPublisher( this.eventPublisher ).
            build().
            execute();
    }

    @Override
    public AccessControlList getPermissionsById( ContentId contentId )
    {
        Content content = getById( contentId );
        if ( content != null && content.getPermissions() != null )
        {
            return content.getPermissions();
        }
        return AccessControlList.empty();
    }

    @Override
    public Contents getByPaths( final ContentPaths paths )
    {
        return GetContentByPathsCommand.create( paths ).
            nodeService( this.nodeService ).
            contentTypeService( this.contentTypeService ).
            translator( this.translator ).
            eventPublisher( this.eventPublisher ).
            build().
            execute();
    }

    @Override
    public FindContentByParentResult findByParent( final FindContentByParentParams params )
    {
        return FindContentByParentCommand.create( params ).
            nodeService( this.nodeService ).
            contentTypeService( this.contentTypeService ).
            translator( this.translator ).
            eventPublisher( this.eventPublisher ).
            build().
            execute();
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
    public Content duplicate( final DuplicateContentParams params )
    {
        final Node createdNode = nodeService.duplicate( NodeId.from( params.getContentId() ), new DuplicateContentProcessor() );
        return translator.fromNode( createdNode, true );
    }

    @Override
    public Content move( final MoveContentParams params )
    {
        final ContentPath destinationPath = params.getParentContentPath();
        if ( !destinationPath.isRoot() )
        {
            final Content parent = this.getByPath( destinationPath );
            if ( parent == null )
            {
                throw new IllegalArgumentException(
                    "Content could not be moved. Children not allowed in destination [" + destinationPath.toString() + "]" );
            }
            final ContentType parentContentType =
                contentTypeService.getByName( new GetContentTypeParams().contentTypeName( parent.getType() ) );
            if ( !parentContentType.allowChildContent() )
            {
                throw new IllegalArgumentException(
                    "Content could not be moved. Children not allowed in destination [" + destinationPath.toString() + "]" );
            }
        }

        try
        {
            final NodeId sourceNodeId = NodeId.from( params.getContentId().toString() );
            final Node sourceNode = nodeService.getById( sourceNodeId );
            if ( sourceNode == null )
            {
                throw new IllegalArgumentException( String.format( "Content with id [%s] not found", params.getContentId() ) );
            }

            final Node movedNode = nodeService.move( sourceNodeId, NodePath.create( ContentConstants.CONTENT_ROOT_PATH ).elements(
                params.getParentContentPath().toString() ).build() );
            return translator.fromNode( movedNode, true );
        }
        catch ( MoveNodeException e )
        {
            throw new MoveContentException( e.getMessage() );
        }
        catch ( NodeAlreadyExistAtPathException e )
        {
            throw new MoveContentException( "Content already exists at path: " + e.getNode().toString() );
        }
        catch ( NodeAccessException e )
        {
            throw new ContentAccessException( e );
        }
    }

    @Override
    public Content rename( final RenameContentParams params )
    {
        return RenameContentCommand.create( params ).
            nodeService( this.nodeService ).
            contentTypeService( this.contentTypeService ).
            translator( this.translator ).
            eventPublisher( this.eventPublisher ).
            build().
            execute();
    }

    @Override
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
        return FindContentIdsByQueryCommand.create().
            query( query ).
            nodeService( this.nodeService ).
            contentTypeService( this.contentTypeService ).
            translator( this.translator ).
            eventPublisher( this.eventPublisher ).
            build().
            execute();
    }

    @Override
    public CompareContentResult compare( final CompareContentParams params )
    {
        return CompareContentCommand.create().
            nodeService( this.nodeService ).
            contentId( params.getContentId() ).
            target( params.getTarget() ).
            build().
            execute();
    }

    @Override
    public CompareContentResults compare( final CompareContentsParams params )
    {
        return CompareContentsCommand.create().
            nodeService( this.nodeService ).
            contentIds( params.getContentIds() ).
            target( params.getTarget() ).
            build().
            execute();
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
    public SetActiveContentVersionResult setActiveContentVersion( final ContentId contentId, final ContentVersionId versionId )
    {
        nodeService.setActiveVersion( NodeId.from( contentId.toString() ), NodeVersionId.from( versionId.toString() ) );

        return new SetActiveContentVersionResult( contentId, versionId );
    }

    @Override
    public Content setChildOrder( final SetContentChildOrderParams params )
    {
        try
        {
            final Node node = nodeService.setChildOrder( SetNodeChildOrderParams.create().
                nodeId( NodeId.from( params.getContentId() ) ).
                childOrder( params.getChildOrder() ).
                build() );

            return translator.fromNode( node, true );
        }
        catch ( NodeAccessException e )
        {
            throw new ContentAccessException( e );
        }

    }

    @Override
    public ReorderChildContentsResult reorderChildren( final ReorderChildContentsParams params )
    {
        final ReorderChildNodesParams.Builder builder = ReorderChildNodesParams.create();

        for ( final ReorderChildParams param : params )
        {
            builder.add( ReorderChildNodeParams.create().
                nodeId( NodeId.from( param.getContentToMove() ) ).
                moveBefore( param.getContentToMoveBefore() == null ? null : NodeId.from( param.getContentToMoveBefore() ) ).
                build() );
        }

        final ReorderChildNodesResult reorderChildNodesResult = this.nodeService.reorderChildren( builder.build() );
        this.nodeService.refresh( RefreshMode.SEARCH );
        return new ReorderChildContentsResult( reorderChildNodesResult.getSize() );
    }

    @Override
    public Future<Integer> applyPermissions( final ApplyContentPermissionsParams params )
    {
        final ApplyContentPermissionsCommand applyPermissionsCommand = ApplyContentPermissionsCommand.create( params ).
            nodeService( this.nodeService ).
            contentTypeService( this.contentTypeService ).
            translator( this.translator ).
            eventPublisher( this.eventPublisher ).
            build();

        final Context context = ContextAccessor.current();

        return CompletableFuture.supplyAsync( () -> {
            // set current context as background thread context
            final Context futureContext = ContextBuilder.from( context ).build();

            return futureContext.callWith( applyPermissionsCommand::execute );

        }, applyPermissionsExecutor );
    }

    @Override
    public AccessControlList getRootPermissions()
    {
        final ContentPath rootContentPath = ContentPath.ROOT;
        final NodePath rootNodePath = ContentNodeHelper.translateContentPathToNodePath( rootContentPath );
        final Node rootNode = runAsContentAdmin( () -> nodeService.getByPath( rootNodePath ) );
        return rootNode != null ? rootNode.getPermissions() : AccessControlList.empty();
    }

    @Override
    public ContentDependencies getDependencies(final ContentId id) {
        final ContentDependenciesResolver contentDependenciesResolver =
            new ContentDependenciesResolver( this );

        return contentDependenciesResolver.resolve( id );
    }

    @Override
    public Collection<ContentId> getOutboundDependenciesIds(final ContentId id) {
        final ContentOutboundDependenciesIdsResolver contentOutboundDependenciesIdsResolver =
                new ContentOutboundDependenciesIdsResolver( this );

        return contentOutboundDependenciesIdsResolver.resolve( id );
    }

    @Override
    public boolean contentExists( final ContentId contentId )
    {
        try
        {
            return this.nodeService.nodeExists( NodeId.from( contentId ) );
        }
        catch ( IllegalArgumentException e )
        {
            return false;
        }
    }

    @Override
    public boolean contentExists( final ContentPath contentPath )
    {
        try
        {
            return this.nodeService.nodeExists( ContentNodeHelper.translateContentPathToNodePath( contentPath ) );
        }
        catch ( IllegalArgumentException e )
        {
            return false;
        }
    }

    private <T> T runAsContentAdmin( final Callable<T> callable )
    {
        final Context context = ContextAccessor.current();
        final AuthenticationInfo authInfo = context.getAuthInfo();
        if ( !authInfo.isAuthenticated() )
        {
            return context.callWith( callable );
        }

        return ContextBuilder.from( ContextAccessor.current() ).
            authInfo( AuthenticationInfo.copyOf( authInfo ).principals( RoleKeys.ADMIN, RoleKeys.CONTENT_MANAGER_ADMIN ).build() ).
            build().
            callWith( callable );
    }

    @Override
    public ByteSource getBinary( final ContentId contentId, final BinaryReference binaryReference )
    {
        return nodeService.getBinary( NodeId.from( contentId.toString() ), binaryReference );
    }

    @Override
    public String getBinaryKey( final ContentId contentId, final BinaryReference binaryReference )
    {
        return nodeService.getBinaryKey( NodeId.from( contentId.toString() ), binaryReference );
    }

    @Override
    public Content reprocess( final ContentId contentId )
    {
        return ReprocessContentCommand.create( ReprocessContentParams.create().contentId( contentId ).build() ).
            nodeService( this.nodeService ).
            contentTypeService( this.contentTypeService ).
            translator( this.translator ).
            eventPublisher( this.eventPublisher ).
            contentService( this ).
            build().
            execute();
    }

    @Override
    public UnpublishContentsResult unpublishContent( final UnpublishContentParams params )
    {
        return UnpublishContentCommand.create().
            params( params ).
            nodeService( this.nodeService ).
            contentTypeService( this.contentTypeService ).
            translator( this.translator ).
            eventPublisher( this.eventPublisher ).
            build().
            execute();
    }

    @Override
    @Deprecated
    public InputStream getBinaryInputStream( final ContentId contentId, final BinaryReference binaryReference )
    {
        try
        {
            return nodeService.getBinary( NodeId.from( contentId.toString() ), binaryReference ).openStream();
        }
        catch ( IOException e )
        {
            e.printStackTrace();
        }
        return null;
    }

    @Reference
    public void setContentTypeService( final ContentTypeService contentTypeService )
    {
        this.contentTypeService = contentTypeService;
    }

    @Reference
    public void setNodeService( final NodeService nodeService )
    {
        this.nodeService = nodeService;
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
    public void setSiteService( final SiteService siteService )
    {
        this.siteService = siteService;
    }

    @Reference
    public void setTranslator( final ContentNodeTranslator translator )
    {
        this.translator = translator;
    }

    @SuppressWarnings("unused")
    @Reference
    public void setIndexService( final IndexService indexService )
    {
        this.indexService = indexService;
    }

    @SuppressWarnings("unused")
    @Reference(cardinality = ReferenceCardinality.MULTIPLE, policy = ReferencePolicy.DYNAMIC)
    public void contentProcessors( final ContentProcessor contentProcessor )
    {
        this.contentProcessors.add( contentProcessor );
    }

    @Reference
    public void setFormDefaultValuesProcessor( final FormDefaultValuesProcessor formDefaultValuesProcessor )
    {
        this.formDefaultValuesProcessor = formDefaultValuesProcessor;
    }
}
