package com.enonic.xp.lib.repo;

import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import com.enonic.xp.lib.repo.mapper.RepositoryMapper;
import com.enonic.xp.repository.RepositoryService;
import com.enonic.xp.script.bean.BeanContext;
import com.enonic.xp.script.bean.ScriptBean;

public class ListRepositoriesHandler
    implements ScriptBean
{
    private Supplier<RepositoryService> repositoryServiceSupplier;

    public List<RepositoryMapper> execute()
    {
        return repositoryServiceSupplier.get().
            list().
            stream().
            map( repository -> new RepositoryMapper( repository ) ).
            collect( Collectors.toList() );
    }

    @Override
    public void initialize( final BeanContext context )
    {
        repositoryServiceSupplier = context.getService( RepositoryService.class );
    }
}
