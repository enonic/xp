package com.enonic.xp.xml.parser;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
final class ContentSelectorAliasConverter
    implements InputTypeAliasConverter
{

    public final static ContentSelectorAliasConverter INSTANCE = new ContentSelectorAliasConverter();

    private ContentSelectorAliasConverter()
    {
    }

    @Override
    public String convert( final String alias )
    {
        if ( alias.equals( "relationship" ) )
        {
            return "relationship-type";
        }
        else if ( alias.equals( "allowType" ) )
        {
            return "allow-content-type";
        }
        return alias;
    }
}
