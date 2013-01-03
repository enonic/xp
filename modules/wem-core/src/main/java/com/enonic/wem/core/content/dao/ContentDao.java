package com.enonic.wem.core.content.dao;


import javax.jcr.Session;

import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.ContentIds;
import com.enonic.wem.api.content.ContentPath;
import com.enonic.wem.api.content.ContentPaths;
import com.enonic.wem.api.content.ContentTree;
import com.enonic.wem.api.content.Contents;
import com.enonic.wem.api.content.type.QualifiedContentTypeName;

public interface ContentDao
    extends ContentDaoConstants
{
    public ContentId createContent( Content content, Session session );

    public void updateContent( Content content, boolean createNewVersion, Session session );

    public void deleteContent( ContentPath contentPath, Session session );

    public void deleteContent( ContentId contentId, Session session );

    public void renameContent( ContentPath contentPath, String newName, Session session );

    public Content findContent( ContentPath contentPath, Session session );

    public Content findContent( ContentId contentId, Session session );

    public Contents findContents( ContentPaths contentPaths, Session session );

    public Contents findContents( ContentIds contentIds, Session session );

    public Contents findChildContent( ContentPath parentPath, Session session );

    public ContentTree getContentTree( final Session session );

    public int countContentTypeUsage( QualifiedContentTypeName qualifiedContentTypeName, Session session );
}
