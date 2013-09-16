package com.enonic.wem.admin.json.content;

import java.util.List;

import com.google.common.collect.ImmutableList;

import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.Contents;


public abstract class AbstractContentListJson<T extends ContentIdJson>
{
    private List<T> contents;

    public AbstractContentListJson( Content content )
    {
        this( Contents.from( content ) );
    }

    public AbstractContentListJson( Contents contents )
    {
        final ImmutableList.Builder<T> builder = ImmutableList.builder();
        for ( final Content content : contents )
        {
            builder.add( createItem( content ) );
        }
        this.contents = builder.build();
    }

    public List<T> getContents()
    {
        return contents;
    }

    protected abstract T createItem( Content content );

}
