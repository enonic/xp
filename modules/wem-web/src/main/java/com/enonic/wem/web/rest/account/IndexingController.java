/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.wem.web.rest.account;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.enonic.wem.core.search.indexing.AccountIndexing;

@Component
@Controller
@RequestMapping(value = "/account/indexing/", produces = MediaType.APPLICATION_JSON_VALUE)
public final class IndexingController
{
    @Autowired
    private AccountIndexing accountIndexing;

    @RequestMapping(method = RequestMethod.GET)
    public IndexRestResponse handleIndex( final @RequestParam("start") boolean start )
    {
        if ( start )
        {
            accountIndexing.indexAccounts();
        }

        IndexRestResponse res = new IndexRestResponse();
        res.setIndexing( accountIndexing.isRunning() );
        res.setProgress( accountIndexing.getProgress() );
        return res;
    }

}
