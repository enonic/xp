package com.enonic.wem.web.rest.rpc.space;


import org.codehaus.jackson.node.ObjectNode;

import com.enonic.wem.api.space.Space;
import com.enonic.wem.web.rest.resource.space.SpaceImageUriResolver;

final class SpaceJsonRpcSerializer
{

    void serialize( final Space space, ObjectNode spaceNode )
    {
        spaceNode.put( "name", space.getName().name() );
        spaceNode.put( "displayName", space.getDisplayName() );
        spaceNode.put( "createdTime", space.getCreatedTime().toString() );
        spaceNode.put( "modifiedTime", space.getModifiedTime().toString() );
        spaceNode.put( "rootContentId", space.getRootContent().toString() );
        spaceNode.put( "iconUrl", SpaceImageUriResolver.resolve( space.getName() ) );
    }
}
