package com.enonic.xp.xml.parser;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
interface InputTypeAliasConverter
{
    String convert( final String alias );
}
