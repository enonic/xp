package com.enonic.xp.admin.impl.rest.resource.security.json;


import com.enonic.xp.security.UserStoreKey;

public final class DeleteUserStoreResultJson
{

    private final UserStoreKey userStoreKey;

    private final boolean deleted;

    private final String failureReason;

    private DeleteUserStoreResultJson( final UserStoreKey userStoreKey, final boolean deleted, final String errorCause )
    {
        this.userStoreKey = userStoreKey;
        this.deleted = deleted;
        this.failureReason = errorCause;
    }

    public static DeleteUserStoreResultJson success( final UserStoreKey userStoreKey )
    {
        return new DeleteUserStoreResultJson( userStoreKey, true, null );
    }

    public static DeleteUserStoreResultJson failure( final UserStoreKey userStoreKey, final String failureReason )
    {
        return new DeleteUserStoreResultJson( userStoreKey, false, failureReason );
    }

    public String getUserStoreKey()
    {
        return userStoreKey.toString();
    }

    public boolean isDeleted()
    {
        return deleted;
    }

    public String getReason()
    {
        return failureReason;
    }
}
