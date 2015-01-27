package com.enonic.wem.core.content;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.wem.api.content.ContentConstants;
import com.enonic.wem.api.data.PropertyTree;
import com.enonic.wem.api.node.CreateNodeParams;
import com.enonic.wem.api.node.Node;
import com.enonic.wem.api.node.NodeIds;
import com.enonic.wem.api.node.NodeService;
import com.enonic.wem.api.node.RootNode;

@Component(immediate = true, service = ContentInitializer.class)
public class ContentInitializer
{
    private final static Logger LOG = LoggerFactory.getLogger( ContentInitializer.class );

    private NodeService nodeService;

    public final void init()
    {
        final RootNode rootNode = ContentConstants.CONTEXT_STAGE.callWith( () -> this.doInitNodeRoot() );
        ContentConstants.CONTEXT_STAGE.runWith( () -> this.doInitContentRootNode( rootNode ) );
    }

    private void doInitContentRootNode( final RootNode rootNode )
    {
        final Node contentRootNode = nodeService.getByPath( ContentConstants.CONTENT_ROOT_PATH );

        if ( contentRootNode == null )
        {
            LOG.info( "Content root-node not found, creating" );

            PropertyTree data = new PropertyTree();
            data.setString( ContentPropertyNames.TYPE, "system:folder" );
            data.setString( ContentPropertyNames.DISPLAY_NAME, "Content" );
            data.addSet( ContentPropertyNames.DATA );
            data.addSet( ContentPropertyNames.FORM );

            final Node root = nodeService.create( CreateNodeParams.create().
                data( data ).
                name( ContentConstants.CONTENT_ROOT_NAME ).
                parent( rootNode.path() ).
                permissions( ContentConstants.CONTENT_ROOT_DEFAULT_ACL ).
                childOrder( ContentConstants.CONTENT_DEFAULT_CHILD_ORDER ).
                build() );

            LOG.info( "Created content root-node: " + root.path() );

            nodeService.push( NodeIds.from( root.id() ), ContentConstants.WORKSPACE_PROD );
        }
    }

    private RootNode doInitNodeRoot()
    {
        final RootNode rootNode = this.nodeService.createRootNode();

        nodeService.push( NodeIds.from( rootNode.id() ), ContentConstants.WORKSPACE_PROD );

        return rootNode;
    }

    @Reference
    public void setNodeService( final NodeService nodeService )
    {
        this.nodeService = nodeService;
    }
}
