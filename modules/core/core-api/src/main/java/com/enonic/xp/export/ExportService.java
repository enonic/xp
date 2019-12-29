package com.enonic.xp.export;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
public interface ExportService
{
    NodeExportResult exportNodes( final ExportNodesParams params );

    NodeImportResult importNodes( final ImportNodesParams params );
}
