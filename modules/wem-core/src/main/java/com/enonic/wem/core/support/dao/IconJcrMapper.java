package com.enonic.wem.core.support.dao;

import javax.jcr.Node;
import javax.jcr.RepositoryException;

import com.enonic.wem.api.icon.Icon;
import com.enonic.wem.api.blob.BlobKey;
import com.enonic.wem.core.jcr.JcrHelper;

public final class IconJcrMapper
{
    public static final String ICON_PROPERTY = "icon";

    public static final String ICON_MIME_TYPE_PROPERTY = "iconMimeType";

    public IconJcrMapper()
    {
    }

    public void toJcr( final Icon icon, final Node targetNode )
        throws RepositoryException
    {
        if ( icon != null )
        {
            targetNode.setProperty( ICON_PROPERTY, icon.getBlobKey().toString() );
            targetNode.setProperty( ICON_MIME_TYPE_PROPERTY, icon.getMimeType() );
        }
        else
        {
            if ( targetNode.hasProperty( ICON_PROPERTY ) )
            {
                targetNode.getProperty( ICON_PROPERTY ).remove();
            }
            if ( targetNode.hasProperty( ICON_MIME_TYPE_PROPERTY ) )
            {
                targetNode.getProperty( ICON_MIME_TYPE_PROPERTY ).remove();
            }
        }
    }

    public Icon toIcon( final Node sourceNode )
        throws RepositoryException
    {
        if ( !sourceNode.hasProperty( ICON_PROPERTY ) )
        {
            return null;
        }

        final BlobKey blobKey = new BlobKey( sourceNode.getProperty( ICON_PROPERTY ).getString() );

        final String iconMimeType =
            JcrHelper.getPropertyString( sourceNode, ICON_MIME_TYPE_PROPERTY, "image/png" ); // TODO remove default value

        return Icon.from( blobKey, iconMimeType );
    }

}
