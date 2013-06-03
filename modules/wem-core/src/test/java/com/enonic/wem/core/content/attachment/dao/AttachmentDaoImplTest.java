package com.enonic.wem.core.content.attachment.dao;

import javax.jcr.Node;

import org.junit.Test;

import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.ContentPath;
import com.enonic.wem.api.content.attachment.Attachment;
import com.enonic.wem.api.content.binary.Binary;
import com.enonic.wem.core.AbstractJcrTest;
import com.enonic.wem.core.content.dao.ContentDao;
import com.enonic.wem.core.content.dao.ContentDaoImpl;
import com.enonic.wem.core.jcr.JcrHelper;

import static com.enonic.wem.api.content.Content.newContent;
import static com.enonic.wem.api.content.attachment.Attachment.newAttachment;
import static org.junit.Assert.*;

public class AttachmentDaoImplTest
    extends AbstractJcrTest
{
    private AttachmentDao attachmentDao;

    private ContentDao contentDao;

    public void setupDao()
        throws Exception
    {
        attachmentDao = new AttachmentDaoImpl();
        contentDao = new ContentDaoImpl();
        session.getNode( "/wem/spaces" ).addNode( "myspace" );
    }

    @Test
    public void createAttachmentByContentId()
        throws Exception
    {
        // setup
        final Binary binary = Binary.from( "some data".getBytes() );
        final Attachment attachment = newAttachment().binary( binary ).name( "file.jpg" ).mimeType( "image/jpeg" ).label( "small" ).build();

        final Content content = newContent().path( ContentPath.from( "myspace:/" ) ).build();
        final ContentId contentId = contentDao.create( content, session );
        commit();

        // exercise
        attachmentDao.createAttachment( contentId, attachment, session );
        commit();

        // verify
        Node contentNode = session.getNode( "/" + ContentDao.SPACES_PATH + "myspace/root" );
        Node attachmentsNode = contentNode.getNode( ContentDao.CONTENT_ATTACHMENTS_NODE );
        Node createdAttachmentNode = attachmentsNode.getNode( attachment.getName() );
        assertNotNull( createdAttachmentNode );
        assertEquals( attachment.getName(), JcrHelper.getPropertyString( createdAttachmentNode, AttachmentJcrMapper.NAME ) );
        assertEquals( attachment.getLabel(), JcrHelper.getPropertyString( createdAttachmentNode, AttachmentJcrMapper.LABEL ) );
        assertEquals( attachment.getMimeType(), JcrHelper.getPropertyString( createdAttachmentNode, AttachmentJcrMapper.MIME_TYPE ) );
        assertEquals( attachment.getSize(), JcrHelper.getPropertyLong( createdAttachmentNode, AttachmentJcrMapper.SIZE ).longValue() );
    }

    @Test
    public void createAttachmentByContentPath()
        throws Exception
    {
        // setup
        final Binary binary = Binary.from( "some data".getBytes() );
        final Attachment attachment = newAttachment().binary( binary ).name( "file.jpg" ).mimeType( "image/jpeg" ).label( "small" ).build();

        final Content content = newContent().path( ContentPath.from( "myspace:/" ) ).build();
        contentDao.create( content, session );
        commit();

        // exercise
        attachmentDao.createAttachment( ContentPath.from( "myspace:/" ), attachment, session );
        commit();

        // verify
        Node contentNode = session.getNode( "/" + ContentDao.SPACES_PATH + "myspace/root" );
        Node attachmentsNode = contentNode.getNode( ContentDao.CONTENT_ATTACHMENTS_NODE );
        Node createdAttachmentNode = attachmentsNode.getNode( attachment.getName() );
        assertNotNull( createdAttachmentNode );
        assertEquals( attachment.getName(), JcrHelper.getPropertyString( createdAttachmentNode, AttachmentJcrMapper.NAME ) );
        assertEquals( attachment.getLabel(), JcrHelper.getPropertyString( createdAttachmentNode, AttachmentJcrMapper.LABEL ) );
        assertEquals( attachment.getMimeType(), JcrHelper.getPropertyString( createdAttachmentNode, AttachmentJcrMapper.MIME_TYPE ) );
        assertEquals( attachment.getSize(), JcrHelper.getPropertyLong( createdAttachmentNode, AttachmentJcrMapper.SIZE ).longValue() );
    }

    @Test
    public void getAttachmentByContentId()
        throws Exception
    {
        // setup
        final Binary binary = Binary.from( "some data".getBytes() );
        final Attachment attachment = newAttachment().binary( binary ).name( "file.jpg" ).mimeType( "image/jpeg" ).label( "small" ).build();
        final Content content = newContent().path( ContentPath.from( "myspace:/" ) ).build();
        final ContentId contentId = contentDao.create( content, session );
        commit();
        attachmentDao.createAttachment( ContentPath.from( "myspace:/" ), attachment, session );
        commit();

        // exercise
        final Attachment retrievedAttachment = attachmentDao.getAttachment( contentId, "file.jpg", session );

        // verify
        assertNotNull( retrievedAttachment );
        assertNotSame( attachment, retrievedAttachment );
        assertEquals( attachment, retrievedAttachment );
    }

    @Test
    public void getAttachmentByContentPath()
        throws Exception
    {
        // setup
        final Binary binary = Binary.from( "some data".getBytes() );
        final Attachment attachment = newAttachment().binary( binary ).name( "file.jpg" ).mimeType( "image/jpeg" ).label( "small" ).build();
        final Content content = newContent().path( ContentPath.from( "myspace:/" ) ).build();
        contentDao.create( content, session );
        commit();
        attachmentDao.createAttachment( ContentPath.from( "myspace:/" ), attachment, session );
        commit();

        // exercise
        final Attachment retrievedAttachment = attachmentDao.getAttachment( ContentPath.from( "myspace:/" ), "file.jpg", session );

        // verify
        assertNotNull( retrievedAttachment );
        assertNotSame( attachment, retrievedAttachment );
        assertEquals( attachment, retrievedAttachment );
    }

    @Test
    public void getAttachmentMissing()
        throws Exception
    {
        // setup
        final Binary binary = Binary.from( "some data".getBytes() );
        final Attachment attachment = newAttachment().binary( binary ).name( "file.jpg" ).mimeType( "image/jpeg" ).label( "small" ).build();
        final Content content = newContent().path( ContentPath.from( "myspace:/" ) ).build();
        contentDao.create( content, session );
        commit();
        attachmentDao.createAttachment( ContentPath.from( "myspace:/" ), attachment, session );
        commit();

        // exercise
        final Attachment retrievedAttachment = attachmentDao.getAttachment( ContentPath.from( "myspace:/" ), "other.jpg", session );

        // verify
        assertNull( retrievedAttachment );
    }

    @Test
    public void deleteAttachmentByContentId()
        throws Exception
    {
        // setup
        final Binary binary = Binary.from( "some data".getBytes() );
        final Attachment attachment = newAttachment().binary( binary ).name( "file.jpg" ).mimeType( "image/jpeg" ).label( "small" ).build();
        final Attachment attachment2 = newAttachment().binary( binary ).name( "file2.jpg" ).mimeType( "image/jpeg" ).label( "big" ).build();
        final Content content = newContent().path( ContentPath.from( "myspace:/" ) ).build();
        final ContentId contentId = contentDao.create( content, session );
        commit();
        attachmentDao.createAttachment( ContentPath.from( "myspace:/" ), attachment, session );
        attachmentDao.createAttachment( ContentPath.from( "myspace:/" ), attachment2, session );
        commit();

        // exercise
        boolean deleted = attachmentDao.deleteAttachment( contentId, "file.jpg", session );
        commit();

        // verify
        assertTrue( deleted );
        Node contentNode = session.getNode( "/" + ContentDao.SPACES_PATH + "myspace/root" );
        Node attachmentsNode = contentNode.getNode( ContentDao.CONTENT_ATTACHMENTS_NODE );
        assertEquals( 1, attachmentsNode.getNodes().getSize() );
        assertNull( JcrHelper.getNodeOrNull( attachmentsNode, attachment.getName() ) );
        assertNotNull( JcrHelper.getNodeOrNull( attachmentsNode, attachment2.getName() ) );
    }

    @Test
    public void deleteAttachmentByContentPath()
        throws Exception
    {
        // setup
        final Binary binary = Binary.from( "some data".getBytes() );
        final Attachment attachment = newAttachment().binary( binary ).name( "file.jpg" ).mimeType( "image/jpeg" ).label( "small" ).build();
        final Attachment attachment2 = newAttachment().binary( binary ).name( "file2.jpg" ).mimeType( "image/jpeg" ).label( "big" ).build();
        final Content content = newContent().path( ContentPath.from( "myspace:/" ) ).build();
        contentDao.create( content, session );
        commit();
        attachmentDao.createAttachment( ContentPath.from( "myspace:/" ), attachment, session );
        attachmentDao.createAttachment( ContentPath.from( "myspace:/" ), attachment2, session );
        commit();

        // exercise
        boolean deleted = attachmentDao.deleteAttachment( ContentPath.from( "myspace:/" ), "file.jpg", session );
        commit();

        // verify
        assertTrue( deleted );
        Node contentNode = session.getNode( "/" + ContentDao.SPACES_PATH + "myspace/root" );
        Node attachmentsNode = contentNode.getNode( ContentDao.CONTENT_ATTACHMENTS_NODE );
        assertEquals( 1, attachmentsNode.getNodes().getSize() );
        assertNull( JcrHelper.getNodeOrNull( attachmentsNode, attachment.getName() ) );
        assertNotNull( JcrHelper.getNodeOrNull( attachmentsNode, attachment2.getName() ) );
    }

    @Test
    public void deleteAttachmentMissing()
        throws Exception
    {
        // setup
        final Binary binary = Binary.from( "some data".getBytes() );
        final Attachment attachment = newAttachment().binary( binary ).name( "file.jpg" ).mimeType( "image/jpeg" ).label( "small" ).build();
        final Content content = newContent().path( ContentPath.from( "myspace:/" ) ).build();
        contentDao.create( content, session );
        commit();
        attachmentDao.createAttachment( ContentPath.from( "myspace:/" ), attachment, session );
        commit();

        // exercise
        boolean deleted = attachmentDao.deleteAttachment( ContentPath.from( "myspace:/" ), "otherfile.jpg", session );
        commit();

        // verify
        assertFalse( deleted );
        Node contentNode = session.getNode( "/" + ContentDao.SPACES_PATH + "myspace/root" );
        Node attachmentsNode = contentNode.getNode( ContentDao.CONTENT_ATTACHMENTS_NODE );
        assertEquals( 1, attachmentsNode.getNodes().getSize() );
        assertNotNull( JcrHelper.getNodeOrNull( attachmentsNode, attachment.getName() ) );
    }

}
