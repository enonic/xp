package com.enonic.xp.lib.node;

import org.junit.Test;
import org.mockito.Mockito;

import com.google.common.io.ByteSource;

import com.enonic.xp.node.CreateNodeParams;
import com.enonic.xp.node.Node;
import com.enonic.xp.repository.Repository;
import com.enonic.xp.repository.RepositoryId;

public class CreateNodeHandlerTest
    extends BaseNodeHandlerTest
{
    private void mockCreateNode()
    {
        final Node node = createNode();
        Mockito.when( this.nodeService.create( Mockito.isA( CreateNodeParams.class ) ) ).
            thenReturn( node );
    }

    @Test
    public void testExample()
    {
        mockCreateNode();

        Mockito.when( this.repositoryService.get( RepositoryId.from( "cms-repo" ) ) ).
            thenReturn( Repository.create().
                id( RepositoryId.from( "cms-repo" ) ).
                build() );

        runScript( "/site/lib/xp/examples/node/create.js" );
    }

    @SuppressWarnings("unused")
    public static ByteSource createByteSource( final String value )
    {
        return ByteSource.wrap( value.getBytes() );
    }
}