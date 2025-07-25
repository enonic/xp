package com.enonic.xp.repo.impl.node;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Sets;

import com.enonic.xp.context.ContextAccessor;
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
import com.enonic.xp.repo.impl.binary.BinaryService;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.util.BinaryReference;
import com.enonic.xp.util.BinaryReferences;

class UpdatedAttachedBinariesResolver
{
    private final Node persistedNode;

    private final EditableNode editableNode;

    private final BinaryService binaryService;

    private final BinaryAttachments binaryAttachments;

    private final BinaryReferences currentBinaryReferences;

    private static final Logger LOG = LoggerFactory.getLogger( UpdatedAttachedBinariesResolver.class );

    private UpdatedAttachedBinariesResolver( Builder builder )
    {
        persistedNode = builder.persistedNode;
        editableNode = builder.editableNode;
        binaryService = builder.binaryService;
        binaryAttachments = builder.binaryAttachments;

        final Set<BinaryReference> referencesInEditedNode = new ReferenceResolver().resolve( this.editableNode.data );
        this.currentBinaryReferences = BinaryReferences.from( referencesInEditedNode );
    }

    AttachedBinaries resolve()
    {
        final Set<BinaryReference> referencesInEditedNode = new ReferenceResolver().resolve( this.editableNode.data );

        final Set<BinaryReference> referencesInPersistedNode = new ReferenceResolver().resolve( this.persistedNode.data() );

        final Set<BinaryReference> changedBinaryReferences = Sets.difference( referencesInEditedNode, referencesInPersistedNode );

        if ( changedBinaryReferences.isEmpty() && this.binaryAttachments.isEmpty() )
        {
            return persistedNode.getAttachedBinaries();
        }

        final Map<BinaryReference, AttachedBinary> resolvedAttachedBinaries = new LinkedHashMap<>();

        processExistingBinaries( resolvedAttachedBinaries, referencesInEditedNode, referencesInPersistedNode );

        verifyAllNewGivenAsBinaryAttachment( changedBinaryReferences );

        updateAttachedBinaries( resolvedAttachedBinaries );

        return AttachedBinaries.from( resolvedAttachedBinaries.values() );
    }

    private void verifyAllNewGivenAsBinaryAttachment( final Set<BinaryReference> changedBinaryReferences )
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
        final RepositoryId repositoryId = ContextAccessor.current().getRepositoryId();
        for ( final BinaryAttachment binaryAttachment : this.binaryAttachments )
        {
            if ( this.currentBinaryReferences.contains( binaryAttachment.getReference() ) )
            {
                final AttachedBinary attachedBinary = binaryService.store( repositoryId, binaryAttachment );
                resolved.put( binaryAttachment.getReference(), attachedBinary );
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
        final Set<BinaryReference> unchangedReferences = Sets.intersection( referencesInPersistedNode, referencesInEditedNode );

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

    static Builder create()
    {
        return new Builder();
    }

    static final class Builder
    {
        private Node persistedNode;

        private EditableNode editableNode;

        private BinaryService binaryService;

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

        Builder binaryService( final BinaryService binaryService )
        {
            this.binaryService = binaryService;
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

    private static class ReferenceResolver
        extends PropertyVisitor
    {
        private final Set<BinaryReference> binaryReferences = new LinkedHashSet<>();

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
