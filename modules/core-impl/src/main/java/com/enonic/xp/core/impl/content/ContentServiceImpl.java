package com.enonic.xp.core.impl.content;

import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.io.ByteSource;
import com.google.common.util.concurrent.ThreadFactoryBuilder;

import com.enonic.xp.content.ApplyContentPermissionsParams;
import com.enonic.xp.content.CompareContentParams;
import com.enonic.xp.content.CompareContentResult;
import com.enonic.xp.content.CompareContentResults;
import com.enonic.xp.content.CompareContentsParams;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentChangeEvent;
import com.enonic.xp.content.ContentConstants;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentNotFoundException;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.ContentPaths;
import com.enonic.xp.content.ContentService;
import com.enonic.xp.content.Contents;
import com.enonic.xp.content.CreateContentParams;
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
import com.enonic.xp.content.SetContentChildOrderParams;
import com.enonic.xp.content.SortContentParams;
import com.enonic.xp.content.UpdateContentParams;
import com.enonic.xp.content.UpdateMediaParams;
import com.enonic.xp.content.site.CreateSiteParams;
import com.enonic.xp.content.site.ModuleConfigsDataSerializer;
import com.enonic.xp.content.site.Site;
import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.event.EventPublisher;
import com.enonic.xp.exception.SystemException;
import com.enonic.xp.media.MediaInfoService;
import com.enonic.xp.module.ModuleService;
import com.enonic.xp.node.MoveNodeException;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeService;
import com.enonic.xp.node.ReorderChildNodeParams;
import com.enonic.xp.node.ReorderChildNodesParams;
import com.enonic.xp.node.ReorderChildNodesResult;
import com.enonic.xp.node.SetNodeChildOrderParams;
import com.enonic.xp.schema.content.ContentType;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.schema.content.ContentTypeService;
import com.enonic.xp.schema.content.GetContentTypeParams;
import com.enonic.xp.schema.mixin.MixinService;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.acl.AccessControlList;
import com.enonic.xp.security.auth.AuthenticationInfo;
import com.enonic.xp.util.BinaryReference;

import static com.enonic.xp.core.impl.content.ContentNodeHelper.translateNodePathToContentPath;

@Component(immediate = true)
public class ContentServiceImpl
    implements ContentService
{
    private final static Logger LOG = LoggerFactory.getLogger( ContentServiceImpl.class );

    public static final String TEMPLATES_FOLDER_NAME = "_templates";

    private static final String TEMPLATES_FOLDER_DISPLAY_NAME = "Templates";

    private static final ModuleConfigsDataSerializer MODULE_CONFIGS_DATA_SERIALIZER = new ModuleConfigsDataSerializer();

    private ContentTypeService contentTypeService;

    private NodeService nodeService;

    private ContentNodeTranslator contentNodeTranslator;

    private EventPublisher eventPublisher;

    private MediaInfoService mediaInfoService;

    private final ExecutorService applyPermissionsExecutor;

    private MixinService mixinService;

    private ModuleService moduleService;

    public ContentServiceImpl()
    {
        final ThreadFactory namedThreadFactory = new ThreadFactoryBuilder().
            setNameFormat( "apply-permissions-thread-%d" ).
            setUncaughtExceptionHandler( ( t, e ) -> LOG.error( "Apply Permissions failed", e ) ).
            build();
        this.applyPermissionsExecutor = Executors.newFixedThreadPool( 5, namedThreadFactory );
    }

    @Activate
    public void initialize()
    {
        new ContentInitializer( this.nodeService ).initialize();
    }

    @Override
    public Site create( final CreateSiteParams params )
    {
        final PropertyTree data = new PropertyTree();
        data.setString( "description", params.getDescription() );

        MODULE_CONFIGS_DATA_SERIALIZER.toProperties( params.getModuleConfigs(), data.getRoot() );

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
            translator( this.contentNodeTranslator ).
            eventPublisher( this.eventPublisher ).
            moduleService( this.moduleService ).
            mixinService( this.mixinService ).
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
        if ( params.getType().isTemplateFolder() )
        {
            validateCreateTemplateFolder( params );
        }
        else if ( params.getType().isPageTemplate() )
        {
            validateCreatePageTemplate( params );
        }

        final Content content = CreateContentCommand.create().
            nodeService( this.nodeService ).
            contentTypeService( this.contentTypeService ).
            translator( this.contentNodeTranslator ).
            eventPublisher( this.eventPublisher ).
            moduleService( this.moduleService ).
            mixinService( this.mixinService ).
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
            translator( this.contentNodeTranslator ).
            eventPublisher( this.eventPublisher ).
            mediaInfoService( this.mediaInfoService ).
            moduleService( this.moduleService ).
            mixinService( this.mixinService ).
            build().
            execute();
    }

    @Override
    public Content update( final UpdateContentParams params )
    {
        return UpdateContentCommand.create( params ).
            nodeService( this.nodeService ).
            contentTypeService( this.contentTypeService ).
            translator( this.contentNodeTranslator ).
            eventPublisher( this.eventPublisher ).
            moduleService( this.moduleService ).
            mixinService( this.mixinService ).
            build().
            execute();
    }

    @Override
    public Content update( final UpdateMediaParams params )
    {
        return UpdateMediaCommand.create( params ).
            nodeService( this.nodeService ).
            contentTypeService( this.contentTypeService ).
            translator( this.contentNodeTranslator ).
            eventPublisher( this.eventPublisher ).
            mediaInfoService( this.mediaInfoService ).
            moduleService( this.moduleService ).
            mixinService( this.mixinService ).
            build().
            execute();
    }

    @Override
    public Content delete( final DeleteContentParams params )
    {
        return DeleteContentCommand.create().
            nodeService( this.nodeService ).
            contentTypeService( this.contentTypeService ).
            translator( this.contentNodeTranslator ).
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
            translator( this.contentNodeTranslator ).
            eventPublisher( this.eventPublisher ).
            contentIds( params.getContentIds() ).
            target( params.getTarget() ).
            includeChildren( params.isIncludeChildren() ).
            strategy( params.isAllowPublishOutsideSelection()
                          ? PushContentCommand.PushContentStrategy.ALLOW_PUBLISH_OUTSIDE_SELECTION
                          : PushContentCommand.PushContentStrategy.STRICT ).
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
            translator( this.contentNodeTranslator ).
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
            translator( this.contentNodeTranslator ).
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
            translator( this.contentNodeTranslator ).
            eventPublisher( this.eventPublisher ).
            build().
            execute();
    }

    @Override
    public AccessControlList getPermissionsByPath( ContentPath path ) {
        Content content = getByPath(path);
        if(content != null && content.getPermissions() != null)
            return content.getPermissions();
        return AccessControlList.empty();
    }

    @Override
    public Contents getByPaths( final ContentPaths paths )
    {
        return GetContentByPathsCommand.create( paths ).
            nodeService( this.nodeService ).
            contentTypeService( this.contentTypeService ).
            translator( this.contentNodeTranslator ).
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
            translator( this.contentNodeTranslator ).
            eventPublisher( this.eventPublisher ).
            build().
            execute();
    }

    private void validateCreateTemplateFolder( final CreateContentParams params )
    {
        try
        {
            final Content parent = this.getByPath( params.getParent() );
            if ( !parent.getType().isSite() )
            {
                final ContentPath path = ContentPath.from( params.getParent(), params.getName().toString() );
                throw new SystemException( "A template folder can only be created below a content of type 'site'. Path: " + path );
            }
        }
        catch ( ContentNotFoundException e )
        {
            final ContentPath path = ContentPath.from( params.getParent(), params.getName().toString() );
            throw new SystemException( e,
                                       "Parent folder not found; A template folder can only be created below a content of type 'site'. Path: " +
                                           path );
        }
    }

    private void validateCreatePageTemplate( final CreateContentParams params )
    {
        try
        {
            final Content parent = this.getByPath( params.getParent() );
            if ( !parent.getType().isTemplateFolder() )
            {
                final ContentPath path = ContentPath.from( params.getParent(), params.getName().toString() );
                throw new SystemException( "A page template can only be created below a content of type 'template-folder'. Path: " + path );
            }
        }
        catch ( ContentNotFoundException e )
        {
            final ContentPath path = ContentPath.from( params.getParent(), params.getName().toString() );
            throw new SystemException( e,
                                       "Parent not found; A page template can only be created below a content of type 'template-folder'. Path: " +
                                           path );
        }
    }

    @Override
    public Content duplicate( final DuplicateContentParams params )
    {
        final Node createdNode = nodeService.duplicate( NodeId.from( params.getContentId() ) );

        final ContentPath targetPath = translateNodePathToContentPath( createdNode.path() );
        eventPublisher.publish( ContentChangeEvent.from( ContentChangeEvent.ContentChangeType.DUPLICATE, targetPath ) );

        return contentNodeTranslator.fromNode( createdNode );
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
            final Node sourceNode = nodeService.getById( NodeId.from( params.getContentId() ) );
            if ( sourceNode == null )
            {
                throw new IllegalArgumentException( String.format( "Content with id [%s] not found", params.getContentId() ) );
            }
            final Node movedNode = nodeService.move( NodeId.from( params.getContentId() ),
                                                     NodePath.newPath( ContentConstants.CONTENT_ROOT_PATH ).elements(
                                                         params.getParentContentPath().toString() ).build() );

            final ContentChangeEvent event = ContentChangeEvent.create().
                change( ContentChangeEvent.ContentChangeType.DELETE, translateNodePathToContentPath( sourceNode.path() ) ).
                change( ContentChangeEvent.ContentChangeType.CREATE, translateNodePathToContentPath( movedNode.path() ) ).
                build();
            eventPublisher.publish( event );

            return contentNodeTranslator.fromNode( movedNode );
        }
        catch ( MoveNodeException e )
        {
            throw new MoveContentException( e.getMessage() );
        }
    }

    @Override
    public Content rename( final RenameContentParams params )
    {
        return RenameContentCommand.create( params ).
            nodeService( this.nodeService ).
            contentTypeService( this.contentTypeService ).
            translator( this.contentNodeTranslator ).
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
            translator( this.contentNodeTranslator ).
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
            translator( this.contentNodeTranslator ).
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
            translator( this.contentNodeTranslator ).
            eventPublisher( this.eventPublisher ).
            contentId( params.getContentId() ).
            branches( params.getBranches() ).
            build().
            execute();
    }

    @Override
    public Content sort( final SortContentParams params )
    {
        Content content = doGetById( params.getContentId() );

        return SortContentCommand.create( params ).
            nodeService( this.nodeService ).
            contentTypeService( this.contentTypeService ).
            translator( this.contentNodeTranslator ).
            eventPublisher( this.eventPublisher ).
            build().
            execute();
    }

    @Override
    public Content setChildOrder( final SetContentChildOrderParams params )
    {
        final Node node = nodeService.setChildOrder( SetNodeChildOrderParams.create().
            nodeId( NodeId.from( params.getContentId() ) ).
            childOrder( params.getChildOrder() ).
            build() );

        return contentNodeTranslator.fromNode( node );
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

        return new ReorderChildContentsResult( reorderChildNodesResult.getSize() );
    }

    @Override
    public String generateContentName( final String displayName )
    {
        return new ContentPathNameGenerator().generatePathName( displayName );
    }

    @Override
    public CompletableFuture<Integer> applyPermissions( final ApplyContentPermissionsParams params )
    {
        final ApplyContentPermissionsCommand applyPermissionsCommand = ApplyContentPermissionsCommand.create( params ).
            nodeService( this.nodeService ).
            contentTypeService( this.contentTypeService ).
            translator( this.contentNodeTranslator ).
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
    public void setContentNodeTranslator( final ContentNodeTranslator contentNodeTranslator )
    {
        this.contentNodeTranslator = contentNodeTranslator;
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
    public void setModuleService( final ModuleService moduleService )
    {
        this.moduleService = moduleService;
    }
}
