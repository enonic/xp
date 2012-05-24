package com.enonic.wem.web.rest.locale;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

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
public final class LocaleController
{
    @Autowired
    private LocaleService localeService;

    @GET
    public LocalesModel getAll()
    {
        final List<Locale> list = Arrays.asList( this.localeService.getLocales() );
        return LocaleModelHelper.toModel( list );
    }
}
