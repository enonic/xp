package com.enonic.xp.lib.node;

import java.time.Instant;

import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.xp.data.PropertySet;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.data.Value;
import com.enonic.xp.index.IndexConfig;
import com.enonic.xp.index.IndexValueProcessor;
import com.enonic.xp.index.PatternIndexConfigDocument;
import com.enonic.xp.node.CreateNodeParams;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeState;
import com.enonic.xp.node.NodeVersionId;
import com.enonic.xp.repository.Repository;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.acl.AccessControlEntry;
import com.enonic.xp.security.acl.AccessControlList;
import com.enonic.xp.security.acl.Permission;

public class CreateNodeHandlerTest
    extends BaseNodeHandlerTest
{
    private void mockCreateNode()
    {
        final PropertyTree data = new PropertyTree();
        data.setString( "displayName", "This is brand new node" );
        final PropertySet someData = data.addSet( "someData" );
        someData.setString( "cars", "skoda" );
        someData.addString( "cars", "tesla model x" );
        someData.setString( "likes", "plywood" );
        someData.setLong( "numberOfUselessGadgets", 123L );

        final PatternIndexConfigDocument indexConfig = PatternIndexConfigDocument.create().
            defaultConfig( IndexConfig.MINIMAL ).
            add( "displayName", IndexConfig.FULLTEXT ).
            build();

        final Node node = Node.create().
            id( NodeId.from( "myId" ) ).
            parentPath( NodePath.ROOT ).
            name( "myName" ).
            data( data ).
            indexConfigDocument( indexConfig ).
            permissions( AccessControlList.create().
                add( AccessControlEntry.create().
                    principal( PrincipalKey.ofAnonymous() ).
                    allow( Permission.READ ).
                    build() ).
                add( AccessControlEntry.create().
                    principal( PrincipalKey.ofRole( "admin" ) ).
                    allowAll().
                    build() ).
                build() ).
            nodeState( NodeState.DEFAULT ).
            nodeVersionId( NodeVersionId.from( "versionKey" ) ).
            timestamp( Instant.parse( "2010-10-10T10:10:10.10Z" ) ).
            build();

        Mockito.when( this.nodeService.create( Mockito.isA( CreateNodeParams.class ) ) ).
            thenReturn( node );
    }

    private IndexValueProcessor createIndexValueProcessor()
    {
        return new IndexValueProcessor()
        {
            @Override
            public Value process( final Value value )
            {
                return null;
            }

            @Override
            public String getName()
            {
                return "myProcessor";
            }
        };
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

}