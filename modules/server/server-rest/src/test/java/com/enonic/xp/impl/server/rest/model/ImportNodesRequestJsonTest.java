package com.enonic.xp.impl.server.rest.model;

import java.util.Map;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ImportNodesRequestJsonTest
{
    @Test
    @SuppressWarnings("deprecation")
    void deprecatedXslFields_areAccepted_andIgnored()
    {
        final ImportNodesRequestJson json =
            new ImportNodesRequestJson( "export", "system-repo:master:/a", null, null, "transform.xsl", Map.of( "k", "v" ) );

        assertEquals( "export", json.getExportName() );
        assertEquals( "system-repo", json.getTargetRepoPath().getRepositoryId().toString() );
        assertTrue( json.isImportWithIds() );
        assertTrue( json.isImportWithPermissions() );
    }
}
