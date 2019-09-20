package com.enonic.xp.repo.impl.repository;

import com.google.common.collect.ImmutableSet;

import com.enonic.xp.node.AttachedBinaries;
import com.enonic.xp.node.AttachedBinary;
import com.enonic.xp.node.BinaryAttachment;
import com.enonic.xp.node.BinaryAttachments;
import com.enonic.xp.repository.RepositoryAttachment;
import com.enonic.xp.repository.RepositoryAttachments;
import com.enonic.xp.repository.RepositoryBinaryAttachments;

public final class RepositoryAttachmentsTranslator
{
    private RepositoryAttachmentsTranslator()
    {
    }

    public static RepositoryAttachments toRepositoryAttachments( final AttachedBinaries attachedBinaries )
    {
        final ImmutableSet<RepositoryAttachment> repositoryAttachments = attachedBinaries.stream().
            map( ab -> new RepositoryAttachment( ab.getBinaryReference(), ab.getBlobKey() ) ).
            collect( ImmutableSet.toImmutableSet() );
        return RepositoryAttachments.from( repositoryAttachments );
    }

    public static AttachedBinaries toAttachedBinaries( final RepositoryAttachments repositoryAttachments )
    {
        final ImmutableSet<AttachedBinary> attachedBinaries = repositoryAttachments.stream().
            map( ra -> new AttachedBinary( ra.getBinaryReference(), ra.getBlobKey() ) )
            .collect( ImmutableSet.toImmutableSet() );
        return AttachedBinaries.fromCollection( attachedBinaries );
    }

    public static BinaryAttachments toBinaryAttachments( final RepositoryBinaryAttachments repositoryBinaryAttachments )
    {
        final BinaryAttachments.Builder builder = BinaryAttachments.create();
        repositoryBinaryAttachments.stream().
            map( rba -> new BinaryAttachment( rba.getReference(), rba.getByteSource() ) ).
            forEach( builder::add );

        return builder.build();
    }
}
