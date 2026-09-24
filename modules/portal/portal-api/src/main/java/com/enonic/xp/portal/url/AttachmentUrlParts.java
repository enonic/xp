package com.enonic.xp.portal.url;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

/**
 * Parts of an attachment URL, for building the full URL from segments:
 * {@code url = apiUrl + path + queryString}. All values are URL-escaped as they appear in the URL.
 *
 * @param apiUrl      the configured {@code media.defaultBaseUrl}, without a trailing slash; {@code null} when none is
 *                    configured, and the caller supplies where the media API is served. Site mounts are not considered
 * @param path        the full media API path with a leading slash: {@code /media:attachment/<context>/<id>:<fingerprint>/<name>}
 * @param queryString URL-escaped query string prefixed with {@code ?}; empty when there are no parameters
 * @param context     project context segment: {@code <project>} on the master branch, {@code <project>:<branch>} otherwise
 * @param id          content id
 * @param fingerprint media fingerprint; joined with the id as {@code <id>:<fingerprint>} in the path
 * @param name        URL-escaped attachment file name segment
 */
@NullMarked
public record AttachmentUrlParts(@Nullable String apiUrl, String path, String queryString, String context, String id,
                                 @Nullable String fingerprint, String name)
{
}
