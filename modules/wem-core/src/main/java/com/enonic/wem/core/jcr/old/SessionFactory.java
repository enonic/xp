package com.enonic.wem.core.jcr.old;

public interface SessionFactory
{
    JcrSession getSession();

    void releaseSession(JcrSession session);
}
