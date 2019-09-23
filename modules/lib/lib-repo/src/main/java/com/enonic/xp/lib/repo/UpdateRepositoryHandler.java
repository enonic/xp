package com.enonic.xp.lib.repo;

import java.util.Map;
import java.util.function.Supplier;

import com.enonic.xp.lib.repo.mapper.RepositoryMapper;
import com.enonic.xp.repository.Repository;
import com.enonic.xp.repository.RepositoryBinaryAttachments;
import com.enonic.xp.repository.RepositoryData;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.repository.RepositoryNotFoundException;
import com.enonic.xp.repository.RepositoryService;
import com.enonic.xp.repository.UpdateRepositoryParams;
import com.enonic.xp.script.ScriptValue;
import com.enonic.xp.script.bean.BeanContext;
import com.enonic.xp.script.bean.ScriptBean;

@SuppressWarnings("unused")
public class UpdateRepositoryHandler
    implements ScriptBean
{
    private RepositoryId repositoryId;

    private RepositoryData data;

    private RepositoryBinaryAttachments attachments;

    private Supplier<RepositoryService> repositoryServiceSupplier;

    public void setRepositoryId( final String repositoryId )
    {
        this.repositoryId = RepositoryId.from( repositoryId );
    }

    public void setData( final ScriptValue value )
    {
        final Map<String, Object> map = value.getMap();
        data = data;
    }

    public void setAttachments( final ScriptValue value )
    {
        attachments = new RepositoryBinaryAttachmentsParser().parse( value );
    }

    public RepositoryMapper execute()
    {
        final UpdateRepositoryParams updateRepositoryParams = UpdateRepositoryParams.create().
            repositoryId( repositoryId ).
            data( data ).
            attachments( attachments ).
            build();
        final Repository repository;
        try
        {
            repository = repositoryServiceSupplier.get().updateRepository( updateRepositoryParams );
        }
        catch ( RepositoryNotFoundException e )
        {
            return null;
        }
        return new RepositoryMapper( repository );
    }

    @Override
    public void initialize( final BeanContext context )
    {
        repositoryServiceSupplier = context.getService( RepositoryService.class );
    }
}
