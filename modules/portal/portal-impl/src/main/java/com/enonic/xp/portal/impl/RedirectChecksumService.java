package com.enonic.xp.portal.impl;

import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Security;
import java.util.Arrays;
import java.util.Base64;
import java.util.HexFormat;
import java.util.function.Supplier;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.google.common.base.Suppliers;

import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeService;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.SecurityService;
import com.enonic.xp.security.SystemConstants;
import com.enonic.xp.security.auth.AuthenticationInfo;

@Component(service = RedirectChecksumService.class)
public class RedirectChecksumService
{
    private static final NodePath GENERIC_KEY_PATH = NodePath.create().addElement( "keys" ).addElement( "generic-hmac-sha512" ).build();

    private final NodeService nodeService;

    /**
     * Used to make sure the SecurityInitializer is run before this component is activated.
     */
    @SuppressWarnings("unused")
    @Reference
    private SecurityService securityService;

    private final Supplier<SecretKey> keySupplier = Suppliers.memoize( this::doGetKey );

    @Activate
    public RedirectChecksumService( @Reference final NodeService nodeService )
    {
        this.nodeService = nodeService;
    }

    private SecretKey doGetKey()
    {
        final String storedKey =
            createSystemContext().callWith( () -> nodeService.getByPath( GENERIC_KEY_PATH ) ).data().getString( "key" );
        return new SecretKeySpec( Base64.getDecoder().decode( storedKey ), "HmacSHA512" );
    }

    public String generateChecksum( final String redirect )
    {
        final SecretKey key = keySupplier.get();
        final Mac mac;
        try
        {
            mac = Mac.getInstance( key.getAlgorithm() );
            mac.init( key );
        }
        catch ( NoSuchAlgorithmException | InvalidKeyException e )
        {
            throw new IllegalStateException( e );
        }

        return HexFormat.of().formatHex( mac.doFinal( redirect.getBytes( StandardCharsets.UTF_8 ) ), 0,20  );
    }

    public boolean verifyChecksum( final String redirect, final String checksum )
    {
        final String expectedTicket = generateChecksum( redirect );
        return MessageDigest.isEqual( expectedTicket.getBytes( StandardCharsets.ISO_8859_1 ),
                                      checksum.getBytes( StandardCharsets.ISO_8859_1 ) );
    }

    private static Context createSystemContext()
    {
        return ContextBuilder.from( ContextAccessor.current() )
            .authInfo( AuthenticationInfo.copyOf( ContextAccessor.current().getAuthInfo() ).principals( RoleKeys.ADMIN ).build() )
            .repositoryId( SystemConstants.SYSTEM_REPO_ID )
            .branch( SystemConstants.BRANCH_SYSTEM )
            .build();
    }

    static void main()
    {
        System.out.println(Security.getAlgorithms( "MessageDigest"));
    }
}
