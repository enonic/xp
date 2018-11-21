package com.enonic.xp.lib.node;

import java.time.Instant;

import org.mockito.Mockito;

import com.enonic.xp.blob.BlobKey;
import com.enonic.xp.data.PropertySet;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.index.IndexConfig;
import com.enonic.xp.index.PatternIndexConfigDocument;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeBranchEntry;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeService;
import com.enonic.xp.node.NodeState;
import com.enonic.xp.node.NodeVersionId;
import com.enonic.xp.repository.RepositoryService;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.acl.AccessControlEntry;
import com.enonic.xp.security.acl.AccessControlList;
import com.enonic.xp.security.acl.Permission;
import com.enonic.xp.testing.ScriptTestSupport;

public class OldBaseNodeHandlerTest
    extends ScriptTestSupport
{
    protected NodeService nodeService;

    protected RepositoryService repositoryService;

    @Override
    public void initialize()
        throws Exception
    {
        super.initialize();

        this.nodeService = Mockito.mock( NodeService.class );
        this.repositoryService = Mockito.mock( RepositoryService.class );
        addService( NodeService.class, this.nodeService );
        addService( RepositoryService.class, this.repositoryService );
    }

    NodeBranchEntry createEntry( final String id )
    {
        return NodeBranchEntry.create().
            nodeId( NodeId.from( id ) ).
            nodeState( NodeState.DEFAULT ).
            nodePath( NodePath.create( NodePath.ROOT + id ).
                build() ).
            build();
    }

    protected Node createNode()
    {
        return createNode( NodePath.ROOT, "my-name" );
    }

    protected Node createNode( final NodePath parentPath, final String name )
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
            blobKey( BlobKey.from( "blobKey" ) ).
            timestamp( Instant.parse( "2010-10-10T10:10:10.10Z" ) ).
            build();
    }
}
