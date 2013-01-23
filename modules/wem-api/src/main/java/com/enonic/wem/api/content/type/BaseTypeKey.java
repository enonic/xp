package com.enonic.wem.api.content.type;


import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

import com.enonic.wem.api.content.ModuleBasedQualifiedName;
import com.enonic.wem.api.module.ModuleName;

public class BaseTypeKey
{
    private final static char SEPARATOR = ':';

    private final static Pattern REF_PATTERN = Pattern.compile( "^([^:]+):([^:]+):([^:]+)$" );


    private final String refString;

    private final String type;

    private final ModuleName moduleName;

    private final String localName;

    private BaseTypeKey( final String type, final ModuleName moduleName, final String localName )
    {
        this.type = type;
        this.moduleName = moduleName;
        this.localName = localName;
        this.refString = Joiner.on( SEPARATOR ).join( this.type, this.moduleName, this.localName );
    }

    public String toString()
    {
        return refString;
    }

    public static BaseTypeKey from( Class type, final ModuleBasedQualifiedName qualifiedName )
    {
        return new BaseTypeKey( type.getSimpleName(), qualifiedName.getModuleName(), qualifiedName.getLocalName() );
    }

    public static BaseTypeKey from( final String value )
    {
        Preconditions.checkArgument( !Strings.isNullOrEmpty( value ), "BaseTypeKey cannot be null or empty" );

        final Matcher matcher = REF_PATTERN.matcher( value );
        if ( !matcher.find() )
        {
            throw new IllegalArgumentException( "Not a valid BaseTypeKey [" + value + "]" );
        }

        final String type = matcher.group( 2 );
        final ModuleName moduleName = new ModuleName( matcher.group( 2 ) );
        final String localName = matcher.group( 3 );

        return new BaseTypeKey( type, moduleName, localName );
    }

}
