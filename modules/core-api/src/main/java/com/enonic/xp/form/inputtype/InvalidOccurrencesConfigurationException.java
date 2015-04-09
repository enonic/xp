package com.enonic.xp.form.inputtype;


import com.google.common.annotations.Beta;

@Beta
public class InvalidOccurrencesConfigurationException
    extends RuntimeException
{
    public InvalidOccurrencesConfigurationException( final String message )
    {
        super( message );
    }
}
