package com.enonic.wem.core.content.dao;


import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.space.Space;
import com.enonic.wem.api.space.SpaceName;
import com.enonic.wem.api.content.type.QualifiedContentTypeName;
import com.enonic.wem.api.exception.SpaceAlreadyExistException;
import com.enonic.wem.core.jcr.JcrConstants;

import static com.enonic.wem.api.content.Content.newContent;
import static com.enonic.wem.api.space.Space.newSpace;
import static com.enonic.wem.core.content.dao.ContentDao.CONTENT_NEXT_VERSION_PROPERTY;
import static com.enonic.wem.core.content.dao.ContentDao.CONTENT_VERSION_HISTORY_NODE;
import static org.apache.jackrabbit.JcrConstants.NT_UNSTRUCTURED;


final class CreateSpaceDaoHandler
    extends AbstractSpaceDaoHandler
{
    private final ContentJcrMapper contentJcrMapper = new ContentJcrMapper();

    CreateSpaceDaoHandler( final Session session )
    {
        super( session );
    }

    Space handle( final Space space )
        throws RepositoryException
    {
        final Node root = session.getRootNode();
        final Node spacesNode = root.getNode( SPACES_PATH );
        final SpaceName name = space.getName();

        if ( spacesNode.hasNode( name.name() ) )
        {
            throw new SpaceAlreadyExistException( name );
        }

        final Node spaceNode = spacesNode.addNode( name.name(), JcrConstants.SPACE_TYPE );
        spaceJcrMapper.toJcr( space, spaceNode );

        final Node spaceRootContentNode = spaceNode.addNode( SPACE_CONTENT_ROOT_NODE, JcrConstants.CONTENT_TYPE );
        final Content rootContent = createRootContent( space );
        contentJcrMapper.toJcr( rootContent, spaceRootContentNode );
        createContentVersionHistory( rootContent, spaceRootContentNode );

        final ContentId rootContentId = ContentIdFactory.from( spaceRootContentNode );
        return newSpace( space ).
            rootContent( rootContentId ).
            build();
    }

    private Content createRootContent( final Space space )
    {
        return newContent().
            createdTime( space.getCreatedTime() ).
            modifiedTime( space.getModifiedTime() ).
            displayName( space.getDisplayName() ).
            type( QualifiedContentTypeName.unstructured() ).
            build();
    }

    private Node createContentVersionHistory( final Content content, final Node contentNode )
        throws RepositoryException
    {
        final Node contentVersionNode = contentNode.addNode( CONTENT_VERSION_HISTORY_NODE, NT_UNSTRUCTURED );
        contentVersionNode.setProperty( CONTENT_NEXT_VERSION_PROPERTY, content.getVersionId().id() + 1 );
        return contentVersionNode;
    }
}
