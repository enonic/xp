package com.enonic.wem.portal.view.thymeleaf;

import org.thymeleaf.standard.StandardDialect;

final class ThymeleafDialect
    extends StandardDialect
{
    @Override
    public String getPrefix()
    {
        return "wem";
    }
}
