package com.enonic.xp.impl.server.rest.model;

import java.util.Map;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ImportNodesRequestJsonTest
{
    @Test
    void emptyXslFields_areAccepted()
    {
        final ImportNodesRequestJson json = new ImportNodesRequestJson( "export", "system-repo:master:/a", null, null, "", null );

        assertEquals( "export", json.getExportName() );
        assertTrue( json.isImportWithIds() );
        assertTrue( json.isImportWithPermissions() );
    }

    @Test
    void xslSource_isRejected()
    {
        assertThrows( IllegalArgumentException.class,
                      () -> new ImportNodesRequestJson( "export", "system-repo:master:/a", true, true, "transform.xsl", null ) );
    }

    @Test
    void xslParams_areRejected()
    {
        assertThrows( IllegalArgumentException.class,
                      () -> new ImportNodesRequestJson( "export", "system-repo:master:/a", true, true, null, Map.of( "k", "v" ) ) );
    }
}
