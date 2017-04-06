package com.enonic.xp.repo.impl.repository.event;

import java.util.Optional;

import com.enonic.xp.event.Event;
import com.enonic.xp.repo.impl.InternalContext;
import com.enonic.xp.repo.impl.RepositoryEvents;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.repository.RepositoryService;

public class RepositoryInvalidateByIdHandler
    implements RepositoryEventHandler
{
    private final RepositoryService repositoryService;

    public RepositoryInvalidateByIdHandler( final RepositoryService repositoryService )
    {
        this.repositoryService = repositoryService;
    }

    @Override
    public void handleEvent( final Event event, final InternalContext context )
    {
        final Optional<String> repositoryIdOptional = event.getValueAs( String.class, RepositoryEvents.REPOSITORY_ID_KEY );

        if ( repositoryIdOptional.isPresent() )
        {
            this.repositoryService.invalidate( RepositoryId.from( repositoryIdOptional.get() ) );
        }
    }
}

