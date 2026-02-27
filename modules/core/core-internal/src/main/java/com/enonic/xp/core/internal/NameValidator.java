package com.enonic.xp.core.internal;

import java.util.regex.Pattern;

import com.google.common.base.CharMatcher;

public final class NameValidator
{
    private static final Pattern APP_NAME_PATTERN = Pattern.compile( "^\\w+(?:\\.\\w+)*$" );

    private static final Pattern VALID_BRANCH_ID_REGEX = Pattern.compile( "^[a-zA-Z0-9\\-][a-zA-Z0-9\\-.]*$" );

    private static final Pattern VALID_REPOSITORY_ID_REGEX = Pattern.compile( "^[a-z0-9\\-][a-z0-9_\\-.]*$" );

    private static final Pattern VALID_PROJECT_NAME_REGEX = Pattern.compile( "^[a-z0-9][a-z0-9_\\-]*$" );

    private static final Pattern VALID_DESCRIPTOR_NAME_REGEX = Pattern.compile( "^[^<>:\"'/\\\\]+$" );

    private static final String EXPLICITLY_ILLEGAL_NAME_CHARACTERS = "/\\*?|";

    private static final CharMatcher EXPLICITLY_ILLEGAL_NAME_CHAR_MATCHER = CharMatcher.anyOf( EXPLICITLY_ILLEGAL_NAME_CHARACTERS );

    private static final NameValidator APPLICATION_KEY =
        builder( "ApplicationKey" ).rejectNull().maxLength( 63 ).regex( APP_NAME_PATTERN ).build();

    private static final NameValidator BRANCH = builder( "Branch" ).maxLength( 63 ).regex( VALID_BRANCH_ID_REGEX ).build();

    private static final NameValidator REPOSITORY_ID = builder( "Repository" ).maxLength( 63 ).regex( VALID_REPOSITORY_ID_REGEX ).build();

    private static final NameValidator PROJECT_NAME = builder( "ProjectName" ).maxLength( 48 ).regex( VALID_PROJECT_NAME_REGEX ).build();

    private static final NameValidator DESCRIPTOR_NAME = builder( "Descriptor name" ).regex( VALID_DESCRIPTOR_NAME_REGEX ).build();

    private static final NameValidator ID_PROVIDER_KEY = builder( "IdProviderKey" ).rejectNull().regex( VALID_DESCRIPTOR_NAME_REGEX ).build();

    private static final NameValidator PRINCIPAL_ID = builder( "Principal id" ).rejectNull().regex( VALID_DESCRIPTOR_NAME_REGEX ).build();

    private static final NameValidator MACRO_NAME = builder( "Macro name" ).regex( VALID_DESCRIPTOR_NAME_REGEX ).build();

    private static final NameValidator NAME = builder( "name" ).build();

    private final String type;

    private final boolean rejectNull;

    private final int maxLength;

    private final Pattern regex;

    private NameValidator( final Builder builder )
    {
        this.type = builder.type;
        this.rejectNull = builder.rejectNull;
        this.maxLength = builder.maxLength;
        this.regex = builder.regex;
    }

    public String validate( final String name )
    {
        if ( rejectNull && name == null )
        {
            throw new IllegalArgumentException( type + " must not be null" );
        }

        if ( name.isBlank() )
        {
            throw new IllegalArgumentException( type + " must not be blank" );
        }

        if ( name.equals( "_" ) )
        {
            throw new IllegalArgumentException( type + " must not be '_'" );
        }

        if ( name.charAt( 0 ) == '.' )
        {
            throw new IllegalArgumentException( type + " must not start with '.'" );
        }

        if ( name.charAt( 0 ) == ' ' )
        {
            throw new IllegalArgumentException( type + " must not start with ' '" );
        }

        if ( maxLength > 0 && name.length() > maxLength )
        {
            throw new IllegalArgumentException( type + " must not be longer than " + maxLength + " characters" );
        }

        if ( regex != null && !regex.matcher( name ).matches() )
        {
            throw new IllegalArgumentException( type + " is invalid: " + name );
        }

        return name;
    }

    public static String requireValidApplicationKey( final String name )
    {
        return APPLICATION_KEY.validate( name );
    }

    public static String requireValidBranch( final String name )
    {
        return BRANCH.validate( name );
    }

    public static String requireValidRepositoryId( final String name )
    {
        return REPOSITORY_ID.validate( name );
    }

    public static String requireValidProjectName( final String name )
    {
        return PROJECT_NAME.validate( name );
    }

    public static String requireValidDescriptorName( final String name )
    {
        return DESCRIPTOR_NAME.validate( name );
    }

    public static String requireValidIdProviderKey( final String name )
    {
        ID_PROVIDER_KEY.validate( name );

        // "roles" is reserved as it is used as a node name for roles storage
        if ( "roles".equalsIgnoreCase( name ) )
        {
            throw new IllegalArgumentException( "IdProviderKey is reserved and cannot be used: " + name );
        }

        return name;
    }

    public static String requireValidPrincipalId( final String name )
    {
        return PRINCIPAL_ID.validate( name );
    }

    public static String requireValidMacroName( final String name )
    {
        return MACRO_NAME.validate( name );
    }

    public static String requireValidName( final String name )
    {
        if ( name == null )
        {
            throw new NullPointerException( "name cannot be null" );
        }

        NAME.validate( name );

        if ( EXPLICITLY_ILLEGAL_NAME_CHAR_MATCHER.matchesAnyOf( name ) )
        {
            throw new IllegalArgumentException( "Invalid name: '" + name + "'. Cannot contain " + EXPLICITLY_ILLEGAL_NAME_CHARACTERS );
        }

        for ( int i = 0; i < name.length(); i++ )
        {
            final char c = name.charAt( i );
            if ( !isValidNameCharacter( c ) )
            {
                final String unicodeChar = c > 255 ? " (U+" + Integer.toHexString( c | 0x10000 ).substring( 1 ) + ")" : "";
                throw new IllegalArgumentException( "Invalid character in name: '" + c + "'" + unicodeChar );
            }
        }
        return name;
    }

    public static boolean isInvisible( final char c )
    {
        return CharMatcher.invisible().matches( c );
    }

    private static boolean isValidNameCharacter( final char c )
    {
        if ( c == ' ' || c == '-' )
        {
            return true;
        }

        if ( CharMatcher.invisible().matches( c ) )
        {
            return false;
        }

        final int type = Character.getType( c );
        return type == Character.LOWERCASE_LETTER || type == Character.MODIFIER_LETTER || type == Character.UPPERCASE_LETTER ||
            type == Character.DECIMAL_DIGIT_NUMBER || type == Character.END_PUNCTUATION || type == Character.START_PUNCTUATION ||
            type == Character.FINAL_QUOTE_PUNCTUATION || type == Character.INITIAL_QUOTE_PUNCTUATION ||
            type == Character.OTHER_PUNCTUATION || type == Character.CURRENCY_SYMBOL || type == Character.MODIFIER_SYMBOL ||
            type == Character.MATH_SYMBOL || type == Character.OTHER_SYMBOL || type == Character.DASH_PUNCTUATION ||
            Character.isJavaIdentifierPart( c );
    }

    private static Builder builder( final String type )
    {
        return new Builder( type );
    }

    private static final class Builder
    {
        private final String type;

        private boolean rejectNull;

        private int maxLength;

        private Pattern regex;

        private Builder( final String type )
        {
            this.type = type;
        }

        Builder rejectNull()
        {
            this.rejectNull = true;
            return this;
        }

        Builder maxLength( final int maxLength )
        {
            this.maxLength = maxLength;
            return this;
        }

        Builder regex( final Pattern regex )
        {
            this.regex = regex;
            return this;
        }

        NameValidator build()
        {
            return new NameValidator( this );
        }
    }
}
