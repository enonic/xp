package com.enonic.xp.repo.impl.node;

import org.junit.Test;

import com.enonic.xp.index.ChildOrder;
import com.enonic.xp.node.CreateRootNodeParams;
import com.enonic.xp.node.Node;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.acl.AccessControlEntry;
import com.enonic.xp.security.acl.AccessControlList;

import static org.junit.Assert.*;

public class CreateRootNodeCommandTest
    extends AbstractNodeTest
{

    @Test
    public void create()
        throws Exception
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
    }
}