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
    private final static String ATTACHMENTS_NODE_NAME = "__att";


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
        attachmentNode.setProperty( "size", attachment.size() );
        attachmentNode.setProperty( "mimeType", attachment.mimeType() );
        attachmentNode.setProperty( "blobKey", attachment.blobKey().toString() );
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
                size( attachmentNode.getProperty( "size" ).getLong() ).
                mimeType( attachmentNode.getProperty( "mimeType" ).getString() ).
                blobKey( new BlobKey( attachmentNode.getProperty( "mimeType" ).getString() ) ).
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

        if ( attachments.isEmpty() && !jcrNode.hasNode( ATTACHMENTS_NODE_NAME ) )
        {
            return;
        }
        else if ( attachments.isEmpty() && jcrNode.hasNode( ATTACHMENTS_NODE_NAME ) )
        {
            jcrNode.getNode( ATTACHMENTS_NODE_NAME ).remove();
            return;
        }

        final Node attachmentsNode = jcrNode.getNode( ATTACHMENTS_NODE_NAME );

        removeNodes( attachments, attachmentsNode );
        addNodes( attachments, attachmentsNode );
    }

    private void addNodes( final Attachments attachments, final Node attachmentsNode )
        throws RepositoryException
    {
        for ( Attachment attachment : attachments )
        {
            if ( !attachmentsNode.hasNode( attachment.name() ) )
            {
                addAttachmentJcrNode( attachment, attachmentsNode );
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
