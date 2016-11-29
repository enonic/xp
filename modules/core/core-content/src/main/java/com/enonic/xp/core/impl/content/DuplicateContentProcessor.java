package com.enonic.xp.core.impl.content;

import java.time.Instant;

import com.enonic.xp.content.ContentPropertyNames;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.data.PropertySet;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.node.CreateNodeParams;
import com.enonic.xp.node.DuplicateNodeProcessor;
import com.enonic.xp.security.User;

public class DuplicateContentProcessor
    implements DuplicateNodeProcessor
{
    @Override
    public CreateNodeParams process( final CreateNodeParams originalParams )
    {
        CreateNodeParams.Builder builder = CreateNodeParams.create( originalParams );

        final User user = ContextAccessor.current().getAuthInfo().getUser();

        final PropertyTree originalData = originalParams.getData();

        originalData.setInstant( ContentPropertyNames.CREATED_TIME, Instant.now() );
        originalData.setInstant( ContentPropertyNames.MODIFIED_TIME, Instant.now() );
        originalData.setString( ContentPropertyNames.OWNER, user.getKey().toString() );
        originalData.setString( ContentPropertyNames.CREATOR, user.getKey().toString() );
        originalData.setString( ContentPropertyNames.MODIFIER, user.getKey().toString() );
        final PropertySet publishInfo = originalData.getSet( ContentPropertyNames.PUBLISH_INFO );
        if ( publishInfo != null )
        {
            publishInfo.removeProperties( ContentPropertyNames.PUBLISH_FROM );
            publishInfo.removeProperties( ContentPropertyNames.PUBLISH_TO );
        }

        return builder.build();
    }
}
