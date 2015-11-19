package com.enonic.xp.xml.alias;

import java.util.Map;

import com.google.common.annotations.Beta;
import com.google.common.collect.Maps;

import com.enonic.xp.inputtype.InputTypeName;

@Beta
public final class XmlAliasConverters
{
    public static XmlAliasConverter DEFAULT_CONVERTER = ( s ) -> s;

    private final Map<InputTypeName, XmlAliasConverter> map;

    private final static XmlAliasConverters INSTANCE = new XmlAliasConverters();

    private XmlAliasConverters()
    {
        this.map = Maps.newConcurrentMap();
        this.map.put( InputTypeName.CONTENT_SELECTOR, ContentSelectorAliasConverter.INSTANCE );
        this.map.put( InputTypeName.IMAGE_SELECTOR, ContentSelectorAliasConverter.INSTANCE );
    }

    public static XmlAliasConverter getConverter( final InputTypeName inputTypeName )
    {
        XmlAliasConverter result = INSTANCE.map.get( inputTypeName );
        return result != null ? result : DEFAULT_CONVERTER;
    }

    public static String convert( final InputTypeName inputTypeName, final String alias )
    {
        return getConverter( inputTypeName ).convert( alias );
    }

}
