package com.enonic.wem.core.content.dao;


import javax.jcr.Node;
import javax.jcr.RepositoryException;

import com.enonic.wem.api.space.Space;

import static com.enonic.wem.core.jcr.JcrHelper.getPropertyDateTime;
import static com.enonic.wem.core.jcr.JcrHelper.getPropertyString;
import static com.enonic.wem.core.jcr.JcrHelper.setPropertyDateTime;

final class SpaceJcrMapper
{
    static final String NAME = "name";

    static final String DISPLAY_NAME = "displayName";

    static final String CREATED_TIME = "createdTime";

    static final String MODIFIED_TIME = "modifiedTime";

    void toJcr( final Space space, final Node spaceNode )
        throws RepositoryException
    {
        spaceNode.setProperty( NAME, space.getName().name() );
        spaceNode.setProperty( DISPLAY_NAME, space.getDisplayName() );
        setPropertyDateTime( spaceNode, CREATED_TIME, space.getCreatedTime() );
        setPropertyDateTime( spaceNode, MODIFIED_TIME, space.getModifiedTime() );
    }

    void toSpace( final Node spaceNode, final Space.Builder spaceBuilder )
        throws RepositoryException
    {
        spaceBuilder.name( getPropertyString( spaceNode, NAME ) );
        spaceBuilder.displayName( getPropertyString( spaceNode, DISPLAY_NAME ) );
        spaceBuilder.createdTime( getPropertyDateTime( spaceNode, CREATED_TIME ) );
        spaceBuilder.modifiedTime( getPropertyDateTime( spaceNode, MODIFIED_TIME ) );
    }
}
