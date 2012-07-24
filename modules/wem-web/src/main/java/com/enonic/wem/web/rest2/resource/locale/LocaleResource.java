package com.enonic.wem.web.rest2.resource.locale;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.enonic.cms.core.locale.LocaleService;

@Path("misc/locale")
@Produces(MediaType.APPLICATION_JSON)
@Component
public final class LocaleResource
{
    private LocaleService localeService;

    @GET
    public LocaleResult getAll()
    {
        return new LocaleResult( this.localeService.getLocales() );
    }

    @Autowired
    public void setLocaleService( final LocaleService localeService )
    {
        this.localeService = localeService;
    }
}

