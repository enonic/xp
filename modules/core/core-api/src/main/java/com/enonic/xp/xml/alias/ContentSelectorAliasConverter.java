package com.enonic.xp.xml.alias;

import com.google.common.annotations.Beta;

@Beta
public final class ContentSelectorAliasConverter
    implements XmlAliasConverter
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
