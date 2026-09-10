package com.enonic.xp.portal.url;

import org.jspecify.annotations.NullMarked;

/**
 * Parts of a page URL, for building the full URL from segments:
 * {@code url = <baseUrl> + path + queryString}.
 *
 * @param path        URL-escaped path of the content relative to the site or project the URL belongs to, with a
 *                    leading slash; empty when the content is that site itself
 * @param queryString URL-escaped query string prefixed with {@code ?}; empty when there are no parameters
 */
@NullMarked
public record PageUrlParts(String path, String queryString)
{
}
