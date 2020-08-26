package com.enonic.xp.lib.repo;

import java.util.function.Supplier;

import com.google.common.io.ByteSource;

import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.repository.RepositoryService;
import com.enonic.xp.script.bean.BeanContext;
import com.enonic.xp.script.bean.ScriptBean;
import com.enonic.xp.util.BinaryReference;

@SuppressWarnings("unused")
public class GetRepositoryBinaryHandler
    implements ScriptBean
{
    private RepositoryId repositoryId;

    private BinaryReference binaryReference;

    private Supplier<RepositoryService> repositoryServiceSupplier;

    public void setRepositoryId( final String repositoryId )
    {
        this.repositoryId = RepositoryId.from( repositoryId );
    }

    public void setBinaryReference( final String binaryReference )
    {
        this.binaryReference = BinaryReference.from( binaryReference );
    }

    public ByteSource execute()
    {
        final RepositoryService repositoryService = repositoryServiceSupplier.get();

        return repositoryService.getBinary( repositoryId, binaryReference );
    }

    @Override
    public void initialize( final BeanContext context )
    {
        repositoryServiceSupplier = context.getService( RepositoryService.class );
    }
}
