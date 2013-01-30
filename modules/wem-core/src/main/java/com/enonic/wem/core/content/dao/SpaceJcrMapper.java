package com.enonic.wem.core.content.dao;


import java.io.IOException;

import javax.jcr.Node;
import javax.jcr.RepositoryException;

import com.enonic.wem.api.space.Space;
import com.enonic.wem.core.jcr.JcrHelper;

import static com.enonic.wem.core.jcr.JcrHelper.getPropertyDateTime;
import static com.enonic.wem.core.jcr.JcrHelper.getPropertyString;
import static com.enonic.wem.core.jcr.JcrHelper.setPropertyDateTime;

final class SpaceJcrMapper
{
    static final String NAME = "name";

    static final String DISPLAY_NAME = "displayName";

    static final String CREATED_TIME = "createdTime";

    static final String MODIFIED_TIME = "modifiedTime";

    static final String ICON = "icon";

    void toJcr( final Space space, final Node spaceNode )
        throws RepositoryException
    {
        spaceNode.setProperty( NAME, space.getName().name() );
        spaceNode.setProperty( DISPLAY_NAME, space.getDisplayName() );
        setPropertyDateTime( spaceNode, CREATED_TIME, space.getCreatedTime() );
        setPropertyDateTime( spaceNode, MODIFIED_TIME, space.getModifiedTime() );
        final byte[] icon = space.getIcon();
        if ( icon != null && icon.length > 0 )
        {
            JcrHelper.setPropertyBinary( spaceNode, ICON, icon );
        }
        else if ( spaceNode.hasProperty( ICON ) )
        {
            spaceNode.getProperty( ICON ).remove();
        }
    }

    void toSpace( final Node spaceNode, final Space.Builder spaceBuilder )
        throws RepositoryException
    {
        spaceBuilder.name( getPropertyString( spaceNode, NAME ) );
        spaceBuilder.displayName( getPropertyString( spaceNode, DISPLAY_NAME ) );
        spaceBuilder.createdTime( getPropertyDateTime( spaceNode, CREATED_TIME ) );
        spaceBuilder.modifiedTime( getPropertyDateTime( spaceNode, MODIFIED_TIME ) );
        spaceBuilder.icon( getIcon( spaceNode ) );
    }

    private byte[] getIcon( final Node spaceNode )
        throws RepositoryException
    {
        try
        {
            return JcrHelper.getPropertyBinary( spaceNode, ICON );
        }
        catch ( IOException e )
        {
            throw new RepositoryException( e.getMessage(), e );
        }
    }
}
