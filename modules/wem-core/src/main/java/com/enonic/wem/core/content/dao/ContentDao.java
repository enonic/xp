package com.enonic.wem.core.content.dao;


import javax.jcr.Session;

import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentPath;
import com.enonic.wem.api.content.ContentPaths;
import com.enonic.wem.api.content.ContentTree;
import com.enonic.wem.api.content.Contents;

public interface ContentDao
    extends ContentDaoConstants
{
    public void createContent( Content content, Session session );

    public void updateContent( Content content, Session session );

    public void deleteContent( ContentPath content, Session session );

    public void renameContent( ContentPath content, String newName, Session session );

    public Content findContent( ContentPath contentPath, Session session );

    public Contents findContent( ContentPaths contentPaths, Session session );

    public Contents findChildContent( ContentPath parentPath, Session session );

    public ContentTree getContentTree( final Session session );
}
