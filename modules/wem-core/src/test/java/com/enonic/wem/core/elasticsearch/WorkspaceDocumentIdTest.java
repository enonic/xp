package com.enonic.wem.core.elasticsearch;

import org.junit.Test;

import static org.junit.Assert.*;

public class WorkspaceDocumentIdTest
{
    @Test
    public void from()
        throws Exception
    {
        final WorkspaceDocumentId id = WorkspaceDocumentId.from( "myBlobKey-myWorkspace" );
        assertEquals( "myBlobKey-myWorkspace", id.getValue() );
        assertEquals( "myBlobKey", id.getEntityIdAsString() );
        assertEquals( "myWorkspace", id.getWorkspaceName() );
    }

    @Test(expected = IllegalArgumentException.class)
    public void from_missing_ws()
        throws Exception
    {
        final WorkspaceDocumentId id = WorkspaceDocumentId.from( "myBlobKey-" );
    }

    @Test(expected = IllegalArgumentException.class)
    public void from_missing_entityId()
        throws Exception
    {
        final WorkspaceDocumentId id = WorkspaceDocumentId.from( "-myWorkspace" );
    }


    @Test(expected = IllegalArgumentException.class)
    public void from_missing_separator()
        throws Exception
    {
        final WorkspaceDocumentId id = WorkspaceDocumentId.from( "myBlobKeymyWorkspace" );
    }
}
