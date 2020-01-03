package com.enonic.xp.content;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
public enum CompareStatus
{
    NEW( false, "Offline" ), NEW_TARGET( false, "New in prod" ), NEWER( false, "Modified" ), OLDER( false, "Out-of-date" ), PENDING_DELETE(
    false, "Pending delete" ), PENDING_DELETE_TARGET( false, "Deleted in prod" ), EQUAL( false, "Online" ), MOVED( false,
                                                                                                                   "Moved" ), CONFLICT_PATH_EXISTS(
    true, "Conflict" ), CONFLICT_VERSION_BRANCH_DIVERGS( true, "Conflict version" );

    private final boolean conflict;

    private final String status;

    CompareStatus( final boolean conflict, final String formattedStatus )
    {
        this.conflict = conflict;
        this.status = formattedStatus;
    }

    public boolean isConflict()
    {
        return this.conflict;
    }

    public String getFormattedStatus()
    {
        return this.status;
    }
}
