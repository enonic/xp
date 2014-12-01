package com.enonic.wem.core.content;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.wem.api.content.ContentConstants;
import com.enonic.wem.api.node.CreateNodeParams;
import com.enonic.wem.api.node.Node;
import com.enonic.wem.api.node.NodeNotFoundException;
import com.enonic.wem.api.node.NodeService;

public class ContentInitializer
{
    private final static Logger LOG = LoggerFactory.getLogger( ContentInitializer.class );

    private NodeService nodeService;

    public final void init()
    {
        try
        {
            nodeService.getByPath( ContentConstants.CONTENT_ROOT_PATH );
        }
        catch ( NodeNotFoundException e )
        {
            LOG.info( "Content root-node not found, creating" );

            final Node root = nodeService.create( CreateNodeParams.create().
                name( ContentConstants.CONTENT_ROOT_NAME ).
                parent( ContentConstants.CONTENT_ROOT_PARENT ).
                accessControlList( ContentConstants.CONTENT_ROOT_DEFAULT_ACL ).
                childOrder( ContentConstants.CONTENT_DEFAULT_CHILD_ORDER ).
                build() );

            LOG.info( "Created content root-node: " + root.path() );
        }

    }

    public void setNodeService( final NodeService nodeService )
    {
        this.nodeService = nodeService;
    }
}
