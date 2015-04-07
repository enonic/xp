package com.enonic.xp.export;

import com.google.common.annotations.Beta;

@Beta
public interface ExportService
{
    NodeExportResult exportNodes( final ExportNodesParams params );

    NodeImportResult importNodes( final ImportNodesParams params );
}
