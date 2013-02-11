package com.enonic.wem.api.content.type;


import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

import com.enonic.wem.api.command.content.BaseTypeKind;
import com.enonic.wem.api.content.mixin.QualifiedMixinName;
import com.enonic.wem.api.content.relationshiptype.QualifiedRelationshipTypeName;
import com.enonic.wem.api.module.ModuleName;

public final class BaseTypeKey
{
    private final static char SEPARATOR = ':';

    private final static Pattern REF_PATTERN = Pattern.compile( "^([^:]+):([^:]+):([^:]+)$" );


    private final String refString;

    private final BaseTypeKind type;

    private final ModuleName moduleName;

    private final String localName;

    private BaseTypeKey( final BaseTypeKind type, final ModuleName moduleName, final String localName )
    {
        this.type = type;
        this.moduleName = moduleName;
        this.localName = localName;
        this.refString = Joiner.on( SEPARATOR ).join( this.type, this.moduleName, this.localName );
    }

    public boolean isContentType()
    {
        return this.type == BaseTypeKind.CONTENT_TYPE;
    }

    public boolean isMixin()
    {
        return this.type == BaseTypeKind.MIXIN;
    }

    public boolean isRelationshipType()
    {
        return this.type == BaseTypeKind.RELATIONSHIP_TYPE;
    }

    public ModuleName getModuleName()
    {
        return this.moduleName;
    }

    public String getLocalName()
    {
        return localName;
    }

    public String toString()
    {
        return refString;
    }

    public static BaseTypeKey from( final QualifiedContentTypeName contentTypeName )
    {
        return new BaseTypeKey( BaseTypeKind.CONTENT_TYPE, contentTypeName.getModuleName(), contentTypeName.getLocalName() );
    }

    public static BaseTypeKey from( final QualifiedMixinName mixinName )
    {
        return new BaseTypeKey( BaseTypeKind.MIXIN, mixinName.getModuleName(), mixinName.getLocalName() );
    }

    public static BaseTypeKey from( final QualifiedRelationshipTypeName relationshipTypeName )
    {
        return new BaseTypeKey( BaseTypeKind.RELATIONSHIP_TYPE, relationshipTypeName.getModuleName(), relationshipTypeName.getLocalName() );
    }

    public static BaseTypeKey from( final String value )
    {
        Preconditions.checkArgument( !Strings.isNullOrEmpty( value ), "BaseTypeKey cannot be null or empty" );

        final Matcher matcher = REF_PATTERN.matcher( value );
        if ( !matcher.find() )
        {
            throw new IllegalArgumentException( "Not a valid BaseTypeKey [" + value + "]" );
        }

        final String type = matcher.group( 1 );
        final ModuleName moduleName = new ModuleName( matcher.group( 2 ) );
        final String localName = matcher.group( 3 );

        final BaseTypeKind typeKind = BaseTypeKind.from( type );
        if ( typeKind == null )
        {
            throw new IllegalArgumentException( "Not a valid BaseTypeKey [" + value + "]" );
        }
        return new BaseTypeKey( typeKind, moduleName, localName );
    }

}
