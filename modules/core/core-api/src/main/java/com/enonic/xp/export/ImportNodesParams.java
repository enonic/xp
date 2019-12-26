package com.enonic.xp.export;

import java.util.HashMap;
import java.util.Map;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.vfs.VirtualFile;

@PublicApi
public class ImportNodesParams
{
    private final NodePath targetNodePath;

    private final boolean dryRun;

    private final VirtualFile source;

    private final boolean importNodeids;

    private final boolean importPermissions;

    private final VirtualFile xslt;

    private final Map<String, Object> xsltParams;
    
    private final NodeImportListener nodeImportListener;

    private ImportNodesParams( final Builder builder )
    {
        this.targetNodePath = builder.targetNodePath;
        this.source = builder.source;
        this.dryRun = builder.dryRun;
        this.importNodeids = builder.importNodeIds;
        this.importPermissions = builder.importPermissions;
        this.xslt = builder.xslt;
        this.xsltParams = builder.xsltParams;
        this.nodeImportListener = builder.nodeImportListener;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public NodePath getTargetNodePath()
    {
        return targetNodePath;
    }

    public boolean isDryRun()
    {
        return dryRun;
    }

    public VirtualFile getSource()
    {
        return source;
    }

    public boolean isImportNodeids()
    {
        return importNodeids;
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

        private boolean dryRun = false;

        private boolean importNodeIds;

        private boolean importPermissions;

        private VirtualFile xslt;

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

        public Builder dryRun( boolean dryRun )
        {
            this.dryRun = dryRun;
            return this;
        }

        public Builder source( final VirtualFile source )
        {
            this.source = source;
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

        public ImportNodesParams build()
        {
            return new ImportNodesParams( this );
        }
    }
}
