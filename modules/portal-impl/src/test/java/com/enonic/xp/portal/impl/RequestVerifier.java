package com.enonic.xp.portal.impl;

import com.enonic.xp.portal.PortalRequest;

public interface RequestVerifier
{
    void verify( PortalRequest request )
        throws Exception;
}
