package com.enonic.wem.core.lifecycle;

public interface DisposableBean
{
    public void destroy()
        throws Exception;
}
