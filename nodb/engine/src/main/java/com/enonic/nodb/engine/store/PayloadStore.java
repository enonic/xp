package com.enonic.nodb.engine.store;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Content-addressed node payloads (node data / index config / ACL segments). Tenant-shared
 * pool, unpartitioned by design (schema.sql §"payload"): dedup is cross-repo within a
 * tenant. Every method runs on a connection already inside {@code Tx.inTenantTx} (role +
 * search_path already set), and uses unqualified table names accordingly.
 */
public final class PayloadStore
{
    private PayloadStore()
    {
    }

    /** Stores bytes if unseen (dedup via {@code ON CONFLICT DO NOTHING}); always returns the content hash key. */
    public static String putPayload( Connection connection, byte[] bytes )
        throws SQLException
    {
        String hash = sha256Key( bytes );
        try (PreparedStatement statement =
                 connection.prepareStatement( "INSERT INTO payload (hash, bytes, byte_size) VALUES (?, ?, ?) ON CONFLICT (hash) DO NOTHING" ))
        {
            statement.setString( 1, hash );
            statement.setBytes( 2, bytes );
            statement.setLong( 3, bytes.length );
            statement.executeUpdate();
        }
        return hash;
    }

    public static byte[] getPayload( Connection connection, String hash )
        throws SQLException
    {
        try (PreparedStatement statement = connection.prepareStatement( "SELECT bytes FROM payload WHERE hash = ?" ))
        {
            statement.setString( 1, hash );
            try (ResultSet resultSet = statement.executeQuery())
            {
                return resultSet.next() ? resultSet.getBytes( 1 ) : null;
            }
        }
    }

    public static boolean hasPayload( Connection connection, String hash )
        throws SQLException
    {
        try (PreparedStatement statement = connection.prepareStatement( "SELECT 1 FROM payload WHERE hash = ?" ))
        {
            statement.setString( 1, hash );
            try (ResultSet resultSet = statement.executeQuery())
            {
                return resultSet.next();
            }
        }
    }

    private static String sha256Key( byte[] bytes )
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
            // SHA-256 is a mandatory JCE algorithm on every JVM; this can't happen.
            throw new IllegalStateException( e );
        }
    }
}
