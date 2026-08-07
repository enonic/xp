package com.enonic.xp.portal.impl.url;

import java.util.Objects;

import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.descriptor.DescriptorKey;

import static com.google.common.base.Strings.emptyToNull;

/**
 * Resolves where an API is exposed for the current context, from context attributes:
 *
 * <pre>
 * apiBaseUrl               root of the full API set - the API descriptor is appended
 * apiBaseUrl.&lt;application&gt;:&lt;api&gt;   root of that API alone, used verbatim
 * </pre>
 *
 * The single-API attribute wins over the bulk one. Values are used verbatim, so they decide
 * the URL form themselves: another host, another path on the same host, or a relative root.
 * <p>
 * Attributes of the vhost mapping that matched the request are copied into the context, so
 * {@code mapping.<name>.context.apiBaseUrl} declares locations per host - but that is only
 * one way to set them: anything that establishes the attributes, including a controller or
 * a task running in its own context, declares them just as well.
 */
final class ApiLocationResolver
{
    private static final String API_BASE_URL_ATTRIBUTE = "apiBaseUrl";

    private ApiLocationResolver()
    {
    }

    static String resolve( final DescriptorKey api )
    {
        final Context context = ContextAccessor.current();

        final String single = attribute( context, API_BASE_URL_ATTRIBUTE + "." + api );
        if ( single != null )
        {
            return removeTrailingSlash( single );
        }

        final String bulk = attribute( context, API_BASE_URL_ATTRIBUTE );
        if ( bulk != null )
        {
            final StringBuilder url = new StringBuilder( removeTrailingSlash( bulk ) );
            UrlBuilderHelper.appendPart( url, api.toString() );
            return url.toString();
        }

        return null;
    }

    private static String attribute( final Context context, final String key )
    {
        final Object value = context.getAttribute( key );
        return value == null ? null : emptyToNull( Objects.toString( value ) );
    }

    private static String removeTrailingSlash( final String value )
    {
        return value.endsWith( "/" ) && value.length() > 1 ? value.substring( 0, value.length() - 1 ) : value;
    }
}
