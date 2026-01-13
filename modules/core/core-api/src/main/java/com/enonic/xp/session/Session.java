package com.enonic.xp.session;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.context.MutableAttributes;

@PublicApi
public interface Session
    extends MutableAttributes
{
    SessionKey getKey();

    void invalidate();

    /**
     * Changes the session id and returns the new session id.
     * This is a wrapper for HttpServletRequest.changeSessionId() in Servlet 3.1+.
     *
     * @return the new session id
     */
    String changeSessionId();

    /**
     * Sets the maximum time interval, in seconds, that the session should remain active between client accesses.
     *
     * @param seconds the maximum inactive interval in seconds. A zero or negative value indicates that the session should never timeout.
     */
    void setMaxInactiveInterval( int seconds );
}
