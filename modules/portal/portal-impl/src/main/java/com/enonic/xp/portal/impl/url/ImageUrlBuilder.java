package com.enonic.xp.portal.impl.url;

import java.nio.charset.StandardCharsets;

import com.google.common.collect.Multimap;
import com.google.common.hash.Hashing;
import com.google.common.io.Files;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentConstants;
import com.enonic.xp.content.Media;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.exception.NotFoundException;
import com.enonic.xp.portal.impl.ContentResolverResult;
import com.enonic.xp.portal.url.ImageUrlParams;
import com.enonic.xp.repository.RepositoryUtils;

import static com.google.common.base.Strings.isNullOrEmpty;

final class ImageUrlBuilder
    extends GenericEndpointUrlBuilder<ImageUrlParams>
{
    private boolean legacyImageServiceEnabled;

    ImageUrlBuilder()
    {
        super( "image" );
    }

    @Override
    protected void buildUrl( final StringBuilder url, final Multimap<String, String> params )
    {
        final boolean isSlashAPI = portalRequest.getRawPath().startsWith( "/api/" );
        final String projectName = RepositoryUtils.getContentRepoName( this.portalRequest.getRepositoryId() );
        final Branch branch = this.portalRequest.getBranch();

        if ( isSlashAPI )
        {
            params.putAll( this.params.getParams() );

            url.setLength( 0 );
            appendPart( url, "image" );
            appendPart( url, branch == ContentConstants.BRANCH_DRAFT ? projectName + ":" + branch.getValue() : projectName );
        }
        else
        {
            if ( legacyImageServiceEnabled )
            {
                super.buildUrl( url, params );
            }
            else
            {
                params.putAll( this.params.getParams() );

                final ContentResolverResult contentResolverResult =
                    new com.enonic.xp.portal.impl.ContentResolver( contentService ).resolve( portalRequest );

                if ( contentResolverResult.getNearestSite() != null )
                {
                    appendPart( url, projectName );
                    appendPart( url, branch.getValue() );
                    appendPart( url, contentResolverResult.getNearestSite().getPath().toString() );
                }
                else
                {
                    url.setLength( 0 );
                    appendPart( url, "site" );
                    appendPart( url, projectName );
                    appendPart( url, branch.getValue() );
                }

                appendPart( url, "_" );
                appendPart( url, "media" );
                appendPart( url, "image" );
                appendPart( url, branch == ContentConstants.BRANCH_DRAFT ? projectName + ":" + branch.getValue() : projectName );
            }
        }

        final Media media = resolveMedia();
        final String hash = resolveHash( media );
        final String name = resolveName( media );
        final String scale = resolveScale();

        appendPart( url, media.getId() + ":" + hash );
        appendPart( url, scale );
        appendPart( url, name );

        addParamIfNeeded( params, "quality", this.params.getQuality() );
        addParamIfNeeded( params, "background", this.params.getBackground() );
        addParamIfNeeded( params, "filter", this.params.getFilter() );
    }

    @Override
    protected String getBaseUrl()
    {
        return UrlContextHelper.getMediaServiceBaseUrl();
    }

    @Override
    protected String getTargetUriPrefix()
    {
        return "/api/media";
    }

    public void setLegacyImageServiceEnabled( final boolean legacyImageServiceEnabled )
    {
        this.legacyImageServiceEnabled = legacyImageServiceEnabled;
    }

    private void addParamIfNeeded( final Multimap<String, String> params, final String name, final Object value )
    {
        if ( value != null )
        {
            params.put( name, value.toString() );
        }
    }

    private Media resolveMedia()
    {
        final Content content;
        final ContentResolver contentResolver = new ContentResolver().portalRequest( this.portalRequest )
            .contentService( this.contentService )
            .id( this.params.getId() )
            .path( this.params.getPath() );
        content = contentResolver.resolve();
        if ( !( content instanceof Media ) )
        {
            throw new ContentInNotMediaException(
                String.format( "Content [%s:%s:%s] is not a Media", ContextAccessor.current().getRepositoryId(),
                               ContextAccessor.current().getBranch(), content.getId() ) );
        }
        return (Media) content;
    }

    private String resolveHash( final Media media )
    {
        String binaryKey = this.contentService.getBinaryKey( media.getId(), media.getMediaAttachment().getBinaryReference() );
        return Hashing.sha1().
            newHasher().
            putString( String.valueOf( binaryKey ), StandardCharsets.UTF_8 ).
            putString( String.valueOf( media.getFocalPoint() ), StandardCharsets.UTF_8 ).
            putString( String.valueOf( media.getCropping() ), StandardCharsets.UTF_8 ).
            putString( String.valueOf( media.getOrientation() ), StandardCharsets.UTF_8 ).
            hash().
            toString();
    }

    private String resolveName( final Content media )
    {
        final String name = media.getName().toString();

        if ( this.params.getFormat() != null )
        {
            final String extension = Files.getFileExtension( name );
            if ( isNullOrEmpty( extension ) || !this.params.getFormat().equals( extension ) )
            {
                return name + "." + this.params.getFormat();
            }
        }
        return name;
    }

    private String resolveScale()
    {
        if ( this.params.getScale() == null )
        {
            throw new IllegalArgumentException( "Missing mandatory parameter 'scale' for image URL" );
        }

        return this.params.getScale().replaceAll( "\\s", "" ).replaceAll( "[(,]", "-" ).replace( ")", "" );
    }

    private static class ContentInNotMediaException
        extends NotFoundException
    {
        ContentInNotMediaException( final String message )
        {
            super( message );
        }
    }
}
