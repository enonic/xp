package com.enonic.xp.app;

public interface ApplicationInvalidator
{
    @Deprecated
    void invalidate( ApplicationKey key );

    void invalidate( ApplicationKey key, ApplicationInvalidationLevel level );
}
