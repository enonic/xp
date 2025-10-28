package com.enonic.xp.core.node;

import org.junit.jupiter.api.Test;

import com.enonic.xp.core.AbstractNodeTest;
import com.enonic.xp.index.ChildOrder;
import com.enonic.xp.node.CreateRootNodeParams;
import com.enonic.xp.node.Node;
import com.enonic.xp.repo.impl.node.CreateRootNodeCommand;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.acl.AccessControlEntry;
import com.enonic.xp.security.acl.AccessControlList;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class CreateRootNodeCommandTest
    extends AbstractNodeTest
{

    @Test
    void create()
    {
        final Node rootNode = CreateRootNodeCommand.create().
            params( CreateRootNodeParams.create().
                permissions( AccessControlList.create().add(
                    AccessControlEntry.create().principal( RoleKeys.AUTHENTICATED ).allowAll().build() ).build() ).
                childOrder( ChildOrder.defaultOrder() ).
                build() ).
            indexServiceInternal( this.indexServiceInternal ).
            storageService( this.storageService ).
            searchService( this.searchService ).
            build().
            execute();

        assertNotNull( rootNode );
        assertNotNull( rootNode.getNodeVersionId() );
        assertNotNull( rootNode.getChildOrder() );
        assertNotNull( rootNode.path() );
    }
}
