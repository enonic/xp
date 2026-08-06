package com.enonic.xp.portal.url;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

/**
 * Parts of an image URL, for building the full URL from segments:
 * {@code url = <baseUrl> + path + queryString}. All values are URL-escaped as they appear in the URL.
 *
 * @param path        the full media API path with a leading slash: {@code /media:image/<context>/<id>:<fingerprint>/<scale>/<name>}
 * @param queryString URL-escaped query string prefixed with {@code ?}; empty when there are no parameters
 * @param context     project context segment: {@code <project>} on the master branch, {@code <project>:<branch>} otherwise
 * @param id          content id
 * @param fingerprint media fingerprint; joined with the id as {@code <id>:<fingerprint>} in the path
 * @param scale       processed scale segment (for example {@code max-300})
 * @param name        file name segment, with the requested format extension applied
 */
@NullMarked
public record ImageUrlParts(String path, String queryString, String context, String id, @Nullable String fingerprint, String scale,
                            String name)
{
}
