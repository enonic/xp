package com.enonic.xp.image;

import java.io.IOException;

import org.jspecify.annotations.NullMarked;

import com.google.common.io.ByteSource;

import com.enonic.xp.style.ImageStyle;

/**
 * Resolves predefined image styles and reads or generates image renditions.
 */
@NullMarked
public interface ImageService
{
    /**
     * Resolves and validates a predefined image style.
     *
     * @param key fully qualified style key in {@code application:name} form
     * @return the resolved style
     * @throws com.enonic.xp.style.ImageStyleNotFoundException if the style does not exist
     * @throws IllegalArgumentException if the key or processing settings are invalid
     */
    ImageStyle getStyle( String key );

    /**
     * Reads an existing rendition or generates and caches one when permitted by the request.
     * WebP and AVIF output requires a predefined style. A cache-only request never reads
     * source bytes to discover a checksum or generates a rendition on a cache miss.
     * <p>
     * This API does not authenticate URL fingerprints. Callers exposing image URLs must
     * validate their signatures and set {@link ReadImageParams.Builder#cacheOnly(boolean)}
     * accordingly before invoking this method.
     *
     * @param readImageParams source, processing settings and cache policy
     * @return the encoded image bytes
     * @throws IOException if reading, processing or caching the image fails
     * @throws com.enonic.xp.style.ImageStyleNotFoundException if the requested style does not exist
     * @throws IllegalArgumentException if parameters are invalid, expected style settings have changed,
     *     or a cache-only request cannot be satisfied
     * @throws com.enonic.xp.exception.ThrottlingException if processing admission limits are exceeded
     */
    ByteSource readImage( ReadImageParams readImageParams )
        throws IOException;
}
