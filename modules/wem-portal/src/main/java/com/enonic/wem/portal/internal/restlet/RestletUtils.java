package com.enonic.wem.portal.internal.restlet;

import org.restlet.Message;
import org.restlet.data.Header;
import org.restlet.engine.header.HeaderConstants;
import org.restlet.util.Series;

public final class RestletUtils
{
    public static Series<Header> getHeaders( final Message message, final boolean createIfNeeded )
    {
        final Series<Header> headers = message.getHeaders();
        if ( headers != null )
        {
            return headers;
        }

        if ( !createIfNeeded )
        {
            return null;
        }

        final Series<Header> newHeaders = new Series<>( Header.class );
        message.getAttributes().put( HeaderConstants.ATTRIBUTE_HEADERS, newHeaders );
        return newHeaders;
    }
}
