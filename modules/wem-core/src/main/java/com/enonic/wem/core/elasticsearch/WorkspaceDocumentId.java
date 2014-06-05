package com.enonic.wem.core.elasticsearch;

import com.google.common.base.Preconditions;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.collect.Iterators;

import com.enonic.wem.api.entity.EntityId;
import com.enonic.wem.api.entity.Workspace;

public class WorkspaceDocumentId
{
    private final String value;

    private final String entityIdAsString;

    private final String workspaceName;

    public WorkspaceDocumentId( final EntityId entityId, final Workspace workspace )
    {
        Preconditions.checkNotNull( entityId );
        Preconditions.checkNotNull( workspace );

        this.value = entityId + "-" + workspace.getName();
        this.entityIdAsString = entityId.toString();
        this.workspaceName = workspace.getName();
    }

    private WorkspaceDocumentId( final String value, final String entityIdAsString, final String workspaceName )
    {
        this.value = value;
        this.workspaceName = workspaceName;
        this.entityIdAsString = entityIdAsString;
    }

    public static WorkspaceDocumentId from( final String value )
    {
        if ( !value.contains( "-" ) )
        {
            throw new IllegalArgumentException( "Invalid format of workspace-key: " + value );
        }

        final Iterable<String> split = Splitter.on( "-" ).
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

    public String getEntityIdAsString()
    {
        return entityIdAsString;
    }

    public String getWorkspaceName()
    {
        return workspaceName;
    }

    @Override
    public String toString()
    {
        return value;
    }
}
