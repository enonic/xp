package com.enonic.xp.export;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
public interface ExportService
{
    NodeExportResult exportNodes( ExportNodesParams params );

    NodeImportResult importNodes( ImportNodesParams params );
}
