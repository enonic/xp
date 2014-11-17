package com.enonic.wem.repo.internal.elasticsearch.workspace;

import org.junit.Test;

import com.enonic.wem.repo.internal.workspace.WorkspaceDocumentId;

import static org.junit.Assert.*;

public class StoreWorkspaceDocumentIdTest
{
    @Test
    public void from()
        throws Exception
    {
        final WorkspaceDocumentId id = WorkspaceDocumentId.from( "myBlobKey_myWorkspace" );
        assertEquals( "myBlobKey_myWorkspace", id.getValue() );
        assertEquals( "myBlobKey", id.getNodeId().toString() );
        assertEquals( "myWorkspace", id.getWorkspace().getName() );
    }

    @Test(expected = IllegalArgumentException.class)
    public void from_missing_ws()
        throws Exception
    {
        WorkspaceDocumentId.from( "myBlobKey_" );
    }

    @Test(expected = IllegalArgumentException.class)
    public void from_missing_nodeId()
        throws Exception
    {
        WorkspaceDocumentId.from( "_myWorkspace" );
    }


    @Test(expected = IllegalArgumentException.class)
    public void from_missing_separator()
        throws Exception
    {
        WorkspaceDocumentId.from( "myBlobKeymyWorkspace" );
    }
}
