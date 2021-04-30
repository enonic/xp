package com.enonic.xp.admin.impl.json.content;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentListMetaData;
import com.enonic.xp.content.Contents;

public class ContentListJson<T extends ContentIdJson>
{
    private final ContentListMetaDataJson metadata;

    private final List<T> contents;

    public ContentListJson( final Contents contents, ContentListMetaData contentListMetaData,
                            final Function<Content, T> createItemFunction )
    {
        this.metadata = new ContentListMetaDataJson( contentListMetaData );
        this.contents = contents.stream().map( createItemFunction ).collect( Collectors.toUnmodifiableList() );
    }

    public List<T> getContents()
    {
        return contents;
    }

    public ContentListMetaDataJson getMetadata()
    {
        return metadata;
    }
}
