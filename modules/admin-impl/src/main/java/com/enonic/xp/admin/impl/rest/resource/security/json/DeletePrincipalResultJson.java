package com.enonic.xp.admin.impl.rest.resource.security.json;


import com.enonic.wem.api.security.PrincipalKey;

public final class DeletePrincipalResultJson
{

    private final PrincipalKey principalKey;

    private final boolean deleted;

    private final String failureReason;

    private DeletePrincipalResultJson( final PrincipalKey principalKey, final boolean deleted, final String errorCause )
    {
        this.principalKey = principalKey;
        this.deleted = deleted;
        this.failureReason = errorCause;
    }

    public static DeletePrincipalResultJson success( final PrincipalKey principalKey )
    {
        return new DeletePrincipalResultJson( principalKey, true, null );
    }

    public static DeletePrincipalResultJson failure( final PrincipalKey principalKey, final String failureReason )
    {
        return new DeletePrincipalResultJson( principalKey, false, failureReason );
    }

    public String getPrincipalKey()
    {
        return principalKey.toString();
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
