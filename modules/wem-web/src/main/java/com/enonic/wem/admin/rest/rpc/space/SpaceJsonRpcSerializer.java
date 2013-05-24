package com.enonic.wem.admin.rest.rpc.space;


import org.codehaus.jackson.node.ObjectNode;

import com.enonic.wem.admin.rest.resource.space.SpaceImageUriResolver;
import com.enonic.wem.api.space.Space;
import com.enonic.wem.core.support.serializer.JsonSerializerUtil;

final class SpaceJsonRpcSerializer
{

    void serialize( final Space space, ObjectNode spaceNode )
    {
        spaceNode.put( "name", space.getName().name() );
        spaceNode.put( "displayName", space.getDisplayName() );
        JsonSerializerUtil.setDateTimeValue( "createdTime", space.getCreatedTime(), spaceNode );
        JsonSerializerUtil.setDateTimeValue( "modifiedTime", space.getModifiedTime(), spaceNode );
        spaceNode.put( "rootContentId", space.getRootContent().toString() );
        spaceNode.put( "iconUrl", SpaceImageUriResolver.resolve( space.getName() ) );
    }
}
