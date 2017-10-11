package com.enonic.xp.admin.impl.json.content;

import java.util.ArrayList;
import java.util.List;

import com.enonic.xp.admin.impl.rest.resource.content.json.ContentTreeSelectorJson;
import com.enonic.xp.content.ContentListMetaData;

@SuppressWarnings("UnusedDeclaration")
public class ContentTreeSelectorListJson
{
    private final List<ContentTreeSelectorJson> items;

    private final ContentListMetaDataJson metadata;

    public ContentTreeSelectorListJson( final List<ContentTreeSelectorJson> items, final ContentListMetaDataJson metadata )
    {
        this.items = items;
        this.metadata = metadata;
    }

    public ContentTreeSelectorListJson( final List<ContentTreeSelectorJson> items, final ContentListMetaData metadata )
    {
        this.items = items;
        this.metadata = new ContentListMetaDataJson( metadata );
    }

    public static ContentTreeSelectorListJson empty()
    {
        return new ContentTreeSelectorListJson( new ArrayList<ContentTreeSelectorJson>(), new ContentListMetaDataJson(
            ContentListMetaData.create().hits( 0 ).totalHits( 0 ).build() ) );
    }

    public List<ContentTreeSelectorJson> getItems()
    {
        return items;
    }

    public ContentListMetaDataJson getMetadata()
    {
        return metadata;
    }
}
