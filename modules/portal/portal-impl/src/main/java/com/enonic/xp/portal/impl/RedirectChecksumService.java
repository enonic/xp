package com.enonic.xp.portal.impl;

import org.jspecify.annotations.NullMarked;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * Generates and verifies redirect tickets using a dedicated signing key.
 */
@NullMarked
@Component(service = RedirectChecksumService.class)
public class RedirectChecksumService
{
    private final HmacService hmacService;

    /**
     * Creates a redirect ticket service.
     *
     * @param hmacService service providing named signing keys
     */
    @Activate
    public RedirectChecksumService( @Reference final HmacService hmacService )
    {
        this.hmacService = hmacService;
    }

    /**
     * Generates a ticket for a redirect target.
     *
     * @param redirect redirect target to authenticate
     * @return a 40-character lowercase hexadecimal ticket
     * @throws IllegalStateException if key derivation or signing is unavailable or fails
     */
    public String generateChecksum( final String redirect )
    {
        return hmacService.generateChecksum( "redirect-checksum-v1", redirect );
    }

    /**
     * Verifies that a ticket authenticates the redirect target.
     *
     * @param redirect redirect target
     * @param checksum supplied ticket
     * @return whether the ticket matches the target
     * @throws IllegalStateException if key derivation or signing is unavailable or fails
     */
    public boolean verifyChecksum( final String redirect, final String checksum )
    {
        return hmacService.verifyChecksum( "redirect-checksum-v1", redirect, checksum );
    }
}
