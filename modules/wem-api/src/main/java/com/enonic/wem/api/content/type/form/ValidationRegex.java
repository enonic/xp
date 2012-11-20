package com.enonic.wem.api.content.type.form;


import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.enonic.wem.api.content.data.Data;
import com.enonic.wem.api.content.datatype.JavaType;

public class ValidationRegex
{
    private Pattern pattern;

    private String string;

    public ValidationRegex( final String pattern )
    {
        this.pattern = Pattern.compile( pattern );
        this.string = pattern;
    }

    public void checkValidity( final Data data )
        throws BreaksRegexValidationException
    {
        if ( data.getDataType().getJavaType() != JavaType.STRING )
        {
            return;
        }

        final Matcher matcher = pattern.matcher( data.getString() );
        if ( !matcher.matches() )
        {
            throw new BreaksRegexValidationException( data, pattern.toString() );
        }
    }

    @Override
    public String toString()
    {
        return string;
    }
}
