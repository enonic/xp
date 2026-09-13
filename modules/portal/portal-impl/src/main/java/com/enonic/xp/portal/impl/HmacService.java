package com.enonic.xp.portal.impl;

import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.util.Base64;
import java.util.HexFormat;
import java.util.function.Supplier;
import java.util.regex.Pattern;

import javax.crypto.KDF;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.HKDFParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
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

/**
 * Generates and verifies checksums with the generic secret or a named derived key.
 */
@NullMarked
@Component(service = HmacService.class)
public class HmacService
{
    private static final NodePath GENERIC_KEY_PATH = NodePath.create().addElement( "keys" ).addElement( "generic-hmac-sha512" ).build();

    private static final byte[] DERIVATION_SALT = "enonic:hmac:key-derivation:v1".getBytes( StandardCharsets.US_ASCII );

    private static final Pattern KEY_NAME = Pattern.compile( "[a-zA-Z0-9][a-zA-Z0-9._:/-]*" );

    private final NodeService nodeService;

    // Ensure the security initializer runs before this component is activated.
    @SuppressWarnings("unused")
    @Reference
    private @Nullable SecurityService securityService;

    private final Supplier<SecretKey> keySupplier = Suppliers.memoize( this::doGetKey );

    /**
     * Creates a service backed by the persisted generic HMAC secret.
     *
     * @param nodeService service providing the stored secret
     */
    @Activate
    public HmacService( @Reference final NodeService nodeService )
    {
        this.nodeService = nodeService;
    }

    private SecretKey doGetKey()
    {
        final String storedKey =
            createSystemContext().callWith( () -> nodeService.getByPath( GENERIC_KEY_PATH ) ).data().getString( "key" );
        return new SecretKeySpec( Base64.getDecoder().decode( storedKey ), "HmacSHA512" );
    }

    /**
     * Derives a 512-bit HmacSHA512 key for a named purpose. The same name and generic
     * secret produce the same key. Names are case-sensitive and distinguish independent keys.
     *
     * @param name purpose name beginning with an ASCII letter or digit, followed by ASCII letters,
     *     digits, dots, underscores, colons, slashes or hyphens
     * @return the derived secret key
     * @throws IllegalArgumentException if the purpose name is invalid
     * @throws IllegalStateException if key derivation is unavailable or fails
     */
    public SecretKey deriveKey( final String name )
    {
        if ( !KEY_NAME.matcher( name ).matches() )
        {
            throw new IllegalArgumentException( "Invalid HMAC key name" );
        }
        try
        {
            return KDF.getInstance( "HKDF-SHA512" ).deriveKey( "HmacSHA512",
                HKDFParameterSpec.ofExtract().addIKM( keySupplier.get() ).addSalt( DERIVATION_SALT )
                    .thenExpand( name.getBytes( StandardCharsets.US_ASCII ), 64 ) );
        }
        catch ( GeneralSecurityException e )
        {
            throw new IllegalStateException( "Unable to derive HMAC key", e );
        }
    }

    /**
     * Generates a checksum using the generic secret.
     *
     * @param value value to authenticate
     * @return a 40-character lowercase hexadecimal checksum
     * @throws IllegalStateException if checksum generation is unavailable or fails
     */
    public String generateChecksum( final String value )
    {
        return checksum( keySupplier.get(), value );
    }

    /**
     * Generates a checksum using the key derived for a named purpose.
     *
     * @param name purpose name accepted by {@link #deriveKey(String)}
     * @param value value to authenticate
     * @return a 40-character lowercase hexadecimal checksum
     * @throws IllegalArgumentException if the purpose name is invalid
     * @throws IllegalStateException if derivation or checksum generation is unavailable or fails
     */
    public String generateChecksum( final String name, final String value )
    {
        return checksum( deriveKey( name ), value );
    }

    /**
     * Verifies a checksum made with the generic secret.
     *
     * @param value authenticated value
     * @param checksum supplied checksum
     * @return whether the supplied checksum matches
     * @throws IllegalStateException if checksum generation is unavailable or fails
     */
    public boolean verifyChecksum( final String value, final String checksum )
    {
        return matches( generateChecksum( value ), checksum );
    }

    /**
     * Verifies a checksum made with the key derived for a named purpose.
     *
     * @param name purpose name accepted by {@link #deriveKey(String)}
     * @param value authenticated value
     * @param checksum supplied checksum
     * @return whether the supplied checksum matches for this purpose
     * @throws IllegalArgumentException if the purpose name is invalid
     * @throws IllegalStateException if derivation or checksum generation is unavailable or fails
     */
    public boolean verifyChecksum( final String name, final String value, final String checksum )
    {
        return matches( generateChecksum( name, value ), checksum );
    }

    private static String checksum( final SecretKey key, final String value )
    {
        try
        {
            final Mac mac = Mac.getInstance( key.getAlgorithm() );
            mac.init( key );
            return HexFormat.of().formatHex( mac.doFinal( value.getBytes( StandardCharsets.UTF_8 ) ), 0, 20 );
        }
        catch ( GeneralSecurityException e )
        {
            throw new IllegalStateException( "Unable to generate HMAC checksum", e );
        }
    }

    private static boolean matches( final String expected, final String supplied )
    {
        return MessageDigest.isEqual( expected.getBytes( StandardCharsets.UTF_8 ), supplied.getBytes( StandardCharsets.UTF_8 ) );
    }

    private static Context createSystemContext()
    {
        return ContextBuilder.from( ContextAccessor.current() )
            .authInfo( AuthenticationInfo.copyOf( ContextAccessor.current().getAuthInfo() ).principals( RoleKeys.ADMIN ).build() )
            .repositoryId( SystemConstants.SYSTEM_REPO_ID )
            .branch( SystemConstants.BRANCH_SYSTEM )
            .build();
    }
}
