package com.enonic.wem.api.content.type.formitem;


import com.enonic.wem.api.content.type.formitem.comptype.ComponentTypes;
import com.enonic.wem.api.module.Module;

import static com.enonic.wem.api.content.type.formitem.Component.newComponent;

public final class ComponentTemplates
{
    public static final ComponentTemplate URL = ComponentTemplateBuilder.newComponentTemplate().module( Module.SYSTEM ).component(
        newComponent().name( "url" ).type( ComponentTypes.TEXT_LINE ).validationRegexp(
            "^(http|https|ftp)\\://[a-zA-Z0-9\\-\\.]+\\.[a-zA-Z]{2,3}(:[a-zA-Z0-9]*)?/?([a-zA-Z0-9\\-\\._\\?\\,\\'/\\\\\\+&amp;%\\$#\\=~])*[^\\.\\,\\)\\(\\s]$" ).build() ).build();

    public static final ComponentTemplate EMAIL = ComponentTemplateBuilder.newComponentTemplate().module( Module.SYSTEM ).component(
        newComponent().name( "url" ).type( ComponentTypes.TEXT_LINE ).validationRegexp(
            "[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?" ).build() ).build();
}

