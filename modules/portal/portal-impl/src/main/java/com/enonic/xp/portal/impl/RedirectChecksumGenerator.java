package com.enonic.xp.portal.impl;

import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.KeyGenerator;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.core.internal.HexCoder;
import com.enonic.xp.shared.SharedMap;
import com.enonic.xp.shared.SharedMapService;

@Component(service = RedirectChecksumGenerator.class)
public class RedirectChecksumGenerator
{
    private final SharedMap<String, Object> sharedMap;

    @Activate
    public RedirectChecksumGenerator( @Reference final SharedMapService sharedMapService )
    {
        this.sharedMap = sharedMapService.getSharedMap( "com.enonic.xp.internal.shared" );
    }

    public String generateChecksum( final String redirect )
    {
        final SecretKey key = new SecretKeySpec( (byte[]) sharedMap.modify( "redirectTicketKey", orig -> {

            if ( orig != null )
            {
                return orig;
            }

            final KeyGenerator keyGenerator;
            try
            {
                keyGenerator = KeyGenerator.getInstance( "HmacSHA1" );
            }
            catch ( NoSuchAlgorithmException e )
            {
                throw new IllegalStateException( e );
            }
            return keyGenerator.generateKey().getEncoded();

        } ), "HmacSHA1" );

        final Mac mac;
        try
        {
            mac = Mac.getInstance( key.getAlgorithm() );

            mac.init( key );
        }
        catch ( NoSuchAlgorithmException | InvalidKeyException e )
        {
            throw new RuntimeException( e );
        }

        return HexCoder.toHex( mac.doFinal( redirect.getBytes( StandardCharsets.UTF_8 ) ) );
    }
}
