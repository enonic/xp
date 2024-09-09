package com.enonic.xp.portal.impl.url;

import java.util.concurrent.Callable;

import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Multimap;
import com.google.common.io.Files;

import com.enonic.xp.attachment.Attachment;
import com.enonic.xp.branch.Branch;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentConstants;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.ContentService;
import com.enonic.xp.content.Media;
import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.portal.url.ImageUrlParams;
import com.enonic.xp.project.ProjectName;
import com.enonic.xp.project.ProjectService;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.auth.AuthenticationInfo;

import static com.enonic.xp.portal.impl.url.UrlBuilderHelper.appendParams;
import static com.enonic.xp.portal.impl.url.UrlBuilderHelper.appendPart;
import static com.google.common.base.Strings.isNullOrEmpty;
import static com.google.common.base.Strings.nullToEmpty;

public class ContextImageUrlBuilder
{

    private final ContentService contentService;

    private final ProjectService projectService;

    private ImageUrlParams params;

    public ContextImageUrlBuilder( ContentService contentService, ProjectService projectService, final ImageUrlParams params )
    {
        this.contentService = contentService;
        this.projectService = projectService;
        this.params = params;
    }

    public String build()
    {
        final StringBuilder str = new StringBuilder();
        final Multimap<String, String> params = LinkedListMultimap.create();
        params.putAll( this.params.getParams() );
        buildUrl( str );
        appendParams( str, params.entries() );
        return str.toString();
    }

    private void buildUrl( final StringBuilder url )
    {
        final Context context = ContextBuilder.copyOf( ContextAccessor.current() ).build();

        if ( context.getRepositoryId() == null )
        {
            throw new IllegalArgumentException( "RepositoryId can not be null" );
        }
        if ( context.getBranch() == null )
        {
            throw new IllegalArgumentException( "Branch can not be null" );
        }

        final String contentKey = (String) context.getAttribute( "contentKey" );
        if ( nullToEmpty( contentKey ).isBlank() || nullToEmpty( params.getId() ).isBlank() || nullToEmpty( params.getPath() ).isBlank() )
        {
            throw new IllegalArgumentException( "Id and path can not be null" );
        }

        if ( params.getScale() == null )
        {
            throw new IllegalArgumentException( "Missing mandatory parameter 'scale' for image URL" );
        }

        final Content media = resolveContent( contentKey );
        if ( !( media instanceof Media ) )
        {
            throw new IllegalArgumentException( "Content is not media" );
        }

        // TODO resolve baseURL or use /project/branch/_/media/image/project:branch/contentId:hash/scale/name.format

        final ProjectName projectName = ProjectName.from( context.getRepositoryId() );
        final Branch branch = context.getBranch();

        final String hash = resolveHash( (Media) media );
        final String name = resolveName( media );
        final String scale = resolveScale();

        appendPart( url, "_" );
        appendPart( url, "media" );
        appendPart( url, "image" );
        appendPart( url, branch == ContentConstants.BRANCH_DRAFT ? projectName + ":draft" : projectName.toString() );
        appendPart( url, media.getId() + ( hash != null ? ":" + hash : "" ) );
        appendPart( url, scale );
        appendPart( url, name );
    }

    private Content resolveContent( final String contentKey )
    {
        return callAsContentAdmin( () -> {
            if ( !nullToEmpty( contentKey ).isBlank() )
            {
                if ( contentKey.startsWith( "/" ) )
                {
                    return contentService.getByPath( ContentPath.from( contentKey ) );
                }
                else
                {
                    return contentService.getById( ContentId.from( contentKey ) );
                }
            }
            else if ( !nullToEmpty( params.getId() ).isBlank() )
            {
                return contentService.getById( ContentId.from( params.getId() ) );
            }
            else
            {
                return contentService.getByPath( ContentPath.from( params.getPath() ) );
            }
        } );
    }

    private String resolveName( final Content media )
    {
        final String name = media.getName().toString();

        if ( params.getFormat() != null )
        {
            final String extension = Files.getFileExtension( name );
            if ( isNullOrEmpty( extension ) || !this.params.getFormat().equals( extension ) )
            {
                return name + "." + this.params.getFormat();
            }
        }
        return name;
    }

    private String resolveHash( final Media media )
    {
        final Attachment attachment = media.getMediaAttachment();
        return attachment.getSha512() != null ? attachment.getSha512().substring( 0, 32 ) : null;
    }

    private String resolveScale()
    {
        return params.getScale().replaceAll( "\\s", "" ).replaceAll( "[(,]", "-" ).replace( ")", "" );
    }

    private static <T> T callAsContentAdmin( final Callable<T> callable )
    {
        final Context context = ContextAccessor.current();
        return ContextBuilder.copyOf( context )
            .authInfo( AuthenticationInfo.copyOf( context.getAuthInfo() ).principals( RoleKeys.CONTENT_MANAGER_ADMIN ).build() )
            .build()
            .callWith( callable );
    }
}
