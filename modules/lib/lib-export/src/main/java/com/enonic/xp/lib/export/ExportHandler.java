package com.enonic.xp.lib.export;

import java.util.function.Function;

import com.enonic.xp.export.ExportNodesParams;
import com.enonic.xp.export.ExportService;
import com.enonic.xp.export.NodeExportResult;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.script.bean.BeanContext;
import com.enonic.xp.script.bean.ScriptBean;

public class ExportHandler
    implements ScriptBean
{
    private BeanContext context;

    private String sourceNodePath;

    private String exportName;

    private Function<Long, Void> nodeExported;

    private Function<Long, Void> nodeResolved;

    public NodeExportResultMapper execute()
    {
        final ExportNodesParams.Builder paramsBuilder = ExportNodesParams.create()
            .sourceNodePath( new NodePath( sourceNodePath ) )
            .exportName( exportName )
            .nodeExportListener( new FunctionBasedNodeExportListener( nodeExported, nodeResolved ) );

        final NodeExportResult nodeImportResult = this.context.getService( ExportService.class ).get().exportNodes( paramsBuilder.build() );
        return new NodeExportResultMapper( nodeImportResult );
    }

    public void setSourceNodePath( final String sourceNodePath )
    {
        this.sourceNodePath = sourceNodePath;
    }

    public void setExportName( final String exportName )
    {
        this.exportName = exportName;
    }

    public void setNodeExported( final Function<Long, Void> nodeExported )
    {
        this.nodeExported = nodeExported;
    }

    public void setNodeResolved( final Function<Long, Void> nodeResolved )
    {
        this.nodeResolved = nodeResolved;
    }

    @Override
    public void initialize( final BeanContext context )
    {
        this.context = context;
    }
}
