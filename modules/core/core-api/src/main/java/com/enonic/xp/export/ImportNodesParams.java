package com.enonic.xp.export;

import java.util.HashMap;
import java.util.Map;

import com.google.common.base.Preconditions;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.core.internal.FileNames;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.vfs.VirtualFile;

@PublicApi
public class ImportNodesParams
{
    private final NodePath targetNodePath;

    private final VirtualFile source;

    private final String exportName;

    private final boolean importNodeIds;

    private final boolean importPermissions;

    private final VirtualFile xslt;

    private final Map<String, Object> xsltParams;

    private final NodeImportListener nodeImportListener;

    private ImportNodesParams( final Builder builder )
    {
        this.targetNodePath = builder.targetNodePath;
        this.source = builder.source;
        this.importNodeIds = builder.importNodeIds;
        this.importPermissions = builder.importPermissions;
        this.xslt = builder.xslt;
        this.xsltParams = builder.xsltParams;
        this.nodeImportListener = builder.nodeImportListener;
        this.exportName = builder.exportName;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public NodePath getTargetNodePath()
    {
        return targetNodePath;
    }

    @Deprecated
    public boolean isDryRun()
    {
        return false;
    }

    public VirtualFile getSource()
    {
        return source;
    }

    public String getExportName()
    {
        return exportName;
    }

    public boolean isImportNodeids()
    {
        return importNodeIds;
    }

    public boolean isImportPermissions()
    {
        return importPermissions;
    }

    public VirtualFile getXslt()
    {
        return xslt;
    }

    public Map<String, Object> getXsltParams()
    {
        return xsltParams;
    }

    public NodeImportListener getNodeImportListener()
    {
        return nodeImportListener;
    }

    public static final class Builder
    {
        private NodePath targetNodePath;

        private VirtualFile source;

        private String exportName;

        private boolean importNodeIds;

        private boolean importPermissions;

        private VirtualFile xslt;

        private String xsltFileName;

        private Map<String, Object> xsltParams;

        private NodeImportListener nodeImportListener;

        private Builder()
        {
        }

        public Builder targetNodePath( NodePath targetNodePath )
        {
            this.targetNodePath = targetNodePath;
            return this;
        }

        @Deprecated
        public Builder dryRun( boolean dryRun )
        {
            throw new UnsupportedOperationException( "dryRun is not supported" );
        }

        public Builder source( final VirtualFile source )
        {
            this.source = source;
            return this;
        }

        public Builder exportName( final String exportName )
        {
            this.exportName = exportName;
            return this;
        }

        public Builder includeNodeIds( final boolean importNodeIds )
        {
            this.importNodeIds = importNodeIds;
            return this;
        }

        public Builder includePermissions( final boolean importPermissions )
        {
            this.importPermissions = importPermissions;
            return this;
        }

        public Builder xslt( final VirtualFile xslt )
        {
            this.xslt = xslt;
            return this;
        }

        public Builder xsltFileName( final String xsltFileName )
        {
            this.xsltFileName = xsltFileName;
            return this;
        }

        public Builder xsltParams( final Map<String, Object> xsltParams )
        {
            this.xsltParams = xsltParams;
            return this;
        }

        public Builder xsltParam( final String paramName, final Object paramValue )
        {
            if ( this.xsltParams == null )
            {
                this.xsltParams = new HashMap<>();
            }
            this.xsltParams.put( paramName, paramValue );
            return this;
        }

        public Builder nodeImportListener( final NodeImportListener nodeImportListener )
        {
            this.nodeImportListener = nodeImportListener;
            return this;
        }

        private void validate()
        {
            Preconditions.checkArgument( exportName != null || source != null, "Either exportName or source must be set" );
            Preconditions.checkArgument( !( exportName != null && source != null ), "exportName and source are mutually exclusive" );
            if ( exportName != null )
            {
                Preconditions.checkArgument( FileNames.isSafeFileName( exportName ), "Invalid export name" );
            }

            Preconditions.checkArgument( !( xsltFileName != null && xslt != null ), "xsltFileName and xslt are mutually exclusive" );
            if ( xsltFileName != null )
            {
                Preconditions.checkArgument( FileNames.isSafeFileName( xsltFileName ), "Invalid xslt file name" );
            }
        }

        public ImportNodesParams build()
        {
            this.validate();
            return new ImportNodesParams( this );
        }
    }
}
