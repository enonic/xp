package com.enonic.wem.core.content.attachment.dao;


import javax.jcr.Session;

import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.ContentPath;
import com.enonic.wem.api.content.attachment.Attachment;

public interface AttachmentDao
{
    void createAttachmentById( ContentId contentId, Attachment attachment, Session session );

    void createAttachmentByPath( ContentPath contentPath, Attachment attachment, Session session );

    Attachment getAttachmentById( ContentId contentId, String attachmentName, Session session );

    Attachment getAttachmentByPath( ContentPath contentPath, String attachmentName, Session session );

    boolean deleteAttachmentById( ContentId contentId, String attachmentName, Session session );

    boolean deleteAttachmentByPath( ContentPath contentPath, String attachmentName, Session session );

    boolean renameAttachments( ContentId contentId, String oldContentName, String newContentName, Session session );
}
