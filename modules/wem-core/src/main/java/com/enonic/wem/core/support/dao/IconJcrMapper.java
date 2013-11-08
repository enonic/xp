package com.enonic.wem.core.support.dao;

import javax.jcr.Node;
import javax.jcr.RepositoryException;

import com.enonic.wem.api.Icon;
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
            JcrHelper.setPropertyBinary( targetNode, ICON_PROPERTY, icon.asInputStream() );
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
        final byte[] iconData = JcrHelper.getPropertyBinary( sourceNode, ICON_PROPERTY );

        if ( iconData != null )
        {
            final String iconMimeType =
                JcrHelper.getPropertyString( sourceNode, ICON_MIME_TYPE_PROPERTY, "image/png" ); // TODO remove default value
            final Icon icon = iconData != null && iconData.length > 0 ? Icon.from( iconData, iconMimeType ) : null;
            return icon;
        }
        return null;
    }

}
