package com.enonic.xp.web.session.impl;

public interface WebSessionStoreConfigService
{
    int getSavePeriodSeconds();

    int getGracePeriodSeconds();
}
