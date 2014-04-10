package com.enonic.wem.admin.rest.resource.util;

import java.util.Locale;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.enonic.wem.admin.json.locale.LocaleListJson;
import com.enonic.wem.core.locale.LocaleService;

@Path("util/locale")
@Produces(MediaType.APPLICATION_JSON)
public class LocaleResource
{
    private LocaleService localeService;

    @GET
    public LocaleListJson list()
    {
        final Locale[] locales = this.localeService.getLocales();
        return new LocaleListJson( locales );
    }

    @Inject
    public void setLocaleService( final LocaleService localeService )
    {
        this.localeService = localeService;
    }
}
