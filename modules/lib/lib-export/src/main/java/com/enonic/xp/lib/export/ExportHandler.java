package com.enonic.xp.lib.export;

import java.nio.file.Path;
import java.util.function.Function;

import com.enonic.xp.export.ExportNodesParams;
import com.enonic.xp.export.ExportService;
import com.enonic.xp.export.NodeExportResult;
import com.enonic.xp.home.HomeDir;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.script.bean.BeanContext;
import com.enonic.xp.script.bean.ScriptBean;

public class ExportHandler
    implements ScriptBean
{
    private final Path exportsFolder = HomeDir.get().toPath().resolve( "data" ).resolve( "export" );

    private BeanContext context;

    private String sourceNodePath;

    private String targetDirectory;

    private Boolean includeNodeIds;

    private Boolean includeVersions;

    private Function<Long, Void> nodeExported;

    private Function<Long, Void> nodeResolved;

    public NodeExportResultMapper execute()
    {
        final ExportNodesParams.Builder paramsBuilder = ExportNodesParams.create()
            .sourceNodePath( NodePath.create( sourceNodePath ).build() )
            .targetDirectory( exportsFolder.resolve( targetDirectory ).toString() )
            .nodeExportListener( new FunctionBasedNodeExportListener( nodeExported, nodeResolved ) );

        if ( includeNodeIds != null )
        {
            paramsBuilder.includeNodeIds( includeNodeIds );
        }

        if ( includeVersions != null )
        {
            paramsBuilder.includeVersions( includeVersions );
        }
        final NodeExportResult nodeImportResult = this.context.getService( ExportService.class ).get().exportNodes( paramsBuilder.build() );
        return new NodeExportResultMapper( nodeImportResult );
    }

    public void setSourceNodePath( final String sourceNodePath )
    {
        this.sourceNodePath = sourceNodePath;
    }

    public void setTargetDirectory( final String targetDirectory )
    {
        this.targetDirectory = targetDirectory;
    }

    public void setIncludeNodeIds( final Boolean includeNodeIds )
    {
        this.includeNodeIds = includeNodeIds;
    }

    public void setIncludeVersions( final Boolean includeVersions )
    {
        this.includeVersions = includeVersions;
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
