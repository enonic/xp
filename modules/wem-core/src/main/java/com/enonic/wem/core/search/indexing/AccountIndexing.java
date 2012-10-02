package com.enonic.wem.core.search.indexing;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.enonic.wem.core.search.account.AccountSearchService;

@Component
public class AccountIndexing
{
    private static final Logger LOG = LoggerFactory.getLogger( AccountIndexing.class );

    private AccountSearchService accountSearchService;

    public AccountIndexing()
    {
    }

    @PostConstruct
    public void initialize()
    {
        LOG.info( "Creating index and mapping for accounts." );
        accountSearchService.dropIndex();
        accountSearchService.createIndex();
    }

    @Autowired
    public void setAccountSearchService( AccountSearchService accountSearchService )
    {
        this.accountSearchService = accountSearchService;
    }

}
