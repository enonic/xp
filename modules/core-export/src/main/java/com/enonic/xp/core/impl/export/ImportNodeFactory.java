package com.enonic.xp.core.impl.export;

import com.google.common.base.Preconditions;

import com.enonic.xp.node.InsertManualStrategy;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.security.acl.AccessControlList;

public class ImportNodeFactory
{
    private final Node serializedNode;

    private final NodePath nodeImportPath;

    private final ProcessNodeSettings processNodeSettings;

    private final boolean importNodeIds;

    private final boolean importPermissions;

    private ImportNodeFactory( Builder builder )
    {
        this.serializedNode = builder.serializedNode;
        this.nodeImportPath = builder.importPath;
        this.processNodeSettings = builder.processNodeSettings;
        this.importNodeIds = builder.importNodeIds;
        this.importPermissions = builder.importPermissions;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public Node execute()
    {
        return Node.create( serializedNode ).
            parentPath( this.nodeImportPath.getParentPath() ).
            name( getNodeName() ).
            inheritPermissions( importPermissions ? serializedNode.inheritsPermissions() : true ).
            permissions( importPermissions ? serializedNode.getPermissions() : AccessControlList.empty() ).
            id( importNodeIds && this.serializedNode.id() != null ? NodeId.from( this.serializedNode.id() ) : null ).
            manualOrderValue( getManualOrderValue() ).
            timestamp( serializedNode.getTimestamp() ).
            build();
    }

    private String getNodeName()
    {
        return this.nodeImportPath.getLastElement().toString();
    }

    private Long getManualOrderValue()
    {
        final InsertManualStrategy insertManualStrategy = this.processNodeSettings.getInsertManualStrategy();

        if ( insertManualStrategy != null )
        {
            if ( insertManualStrategy.equals( InsertManualStrategy.MANUAL ) )
            {
                return this.processNodeSettings.getManualOrderValue();
            }
        }

        return null;
    }

    public static final class Builder
    {
        private Node serializedNode;

        private NodePath importPath;

        private ProcessNodeSettings processNodeSettings;

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

        public Builder importPath( NodePath importPath )
        {
            this.importPath = importPath;
            return this;
        }

        public Builder processNodeSettings( ProcessNodeSettings processNodeSettings )
        {
            this.processNodeSettings = processNodeSettings;
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

            Preconditions.checkNotNull( this.processNodeSettings, "ProcessNodeSettings cannot be null" );
            Preconditions.checkNotNull( this.importPath, "Importpath cannot be null" );
            Preconditions.checkNotNull( this.serializedNode, "Serialized node cannot be null" );
        }

        public ImportNodeFactory build()
        {
            return new ImportNodeFactory( this );
        }
    }

}
