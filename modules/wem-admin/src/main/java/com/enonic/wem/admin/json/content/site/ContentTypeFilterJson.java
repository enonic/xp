package com.enonic.wem.admin.json.content.site;

import java.util.List;

import com.google.common.collect.ImmutableList;

import com.enonic.wem.api.content.site.ContentTypeFilter;
import com.enonic.wem.api.schema.content.ContentTypeName;

public final class ContentTypeFilterJson
{
    private final static ImmutableList<String> LIST_ALL = ImmutableList.of( "*" );

    private final ImmutableList<String> allow;

    private final ImmutableList<String> deny;

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
                    denyList.add( contentType.getContentTypeName() );
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
                    allowList.add( contentType.getContentTypeName() );
                }
            }
            this.allow = allowList.build();
        }
    }

    public List<String> getAllow()
    {
        return allow;
    }

    public List<String> getDeny()
    {
        return deny;
    }
}
