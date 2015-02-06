package com.enonic.wem.repo.internal.entity;

import org.junit.Test;

import com.enonic.wem.api.index.ChildOrder;
import com.enonic.wem.api.node.CreateRootNodeParams;
import com.enonic.wem.api.node.RootNode;
import com.enonic.wem.api.security.acl.AccessControlList;

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
                permissions( AccessControlList.empty() ).
                childOrder( ChildOrder.defaultOrder() ).
                build() ).
            indexService( this.indexService ).
            queryService( this.queryService ).
            nodeDao( this.nodeDao ).
            versionService( this.versionService ).
            branchService( this.branchService ).
            build().
            execute();

        assertNotNull( rootNode );
    }
}