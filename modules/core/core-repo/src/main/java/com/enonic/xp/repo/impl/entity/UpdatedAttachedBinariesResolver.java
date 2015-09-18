package com.enonic.xp.repo.impl.entity;

import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import com.enonic.xp.data.Property;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.data.PropertyVisitor;
import com.enonic.xp.data.ValueTypes;
import com.enonic.xp.node.AttachedBinaries;
import com.enonic.xp.node.AttachedBinary;
import com.enonic.xp.node.BinaryAttachment;
import com.enonic.xp.node.BinaryAttachments;
import com.enonic.xp.node.EditableNode;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeBinaryReferenceException;
import com.enonic.xp.repo.impl.blob.Blob;
import com.enonic.xp.repo.impl.blob.BlobStore;
import com.enonic.xp.util.BinaryReference;
import com.enonic.xp.util.BinaryReferences;
import com.enonic.xp.util.Exceptions;

class UpdatedAttachedBinariesResolver
{
    private final Node persistedNode;

    private final EditableNode editableNode;

    private final BlobStore blobStore;

    private final BinaryAttachments binaryAttachments;

    private final BinaryReferences currentBinaryReferences;

    private final static Logger LOG = LoggerFactory.getLogger( UpdatedAttachedBinariesResolver.class );

    private UpdatedAttachedBinariesResolver( Builder builder )
    {
        persistedNode = builder.persistedNode;
        editableNode = builder.editableNode;
        blobStore = builder.blobStore;
        binaryAttachments = builder.binaryAttachments;

        final Set<BinaryReference> referencesInEditedNode = new ReferenceResolver().resolve( this.editableNode.data );

        final BinaryReferences.Builder referenceBuilder = BinaryReferences.create();

        referencesInEditedNode.forEach( referenceBuilder::add );

        this.currentBinaryReferences = referenceBuilder.build();
    }

    AttachedBinaries resolve()
    {
        final Set<BinaryReference> referencesInEditedNode = new ReferenceResolver().resolve( this.editableNode.data );

        final Set<BinaryReference> referencesInPersistedNode = new ReferenceResolver().resolve( this.persistedNode.data() );

        final Sets.SetView<BinaryReference> changedBinaryReferences = Sets.difference( referencesInEditedNode, referencesInPersistedNode );

        if ( changedBinaryReferences.isEmpty() && this.binaryAttachments.isEmpty() )
        {
            return persistedNode.getAttachedBinaries();
        }

        final Map<BinaryReference, AttachedBinary> resolvedAttachedBinaries = Maps.newLinkedHashMap();

        processExistingBinaries( resolvedAttachedBinaries, referencesInEditedNode, referencesInPersistedNode );

        verifyAllNewGivenAsBinaryAttachment( changedBinaryReferences );

        updateAttachedBinaries( resolvedAttachedBinaries );

        return AttachedBinaries.fromCollection( resolvedAttachedBinaries.values() );
    }

    private void verifyAllNewGivenAsBinaryAttachment( final Sets.SetView<BinaryReference> changedBinaryReferences )
    {
        for ( final BinaryReference binaryReference : changedBinaryReferences )
        {
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

    private void processExistingBinaries( final Map<BinaryReference, AttachedBinary> resolved,
                                          final Set<BinaryReference> referencesInEditedNode,
                                          final Set<BinaryReference> referencesInPersistedNode )
    {
        final Sets.SetView<BinaryReference> unchangedReferences = Sets.intersection( referencesInPersistedNode, referencesInEditedNode );

        final AttachedBinaries attachedBinaries = persistedNode.getAttachedBinaries();

        for ( final BinaryReference unchangedReference : unchangedReferences )
        {
            final AttachedBinary existingAttachedBinary = attachedBinaries.getByBinaryReference( unchangedReference );

            if ( existingAttachedBinary != null )
            {
                resolved.put( unchangedReference, existingAttachedBinary );
            }
        }
    }

    private void storeAndAttachBinary( final Map<BinaryReference, AttachedBinary> resolved, final BinaryAttachment newBinaryAttachment )
    {
        try
        {
            final Blob blob = this.blobStore.addRecord( newBinaryAttachment.getByteSource().openStream() );
            resolved.put( newBinaryAttachment.getReference(),
                          new AttachedBinary( newBinaryAttachment.getReference(), blob.getKey().toString() ) );
        }
        catch ( final IOException e )
        {
            throw Exceptions.unchecked( e );
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

        private BlobStore blobStore;

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

        Builder binaryBlobStore( BlobStore blobStore )
        {
            this.blobStore = blobStore;
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

    private class ReferenceResolver
        extends PropertyVisitor
    {
        private Set<BinaryReference> binaryReferences = new LinkedHashSet<>();

        private ReferenceResolver()
        {
            visitPropertiesWithSet( false );
            restrictType( ValueTypes.BINARY_REFERENCE );
        }

        @Override
        public void visit( final Property property )
        {
            binaryReferences.add( property.getBinaryReference() );
        }

        public Set<BinaryReference> resolve( final PropertyTree tree )
        {
            this.traverse( tree );
            return binaryReferences;
        }
    }
}
