package com.enonic.wem.core.jcr;

import javax.jcr.Repository;

class JcrRepositoryImpl
    implements JcrRepository
{
    private final Repository repository;

    JcrRepositoryImpl( Repository repository )
    {
        this.repository = repository;
    }

    public Repository getRepository()
    {
        return repository;
    }

}
