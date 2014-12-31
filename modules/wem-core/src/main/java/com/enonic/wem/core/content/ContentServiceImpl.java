package com.enonic.wem.core.content;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.io.ByteSource;
import com.google.common.util.concurrent.ThreadFactoryBuilder;

import com.enonic.wem.api.blob.BlobService;
import com.enonic.wem.api.content.ApplyContentPermissionsParams;
import com.enonic.wem.api.content.CompareContentParams;
import com.enonic.wem.api.content.CompareContentResult;
import com.enonic.wem.api.content.CompareContentResults;
import com.enonic.wem.api.content.CompareContentsParams;
import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentConstants;
import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.ContentNotFoundException;
import com.enonic.wem.api.content.ContentPath;
import com.enonic.wem.api.content.ContentPaths;
import com.enonic.wem.api.content.ContentService;
import com.enonic.wem.api.content.Contents;
import com.enonic.wem.api.content.CreateContentParams;
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
import com.enonic.wem.api.content.GetContentByIdsParams;
import com.enonic.wem.api.content.MoveContentParams;
import com.enonic.wem.api.content.PushContentParams;
import com.enonic.wem.api.content.RenameContentParams;
import com.enonic.wem.api.content.ReorderChildContentsParams;
import com.enonic.wem.api.content.ReorderChildContentsResult;
import com.enonic.wem.api.content.ReorderChildParams;
import com.enonic.wem.api.content.SetContentChildOrderParams;
import com.enonic.wem.api.content.UpdateContentParams;
import com.enonic.wem.api.content.UpdateMediaParams;
import com.enonic.wem.api.content.ValidateContentData;
import com.enonic.wem.api.content.site.CreateSiteParams;
import com.enonic.wem.api.content.site.ModuleConfigsDataSerializer;
import com.enonic.wem.api.content.site.Site;
import com.enonic.wem.api.data.PropertyTree;
import com.enonic.wem.api.event.EventPublisher;
import com.enonic.wem.api.exception.SystemException;
import com.enonic.wem.api.media.MediaInfoService;
import com.enonic.wem.api.node.Node;
import com.enonic.wem.api.node.NodeId;
import com.enonic.wem.api.node.NodePath;
import com.enonic.wem.api.node.NodeService;
import com.enonic.wem.api.node.ReorderChildNodeParams;
import com.enonic.wem.api.node.ReorderChildNodesParams;
import com.enonic.wem.api.node.ReorderChildNodesResult;
import com.enonic.wem.api.node.SetNodeChildOrderParams;
import com.enonic.wem.api.schema.content.ContentTypeName;
import com.enonic.wem.api.schema.content.ContentTypeService;
import com.enonic.wem.api.schema.content.validator.DataValidationErrors;
import com.enonic.wem.api.util.BinaryReference;

public class ContentServiceImpl
    implements ContentService
{
    private final static Logger LOG = LoggerFactory.getLogger( ContentServiceImpl.class );

    public static final String TEMPLATES_FOLDER_NAME = "_templates";

    private static final String TEMPLATES_FOLDER_DISPLAY_NAME = "Templates";

    private static final ModuleConfigsDataSerializer MODULE_CONFIGS_DATA_SERIALIZER = new ModuleConfigsDataSerializer();

    private ContentTypeService contentTypeService;

    private NodeService nodeService;

    private BlobService blobService;

    private ContentNodeTranslator contentNodeTranslator;

    private EventPublisher eventPublisher;

    private MediaInfoService mediaInfoService;

    private final ExecutorService applyPermissionsExecutor;

    public ContentServiceImpl()
    {
        final ThreadFactory namedThreadFactory = new ThreadFactoryBuilder().
            setNameFormat( "apply-permissions-thread-%d" ).
            setUncaughtExceptionHandler( ( t, e ) -> LOG.error( "Apply Permissions failed", e ) ).
            build();
        this.applyPermissionsExecutor = Executors.newFixedThreadPool( 5, namedThreadFactory );
    }

    @Override
    public Site create( final CreateSiteParams params )
    {
        // TODO: validate that PageTemplates are only created below  Site/templates

        final PropertyTree data = new PropertyTree();
        data.setString( "description", params.getDescription() );

        MODULE_CONFIGS_DATA_SERIALIZER.toProperties( params.getModuleConfigs(), data.getRoot() );

        final CreateContentParams createContentParams = new CreateContentParams().
            type( ContentTypeName.site() ).
            parent( params.getParentContentPath() ).
            name( params.getName() ).
            displayName( params.getDisplayName() ).
            form( ContentTypeForms.SITE ).
            contentData( data ).draft( params.isDraft() );

        final Site site = (Site) CreateContentCommand.create().
            nodeService( this.nodeService ).
            contentTypeService( this.contentTypeService ).
            blobService( this.blobService ).
            translator( this.contentNodeTranslator ).
            eventPublisher( this.eventPublisher ).
            params( createContentParams ).
            build().
            execute();

        this.create( new CreateContentParams().
            owner( site.getOwner() ).
            displayName( TEMPLATES_FOLDER_DISPLAY_NAME ).
            name( TEMPLATES_FOLDER_NAME ).
            parent( site.getPath() ).
            type( ContentTypeName.templateFolder() ).
            draft( false ).
            contentData( new PropertyTree() ) );

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
            blobService( this.blobService ).
            translator( this.contentNodeTranslator ).
            eventPublisher( this.eventPublisher ).
            params( params ).
            build().
            execute();

        if ( content instanceof Site )
        {
            this.create( new CreateContentParams().
                owner( content.getOwner() ).
                displayName( TEMPLATES_FOLDER_DISPLAY_NAME ).
                name( TEMPLATES_FOLDER_NAME ).
                parent( content.getPath() ).
                type( ContentTypeName.templateFolder() ).
                draft( false ).
                contentData( new PropertyTree() ) );
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
            blobService( this.blobService ).
            translator( this.contentNodeTranslator ).
            eventPublisher( this.eventPublisher ).
            mediaInfoService( this.mediaInfoService ).
            build().
            execute();
    }

    @Override
    public Content update( final UpdateContentParams params )
    {
        return UpdateContentCommand.create( params ).
            nodeService( this.nodeService ).
            contentTypeService( this.contentTypeService ).
            blobService( this.blobService ).
            translator( this.contentNodeTranslator ).
            eventPublisher( this.eventPublisher ).
            build().
            execute();
    }

    @Override
    public Content update( final UpdateMediaParams params )
    {
        return UpdateMediaCommand.create( params ).
            nodeService( this.nodeService ).
            contentTypeService( this.contentTypeService ).
            blobService( this.blobService ).
            translator( this.contentNodeTranslator ).
            eventPublisher( this.eventPublisher ).
            mediaInfoService( this.mediaInfoService ).
            build().
            execute();
    }

    @Override
    public Content delete( final DeleteContentParams params )
    {
        return DeleteContentCommand.create().
            nodeService( this.nodeService ).
            contentTypeService( this.contentTypeService ).
            blobService( this.blobService ).
            translator( this.contentNodeTranslator ).
            params( params ).
            build().
            execute();
    }

    @Override
    public Content push( final PushContentParams params )
    {
        params.getContentId();

        return PushContentCommand.create().
            nodeService( this.nodeService ).
            contentTypeService( this.contentTypeService ).
            blobService( this.blobService ).
            translator( this.contentNodeTranslator ).
            eventPublisher( this.eventPublisher ).
            contentId( params.getContentId() ).
            target( params.getTarget() ).
            build().
            execute();
    }

    @Override
    public Content getById( final ContentId id )
    {
        return doGetById( id );
    }

    private Content doGetById( final ContentId id )
    {
        return GetContentByIdCommand.create( id ).
            nodeService( this.nodeService ).
            contentTypeService( this.contentTypeService ).
            blobService( this.blobService ).
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
            blobService( this.blobService ).
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
            blobService( this.blobService ).
            translator( this.contentNodeTranslator ).
            eventPublisher( this.eventPublisher ).
            build().
            execute();
    }

    @Override
    public Contents getByPaths( final ContentPaths paths )
    {
        return GetContentByPathsCommand.create( paths ).
            nodeService( this.nodeService ).
            contentTypeService( this.contentTypeService ).
            blobService( this.blobService ).
            translator( this.contentNodeTranslator ).
            eventPublisher( this.eventPublisher ).
            build().
            execute();
    }

    @Override
    public FindContentByParentResult findByParent( final FindContentByParentParams params )
    {
        return FindContentByParentCommand.create( params ).
            //    queryService( this.queryService ).
                nodeService( this.nodeService ).
            contentTypeService( this.contentTypeService ).
            blobService( this.blobService ).
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
                                           path
            );
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
                                           path
            );
        }
    }

    @Override
    public DataValidationErrors validate( final ValidateContentData data )
    {
        return new ValidateContentDataCommand().
            contentTypeService( this.contentTypeService ).
            data( data ).
            execute();
    }

    @Override
    public Content duplicate( final DuplicateContentParams params )
    {
        final Node createdNode = nodeService.duplicate( NodeId.from( params.getContentId() ) );

        return contentNodeTranslator.fromNode( createdNode );
    }

    @Override
    public Content move( final MoveContentParams params )
    {
        final Node movedNode = nodeService.move( NodeId.from( params.getContentId() ),
                                                 NodePath.newPath( ContentConstants.CONTENT_ROOT_PATH ).elements(
                                                     params.getParentContentPath().toString() ).build()
        );

        return contentNodeTranslator.fromNode( movedNode );
    }

    @Override
    public Content rename( final RenameContentParams params )
    {
        return RenameContentCommand.create( params ).
            nodeService( this.nodeService ).
            contentTypeService( this.contentTypeService ).
            blobService( this.blobService ).
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
            blobService( this.blobService ).
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
            blobService( this.blobService ).
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
            blobService( this.blobService ).
            translator( this.contentNodeTranslator ).
            eventPublisher( this.eventPublisher ).
            contentId( params.getContentId() ).
            workspaces( params.getWorkspaces() ).
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
            blobService( this.blobService ).
            translator( this.contentNodeTranslator ).
            eventPublisher( this.eventPublisher ).
            build();

        return CompletableFuture.supplyAsync( applyPermissionsCommand::execute, applyPermissionsExecutor );
    }

    @Override
    public ByteSource getBinary( final ContentId contentId, final BinaryReference binaryReference )
    {
        return nodeService.getBinary( NodeId.from( contentId.toString() ), binaryReference );
    }

    public void setContentTypeService( final ContentTypeService contentTypeService )
    {
        this.contentTypeService = contentTypeService;
    }

    public void setNodeService( final NodeService nodeService )
    {
        this.nodeService = nodeService;
    }

    public void setBlobService( final BlobService blobService )
    {
        this.blobService = blobService;
    }

    public void setContentNodeTranslator( final ContentNodeTranslator contentNodeTranslator )
    {
        this.contentNodeTranslator = contentNodeTranslator;
    }

    public void setEventPublisher( final EventPublisher eventPublisher )
    {
        this.eventPublisher = eventPublisher;
    }

    public void setMediaInfoService( final MediaInfoService mediaInfoService )
    {
        this.mediaInfoService = mediaInfoService;
    }
}
