package com.enonic.xp.lib.export;

import java.util.Map;
import java.util.function.Function;

import com.enonic.xp.export.ExportService;
import com.enonic.xp.export.ImportNodesParams;
import com.enonic.xp.export.NodeImportResult;
import com.enonic.xp.node.Attributes;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.VersionAttributesResolver;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.resource.ResourceService;
import com.enonic.xp.script.ScriptValue;
import com.enonic.xp.script.bean.BeanContext;
import com.enonic.xp.script.bean.ScriptBean;
import com.enonic.xp.util.GenericValue;
import com.enonic.xp.vfs.VirtualFile;

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

    private ScriptValue versionAttributes;

    private Function<Integer, Void> nodeImported;

    private Function<Integer, Void> nodeResolved;

    private Function<Integer, Void> nodeSkipped;

    public NodeImportResultMapper execute()
    {
        final ImportNodesParams.Builder paramsBuilder = ImportNodesParams.create()
            .targetNodePath( new NodePath( targetNodePath ) )
            .nodeImportListener( new FunctionBasedNodeImportListener( nodeImported, nodeResolved, nodeSkipped ) );

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
        if ( versionAttributes != null )
        {
            paramsBuilder.versionAttributesResolver( VersionAttributesResolver.of( toAttributes( versionAttributes.getMap() ) ) );
        }
        final NodeImportResult nodeImportResult = this.context.getService( ExportService.class ).get().importNodes( paramsBuilder.build() );
        return new NodeImportResultMapper( nodeImportResult );
    }

    private static Attributes toAttributes( final Map<String, Object> map )
    {
        final Attributes.Builder builder = Attributes.create();
        for ( Map.Entry<String, Object> entry : map.entrySet() )
        {
            if ( entry.getValue() != null )
            {
                builder.attribute( entry.getKey(), GenericValue.fromRawJava( entry.getValue() ) );
            }
        }
        return builder.build();
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

    public void setVersionAttributes( final ScriptValue versionAttributes )
    {
        this.versionAttributes = versionAttributes;
    }

    public void setNodeImported( final Function<Integer, Void> nodeImported )
    {
        this.nodeImported = nodeImported;
    }

    public void setNodeResolved( final Function<Integer, Void> nodeResolved )
    {
        this.nodeResolved = nodeResolved;
    }

    public void setNodeSkipped( final Function<Integer, Void> nodeSkipped )
    {
        this.nodeSkipped = nodeSkipped;
    }

    private VirtualFile toVirtualFile( final ResourceKey resourceKey )
    {
        return context.getService( ResourceService.class ).get().getVirtualFile( resourceKey );
    }

    @Override
    public void initialize( final BeanContext context )
    {
        this.context = context;
    }
}
