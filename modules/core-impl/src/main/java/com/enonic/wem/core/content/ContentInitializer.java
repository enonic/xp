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

@Component(immediate = true, service = ContentInitializer.class)
public class ContentInitializer
{
    private final static Logger LOG = LoggerFactory.getLogger( ContentInitializer.class );

    private NodeService nodeService;

    public final void init()
    {
        ContentConstants.CONTEXT_STAGE.runWith( this::doInitRootNode );
    }

    private void doInitRootNode()
    {
        final Node rootNode = nodeService.getByPath( ContentConstants.CONTENT_ROOT_PATH );

        if ( rootNode == null )
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
                parent( ContentConstants.CONTENT_ROOT_PARENT ).
                permissions( ContentConstants.CONTENT_ROOT_DEFAULT_ACL ).
                childOrder( ContentConstants.CONTENT_DEFAULT_CHILD_ORDER ).
                build() );

            LOG.info( "Created content root-node: " + root.path() );

            nodeService.push( NodeIds.from( root.id() ), ContentConstants.WORKSPACE_PROD );
        }
    }

    @Reference
    public void setNodeService( final NodeService nodeService )
    {
        this.nodeService = nodeService;
    }
}
