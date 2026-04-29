package com.enonic.xp.lib.export;

import org.junit.jupiter.api.Test;

import com.enonic.xp.export.ExportInfo;
import com.enonic.xp.export.ExportService;
import com.enonic.xp.export.ListExportsResult;
import com.enonic.xp.testing.ScriptTestSupport;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ListExportsHandlerTest
    extends ScriptTestSupport
{
    ExportService exportService;

    public void initialize()
        throws Exception
    {
        super.initialize();
        this.exportService = mock( ExportService.class );
        addService( ExportService.class, exportService );
    }

    @Test
    void testExample()
    {
        final ListExportsResult result = ListExportsResult.create()
            .addExport( new ExportInfo( "export-a" ) )
            .addExport( new ExportInfo( "export-b" ) )
            .build();

        when( exportService.list() ).thenReturn( result );
        runScript( "/lib/xp/examples/export/listExports.js" );
    }
}
