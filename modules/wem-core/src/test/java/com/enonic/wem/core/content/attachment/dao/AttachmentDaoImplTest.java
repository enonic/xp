package com.enonic.wem.core.content.attachment.dao;

import java.util.Set;

import javax.jcr.Node;
import javax.jcr.NodeIterator;

import org.junit.Test;
import org.mockito.Mockito;

import com.google.common.collect.Sets;

import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentPath;
import com.enonic.wem.api.content.attachment.Attachment;
import com.enonic.wem.api.content.binary.Binary;
import com.enonic.wem.core.AbstractJcrTest;
import com.enonic.wem.core.content.dao.ContentDao;
import com.enonic.wem.core.content.dao.ContentDaoImpl;
import com.enonic.wem.core.index.IndexService;
import com.enonic.wem.core.jcr.JcrHelper;

import static com.enonic.wem.api.content.Content.newContent;
import static com.enonic.wem.api.content.attachment.Attachment.newAttachment;
import static org.junit.Assert.*;

public class AttachmentDaoImplTest
    extends AbstractJcrTest
{
    private AttachmentDao attachmentDao;

    private ContentDao contentDao;

    private IndexService indexService;

    public void setupDao()
        throws Exception
    {
        attachmentDao = new AttachmentDaoImpl();
        contentDao = new ContentDaoImpl();

        indexService = Mockito.mock( IndexService.class );
        ( (ContentDaoImpl) contentDao ).setIndexService( indexService );
    }

    @Test
    public void createAttachmentByContentId()
        throws Exception
    {
        // setup
        final Binary binary = Binary.from( "some data".getBytes() );
        final Attachment attachment = newAttachment().binary( binary ).name( "file.jpg" ).mimeType( "image/jpeg" ).label( "small" ).build();

        final Content content = newContent().path( ContentPath.from( "/mysite" ) ).build();
        final Content storedContent = contentDao.create( content, session );
        commit();

        // exercise
        attachmentDao.createAttachment( storedContent.getId(), attachment, session );
        commit();

        // verify
        Node contentNode = session.getNode( "/" + ContentDao.CONTENTS_ROOT_PATH + "mysite" );
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

        final Content content = newContent().path( ContentPath.from( "/mysite" ) ).build();
        contentDao.create( content, session );
        commit();

        // exercise
        attachmentDao.createAttachment( ContentPath.from( "/mysite" ), attachment, session );
        commit();

        // verify
        Node contentNode = session.getNode( "/" + ContentDao.CONTENTS_ROOT_PATH + "mysite" );
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
        final Content content = newContent().path( ContentPath.from( "/mysite" ) ).build();
        final Content storedContent = contentDao.create( content, session );
        commit();
        attachmentDao.createAttachment( ContentPath.from( "/mysite" ), attachment, session );
        commit();

        // exercise
        final Attachment retrievedAttachment = attachmentDao.getAttachment( storedContent.getId(), "file.jpg", session );

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
        final Content content = newContent().path( ContentPath.from( "/mysite" ) ).build();
        contentDao.create( content, session );
        commit();
        attachmentDao.createAttachment( ContentPath.from( "/mysite" ), attachment, session );
        commit();

        // exercise
        final Attachment retrievedAttachment = attachmentDao.getAttachment( ContentPath.from( "/mysite" ), "file.jpg", session );

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
        final Content content = newContent().path( ContentPath.from( "/mysite" ) ).build();
        contentDao.create( content, session );
        commit();
        attachmentDao.createAttachment( ContentPath.from( "/mysite" ), attachment, session );
        commit();

        // exercise
        final Attachment retrievedAttachment = attachmentDao.getAttachment( ContentPath.from( "/mysite" ), "other.jpg", session );

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
        final Content content = newContent().path( ContentPath.from( "/mysite" ) ).build();
        final Content storedContent = contentDao.create( content, session );
        commit();
        attachmentDao.createAttachment( ContentPath.from( "/mysite" ), attachment, session );
        attachmentDao.createAttachment( ContentPath.from( "/mysite" ), attachment2, session );
        commit();

        // exercise
        boolean deleted = attachmentDao.deleteAttachment( storedContent.getId(), "file.jpg", session );
        commit();

        // verify
        assertTrue( deleted );
        Node contentNode = session.getNode( "/" + ContentDao.CONTENTS_ROOT_PATH + "mysite" );
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
        final Content content = newContent().path( ContentPath.from( "/mysite" ) ).build();
        contentDao.create( content, session );
        commit();
        attachmentDao.createAttachment( ContentPath.from( "/mysite" ), attachment, session );
        attachmentDao.createAttachment( ContentPath.from( "/mysite" ), attachment2, session );
        commit();

        // exercise
        boolean deleted = attachmentDao.deleteAttachment( ContentPath.from( "/mysite" ), "file.jpg", session );
        commit();

        // verify
        assertTrue( deleted );
        Node contentNode = session.getNode( "/" + ContentDao.CONTENTS_ROOT_PATH + "mysite" );
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
        final Content content = newContent().path( ContentPath.from( "/mysite" ) ).build();
        contentDao.create( content, session );
        commit();
        attachmentDao.createAttachment( ContentPath.from( "/mysite" ), attachment, session );
        commit();

        // exercise
        boolean deleted = attachmentDao.deleteAttachment( ContentPath.from( "/mysite" ), "otherfile.jpg", session );
        commit();

        // verify
        assertFalse( deleted );
        Node contentNode = session.getNode( "/" + ContentDao.CONTENTS_ROOT_PATH + "mysite" );
        Node attachmentsNode = contentNode.getNode( ContentDao.CONTENT_ATTACHMENTS_NODE );
        assertEquals( 1, attachmentsNode.getNodes().getSize() );
        assertNotNull( JcrHelper.getNodeOrNull( attachmentsNode, attachment.getName() ) );
    }

    @Test
    public void renameAttachments()
        throws Exception
    {
        // setup
        final Binary binary = Binary.from( "some data".getBytes() );
        final Attachment attachment = newAttachment().binary( binary ).name( "file.jpg" ).mimeType( "image/jpeg" ).build();
        final Attachment attachment2 =
            newAttachment().binary( binary ).name( "file-small.jpg" ).mimeType( "image/jpeg" ).label( "small" ).build();
        final Attachment attachment3 =
            newAttachment().binary( binary ).name( "file-large.jpg" ).mimeType( "image/jpeg" ).label( "large" ).build();

        final Content contentRoot = newContent().path( ContentPath.from( "/mysite" ) ).build();
        contentDao.create( contentRoot, session );
        final Content content = newContent().path( ContentPath.from( "/mysite/file" ) ).build();
        final Content storedContent = contentDao.create( content, session );
        commit();
        attachmentDao.createAttachment( ContentPath.from( "/mysite/file" ), attachment, session );
        attachmentDao.createAttachment( ContentPath.from( "/mysite/file" ), attachment2, session );
        attachmentDao.createAttachment( ContentPath.from( "/mysite/file" ), attachment3, session );
        commit();

        // exercise
        boolean renamed = attachmentDao.renameAttachments( storedContent.getId(), "file", "other-file.jpg", session );
        commit();

        // verify
        assertTrue( renamed );
        Node contentNode = session.getNode( "/" + ContentDao.CONTENTS_ROOT_PATH + "mysite/file" );
        Node attachmentsNode = contentNode.getNode( ContentDao.CONTENT_ATTACHMENTS_NODE );
        assertEquals( 3, attachmentsNode.getNodes().getSize() );
        final NodeIterator attachmentsNodes = attachmentsNode.getNodes();
        final Set<String> newNodeNames = Sets.newHashSet();
        while ( attachmentsNodes.hasNext() )
        {
            final Node attachmentNode = attachmentsNodes.nextNode();
            newNodeNames.add( attachmentNode.getName() );
        }

        assertTrue( newNodeNames.contains( "other-file.jpg" ) );
        assertTrue( newNodeNames.contains( "other-file-small.jpg" ) );
        assertTrue( newNodeNames.contains( "other-file-large.jpg" ) );
    }

}
