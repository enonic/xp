package com.enonic.xp.portal.url;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

/**
 * Parts of a page URL, for building the full URL from segments:
 * {@code url = baseUrl + path + queryString}.
 *
 * @param baseUrl     the Base URL configured for the site or project the URL belongs to, without a trailing slash;
 *                    {@code null} when none is configured, and the caller supplies the origin the site is served from
 * @param path        URL-escaped path of the content relative to the site or project the URL belongs to, with a
 *                    leading slash; empty when the content is that site itself
 * @param queryString URL-escaped query string prefixed with {@code ?}; empty when there are no parameters
 */
@NullMarked
public record PageUrlParts(@Nullable String baseUrl, String path, String queryString)
{
}
