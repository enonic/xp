package com.enonic.xp.web.session.impl.ignite;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.io.UncheckedIOException;

import org.eclipse.jetty.server.session.SessionData;
import org.eclipse.jetty.util.ClassLoadingObjectInputStream;

/**
 * Jetty itself does magic on sessionData serialization/deserialization
 * Class exists to prevent Ignate and Jetty to conflict which classloader to use.
 */
public final class IgniteSessionData
    implements Serializable
{
    private final String id;

    private final String contextPath;

    private final String vhost;

    private final String lastNode;

    private final long expiry;

    private final long created;

    private final long cookieSet;

    private final long accessed;

    private final long lastAccessed;

    private final long maxInactiveMs;

    private final long lastSaved;

    private final byte[] attributesData;

    public IgniteSessionData( final SessionData data )
    {
        this.id = data.getId();
        this.contextPath = data.getContextPath();
        this.vhost = data.getVhost();
        this.lastNode = data.getLastNode();
        this.expiry = data.getExpiry();
        this.created = data.getCreated();
        this.cookieSet = data.getCookieSet();
        this.accessed = data.getAccessed();
        this.lastAccessed = data.getLastAccessed();
        this.maxInactiveMs = data.getMaxInactiveMs();
        this.lastSaved = data.getLastSaved();
        try (final ByteArrayOutputStream baos = new ByteArrayOutputStream(); final ObjectOutputStream oos = new ObjectOutputStream( baos ))
        {
            SessionData.serializeAttributes( data, oos );
            attributesData = baos.toByteArray();
        }
        catch ( IOException e )
        {
            throw new UncheckedIOException( e );
        }
    }

    public SessionData toSessionData()
        throws ClassNotFoundException
    {
        SessionData data = new SessionData( id, contextPath, vhost, created, accessed, lastAccessed, maxInactiveMs );
        data.setCookieSet( cookieSet );
        data.setLastNode( lastNode );
        data.setLastSaved( lastSaved );
        data.setExpiry( expiry );
        data.setContextPath( contextPath );
        data.setVhost( vhost );

        try (final InputStream is = new ByteArrayInputStream(
            attributesData ); final ClassLoadingObjectInputStream ois = new ClassLoadingObjectInputStream( is ))
        {
            SessionData.deserializeAttributes( data, ois );
        }
        catch ( IOException e )
        {
            throw new UncheckedIOException( e );
        }

        return data;
    }

    private static final long serialVersionUID = 1L;
}
