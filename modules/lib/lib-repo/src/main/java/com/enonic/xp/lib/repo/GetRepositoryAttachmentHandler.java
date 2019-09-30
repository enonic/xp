package com.enonic.xp.lib.repo;

import java.util.function.Supplier;

import com.google.common.io.ByteSource;

import com.enonic.xp.repository.Repository;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.repository.RepositoryService;
import com.enonic.xp.script.bean.BeanContext;
import com.enonic.xp.script.bean.ScriptBean;
import com.enonic.xp.util.BinaryReference;

public class GetRepositoryAttachmentHandler
    implements ScriptBean
{
    private RepositoryId repositoryId;

    private BinaryReference binaryReference;

    private Supplier<RepositoryService> repositoryServiceSupplier;

    public void setRepositoryId( final String repositoryId )
    {
        this.repositoryId = repositoryId == null ? null : RepositoryId.from( repositoryId );
    }

    public void setBinaryReference( final String binaryReference )
    {
        this.binaryReference = BinaryReference.from( binaryReference );
    }

    public ByteSource execute()
    {
        final RepositoryService repositoryService = repositoryServiceSupplier.get();

        final Repository repository = repositoryService.get( repositoryId );

        return repositoryService.getAttachment( repository.getAttachments().getByBinaryReference( binaryReference ) );
    }

    @Override
    public void initialize( final BeanContext context )
    {
        repositoryServiceSupplier = context.getService( RepositoryService.class );
    }
}
