package com.enonic.xp.lib.node;

import java.time.Instant;

import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.data.Value;
import com.enonic.xp.index.IndexConfig;
import com.enonic.xp.index.IndexValueProcessor;
import com.enonic.xp.index.PatternIndexConfigDocument;
import com.enonic.xp.node.AttachedBinaries;
import com.enonic.xp.node.AttachedBinary;
import com.enonic.xp.node.CreateNodeParams;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeState;
import com.enonic.xp.node.NodeType;
import com.enonic.xp.node.NodeVersionId;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.acl.AccessControlEntry;
import com.enonic.xp.security.acl.AccessControlList;
import com.enonic.xp.security.acl.Permission;
import com.enonic.xp.util.BinaryReference;

public class CreateNodeHandlerTest
    extends BaseNodeHandlerTest
{
    private void mockCreateNode()
    {
        final PropertyTree data = new PropertyTree();
        data.setString( "myHtmlField", "<h1>Html here</h1>" );
        data.setString( "displayName", "House1" );
        data.setString( "type", "com.enonic.app.features:house" );
        data.setString( "owner", "user:system:su" );
        data.setInstant( "modifiedTime", Instant.parse( "2015-10-05T12:11:01.272Z" ) );

        final PatternIndexConfigDocument indexConfig = PatternIndexConfigDocument.create().
            analyzer( "myAnalyzer" ).
            defaultConfig( IndexConfig.BY_TYPE ).
            add( "myHtmlField", IndexConfig.create().
                includeInAllText( true ).
                addIndexValueProcessor( createIndexValueProcessor() ).
                build() ).
            add( "type", IndexConfig.NONE ).
            add( "displayName", IndexConfig.FULLTEXT ).
            build();

        final Node node = Node.create().
            id( NodeId.from( "123456" ) ).
            parentPath( NodePath.ROOT ).
            name( "myNode" ).
            data( data ).
            indexConfigDocument( indexConfig ).
            permissions( AccessControlList.create().
                add( AccessControlEntry.create().
                    principal( PrincipalKey.ofAnonymous() ).
                    allow( Permission.READ ).
                    build() ).
                add( AccessControlEntry.create().
                    principal( PrincipalKey.ofRole( "authenticated" ) ).
                    allowAll().
                    build() ).
                add( AccessControlEntry.create().
                    principal( PrincipalKey.ofRole( "admin" ) ).
                    allowAll().
                    build() ).
                add( AccessControlEntry.create().
                    principal( PrincipalKey.ofRole( "everyone" ) ).
                    allow( Permission.READ ).
                    build() ).
                build() ).
            attachedBinaries( AttachedBinaries.create().
                add( new AttachedBinary( BinaryReference.from( "myRef" ), "abc" ) ).
                add( new AttachedBinary( BinaryReference.from( "myRef2" ), "def" ) ).
                build() ).
            nodeState( NodeState.DEFAULT ).
            nodeType( NodeType.from( "myNodeType" ) ).
            nodeVersionId( NodeVersionId.from( "versionId" ) ).
            manualOrderValue( 0L ).
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
        runScript( "/site/lib/xp/examples/node/create.js" );
    }

}