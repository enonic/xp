package com.enonic.xp.admin.impl.rest.resource.security.json;


import com.enonic.xp.security.UserStoreKey;

public final class SynchUserStoreResultJson
{

    private final UserStoreKey userStoreKey;

    private final boolean synch;

    private final String failureReason;

    private SynchUserStoreResultJson( final UserStoreKey userStoreKey, final boolean synch, final String errorCause )
    {
        this.userStoreKey = userStoreKey;
        this.synch = synch;
        this.failureReason = errorCause;
    }

    public static SynchUserStoreResultJson success( final UserStoreKey userStoreKey )
    {
        return new SynchUserStoreResultJson( userStoreKey, true, null );
    }

    public static SynchUserStoreResultJson failure( final UserStoreKey userStoreKey, final String failureReason )
    {
        return new SynchUserStoreResultJson( userStoreKey, false, failureReason );
    }

    public String getUserStoreKey()
    {
        return userStoreKey.toString();
    }

    public boolean isSynchronized()
    {
        return synch;
    }

    public String getReason()
    {
        return failureReason;
    }
}
