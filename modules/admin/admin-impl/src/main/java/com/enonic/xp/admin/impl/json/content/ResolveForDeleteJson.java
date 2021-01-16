package com.enonic.xp.admin.impl.json.content;

import java.util.List;
import java.util.stream.Collectors;

import com.enonic.xp.content.ContentIds;

public class ResolveForDeleteJson
{
    private final ContentIds toDelete;

    private final ContentIds toUnpublish;

    public ResolveForDeleteJson( final ContentIds toDelete, final ContentIds toUnpublish )
    {
        this.toDelete = toDelete;
        this.toUnpublish = toUnpublish;
    }

    public List<ContentIdJson> getToDelete()
    {
        return toDelete.stream().
            map( ContentIdJson::new ).
            collect( Collectors.toList() );
    }

    public List<ContentIdJson> getToUnpublish()
    {
        return toUnpublish.stream().
            map( ContentIdJson::new ).
            collect( Collectors.toList() );
    }
}
