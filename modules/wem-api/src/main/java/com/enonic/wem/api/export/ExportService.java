package com.enonic.wem.api.export;

import com.enonic.wem.api.node.NodePath;

public interface ExportService
{

    public NodeExportResult exportNodes( final NodePath nodePath );

    public NodeImportResult importNodes( final String exportName, final NodePath importPath );

}
