package com.enonic.xp.admin.impl.rest.resource.content;

import java.util.List;
import java.util.stream.Collectors;

import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.ContentPaths;

final class ReprocessContentResultJson
{
    public final List<String> updatedContent;

    public ReprocessContentResultJson( final ContentPaths result )
    {
        this.updatedContent = result.stream().map( ContentPath::toString ).collect( Collectors.toList() );
    }
}
