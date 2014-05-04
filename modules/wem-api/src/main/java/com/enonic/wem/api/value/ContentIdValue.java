package com.enonic.wem.api.value;

import com.enonic.wem.api.content.ContentId;

public final class ContentIdValue
    extends Value<ContentId>
{
    public ContentIdValue( final ContentId object )
    {
        super( ValueType.CONTENT_ID, object );
    }
}
