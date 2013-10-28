package com.enonic.wem.core.schema.content;


import javax.jcr.Session;

import com.enonic.wem.api.schema.content.ContentType;
import com.enonic.wem.api.schema.content.ContentTypeFetcher;
import com.enonic.wem.api.schema.content.ContentTypes;
import com.enonic.wem.api.schema.content.ContentTypeName;
import com.enonic.wem.api.schema.content.ContentTypeNames;
import com.enonic.wem.core.schema.content.dao.ContentTypeDao;


public class InternalContentTypeFetcher
    implements ContentTypeFetcher
{

    private ContentTypeDao contentTypeDao;

    private Session session;

    public InternalContentTypeFetcher( Session session, ContentTypeDao contentTypeDao )
    {
        this.session = session;
        this.contentTypeDao = contentTypeDao;
    }

    @Override
    public ContentType getContentType( final ContentTypeName contentTypeName )
    {
        final ContentTypes contentTypes = contentTypeDao.select( ContentTypeNames.from( contentTypeName ), session );
        if ( contentTypes.isEmpty() )
        {
            return null;
        }
        else
        {
            return contentTypes.first();
        }
    }
}
