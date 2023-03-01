package com.enonic.xp.lib.export;

import java.util.function.Function;

import org.osgi.framework.Bundle;

import com.enonic.xp.app.ApplicationService;
import com.enonic.xp.export.ExportService;
import com.enonic.xp.export.ImportNodesParams;
import com.enonic.xp.export.NodeImportResult;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.script.ScriptValue;
import com.enonic.xp.script.bean.BeanContext;
import com.enonic.xp.script.bean.ScriptBean;
import com.enonic.xp.vfs.VirtualFile;
import com.enonic.xp.vfs.VirtualFiles;

public class ImportHandler
    implements ScriptBean
{
    private BeanContext context;

    private Object source;

    private String targetNodePath;

    private Object xslt;

    private ScriptValue xsltParams;

    private Boolean includeNodeIds;

    private Boolean includePermissions;

    private Function<Long, Void> nodeImported;

    private Function<Long, Void> nodeResolved;

    public NodeImportResultMapper execute()
    {
        final ImportNodesParams.Builder paramsBuilder = ImportNodesParams.create()
            .targetNodePath( new NodePath( targetNodePath ) )
            .nodeImportListener( new FunctionBasedNodeImportListener( nodeImported, nodeResolved ) );

        if ( source instanceof ResourceKey )
        {
            paramsBuilder.source( toVirtualFile( (ResourceKey) source ) );
        }
        else
        {
            paramsBuilder.exportName( source.toString() );
        }

        if ( xslt instanceof ResourceKey )
        {
            paramsBuilder.xslt( toVirtualFile( (ResourceKey) xslt ) );
        }
        else
        {
            paramsBuilder.xsltFileName( xslt.toString() );
        }

        if ( xsltParams != null )
        {
            paramsBuilder.xsltParams( xsltParams.getMap() );
        }
        if ( includeNodeIds != null )
        {
            paramsBuilder.includeNodeIds( includeNodeIds );
        }
        if ( includePermissions != null )
        {
            paramsBuilder.includePermissions( includePermissions );
        }
        final NodeImportResult nodeImportResult = this.context.getService( ExportService.class ).get().importNodes( paramsBuilder.build() );
        return new NodeImportResultMapper( nodeImportResult );
    }

    public void setSource( final Object source )
    {
        this.source = source;
    }

    public void setXslt( final Object xslt )
    {
        this.xslt = xslt;
    }

    public void setTargetNodePath( final String targetNodePath )
    {
        this.targetNodePath = targetNodePath;
    }

    public void setXsltParams( final ScriptValue xsltParams )
    {
        this.xsltParams = xsltParams;
    }

    public void setIncludeNodeIds( final Boolean includeNodeIds )
    {
        this.includeNodeIds = includeNodeIds;
    }

    public void setIncludePermissions( final Boolean includePermissions )
    {
        this.includePermissions = includePermissions;
    }

    public void setNodeImported( final Function<Long, Void> nodeImported )
    {
        this.nodeImported = nodeImported;
    }

    public void setNodeResolved( final Function<Long, Void> nodeResolved )
    {
        this.nodeResolved = nodeResolved;
    }

    private VirtualFile toVirtualFile( final ResourceKey resourceKey )
    {
        final Bundle bundle =
            context.getService( ApplicationService.class ).get().getInstalledApplication( resourceKey.getApplicationKey() ).getBundle();
        return VirtualFiles.from( bundle, resourceKey.getPath() );
    }

    @Override
    public void initialize( final BeanContext context )
    {
        this.context = context;
    }
}
