package com.enonic.xp.export;

public interface ExportService
{
    NodeExportResult exportNodes( ExportNodesParams params );

    NodeImportResult importNodes( ImportNodesParams params );

    /**
     * Lists the node-exports available in the exports directory.
     *
     * @return names of the available node-exports.
     * @throws com.enonic.xp.exception.ForbiddenAccessException if the caller does not have the {@code system.admin} role.
     */
    ListExportsResult list();
}
