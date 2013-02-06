package com.enonic.wem.core.space.dao;

import java.util.List;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import com.google.common.collect.Lists;

import com.enonic.wem.api.space.Space;
import com.enonic.wem.api.space.SpaceName;
import com.enonic.wem.api.space.Spaces;
import com.enonic.wem.core.content.dao.ContentIdFactory;
import com.enonic.wem.core.jcr.JcrHelper;

import static com.enonic.wem.api.space.Space.newSpace;

final class SpaceDaoHandlerGet
    extends AbstractSpaceDaoHandler
{
    SpaceDaoHandlerGet( final Session session )
    {
        super( session );
    }

    Space getSpace( final SpaceName spaceName )
        throws RepositoryException
    {
        final Node spaceNode = getSpaceNode( spaceName );
        return buildSpace( spaceNode );
    }

    Spaces getAllSpaces()
        throws RepositoryException
    {
        final Node rootNode = session.getRootNode();
        final Node spacesNode = JcrHelper.getNodeOrNull( rootNode, SPACES_PATH );
        final List<Space> spaceList = Lists.newArrayList();
        final NodeIterator spaceNodes = spacesNode.getNodes();
        while ( spaceNodes.hasNext() )
        {
            final Node spaceNode = spaceNodes.nextNode();
            final Space space = buildSpace( spaceNode );
            if ( space != null )
            {
                spaceList.add( space );
            }
        }
        return Spaces.from( spaceList );
    }

    private Space buildSpace( final Node spaceNode )
        throws RepositoryException
    {
        if ( spaceNode == null )
        {
            return null;
        }

        final Space.Builder spaceBuilder = newSpace();
        spaceJcrMapper.toSpace( spaceNode, spaceBuilder );
        spaceBuilder.rootContent( ContentIdFactory.from( spaceNode.getNode( SPACE_CONTENT_ROOT_NODE ).getIdentifier() ) );
        return spaceBuilder.build();
    }

}
