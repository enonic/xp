package com.enonic.wem.core.content;

import javax.jcr.Session;

public abstract class ContentService
{
    public static final ContentNodeTranslator CONTENT_TO_NODE_TRANSLATOR = new ContentNodeTranslator();

    Session session;

    protected ContentService( final Session session )
    {
        this.session = session;
    }
}
