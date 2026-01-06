package com.enonic.xp.lib.content;

import java.util.List;
import java.util.Locale;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.branch.Branches;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentNotFoundException;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.UpdateMetadataParams;
import com.enonic.xp.content.UpdateMetadataResult;
import com.enonic.xp.lib.content.mapper.UpdateMetadataResultMapper;
import com.enonic.xp.security.PrincipalKey;

public final class UpdateMetadataHandler
    extends BaseContentHandler
{
    private String key;

    private String language;

    private String owner;

    private List<String> branches;

    @Override
    protected Object doExecute()
    {
        final Content existingContent = getExistingContent( this.key );
        if ( existingContent == null )
        {
            return null;
        }

        final UpdateMetadataParams.Builder params = UpdateMetadataParams.create().contentId( existingContent.getId() );

        if ( language != null )
        {
            params.language( Locale.forLanguageTag( language ) );
        }

        if ( owner != null )
        {
            params.owner( PrincipalKey.from( owner ) );
        }

        if ( branches != null )
        {
            params.branches( branches.stream().map( Branch::from ).collect( Branches.collector() ) );
        }

        final UpdateMetadataResult result;
        try
        {
            result = this.contentService.updateMetadata( params.build() );
        }
        catch ( Exception e )
        {
            throw new RuntimeException( e );
        }

        return new UpdateMetadataResultMapper( result );
    }

    @Override
    protected boolean strictDataValidation()
    {
        return false;
    }

    private Content getExistingContent( final String key )
    {
        try
        {
            if ( !key.startsWith( "/" ) )
            {
                return this.contentService.getById( ContentId.from( key ) );
            }
            else
            {
                return this.contentService.getByPath( ContentPath.from( key ) );
            }
        }
        catch ( final ContentNotFoundException e )
        {
            return null;
        }
    }

    public void setKey( final String key )
    {
        this.key = key;
    }

    public void setLanguage( final String language )
    {
        this.language = language;
    }

    public void setOwner( final String owner )
    {
        this.owner = owner;
    }

    public void setBranches( final List<String> branches )
    {
        this.branches = branches;
    }
}
