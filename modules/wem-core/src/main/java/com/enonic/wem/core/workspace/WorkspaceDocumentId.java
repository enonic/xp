package com.enonic.wem.core.workspace;

import com.google.common.base.Preconditions;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.collect.Iterators;

import com.enonic.wem.api.workspace.Workspace;
import com.enonic.wem.core.entity.EntityId;

public class WorkspaceDocumentId
{
    private static final String SEPARATOR = "_";

    private final String value;

    private final EntityId entityId;

    private final Workspace workspace;

    public WorkspaceDocumentId( final EntityId entityId, final Workspace workspace )
    {
        Preconditions.checkNotNull( entityId );
        Preconditions.checkNotNull( workspace );

        this.value = entityId + SEPARATOR + workspace.getName();
        this.entityId = entityId;
        this.workspace = workspace;
    }

    private WorkspaceDocumentId( final String value, final String entityIdAsString, final String workspaceName )
    {
        this.value = value;
        this.entityId = EntityId.from( entityIdAsString );
        this.workspace = Workspace.from( workspaceName );
    }

    public static WorkspaceDocumentId from( final String value )
    {
        if ( !value.contains( SEPARATOR ) )
        {
            throw new IllegalArgumentException( "Invalid format of workspace-key: " + value );
        }

        final Iterable<String> split = Splitter.on( SEPARATOR ).
            split( value );

        final String entityIdAsString = Iterators.get( split.iterator(), 0 );
        final String workspaceName = Iterators.get( split.iterator(), 1 );

        Preconditions.checkArgument( !Strings.isNullOrEmpty( entityIdAsString ) );
        Preconditions.checkArgument( !Strings.isNullOrEmpty( workspaceName ) );

        return new WorkspaceDocumentId( value, entityIdAsString, workspaceName );
    }

    public String getValue()
    {
        return value;
    }

    public EntityId getEntityId()
    {
        return entityId;
    }

    public Workspace getWorkspace()
    {
        return workspace;
    }

    @Override
    public String toString()
    {
        return value;
    }
}
