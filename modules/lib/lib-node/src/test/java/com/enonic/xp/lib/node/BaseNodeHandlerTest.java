package com.enonic.xp.lib.node;

import java.time.Instant;

import org.mockito.Mockito;

import com.google.common.io.ByteSource;

import com.enonic.xp.data.PropertySet;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.index.IndexConfig;
import com.enonic.xp.index.PatternIndexConfigDocument;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeName;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeService;
import com.enonic.xp.node.NodeVersionId;
import com.enonic.xp.repository.RepositoryService;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.SecurityService;
import com.enonic.xp.security.acl.AccessControlEntry;
import com.enonic.xp.security.acl.AccessControlList;
import com.enonic.xp.testing.ScriptTestSupport;

public class BaseNodeHandlerTest
    extends ScriptTestSupport
{
    protected NodeService nodeService;

    protected RepositoryService repositoryService;

    protected SecurityService securityService;

    @Override
    public void initialize()
        throws Exception
    {
        super.initialize();

        this.repositoryService = Mockito.mock( RepositoryService.class );
        this.nodeService = Mockito.mock( NodeService.class );
        this.securityService = Mockito.mock( SecurityService.class );

        addService( NodeService.class, this.nodeService );
        addService( RepositoryService.class, this.repositoryService );
        addService( SecurityService.class, this.securityService );
    }

    protected Node createNode()
    {
        return createNode( NodePath.ROOT, NodeName.from( "my-name" ) );
    }

    protected Node createNode( final NodePath parentPath, final NodeName name )
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

        return Node.create().
            id( NodeId.from( "nodeId" ) ).
            parentPath( parentPath ).
            name( name ).
            data( data ).
            indexConfigDocument( indexConfig ).
            permissions( AccessControlList.create().
                add( AccessControlEntry.create().
                    principal( PrincipalKey.ofRole( "admin" ) ).
                    allowAll().
                    build() ).
                build() ).
            nodeVersionId( NodeVersionId.from( "versionKey" ) ).
            timestamp( Instant.parse( "2010-10-10T10:10:10.10Z" ) ).
            build();
    }

    @SuppressWarnings("unused")
    public static ByteSource createByteSource( final String value )
    {
        return ByteSource.wrap( value.getBytes() );
    }
}
