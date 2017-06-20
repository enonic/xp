package com.enonic.xp.admin.impl.rest.resource.content.json;

import java.util.Set;
import java.util.stream.Collectors;

import com.enonic.xp.admin.impl.json.issue.PublishRequestItemJson;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.issue.PublishRequest;
import com.enonic.xp.issue.PublishRequestItem;

public class PublishRequestJson
{
    private Set<String> excludeIds;

    private Set<PublishRequestItemJson> items;

    public void setExcludeIds( final Set<String> excludeIds )
    {
        this.excludeIds = excludeIds;
    }

    public Set<String> getExcludeIds()
    {
        return excludeIds;
    }

    public Set<PublishRequestItemJson> getItems()
    {
        return items;
    }

    public void setItems( final Set<PublishRequestItemJson> itemsJson )
    {
        this.items = itemsJson;
    }

    public static PublishRequestJson from( final PublishRequest publishRequest )
    {
        final PublishRequestJson publishRequestJson = new PublishRequestJson();
        publishRequestJson.setExcludeIds( publishRequest.getExcludeIds().asStrings() );
        publishRequestJson.setItems(
            publishRequest.getItems().getSet().stream().map( item -> new PublishRequestItemJson( item ) ).collect( Collectors.toSet() ) );

        return publishRequestJson;
    }

    public PublishRequest toRequest()
    {
        return PublishRequest.create().
            addExcludeIds( this.excludeIds.stream().map( id -> ContentId.from( id ) ).collect( Collectors.toList() ) ).
            addItems( this.items.stream().
                map( item -> PublishRequestItem.create().
                    id( ContentId.from( item.getId() ) ).
                    includeChildren( item.getIncludeChildren() ).build() ).
                collect( Collectors.toSet() ) ).
            build();
    }
}