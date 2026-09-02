package com.enonic.xp.impl.server.rest.model;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.enonic.xp.core.internal.json.ObjectMapperHelper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ImportNodesRequestJsonTest
{
    private static final ObjectMapper MAPPER = ObjectMapperHelper.create();

    @Test
    void deprecatedXslFields_areAccepted_andIgnored()
        throws Exception
    {
        final ImportNodesRequestJson json = MAPPER.readValue( """
                                                                  {
                                                                    "exportName": "export",
                                                                    "targetRepoPath": "system-repo:master:/a",
                                                                    "importWithIds": false,
                                                                    "xslSource": "transform.xsl",
                                                                    "xslParams": {"applicationId": "com.acme.app"}
                                                                  }
                                                                  """, ImportNodesRequestJson.class );

        assertEquals( "export", json.getExportName() );
        assertEquals( "system-repo", json.getTargetRepoPath().getRepositoryId().toString() );
        assertFalse( json.isImportWithIds() );
        assertTrue( json.isImportWithPermissions() );
    }
}
