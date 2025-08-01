package com.enonic.xp.xml.parser;

final class ContentSelectorAliasConverter
    implements InputTypeAliasConverter
{

    public static final ContentSelectorAliasConverter INSTANCE = new ContentSelectorAliasConverter();

    private ContentSelectorAliasConverter()
    {
    }

    @Override
    public String convert( final String alias )
    {
        if ( alias.equals( "allowType" ) )
        {
            return "allow-content-type";
        }
        return alias;
    }
}
