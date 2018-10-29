package com.enonic.xp.content;

import com.google.common.annotations.Beta;

import com.enonic.xp.schema.content.ContentTypeName;

@Beta
public class ContentDependenciesAggregation
{
    private ContentTypeName type;

    private Contents contents;

    public ContentDependenciesAggregation( final ContentTypeName type, final Contents contents )
    {
        this.type = type;
        this.contents = contents;
    }

    public ContentTypeName getType()
    {
        return type;
    }

    public Contents getContents()
    {
        return contents;
    }
}
