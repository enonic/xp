package com.enonic.wem.core.jcr;

public interface SessionFactory
{
    JcrSession getSession();

    void releaseSession(JcrSession session);
}
