package com.enonic.xp.core.impl.export;

import com.google.common.base.Preconditions;

import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.repository.RepositoryConstants;
import com.enonic.xp.security.acl.AccessControlList;

public class ImportNodeFactory
{
    private final Node serializedNode;

    private final NodePath nodeImportPath;

    private final Long manualOrderValue;

    private final boolean importNodeIds;

    private final boolean importPermissions;

    private ImportNodeFactory( Builder builder )
    {
        this.serializedNode = builder.serializedNode;
        this.nodeImportPath = builder.importPath;
        this.manualOrderValue = builder.manualOrderValue;
        this.importNodeIds = builder.importNodeIds;
        this.importPermissions = builder.importPermissions;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public Node execute()
    {
        if ( serializedNode.isRoot() )
        {
            return Node.create( serializedNode ).
                permissions( importPermissions ? serializedNode.getPermissions() : RepositoryConstants.DEFAULT_REPO_PERMISSIONS ).
                manualOrderValue( manualOrderValue ).
                build();
        }
        else
        {
            return Node.create( serializedNode ).
                parentPath( this.nodeImportPath.getParentPath() ).
                name( this.nodeImportPath.getName() ).
                permissions( importPermissions ? serializedNode.getPermissions() : AccessControlList.empty() ).
                id( importNodeIds && this.serializedNode.id() != null ? NodeId.from( this.serializedNode.id() ) : null ).
                manualOrderValue( manualOrderValue ).
                timestamp( serializedNode.getTimestamp() ).
                build();
        }
    }

    public static final class Builder
    {
        private Node serializedNode;

        private NodePath importPath;

        private Long manualOrderValue;

        private boolean importNodeIds = true;

        private boolean importPermissions = true;

        private Builder()
        {
        }

        public Builder serializedNode( final Node serializedNode )
        {
            this.serializedNode = serializedNode;
            return this;
        }

        public Builder importPath( final NodePath importPath )
        {
            this.importPath = importPath;
            return this;
        }

        public Builder manualOrderValue( final Long manualOrderValue )
        {
            this.manualOrderValue = manualOrderValue;
            return this;
        }

        public Builder importNodeIds( final boolean importNodeIds )
        {
            this.importNodeIds = importNodeIds;
            return this;
        }


        public Builder importPermissions( final boolean importPermissions )
        {
            this.importPermissions = importPermissions;
            return this;
        }

        private void validate()
        {
            Preconditions.checkNotNull( this.importPath, "Importpath cannot be null" );
            Preconditions.checkNotNull( this.serializedNode, "Serialized node cannot be null" );
        }

        public ImportNodeFactory build()
        {
            validate();
            return new ImportNodeFactory( this );
        }
    }

}
