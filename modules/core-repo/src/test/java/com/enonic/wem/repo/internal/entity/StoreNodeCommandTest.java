package com.enonic.wem.repo.internal.entity;

import org.junit.Test;

import com.enonic.wem.api.index.ChildOrder;
import com.enonic.wem.api.node.CreateNodeParams;
import com.enonic.wem.api.node.Node;
import com.enonic.wem.api.node.NodePath;
import com.enonic.wem.api.security.PrincipalKey;
import com.enonic.wem.api.security.acl.AccessControlEntry;
import com.enonic.wem.api.security.acl.AccessControlList;
import com.enonic.wem.api.security.acl.Permission;

public class StoreNodeCommandTest
    extends AbstractNodeTest
{

    @Test
    public void with_acl()
        throws Exception
    {

        Node newNode = createNode( CreateNodeParams.create().
            name( "my-node" ).
            parent( NodePath.ROOT ).
            build() );

        Node updatedNode = Node.newNode( newNode ).
            permissions( AccessControlList.create().
                add( AccessControlEntry.create().
                    allow( Permission.READ ).
                    allow( Permission.CREATE ).
                    allow( Permission.DELETE ).
                    principal( PrincipalKey.from( "user:myuserstore:rmy" ) ).
                    build() ).
                build() ).
            childOrder( ChildOrder.from( "name DESC" ) ).
            build();

        refresh();

        StoreNodeCommand.create().
            node( updatedNode ).
            branchService( this.branchService ).
            indexServiceInternal( this.indexServiceInternal ).
            nodeDao( this.nodeDao ).
            versionService( this.versionService ).
            queryService( this.queryService ).
            build().
            execute();
        refresh();


    }
}
