package com.enonic.wem.repo.internal.entity;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import com.enonic.wem.api.blob.Blob;
import com.enonic.wem.api.blob.BlobService;
import com.enonic.wem.api.data.Property;
import com.enonic.wem.api.data.ValueTypes;
import com.enonic.wem.api.node.AttachedBinaries;
import com.enonic.wem.api.node.AttachedBinary;
import com.enonic.wem.api.node.BinaryAttachment;
import com.enonic.wem.api.node.BinaryAttachments;
import com.enonic.wem.api.node.EditableNode;
import com.enonic.wem.api.node.Node;
import com.enonic.wem.api.node.NodeBinaryReferenceException;
import com.enonic.wem.api.util.BinaryReference;
import com.enonic.wem.api.util.BinaryReferences;

class UpdatedAttachedBinariesResolver
{
    private final Node persistedNode;

    private final EditableNode editableNode;

    private final BlobService blobService;

    private final BinaryAttachments binaryAttachments;

    private final BinaryReferences currentBinaryReferences;

    private final static Logger LOG = LoggerFactory.getLogger( UpdatedAttachedBinariesResolver.class );

    private UpdatedAttachedBinariesResolver( Builder builder )
    {
        persistedNode = builder.persistedNode;
        editableNode = builder.editableNode;
        blobService = builder.blobService;
        binaryAttachments = builder.binaryAttachments;

        final Set<Property> referencesInEditedNode = getEditedNodeReferences();

        final BinaryReferences.Builder referenceBuilder = BinaryReferences.create();

        for ( final Property property : referencesInEditedNode )
        {
            referenceBuilder.add( property.getBinaryReference() );
        }

        this.currentBinaryReferences = referenceBuilder.build();
    }

    AttachedBinaries resolve()
    {
        final Set<Property> referencesInEditedNode = getEditedNodeReferences();

        final Set<Property> referencesInPersistedNode = getPersistedNodeReferences();

        final Sets.SetView<Property> changedBinaryReferences = Sets.difference( referencesInEditedNode, referencesInPersistedNode );

        if ( changedBinaryReferences.isEmpty() && this.binaryAttachments.isEmpty() )
        {
            return persistedNode.getAttachedBinaries();
        }

        final Map<BinaryReference, AttachedBinary> resolvedAttachedBinaries = Maps.newHashMap();

        processExistingBinaries( resolvedAttachedBinaries, referencesInEditedNode, referencesInPersistedNode );

        verifyAllNewGivenAsBinaryAttachment( changedBinaryReferences );

        updateAttachedBinaries( resolvedAttachedBinaries );

        return AttachedBinaries.fromCollection( resolvedAttachedBinaries.values() );
    }

    private void verifyAllNewGivenAsBinaryAttachment( final Sets.SetView<Property> changedBinaryReferences )
    {
        for ( final Property property : changedBinaryReferences )
        {
            final BinaryReference binaryReference = property.getBinaryReference();

            final boolean attachmentGivenOrExistsOnNodeAlready = this.binaryAttachments.get( binaryReference ) != null ||
                persistedNode.getAttachedBinaries().getByBinaryReference( binaryReference ) != null;

            if ( !attachmentGivenOrExistsOnNodeAlready )
            {
                throw new NodeBinaryReferenceException( "No binary with reference " + binaryReference + " attached in updateNodeParams" );
            }
        }
    }

    private void updateAttachedBinaries( final Map<BinaryReference, AttachedBinary> resolved )
    {
        for ( final BinaryAttachment binaryAttachment : this.binaryAttachments )
        {
            if ( this.currentBinaryReferences.contains( binaryAttachment.getReference() ) )
            {
                storeAndAttachBinary( resolved, binaryAttachment );
            }
            else
            {
                LOG.info( "Attached binary without reference in data, ignoring" );
            }
        }
    }

    private Set<Property> getPersistedNodeReferences()
    {
        return persistedNode.data().getByValueType( ValueTypes.BINARY_REFERENCE );
    }

    private Set<Property> getEditedNodeReferences()
    {
        return this.editableNode.data.getByValueType( ValueTypes.BINARY_REFERENCE );
    }

    private void processExistingBinaries( final Map<BinaryReference, AttachedBinary> resolved, final Set<Property> referencesInEditedNode,
                                          final Set<Property> referencesInPersistedNode )
    {
        final Sets.SetView<Property> unchangedReferences = Sets.intersection( referencesInPersistedNode, referencesInEditedNode );

        final AttachedBinaries attachedBinaries = persistedNode.getAttachedBinaries();

        for ( final Property property : unchangedReferences )
        {
            final AttachedBinary existingAttachedBinary = attachedBinaries.getByBinaryReference( property.getBinaryReference() );

            if ( existingAttachedBinary != null )
            {
                resolved.put( property.getBinaryReference(), existingAttachedBinary );
            }
        }
    }

    private void storeAndAttachBinary( final Map<BinaryReference, AttachedBinary> resolved, final BinaryAttachment newBinaryAttachment )
    {
        try
        {
            final Blob blob = this.blobService.create( newBinaryAttachment.getByteSource().openStream() );
            resolved.put( newBinaryAttachment.getReference(), new AttachedBinary( newBinaryAttachment.getReference(), blob.getKey() ) );
        }
        catch ( IOException e )
        {
            e.printStackTrace();
        }
    }

    static Builder create()
    {
        return new Builder();
    }

    static final class Builder
    {
        private Node persistedNode;

        private EditableNode editableNode;

        private BlobService blobService;

        private BinaryAttachments binaryAttachments;

        private Builder()
        {
        }

        Builder persistedNode( Node persistedNode )
        {
            this.persistedNode = persistedNode;
            return this;
        }

        Builder editableNode( EditableNode editableNode )
        {
            this.editableNode = editableNode;
            return this;
        }

        Builder blobService( BlobService blobService )
        {
            this.blobService = blobService;
            return this;
        }

        Builder binaryAttachments( BinaryAttachments binaryAttachments )
        {
            this.binaryAttachments = binaryAttachments;
            return this;
        }

        UpdatedAttachedBinariesResolver build()
        {
            return new UpdatedAttachedBinariesResolver( this );
        }
    }
}
