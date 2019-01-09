package com.enonic.xp.admin.impl.rest.resource.security.json;


import com.enonic.xp.security.IdProviderKey;

public final class SyncIdProviderResultJson
{

    private final IdProviderKey idProviderKey;

    private final boolean synch;

    private final String failureReason;

    private SyncIdProviderResultJson( final IdProviderKey idProviderKey, final boolean synch, final String errorCause )
    {
        this.idProviderKey = idProviderKey;
        this.synch = synch;
        this.failureReason = errorCause;
    }

    public static SyncIdProviderResultJson success( final IdProviderKey idProviderKey )
    {
        return new SyncIdProviderResultJson( idProviderKey, true, null );
    }

    public static SyncIdProviderResultJson failure( final IdProviderKey idProviderKey, final String failureReason )
    {
        return new SyncIdProviderResultJson( idProviderKey, false, failureReason );
    }

    public String getIdProviderKey()
    {
        return idProviderKey.toString();
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
