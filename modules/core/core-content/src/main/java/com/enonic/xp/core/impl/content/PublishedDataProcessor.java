package com.enonic.xp.core.impl.content;

import com.enonic.xp.content.ContentPropertyNames;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.node.NodePath;

public class PublishedDataProcessor
{
    public static PropertyTree removePublished( final PropertyTree originalData, final NodePath nodePath )
    {
        if ( originalData.hasProperty( ContentPropertyNames.PUBLISH_INFO ) )
        {
            final PropertyTree data = originalData.copy();
            data.getSet( ContentPropertyNames.PUBLISH_INFO ).removeProperties( ContentPropertyNames.PUBLISH_PUBLISHED );
            return data;
        }
        else
        {
            return originalData;
        }
    }
}
