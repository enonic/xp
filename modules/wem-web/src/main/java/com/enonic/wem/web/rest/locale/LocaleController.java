package com.enonic.wem.web.rest.locale;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.enonic.cms.core.locale.LocaleService;

@Controller
@RequestMapping(value = "/misc/locale", produces = "application/json")
public final class LocaleController
{
    @Autowired
    private LocaleService localeService;

    @RequestMapping(value = "list")
    @ResponseBody
    public LocalesModel getAll()
    {
        final List<Locale> list = Arrays.asList( this.localeService.getLocales() );
        return LocaleModelHelper.toModel( list );
    }
}
