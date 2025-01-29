package com.enonic.xp.portal.impl.url2;

import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.portal.url.AttachmentMediaUrlParams;
import com.enonic.xp.portal.url.ImageMediaUrlParams;

import static com.enonic.xp.portal.impl.url.UrlBuilderHelper.appendPart;

public class Test
{
    public String generateImagePath( final ImageMediaUrlParams params )
    {
        final StringBuilder url = new StringBuilder();

        url.append( "media" );
        url.append( "image" );
        url.append( "project:draft" );
        url.append( "id:fingerprint" );
        url.append( "scale" );
        url.append( "name" );
        url.append( "?queryParams" );

        return url.toString();
    }

    public String generateAttachmentPath( final AttachmentMediaUrlParams params )
    {
        final StringBuilder url = new StringBuilder();

        url.append( "media" );
        url.append( "attachment" );
        url.append( "project:draft" );
        url.append( "id:fingerprint" );
        url.append( "name" );
        url.append( "?download&queryParams" );

        return url.toString();
    }

    public interface ContextAttributeAccessor
    {
        <T> T getAttribute( String key );
    }


    public String imageUrl( final ImageMediaUrlParams params )
    {
        final String path = generateImagePath( params );
        final String rewrittenUri = rewritePath( path );
        final String baseUrl = resolveBaseUrl( params.getWebRequest(), params.getUrlType() );

        final StringBuilder url = new StringBuilder();

        appendPart( url, baseUrl );
        appendPart( url, rewrittenUri );

        return url.toString();
    }

    public String rewritePath( final String path )
    {
        return path;
    }

    public String resolveBaseUrl( final Object policy, final String urlType )
    {
        return null;
    }

    public static void main( String[] args )
    {
        final Context context = ContextBuilder.copyOf( ContextAccessor.current() )
            .attribute( "pathGenerator.project", "project" )
            .attribute( "pathGenerator.branch", "project" )
            .attribute( "pathGenerator.contentKey", "fingerprint" )
            .build();

        final String path = context.callWith( () -> {
            Test generator = new Test();
            return generator.generateImagePath( ImageMediaUrlParams.create().build() );
        } );

        System.out.println( path );
    }

}
