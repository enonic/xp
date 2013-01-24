package com.enonic.wem.core.content.dao;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import com.enonic.wem.api.content.space.SpaceName;
import com.enonic.wem.core.jcr.JcrConstants;

import static com.enonic.wem.core.jcr.JcrHelper.getNodeOrNull;

abstract class AbstractSpaceDaoHandler
{
    static final String SPACES_NODE = "spaces";

    static final String SPACES_PATH = JcrConstants.ROOT_NODE + "/" + SPACES_NODE + "/";

    static final String SPACE_CONTENT_ROOT_NODE = "root";

    protected final Session session;

    protected final SpaceJcrMapper spaceJcrMapper = new SpaceJcrMapper();

    AbstractSpaceDaoHandler( final Session session )
    {
        this.session = session;
    }


    protected Node getSpaceNode( final SpaceName spaceName )
        throws RepositoryException
    {
        final String path = SPACES_PATH + spaceName.name();
        final Node rootNode = this.session.getRootNode();
        return getNodeOrNull( rootNode, path );
    }
}
