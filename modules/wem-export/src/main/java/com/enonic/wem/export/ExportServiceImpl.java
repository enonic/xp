package com.enonic.wem.export;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.wem.api.export.ExportService;
import com.enonic.wem.api.node.NodePath;
import com.enonic.wem.api.node.NodeService;

@Component(immediate = true)
public class ExportServiceImpl
    implements ExportService
{
    private NodeService nodeService;

    @Override
    public void export( final NodePath nodePath )
    {

    }

    @Reference
    public void setNodeService( final NodeService nodeService )
    {
        this.nodeService = nodeService;
    }
}
