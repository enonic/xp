package com.enonic.wem.core.lifecycle;

public interface InitializingBean
{
    public void afterPropertiesSet()
        throws Exception;
}
