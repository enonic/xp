package com.enonic.xp.export;

public interface ExportService
{
    NodeExportResult exportNodes( ExportNodesParams params );

    NodeImportResult importNodes( ImportNodesParams params );

    ListExportsResult list();
}
