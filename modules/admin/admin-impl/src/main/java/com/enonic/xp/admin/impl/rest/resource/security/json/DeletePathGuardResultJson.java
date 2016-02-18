package com.enonic.xp.admin.impl.rest.resource.security.json;


public final class DeletePathGuardResultJson
{
    private final String key;

    private final boolean deleted;

    private final String failureReason;

    private DeletePathGuardResultJson( final String key, final boolean deleted, final String errorCause )
    {
        this.key = key;
        this.deleted = deleted;
        this.failureReason = errorCause;
    }

    public static DeletePathGuardResultJson success( final String key )
    {
        return new DeletePathGuardResultJson( key, true, null );
    }

    public static DeletePathGuardResultJson failure( final String key, final String failureReason )
    {
        return new DeletePathGuardResultJson( key, false, failureReason );
    }

    public String getKey()
    {
        return key;
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
