package com.enonic.xp.portal.impl.url;

import com.enonic.xp.descriptor.DescriptorKey;

/**
 * Raw (unescaped) segments of a media API path: {@code <context>/<id>[:<hash>]/[<scale>/]<name>}.
 */
record MediaPathParts(String context, String id, String hash, String scale, String name)
{
    /**
     * The context of a media that does not resolve: {@code _error} is no valid project name, so the media API answers
     * the URL with 404.
     */
    static final String UNRESOLVED_CONTEXT = "_error";

    private static final String UNRESOLVED_ID = "0";

    /**
     * @param id    id of the content, or {@code null} when the URL named it otherwise
     * @param scale scale segment, {@code null} for an attachment
     * @param name  name segment, or {@code null} for the id
     * @return the segments of a media URL the media API answers with 404, keeping the id of the content
     */
    static MediaPathParts unresolved( final String id, final String scale, final String name )
    {
        final String key = id != null ? id : UNRESOLVED_ID;
        final String fallbackName = id != null ? id : UNRESOLVED_CONTEXT;
        return new MediaPathParts( UNRESOLVED_CONTEXT, key, null, scale, name != null ? name : fallbackName );
    }

    /**
     * @return the path of the URL in the given media API, relative to its base URL
     */
    String path( final DescriptorKey api )
    {
        final StringBuilder path = new StringBuilder();
        UrlBuilderHelper.appendPart( path, api.toString() );
        UrlBuilderHelper.appendPart( path, context );
        UrlBuilderHelper.appendPart( path, idWithHash() );
        UrlBuilderHelper.appendPart( path, scale );
        UrlBuilderHelper.appendPart( path, name );
        return path.toString();
    }

    String idWithHash()
    {
        return hash != null ? id + ":" + hash : id;
    }
}
