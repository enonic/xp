package com.enonic.wem.core.content.schema.content;


import javax.jcr.Session;

import com.enonic.wem.api.content.schema.content.ContentType;
import com.enonic.wem.api.content.schema.content.ContentTypeFetcher;
import com.enonic.wem.api.content.schema.content.ContentTypes;
import com.enonic.wem.api.content.schema.content.QualifiedContentTypeName;
import com.enonic.wem.api.content.schema.content.QualifiedContentTypeNames;
import com.enonic.wem.core.content.schema.content.dao.ContentTypeDao;


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
    public ContentType getContentType( final QualifiedContentTypeName qualifiedContentTypeName )
    {
        final ContentTypes contentTypes = contentTypeDao.select( QualifiedContentTypeNames.from( qualifiedContentTypeName ), session );
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
