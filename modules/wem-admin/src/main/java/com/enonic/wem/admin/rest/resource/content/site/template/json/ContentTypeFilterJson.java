package com.enonic.wem.admin.rest.resource.content.site.template.json;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.enonic.wem.api.content.site.ContentTypeFilter;

public class ContentTypeFilterJson
{
    final List<String> allows;

    final List<String> denys;

    @JsonCreator
    ContentTypeFilterJson( @JsonProperty("allow") final List<String> allows, @JsonProperty("deny") final List<String> denys )
    {
        this.allows = allows;
        this.denys = denys;
    }

    public ContentTypeFilter toContentTypeFilter()
    {
        final ContentTypeFilter.Builder filter = ContentTypeFilter.newContentFilter();

        for ( final String allow : allows )
        {
            filter.allowContentType( allow );
        }

        for ( final String deny : denys )
        {
            filter.denyContentType( deny );
        }

        return filter.build();
    }
}
