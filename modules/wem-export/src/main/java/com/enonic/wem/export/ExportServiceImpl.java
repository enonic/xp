package com.enonic.wem.export;

import com.enonic.wem.api.export.ExportService;
import com.enonic.wem.api.node.NodePath;
import com.enonic.wem.api.node.NodeService;

public class ExportServiceImpl
    implements ExportService
{
    private NodeService nodeService;

    @Override
    public void export( final NodePath nodePath )
    {

    }

    public void setNodeService( final NodeService nodeService )
    {
        this.nodeService = nodeService;
    }
}
