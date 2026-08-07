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
 * portal.apiBaseUrl                       root of the full API set - the API descriptor is appended
 * portal.apiBaseUrl.&lt;application&gt;:&lt;api&gt;   root of that API alone, used verbatim
 * </pre>
 *
 * The attributes are consulted in order of how specifically they name the API: the one naming
 * the API itself first, the bulk one last - with the {@code media.defaultBaseUrl} configuration,
 * which names the media APIs, ranking between them. Values are used verbatim, so they decide
 * the URL form themselves: another host, another path on the same host, or a relative root.
 * <p>
 * Attributes of the vhost mapping that matched the request are copied into the context, so a
 * vhost mapping declares locations per host - the colon of an API descriptor has to be escaped
 * in the configuration file, as any key of a properties file:
 *
 * <pre>
 * mapping.example.context.portal.apiBaseUrl = https://apis.example.com
 * mapping.example.context.portal.apiBaseUrl.media\:image = https://images.example.com
 * </pre>
 *
 * A vhost mapping is only one way to set the attributes: anything that establishes them,
 * including a controller or a task running in its own context, declares locations just as well.
 */
final class ApiLocationResolver
{
    private static final String API_BASE_URL_ATTRIBUTE = "portal.apiBaseUrl";

    private ApiLocationResolver()
    {
    }

    /**
     * @return the root declared for this API alone, used verbatim, or {@code null}
     */
    static String resolveSingle( final DescriptorKey api )
    {
        final String single = attribute( ContextAccessor.current(), API_BASE_URL_ATTRIBUTE + "." + api );
        return single == null ? null : removeTrailingSlash( single );
    }

    /**
     * @return the root declared for any API, with the descriptor appended, or {@code null}
     */
    static String resolveBulk( final DescriptorKey api )
    {
        final String bulk = attribute( ContextAccessor.current(), API_BASE_URL_ATTRIBUTE );
        if ( bulk == null )
        {
            return null;
        }

        final StringBuilder url = new StringBuilder( removeTrailingSlash( bulk ) );
        UrlBuilderHelper.appendPart( url, api.toString() );
        return url.toString();
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
