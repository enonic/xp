package com.enonic.xp.admin.impl.rest.resource.security.json;


import com.enonic.xp.security.IdProviderKey;

public final class DeleteIdProviderResultJson
{

    private final IdProviderKey idProviderKey;

    private final boolean deleted;

    private final String failureReason;

    private DeleteIdProviderResultJson( final IdProviderKey idProviderKey, final boolean deleted, final String errorCause )
    {
        this.idProviderKey = idProviderKey;
        this.deleted = deleted;
        this.failureReason = errorCause;
    }

    public static DeleteIdProviderResultJson success( final IdProviderKey idProviderKey )
    {
        return new DeleteIdProviderResultJson( idProviderKey, true, null );
    }

    public static DeleteIdProviderResultJson failure( final IdProviderKey idProviderKey, final String failureReason )
    {
        return new DeleteIdProviderResultJson( idProviderKey, false, failureReason );
    }

    public String getIdProviderKey()
    {
        return idProviderKey.toString();
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
