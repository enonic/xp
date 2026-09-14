package com.enonic.xp.portal.impl;

import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeService;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public final class HmacTestHelper
{
    public static HmacService createHmacService()
    {
        return createHmacService( "8Kb/zREmBxlC9KkRcKw8fy7mA/b4tg/MElM1KYgJPWj62PRo74xCcKR88O4tt/51zXO12Ip+AR1lsIxBE5E4VA==" );
    }

    public static HmacService createHmacService( final String key )
    {
        final NodeService nodes = mock( NodeService.class );
        final NodePath path = NodePath.create().addElement( "keys" ).addElement( "generic-hmac-sha512" ).build();
        final PropertyTree data = new PropertyTree();
        data.setString( "key", key );
        when( nodes.getByPath( path ) ).thenReturn(
            Node.create().name( path.getName() ).parentPath( path.getParentPath() ).data( data ).build() );
        return new HmacService( nodes );
    }
}
