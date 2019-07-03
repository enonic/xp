package com.enonic.xp.core.impl.layer;

import java.text.MessageFormat;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.content.ContentConstants;
import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.data.ValueFactory;
import com.enonic.xp.index.IndexService;
import com.enonic.xp.layer.ContentLayer;
import com.enonic.xp.layer.ContentLayerConstants;
import com.enonic.xp.layer.ContentLayerException;
import com.enonic.xp.layer.ContentLayerName;
import com.enonic.xp.layer.ContentLayerService;
import com.enonic.xp.layer.ContentLayers;
import com.enonic.xp.layer.CreateContentLayerParams;
import com.enonic.xp.node.CreateNodeParams;
import com.enonic.xp.node.FindNodesByQueryResult;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeIndexPath;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeQuery;
import com.enonic.xp.node.NodeService;
import com.enonic.xp.node.NodeType;
import com.enonic.xp.query.filter.ValueFilter;
import com.enonic.xp.repository.CreateBranchParams;
import com.enonic.xp.repository.Repository;
import com.enonic.xp.repository.RepositoryService;

@Component
public class ContentLayerServiceImpl
    implements ContentLayerService
{
    private IndexService indexService;

    private NodeService nodeService;

    private RepositoryService repositoryService;

    @Activate
    public void initialize()
    {
        ContentLayerInitializer.create().
            setIndexService( indexService ).
            setNodeService( nodeService ).
            build().
            initialize();
    }

    @Override
    public ContentLayers list()
    {
        return createContext().callWith( this::doList );
    }

    private ContentLayers doList()
    {
        final ValueFilter valueFilter = ValueFilter.create().
            fieldName( NodeIndexPath.NODE_TYPE.getPath() ).
            addValue( ValueFactory.newString( ContentLayerConstants.NODE_TYPE ) ).
            build();

        final NodeQuery nodeQuery = NodeQuery.create().
            addQueryFilter( valueFilter ).
            build();

        final FindNodesByQueryResult result = nodeService.findByQuery( nodeQuery );

        final List<ContentLayer> contentLayers = result.getNodeIds().
            stream().
            map( nodeService::getById ).
            filter( Objects::nonNull ).
            map( this::toContentLayer ).
            collect( Collectors.toList() );

        return ContentLayers.from( contentLayers );
    }

    @Override
    public ContentLayer get( final ContentLayerName name )
    {
        return createContext().callWith( () -> doGet( name ) );
    }

    private ContentLayer doGet( final ContentLayerName name )
    {
        final Node node = nodeService.getByPath( toNodePath( name ) );
        return toContentLayer( node );
    }

    @Override
    public ContentLayer create( final CreateContentLayerParams params )
    {
        return createContext().callWith( () -> doCreate( params ) );
    }

    private ContentLayer doCreate( final CreateContentLayerParams params )
    {
        if ( nodeService.nodeExists( toNodePath( params.getName() ) ) )
        {
            throw new ContentLayerException( MessageFormat.format( "Layer [{0}] already exists", params.getName() ) );
        }

        //Creates branches
        final Branch draftBranch = Branch.from( ContentLayerConstants.BRANCH_PREFIX_DRAFT + params.getName() );
        final Branch masterBranch = Branch.from( ContentLayerConstants.BRANCH_PREFIX_MASTER + params.getName() );
        final ContentLayerName parentLayer = params.getParentName();
        final Branch parentDraftBranch = parentLayer.getDraftBranch();

        final Repository contentRepository = repositoryService.get( ContentConstants.CONTENT_REPO_ID );
        if ( contentRepository == null || contentRepository.getChildBranchInfos( parentDraftBranch ) == null )
        {
            throw new ContentLayerException( MessageFormat.format( "Branch [{0}] not found", parentDraftBranch ) );
        }

        final CreateBranchParams createDraftBranchParams = new CreateBranchParams( draftBranch, parentDraftBranch );
        repositoryService.createBranch( createDraftBranchParams );

        final CreateBranchParams createMasterBranchParams = new CreateBranchParams( masterBranch );
        repositoryService.createBranch( createMasterBranchParams );

        //Creates node representation
        PropertyTree data = new PropertyTree();
        data.setString( ContentLayerConstants.NAME_PROPERTY_PATH, params.getName().getValue() );
        data.setString( ContentLayerConstants.PARENT_NAME_PROPERTY_PATH,
                        params.getParentName() == null ? null : params.getParentName().getValue() );
        data.setString( ContentLayerConstants.DISPLAY_NAME_PROPERTY_PATH, params.getDisplayName() );
        final Node createdNode = nodeService.create( CreateNodeParams.create().
            parent( ContentLayerConstants.LAYER_PARENT_PATH ).
            name( params.getName().getValue() ).
            data( data ).
            nodeType( NodeType.from( ContentLayerConstants.NODE_TYPE ) ).
            inheritPermissions( true ).
            build() );

        return toContentLayer( createdNode );
    }

    private NodePath toNodePath( final ContentLayerName name )
    {
        return NodePath.create( ContentLayerConstants.LAYER_PARENT_PATH, name.getValue() ).build();
    }

    private Context createContext()
    {
        return ContextBuilder.from( ContextAccessor.current() ).
            branch( ContentConstants.BRANCH_MASTER ).
            build();
    }

    private ContentLayer toContentLayer( final Node node )
    {
        if ( node == null )
        {
            return null;
        }
        final String name = node.data().getString( ContentLayerConstants.NAME_PROPERTY_PATH );
        final String parentName = node.data().getString( ContentLayerConstants.PARENT_NAME_PROPERTY_PATH );
        final String displayName = node.data().getString( ContentLayerConstants.DISPLAY_NAME_PROPERTY_PATH );
        return ContentLayer.create().
            name( ContentLayerName.from( name ) ).
            parentName( parentName == null ? null : ContentLayerName.from( parentName ) ).
            displayName( displayName ).
            build();
    }

    @Reference
    public void setIndexService( final IndexService indexService )
    {
        this.indexService = indexService;
    }

    @Reference
    public void setNodeService( final NodeService nodeService )
    {
        this.nodeService = nodeService;
    }

    @Reference
    public void setRepositoryService( final RepositoryService repositoryService )
    {
        this.repositoryService = repositoryService;
    }
}
