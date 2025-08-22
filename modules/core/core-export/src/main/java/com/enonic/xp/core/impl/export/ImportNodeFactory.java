package com.enonic.xp.core.impl.export;

import java.util.Objects;

import com.enonic.xp.node.Node;
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
                id( importNodeIds && this.serializedNode.id() != null ? this.serializedNode.id() : null ).
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
            Objects.requireNonNull( this.importPath, "importPath cannot be null" );
            Objects.requireNonNull( this.serializedNode, "serializedNode cannot be null" );
        }

        public ImportNodeFactory build()
        {
            validate();
            return new ImportNodeFactory( this );
        }
    }

}
