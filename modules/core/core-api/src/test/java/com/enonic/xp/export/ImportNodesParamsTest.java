package com.enonic.xp.export;

import java.util.HashMap;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.enonic.xp.node.NodePath;
import com.enonic.xp.vfs.VirtualFile;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ImportNodesParamsTest
{
    @Test
    public void builder()
    {
        ImportNodesParams.Builder builder = ImportNodesParams.create();

        builder.
            includeNodeIds( true ).
            includePermissions( true ).
            targetNodePath( NodePath.ROOT ).
            source( Mockito.mock( VirtualFile.class ) ).
            xslt( Mockito.mock( VirtualFile.class ) ).
            xsltParam( "name", "value" ).
            xsltParams( new HashMap<>() );

        ImportNodesParams result = builder.build();

        assertTrue( result.isImportNodeids() );
        assertTrue( result.isImportPermissions() );
        assertTrue( result.getTargetNodePath().isRoot() );
        assertNotNull( result.getSource() );
        assertNotNull( result.getXslt() );
        assertNotNull( result.getXsltParams() );
    }
}
