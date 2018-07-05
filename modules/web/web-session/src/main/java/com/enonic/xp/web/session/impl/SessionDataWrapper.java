package com.enonic.xp.web.session.impl;

import java.io.Serializable;

import org.eclipse.jetty.server.session.SessionData;

/**
 * Wrapper of SessionData that includes the lastSaved field when it is serialized for session replication in the cluster.
 */
public final class SessionDataWrapper
    implements Serializable
{
    private static final long serialVersionUID = 1L;

    private final SessionData sessionData;

    private final long lastSaved;

    public SessionDataWrapper( final SessionData sessionData )
    {
        this.sessionData = sessionData;
        this.lastSaved = sessionData.getLastSaved();
    }

    public SessionData getSessionData()
    {
        sessionData.setLastSaved( this.lastSaved );
        return sessionData;
    }
}
