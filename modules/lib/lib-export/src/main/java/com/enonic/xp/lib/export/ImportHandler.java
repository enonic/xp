package com.enonic.xp.lib.export;

import java.nio.file.Path;
import java.util.Map;
import java.util.function.Function;

import org.osgi.framework.Bundle;

import com.enonic.xp.app.ApplicationService;
import com.enonic.xp.export.ExportService;
import com.enonic.xp.export.ImportNodesParams;
import com.enonic.xp.export.NodeImportListener;
import com.enonic.xp.export.NodeImportResult;
import com.enonic.xp.home.HomeDir;
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
    private final Path exportsFolder = HomeDir.get().toPath().resolve( "data" ).resolve( "export" );

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
            .source( toVirtualFile( source ) )
            .targetNodePath( NodePath.create( targetNodePath ).build() )
            .xslt( toVirtualFile( xslt ) )
            .xsltParams( xsltParams != null ? xsltParams.getMap() : Map.of() )
            .nodeImportListener( new NodeImportListener()
            {
                @Override
                public void nodeImported( final long count )
                {
                    if ( nodeImported != null )
                    {
                        nodeImported.apply( count );
                    }
                }

                @Override
                public void nodeResolved( final long count )
                {
                    if ( nodeResolved != null )
                    {
                        nodeResolved.apply( count );
                    }
                }
            } );

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

    private VirtualFile toVirtualFile( final Object value )
    {
        if ( value == null )
        {
            return null;
        }

        if ( value instanceof ResourceKey )
        {
            final ResourceKey resourceKey = (ResourceKey) value;
            final Bundle bundle =
                context.getService( ApplicationService.class ).get().getInstalledApplication( resourceKey.getApplicationKey() ).getBundle();
            return VirtualFiles.from( bundle, resourceKey.getPath() );
        }
        else
        {
            return VirtualFiles.from( exportsFolder.resolve( value.toString() ) );
        }
    }

    @Override
    public void initialize( final BeanContext context )
    {
        this.context = context;
    }
}
