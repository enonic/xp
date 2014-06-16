package com.enonic.wem.core.elasticsearch;

import org.junit.Test;

import static org.junit.Assert.*;

public class WorkspaceDocumentIdTest
{
    @Test
    public void from()
        throws Exception
    {
        final WorkspaceDocumentId id = WorkspaceDocumentId.from( "myBlobKey_myWorkspace" );
        assertEquals( "myBlobKey_myWorkspace", id.getValue() );
        assertEquals( "myBlobKey", id.getEntityId().toString() );
        assertEquals( "myWorkspace", id.getWorkspace().getName() );
    }

    @Test(expected = IllegalArgumentException.class)
    public void from_missing_ws()
        throws Exception
    {
        final WorkspaceDocumentId id = WorkspaceDocumentId.from( "myBlobKey_" );
    }

    @Test(expected = IllegalArgumentException.class)
    public void from_missing_entityId()
        throws Exception
    {
        final WorkspaceDocumentId id = WorkspaceDocumentId.from( "_myWorkspace" );
    }


    @Test(expected = IllegalArgumentException.class)
    public void from_missing_separator()
        throws Exception
    {
        final WorkspaceDocumentId id = WorkspaceDocumentId.from( "myBlobKeymyWorkspace" );
    }
}
