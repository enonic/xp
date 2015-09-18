package com.enonic.xp.content;

import com.google.common.annotations.Beta;

@Beta
public enum CompareStatus
{
    NEW( false ),
    NEW_TARGET( false ),
    NEWER( false ),
    OLDER( false ),
    PENDING_DELETE( false ),
    PENDING_DELETE_TARGET( false ),
    EQUAL( false ),
    MOVED( false ),
    CONFLICT_PATH_EXISTS( true ),
    CONFLICT_VERSION_BRANCH_DIVERGS( true );

    private final boolean conflict;

    CompareStatus( final boolean conflict )
    {
        this.conflict = conflict;
    }

    public boolean isConflict()
    {
        return this.conflict;
    }
}
