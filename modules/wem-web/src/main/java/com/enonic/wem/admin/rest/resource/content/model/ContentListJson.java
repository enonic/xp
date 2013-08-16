package com.enonic.wem.admin.rest.resource.content.model;

import java.util.List;

import com.google.common.collect.ImmutableList;

import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.Contents;

public class ContentListJson
{
    private ImmutableList<ContentJson> list;

    public ContentListJson( Content content )
    {
        this( Contents.from( content ) );
    }

    public ContentListJson( Contents contents )
    {
        final ImmutableList.Builder<ContentJson> builder = ImmutableList.builder();
        for ( final Content content : contents )
        {
            builder.add( new ContentJson( content ) );
        }

        this.list = builder.build();
    }

    public List<ContentJson> getContent()
    {
        return list;
    }
}
