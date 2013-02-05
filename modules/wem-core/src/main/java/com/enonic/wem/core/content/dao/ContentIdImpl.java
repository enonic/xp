package com.enonic.wem.core.content.dao;

import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.core.support.dao.AbstractId;

public final class ContentIdImpl
    extends AbstractId
    implements ContentId
{
    ContentIdImpl( final String id )
    {
        super( id );
    }
}
