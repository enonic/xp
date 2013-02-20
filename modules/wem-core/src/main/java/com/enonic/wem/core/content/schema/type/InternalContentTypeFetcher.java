package com.enonic.wem.core.content.schema.type;


import javax.jcr.Session;

import com.enonic.wem.api.content.schema.type.ContentType;
import com.enonic.wem.api.content.schema.type.ContentTypeFetcher;
import com.enonic.wem.api.content.schema.type.ContentTypes;
import com.enonic.wem.api.content.schema.type.QualifiedContentTypeName;
import com.enonic.wem.api.content.schema.type.QualifiedContentTypeNames;
import com.enonic.wem.core.content.schema.type.dao.ContentTypeDao;


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
