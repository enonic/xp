package com.enonic.xp.lib.thymeleaf;

import org.thymeleaf.templateresource.ITemplateResource;

import com.enonic.xp.resource.ResourceKey;

interface TemplateResourceResolver
{
    ITemplateResource resolve( ResourceKey base, String location );
}
