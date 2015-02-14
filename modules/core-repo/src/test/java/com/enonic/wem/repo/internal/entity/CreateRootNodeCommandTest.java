package com.enonic.wem.repo.internal.entity;

import org.junit.Test;

import com.enonic.xp.core.index.ChildOrder;
import com.enonic.xp.core.node.CreateRootNodeParams;
import com.enonic.xp.core.node.RootNode;
import com.enonic.xp.core.security.RoleKeys;
import com.enonic.xp.core.security.acl.AccessControlEntry;
import com.enonic.xp.core.security.acl.AccessControlList;

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
            queryService( this.queryService ).
            nodeDao( this.nodeDao ).
            versionService( this.versionService ).
            branchService( this.branchService ).
            build().
            execute();

        assertNotNull( rootNode );
    }
}