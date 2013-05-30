package com.enonic.wem.core.content.attachment.dao;


import javax.jcr.Session;

import com.enonic.wem.api.content.ContentSelector;
import com.enonic.wem.api.content.attachment.Attachment;

public interface AttachmentDao
{
    void createAttachment( ContentSelector contentSelector, Attachment attachment, Session session );

    Attachment getAttachment( ContentSelector contentSelector, String attachmentName, Session session );

    boolean deleteAttachment( ContentSelector contentSelector, String attachmentName, Session session );
}
