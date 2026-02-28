package com.enonic.xp.core.internal;

import java.util.Arrays;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

import org.jspecify.annotations.NonNull;

public final class NameValidator
{
    public static String HTML_FORBITTEN_CHARS = "<>&\"'";

    public static String FILENAME_FORBITTEN_CHARS = "<>:\"/\\|?*";

    public static final NameValidator NAME = builder( "Name" ).invalidChars( FILENAME_FORBITTEN_CHARS )
        .validCharTypes( Character.LOWERCASE_LETTER, Character.MODIFIER_LETTER, Character.UPPERCASE_LETTER, Character.TITLECASE_LETTER,
                         Character.OTHER_LETTER, Character.DECIMAL_DIGIT_NUMBER, Character.START_PUNCTUATION, Character.END_PUNCTUATION,
                         Character.INITIAL_QUOTE_PUNCTUATION, Character.FINAL_QUOTE_PUNCTUATION, Character.DASH_PUNCTUATION,
                         Character.CONNECTOR_PUNCTUATION, Character.OTHER_PUNCTUATION, Character.CURRENCY_SYMBOL, Character.MODIFIER_SYMBOL,
                         Character.MATH_SYMBOL, Character.OTHER_SYMBOL )
        .build();

    private final String type;

    private final int maxLength;

    private final Pattern regex;

    private final int[] invalidChars;

    private final int[] validCharTypes;

    private final Set<String> reservedNames;

    private NameValidator( final Builder builder )
    {
        this.type = builder.type;
        this.maxLength = builder.maxLength;
        this.regex = builder.regex;
        this.invalidChars = builder.invalidChars;
        this.validCharTypes = builder.validCharTypes;
        this.reservedNames = Set.of( "_" );
    }

    public String validate( final String name )
    {
        Objects.requireNonNull( name, () -> type + " must not be null" );

        if ( name.isEmpty() )
        {
            throw new IllegalArgumentException( type + " must not be empty" );
        }

        if ( reservedNames.contains( name ) )
        {
            throw new IllegalArgumentException( type + " must not be " + name );
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

        name.chars().filter( this::isInvalidChar ).findFirst().ifPresent( cp -> {
            throw new IllegalArgumentException( type + " must not contain '" + toUCode( cp ) + "'" );
        } );

        if ( regex != null && !regex.matcher( name ).matches() )
        {
            throw new IllegalArgumentException( type + " is invalid: " + name );
        }

        return name;
    }

    private static @NonNull String toUCode( final int cp )
    {
        if ( cp >= 0x20 && cp <= 0x7E )
        {
            return String.valueOf( (char) cp );
        }
        return String.format( "U+%04X", cp );
    }

    private boolean isInvalidChar( final int cp )
    {
        if ( invalidChars != null && Arrays.binarySearch( invalidChars, cp ) >= 0 )
        {
            return true;
        }

        if ( cp == ' ' )
        {
            return false;
        }

        if ( Character.isWhitespace( cp ) )
        {
            return true;
        }

        if ( validCharTypes != null && Arrays.binarySearch( validCharTypes, Character.getType( cp ) ) < 0 )
        {
            return true;
        }

        return false;
    }

    public static String requireValidName( final String name )
    {
        return NAME.validate( name );
    }

    public static Builder builder( final Class<?> type )
    {
        return builder( type.getSimpleName() );
    }

    public static Builder builder( final Class<?> type, final NameValidator base )
    {
        return builder( type.getSimpleName(), base );
    }

    public static Builder builder( final String type )
    {
        return new Builder( type );
    }

    public static Builder builder( final String type, final NameValidator base )
    {
        return new Builder( type ).validCharTypes( base.validCharTypes )
            .invalidChars( base.invalidChars )
            .maxLength( base.maxLength )
            .regex( base.regex );
    }

    public static final class Builder
    {
        private final String type;

        private int maxLength;

        private Pattern regex;

        private int[] invalidChars;

        private int[] validCharTypes;

        private Builder( final String type )
        {
            this.type = type;
        }

        public Builder maxLength( final int maxLength )
        {
            this.maxLength = maxLength;
            return this;
        }

        public Builder regex( final Pattern regex )
        {
            this.regex = regex;
            return this;
        }

        public Builder invalidChars( final String invalidChars )
        {
            this.invalidChars = invalidChars.chars().sorted().distinct().toArray();
            return this;
        }

        Builder invalidChars( final int[] invalidChars )
        {
            this.invalidChars = Arrays.copyOf( invalidChars, invalidChars.length );
            return this;
        }

        public Builder validCharTypes( final int... validCharTypes )
        {
            this.validCharTypes = IntStream.of( validCharTypes ).distinct().sorted().toArray();
            return this;
        }

        public NameValidator build()
        {
            return new NameValidator( this );
        }
    }

    static void main()
    {
        for ( int cp = Character.MIN_CODE_POINT; cp <= Character.MAX_CODE_POINT; cp++ )
        {
            if ( Character.getType( cp ) == Character.CURRENCY_SYMBOL )
            {
                System.out.printf( "U+%04X %s %s%n", cp, toUCode( cp ), Character.isJavaIdentifierStart( cp ) );
            }
        }
    }
}
