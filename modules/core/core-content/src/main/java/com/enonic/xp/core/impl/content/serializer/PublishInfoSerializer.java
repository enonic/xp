package com.enonic.xp.core.impl.content.serializer;

import com.enonic.xp.content.ContentPropertyNames;
import com.enonic.xp.content.ContentPublishInfo;
import com.enonic.xp.data.PropertySet;

public class PublishInfoSerializer
{
    private PublishInfoSerializer()
    {
    }

    public static ContentPublishInfo serialize( final PropertySet publishInfo )
    {
        if ( publishInfo == null )
        {
            return null;
        }

        return ContentPublishInfo.create().
            first( publishInfo.getInstant( ContentPropertyNames.PUBLISH_FIRST ) ).
            from( publishInfo.getInstant( ContentPropertyNames.PUBLISH_FROM ) ).
            to( publishInfo.getInstant( ContentPropertyNames.PUBLISH_TO ) ).
            build();
    }

}
