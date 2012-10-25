package com.enonic.wem.core.content.dao;


import javax.jcr.Session;

import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentPath;
import com.enonic.wem.api.content.ContentPaths;
import com.enonic.wem.api.content.Contents;

public interface ContentDao
    extends ContentDaoConstants
{
    public void createContent( Session session, Content content );

    public Content findContent( Session session, ContentPath contentPath );

    public Contents findContent( Session session, ContentPaths contentPaths );
}
