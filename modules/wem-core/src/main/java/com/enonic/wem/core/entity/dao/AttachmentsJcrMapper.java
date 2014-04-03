package com.enonic.wem.core.entity.dao;

import java.util.ArrayList;
import java.util.List;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;

import com.enonic.wem.api.blob.BlobKey;
import com.enonic.wem.api.entity.Attachment;
import com.enonic.wem.api.entity.Attachments;

import static com.enonic.wem.api.entity.Attachment.newAttachment;

public class AttachmentsJcrMapper
{

    public final static String ATTACHMENTS_NODE_NAME = "__att";

    public static final String BLOB_KEY_PROPERTY = "blobKey";

    public static final String MIME_TYPE_PROPERTY = "mimeType";

    public static final String SIZE_PROPERTY = "size";


    void toJcr( final Attachments attachments, final Node jcrNode )
        throws RepositoryException
    {
        final Node attachmentsNode = jcrNode.addNode( ATTACHMENTS_NODE_NAME );
        for ( final Attachment attachment : attachments )
        {
            addAttachmentJcrNode( attachment, attachmentsNode );
        }
    }

    private void addAttachmentJcrNode( final Attachment attachment, final Node attachmentsNode )
        throws RepositoryException
    {
        final Node attachmentNode = attachmentsNode.addNode( attachment.name() );
        attachmentNode.setProperty( SIZE_PROPERTY, attachment.size() );
        attachmentNode.setProperty( MIME_TYPE_PROPERTY, attachment.mimeType() );
        attachmentNode.setProperty( BLOB_KEY_PROPERTY, attachment.blobKey().toString() );
    }

    public Attachments toAttachments( final Node jcrNode )
        throws RepositoryException
    {
        if ( !jcrNode.hasNode( ATTACHMENTS_NODE_NAME ) )
        {
            return Attachments.empty();
        }

        final Node attachmentsNode = jcrNode.getNode( ATTACHMENTS_NODE_NAME );
        final NodeIterator nodeIterator = attachmentsNode.getNodes();
        final Attachments.Builder attachmentsBuilder = new Attachments.Builder();
        while ( nodeIterator.hasNext() )
        {
            final Node attachmentNode = nodeIterator.nextNode();
            final Attachment attachment = newAttachment().
                name( attachmentNode.getName() ).
                size( attachmentNode.getProperty( SIZE_PROPERTY ).getLong() ).
                mimeType( attachmentNode.getProperty( MIME_TYPE_PROPERTY ).getString() ).
                blobKey( new BlobKey( attachmentNode.getProperty( BLOB_KEY_PROPERTY ).getString() ) ).
                build();
            attachmentsBuilder.add( attachment );
        }
        return attachmentsBuilder.build();
    }

    void synchronizeJcr( final Attachments attachments, final Node jcrNode )
        throws RepositoryException
    {
        if ( attachments == null )
        {
            return;
        }
        else if ( attachments.isEmpty() && !jcrNode.hasNode( ATTACHMENTS_NODE_NAME ) )
        {
            return;
        }

        if ( attachments.isEmpty() && jcrNode.hasNode( ATTACHMENTS_NODE_NAME ) )
        {
            jcrNode.getNode( ATTACHMENTS_NODE_NAME ).remove();
        }
        else if ( attachments.isNotEmpty() && !jcrNode.hasNode( ATTACHMENTS_NODE_NAME ) )
        {
            addNodes( attachments, jcrNode.addNode( ATTACHMENTS_NODE_NAME ), false );
        }
        else
        {
            final Node attachmentsNode = jcrNode.getNode( ATTACHMENTS_NODE_NAME );
            removeNodes( attachments, attachmentsNode );
            addNodes( attachments, attachmentsNode, true );
        }
    }

    private void addNodes( final Attachments attachments, final Node attachmentsNode, final boolean replaceExisting )
        throws RepositoryException
    {
        for ( Attachment attachment : attachments )
        {
            final String attachmentName = attachment.name();
            final boolean nodeExists = attachmentsNode.hasNode( attachmentName );
            if ( !nodeExists )
            {
                addAttachmentJcrNode( attachment, attachmentsNode );
            }
            else if ( replaceExisting )
            {
                final Node attachmentNode = attachmentsNode.getNode( attachmentName );
                final String blobKey = attachmentNode.getProperty( BLOB_KEY_PROPERTY ).getString();
                final boolean isSameAttachment = blobKey.equals( attachment.blobKey().toString() );
                if ( !isSameAttachment )
                {
                    attachmentNode.remove();
                    addAttachmentJcrNode( attachment, attachmentsNode );
                }
            }
        }
    }

    private void removeNodes( final Attachments attachments, final Node attachmentsNode )
        throws RepositoryException
    {
        final NodeIterator nodeIterator = attachmentsNode.getNodes();
        final List<Node> attachmentsNodeToRemove = new ArrayList<>();
        while ( nodeIterator.hasNext() )
        {
            final Node attachmentNode = nodeIterator.nextNode();
            if ( !attachments.hasAttachment( attachmentNode.getName() ) )
            {
                attachmentsNodeToRemove.add( attachmentNode );
            }
        }
        for ( Node toRemove : attachmentsNodeToRemove )
        {
            toRemove.remove();
        }
    }
}
