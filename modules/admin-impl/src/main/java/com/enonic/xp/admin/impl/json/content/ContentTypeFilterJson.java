package com.enonic.xp.admin.impl.json.content;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableList;

import com.enonic.xp.schema.content.ContentTypeFilter;
import com.enonic.xp.schema.content.ContentTypeName;

@SuppressWarnings("UnusedDeclaration")
public final class ContentTypeFilterJson
{
    private final static ImmutableList<String> LIST_ALL = ImmutableList.of( "*" );

    private final ImmutableList<String> allow;

    private final ImmutableList<String> deny;

    @JsonCreator
    ContentTypeFilterJson( @JsonProperty("allow") final List<String> allow, @JsonProperty("deny") final List<String> deny )
    {
        this.allow = ImmutableList.copyOf( allow );
        this.deny = ImmutableList.copyOf( deny );
    }

    public ContentTypeFilterJson( final ContentTypeFilter contentTypeFilter )
    {
        if ( contentTypeFilter.getDefaultAccess() == ContentTypeFilter.AccessType.ALLOW )
        {
            this.allow = LIST_ALL;
            final ImmutableList.Builder<String> denyList = ImmutableList.builder();
            for ( ContentTypeName contentType : contentTypeFilter )
            {
                if ( !contentTypeFilter.isContentTypeAllowed( contentType ) )
                {
                    denyList.add( contentType.toString() );
                }
            }
            this.deny = denyList.build();
        }
        else
        {
            this.deny = LIST_ALL;
            final ImmutableList.Builder<String> allowList = ImmutableList.builder();
            for ( ContentTypeName contentType : contentTypeFilter )
            {
                if ( contentTypeFilter.isContentTypeAllowed( contentType ) )
                {
                    allowList.add( contentType.toString() );
                }
            }
            this.allow = allowList.build();
        }
    }

    public List<String> getAllow()
    {
        return this.allow;
    }

    public List<String> getDeny()
    {
        return this.deny;
    }

    @JsonIgnore
    public ContentTypeFilter toContentTypeFilter()
    {
        final ContentTypeFilter.Builder filter = ContentTypeFilter.create();

        for ( final String allowStr : allow )
        {
            filter.allowContentType( allowStr );
        }

        for ( final String denyStr : deny )
        {
            filter.denyContentType( denyStr );
        }

        return filter.build();
    }
}
