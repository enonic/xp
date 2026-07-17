package com.enonic.nodb.server.auth;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

/**
 * The "trivial dev issuer" keypair (DESIGN.md §7.2/§9 Phase 1: "trivial local issuer", no
 * control plane). RS256: an asymmetric keypair where {@link NodbTokenTool} (playing the
 * control-plane role) holds the private key and mints tokens, and {@link
 * com.enonic.nodb.server.NodbServer} holds only the public key to verify them offline —
 * matching the real control-plane split this stands in for. Keys are plain PEM
 * (PKCS8/X509, unencrypted) under a configurable directory so both processes agree on the
 * same keypair across restarts; generated on first use if absent.
 */
public final class DevKeys
{
    private static final String PRIVATE_FILE = "nodb-dev-private.pem";

    private static final String PUBLIC_FILE = "nodb-dev-public.pem";

    private DevKeys()
    {
    }

    /** Loads the keypair from {@code dir}, generating and persisting a new RSA-2048 pair if none exists yet. */
    public static KeyPair loadOrGenerate( Path dir )
    {
        Path privatePath = dir.resolve( PRIVATE_FILE );
        Path publicPath = dir.resolve( PUBLIC_FILE );

        if ( Files.exists( privatePath ) && Files.exists( publicPath ) )
        {
            return new KeyPair( readPublic( publicPath ), readPrivate( privatePath ) );
        }

        try
        {
            Files.createDirectories( dir );
            KeyPairGenerator generator = KeyPairGenerator.getInstance( "RSA" );
            generator.initialize( 2048 );
            KeyPair keyPair = generator.generateKeyPair();

            writePem( privatePath, "PRIVATE KEY", keyPair.getPrivate().getEncoded() );
            writePem( publicPath, "PUBLIC KEY", keyPair.getPublic().getEncoded() );
            return keyPair;
        }
        catch ( NoSuchAlgorithmException | IOException e )
        {
            throw new IllegalStateException( "Failed to generate dev issuer keypair in " + dir, e );
        }
    }

    /** Loads only the public key — all {@link com.enonic.nodb.server.NodbServer} needs to verify tokens. */
    public static RSAPublicKey loadOrGeneratePublicKey( Path dir )
    {
        return (RSAPublicKey) loadOrGenerate( dir ).getPublic();
    }

    private static RSAPublicKey readPublic( Path path )
    {
        try
        {
            byte[] der = decodePem( Files.readString( path, StandardCharsets.US_ASCII ) );
            return (RSAPublicKey) KeyFactory.getInstance( "RSA" ).generatePublic( new X509EncodedKeySpec( der ) );
        }
        catch ( IOException | NoSuchAlgorithmException | InvalidKeySpecException e )
        {
            throw new UncheckedIOException( new IOException( "Failed to read public key from " + path, e ) );
        }
    }

    private static RSAPrivateKey readPrivate( Path path )
    {
        try
        {
            byte[] der = decodePem( Files.readString( path, StandardCharsets.US_ASCII ) );
            return (RSAPrivateKey) KeyFactory.getInstance( "RSA" ).generatePrivate( new PKCS8EncodedKeySpec( der ) );
        }
        catch ( IOException | NoSuchAlgorithmException | InvalidKeySpecException e )
        {
            throw new UncheckedIOException( new IOException( "Failed to read private key from " + path, e ) );
        }
    }

    private static void writePem( Path path, String label, byte[] der )
        throws IOException
    {
        String base64 = Base64.getMimeEncoder( 64, "\n".getBytes( StandardCharsets.US_ASCII ) ).encodeToString( der );
        String pem = "-----BEGIN " + label + "-----\n" + base64 + "\n-----END " + label + "-----\n";
        Files.writeString( path, pem, StandardCharsets.US_ASCII );
    }

    private static byte[] decodePem( String pem )
    {
        String base64 = pem.replaceAll( "-----BEGIN [^-]+-----", "" ).replaceAll( "-----END [^-]+-----", "" ).replaceAll( "\\s", "" );
        return Base64.getDecoder().decode( base64 );
    }
}
