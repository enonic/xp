package com.enonic.wem.core.jcr;

import javax.jcr.Node;
import javax.jcr.RepositoryException;

import org.apache.jackrabbit.commons.JcrUtils;

public abstract class JcrHelper
{
    public static Node getOrAddNode( final Node parent, final String relPath )
        throws RepositoryException
    {
        return JcrUtils.getOrAddNode( parent, relPath );
    }

    public static Node getOrAddNode( final Node parent, final String relPath, final String type )
        throws RepositoryException
    {
        return JcrUtils.getOrAddNode( parent, relPath, type );
    }

    public static Node getNodeOrNull( final Node parent, final String relPath )
        throws RepositoryException
    {
        if ( parent.hasNode( relPath ) )
        {
            return parent.getNode( relPath );
        }
        else
        {
            return null;
        }
    }
}
