package com.enonic.nodb.bench;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Mirrors {@code com.enonic.nodb.engine.store.PayloadStore}'s content-hash format exactly
 * ({@code "sha256:" + lowercase hex}). The bench harness needs to precompute this
 * client-side: {@code Version.nodeDataHash} must equal the hash the server will actually
 * store the inline node-data payload under, and there is no server-side cross-check tying
 * the two together (see {@code WriteService.write} — it validates hash-only refs exist, but
 * never checks that a version's declared hash matches an accompanying inline payload's
 * actual hash), so an honest bench has to get this right itself.
 */
final class Sha256
{
    private Sha256()
    {
    }

    static String hashOf( byte[] bytes )
    {
        try
        {
            MessageDigest digest = MessageDigest.getInstance( "SHA-256" );
            byte[] hash = digest.digest( bytes );
            StringBuilder sb = new StringBuilder( "sha256:" );
            for ( byte b : hash )
            {
                sb.append( String.format( "%02x", b ) );
            }
            return sb.toString();
        }
        catch ( NoSuchAlgorithmException e )
        {
            throw new IllegalStateException( e );
        }
    }
}
