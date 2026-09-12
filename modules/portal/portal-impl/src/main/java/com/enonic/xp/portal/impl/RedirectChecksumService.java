package com.enonic.xp.portal.impl;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

@Component(service = RedirectChecksumService.class)
public class RedirectChecksumService
{
    private final HmacService hmacService;

    @Activate
    public RedirectChecksumService( @Reference final HmacService hmacService )
    {
        this.hmacService = hmacService;
    }

    public String generateChecksum( final String redirect )
    {
        return hmacService.generateChecksum( redirect );
    }

    public boolean verifyChecksum( final String redirect, final String checksum )
    {
        return hmacService.verifyChecksum( redirect, checksum );
    }
}
