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

    private Boolean includeNodeIds;

    private Boolean includeVersions;

    private Integer batchSize;

    private Function<Long, Void> nodeExported;

    private Function<Long, Void> nodeResolved;

    public NodeExportResultMapper execute()
    {
        final ExportNodesParams.Builder paramsBuilder = ExportNodesParams.create()
            .sourceNodePath( new NodePath( sourceNodePath ) )
            .exportName( exportName )
            .nodeExportListener( new FunctionBasedNodeExportListener( nodeExported, nodeResolved ) );

        if ( includeNodeIds != null )
        {
            paramsBuilder.includeNodeIds( includeNodeIds );
        }

        if ( includeVersions != null )
        {
            paramsBuilder.includeVersions( includeVersions );
        }

        if ( batchSize != null )
        {
            paramsBuilder.batchSize( batchSize );
        }
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

    public void setIncludeNodeIds( final Boolean includeNodeIds )
    {
        this.includeNodeIds = includeNodeIds;
    }

    public void setIncludeVersions( final Boolean includeVersions )
    {
        this.includeVersions = includeVersions;
    }

    public void setBatchSize( final Integer batchSize )
    {
        this.batchSize = batchSize;
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
