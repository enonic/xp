package com.enonic.wem.core.jcr.provider;

import javax.jcr.Session;

public interface JcrSessionProvider
{
    public Session login()
        throws Exception;

    public Session loginAdmin()
        throws Exception;
}
