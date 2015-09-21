package com.enonic.wem.repo.internal.entity;

import org.junit.Test;

import com.enonic.xp.index.ChildOrder;
import com.enonic.xp.node.CreateRootNodeParams;
import com.enonic.xp.node.RootNode;
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
        final RootNode rootNode = CreateRootNodeCommand.create().
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