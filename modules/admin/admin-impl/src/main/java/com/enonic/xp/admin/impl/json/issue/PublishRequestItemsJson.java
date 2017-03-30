package com.enonic.xp.admin.impl.json.issue;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.codehaus.jparsec.util.Lists;

import com.enonic.xp.issue.PublishRequestItem;
import com.enonic.xp.issue.PublishRequestItems;

public class PublishRequestItemsJson
{
    private List<PublishRequestItemJson> items;

    public PublishRequestItemsJson()
    {
        this.items = Lists.arrayList();
    }

    public static PublishRequestItemsJson from( final PublishRequestItems items )
    {
        final PublishRequestItemsJson json = new PublishRequestItemsJson();
        json.addItems( items.getSet() );

        return json;
    }

    public Stream<PublishRequestItemJson> stream()
    {
        return this.items.stream();
    }

    public void addItem( PublishRequestItem publishRequestItem )
    {
        this.items.add( new PublishRequestItemJson( publishRequestItem ) );
    }

    public void addItems( Collection<PublishRequestItem> publishRequestItems )
    {
        this.items.addAll( publishRequestItems.stream().map( issueItem -> new PublishRequestItemJson( issueItem ) ).collect( Collectors.toList() ) );
    }

    public List<PublishRequestItemJson> getItems()
    {
        return items;
    }

    public PublishRequestItems toItems()
    {
        return PublishRequestItems.from( this.items.stream().map( json -> json.toItem() ).collect( Collectors.toList() ) );
    }
}
