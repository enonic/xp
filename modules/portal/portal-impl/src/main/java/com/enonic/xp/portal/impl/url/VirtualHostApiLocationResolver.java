package com.enonic.xp.portal.impl.url;

import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;

import com.enonic.xp.descriptor.DescriptorKey;
import com.enonic.xp.web.vhost.VirtualHost;
import com.enonic.xp.web.vhost.VirtualHostHelper;

import static com.google.common.base.Strings.emptyToNull;

/**
 * Resolves where an API is exposed for the current request, from the context of the vhost
 * mapping that matched it. The mapping serving the request declares its API locations
 * explicitly - in bulk, and/or one by one:
 *
 * <pre>
 * mapping.example.context.apiBaseUrl = https://apis.example.com
 * mapping.example.context.apiBaseUrl.media:image = https://images.example.com
 * </pre>
 *
 * The bulk entry is the root of the full API set: the API descriptor is appended.
 * A single-API entry is the root of that API alone, used verbatim - nothing is appended.
 * The single-API entry wins over the bulk entry. Values are used verbatim, so they decide
 * the URL form themselves: another host, another path on the same host, or a relative root.
 */
final class VirtualHostApiLocationResolver
{
    private static final String API_BASE_URL_CONTEXT_KEY = "apiBaseUrl";

    private VirtualHostApiLocationResolver()
    {
    }

    static String resolve( final HttpServletRequest request, final DescriptorKey api )
    {
        if ( request == null )
        {
            return null;
        }

        final VirtualHost virtualHost = VirtualHostHelper.getVirtualHost( request );
        if ( virtualHost == null || virtualHost.getContext() == null )
        {
            return null;
        }

        final Map<String, String> context = virtualHost.getContext();

        final String single = emptyToNull( context.get( API_BASE_URL_CONTEXT_KEY + "." + api ) );
        if ( single != null )
        {
            return removeTrailingSlash( single );
        }

        final String bulk = emptyToNull( context.get( API_BASE_URL_CONTEXT_KEY ) );
        if ( bulk != null )
        {
            final StringBuilder url = new StringBuilder( removeTrailingSlash( bulk ) );
            UrlBuilderHelper.appendPart( url, api.toString() );
            return url.toString();
        }

        return null;
    }

    private static String removeTrailingSlash( final String value )
    {
        return value.endsWith( "/" ) && value.length() > 1 ? value.substring( 0, value.length() - 1 ) : value;
    }
}
