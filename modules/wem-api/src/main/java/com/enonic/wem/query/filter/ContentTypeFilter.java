package com.enonic.wem.query.filter;

import java.util.Set;

import com.google.common.collect.Sets;

import com.enonic.wem.api.data.Value;

public class ContentTypeFilter
    extends ValueFilter
{

    private ContentTypeFilter( final Builder builder )
    {
        super( "contentType", builder.contentTypeIds );
    }

    public static class Builder
    {
        private Set<Value> contentTypeIds = Sets.newHashSet();

        public Builder add( final String... contentTypeIds )
        {
            for ( final String contentTypeId : contentTypeIds )
            {
                this.contentTypeIds.add( new Value.String( contentTypeId ) );
            }

            return this;
        }

        public ContentTypeFilter build()
        {
            return new ContentTypeFilter( this );
        }
    }
}
