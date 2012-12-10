package com.enonic.wem.core.content.type;


import javax.jcr.Session;

import com.enonic.wem.api.content.type.ContentType;
import com.enonic.wem.api.content.type.ContentTypeFetcher;
import com.enonic.wem.api.content.type.ContentTypes;
import com.enonic.wem.api.content.type.QualifiedContentTypeName;
import com.enonic.wem.api.content.type.QualifiedContentTypeNames;
import com.enonic.wem.core.content.type.dao.ContentTypeDao;


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
        final ContentTypes contentTypes =
            contentTypeDao.retrieveContentTypes( session, QualifiedContentTypeNames.from( qualifiedContentTypeName ) );
        if ( contentTypes.isEmpty() )
        {
            return null;
        }
        else
        {
            return contentTypes.getFirst();
        }
    }
}
