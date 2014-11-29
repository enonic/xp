package com.enonic.wem.api.export;

import com.enonic.wem.api.node.NodePath;

public interface ExportService
{

    public NodeExportResult export( final NodePath nodePath );

}
