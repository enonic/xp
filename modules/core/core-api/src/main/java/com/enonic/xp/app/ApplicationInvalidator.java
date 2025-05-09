package com.enonic.xp.app;

public interface ApplicationInvalidator
{
    void invalidate( ApplicationKey key, ApplicationInvalidationLevel level );
}
