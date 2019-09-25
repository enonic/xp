package com.enonic.xp.repo.impl.repository;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

import com.enonic.xp.node.AttachedBinaries;
import com.enonic.xp.node.BinaryAttachment;
import com.enonic.xp.node.BinaryAttachments;
import com.enonic.xp.repository.RepositoryAttachedBinaries;
import com.enonic.xp.util.AttachedBinary;

public final class RepositoryAttachmentsTranslator
{
    private RepositoryAttachmentsTranslator()
    {
    }

    public static RepositoryAttachedBinaries toRepositoryAttachedBinaries( final AttachedBinaries attachedBinaries )
    {
        final ImmutableSet<AttachedBinary> repositoryAttachments = attachedBinaries.stream().
            map( ab -> new AttachedBinary( ab.getBinaryReference(), ab.getBlobKey() ) ).
            collect( ImmutableSet.toImmutableSet() );
        return RepositoryAttachedBinaries.from( repositoryAttachments );
    }

    public static BinaryAttachments toNodeBinaryAttachments(
        final ImmutableList<com.enonic.xp.util.BinaryAttachment> repositoryBinaryAttachments )
    {
        final BinaryAttachments.Builder builder = BinaryAttachments.create();
        repositoryBinaryAttachments.stream().
            map( rba -> new BinaryAttachment( rba.getReference(), rba.getByteSource() ) ).
            forEach( builder::add );

        return builder.build();
    }
}
