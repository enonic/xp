package com.enonic.wem.api.export;

public interface ExportService
{
    public NodeExportResult exportNodes( final ExportNodesParams params );

    public NodeImportResult importNodes( final ImportNodesParams params );

}
