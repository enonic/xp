package com.enonic.xp.core.internal;

import java.util.Arrays;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

import org.jspecify.annotations.NonNull;

import static java.util.Objects.requireNonNull;

public final class NameValidator
{
    /**
     * Legacy illegal characters for node names.
     * Note that they are similar to what filesystems usually prohibit, but allow {@code <>:"}
     */
    public static final String NAME_ILLEGAL_CHARACTERS = "/\\|?*";

    /**
     * Additional illegal characters for names that are likely filenames.
     * <p>
     * From Windows File Naming Conventions: reserved characters {@code <>:"/\|?*}
     */
    public static final String FILENAME_ILLEGAL_CHARACTERS = "<>:\"/\\|?*";

    /**
     * For names that are likely to appear on HTML unescaped.
     */
    public static final String HTML_SPECIAL_CHARACTERS = "<>&\"'";

    public static final NameValidator NAME = builder( NameValidator.class ).invalidChars( NAME_ILLEGAL_CHARACTERS )
        .invalidStartChars( " " )
        .invalidEndChars( " " )
        .validCharTypes( Character.LOWERCASE_LETTER, Character.MODIFIER_LETTER, Character.UPPERCASE_LETTER, Character.TITLECASE_LETTER,
                         Character.OTHER_LETTER, Character.DECIMAL_DIGIT_NUMBER, Character.START_PUNCTUATION, Character.END_PUNCTUATION,
                         Character.INITIAL_QUOTE_PUNCTUATION, Character.FINAL_QUOTE_PUNCTUATION, Character.DASH_PUNCTUATION,
                         Character.CONNECTOR_PUNCTUATION, Character.OTHER_PUNCTUATION, Character.CURRENCY_SYMBOL, Character.MODIFIER_SYMBOL,
                         Character.MATH_SYMBOL, Character.OTHER_SYMBOL )
        .build();

    private final Class<?> type;

    private final int maxLength;

    private final Pattern regex;

    private final int[] invalidChars;

    private final int[] validCharTypes;

    private final int[] invalidStartChars;

    private final int[] invalidEndChars;

    private final Set<String> reservedNames;

    private final ClassValue<NameValidator> extendCache = new ClassValue<>()
    {
        @Override
        protected NameValidator computeValue( final @NonNull Class<?> type )
        {
            return extend( type ).build();
        }
    };

    private NameValidator( final Builder builder )
    {
        this.type = builder.type;
        this.maxLength = builder.maxLength;
        this.regex = builder.regex;
        this.invalidChars = builder.invalidChars;
        this.validCharTypes = builder.validCharTypes;
        this.invalidStartChars = builder.invalidStartChars;
        this.invalidEndChars = builder.invalidEndChars;
        this.reservedNames = Set.of( "_", ".", ".." );
    }

    public String validate( final String name, String typeOverride )
    {
        requireNonNull( name, () -> typeOverride + " must not be null" );

        if ( name.isEmpty() )
        {
            throw new IllegalArgumentException( typeOverride + " must not be empty" );
        }

        if ( reservedNames.contains( name ) )
        {
            throw new IllegalArgumentException( typeOverride + " must not be " + name );
        }

        if ( invalidStartChars != null )
        {
            final char firstChar = name.charAt( 0 );
            if ( Arrays.binarySearch( invalidStartChars, firstChar ) >= 0 )
            {
                throw new IllegalArgumentException( typeOverride + " must not start with '" + toUCode( firstChar ) + "'" );
            }
        }

        if ( invalidEndChars != null )
        {
            final char lastChar = name.charAt( name.length() - 1 );
            if ( Arrays.binarySearch( invalidEndChars, lastChar ) >= 0 )
            {
                throw new IllegalArgumentException( typeOverride + " must not end with '" + toUCode( lastChar ) + "'" );
            }
        }

        if ( maxLength > 0 && name.length() > maxLength )
        {
            throw new IllegalArgumentException( typeOverride + " must not be longer than " + maxLength + " characters" );
        }

        name.chars().filter( this::isInvalidChar ).findFirst().ifPresent( cp -> {
            throw new IllegalArgumentException( typeOverride + " must not contain '" + toUCode( cp ) + "'" );
        } );

        if ( regex != null && !regex.matcher( name ).matches() )
        {
            throw new IllegalArgumentException( typeOverride + " is invalid: " + name );
        }

        return name;
    }

    public String validate( final String name )
    {
        final String simpleName = type.getSimpleName();
        return validate( name, simpleName.isEmpty() ? "Name" : simpleName );
    }

    public Validator withSubject( final String subject )
    {
        return name -> validate( name, subject );
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

    public NameValidator cachedExtend( final Class<?> type )
    {
        return extendCache.get( type );
    }

    public Builder extend( final Class<?> type )
    {
        final Builder b = builder( type );
        b.maxLength = maxLength;
        b.regex = regex;
        b.invalidChars = invalidChars;
        b.invalidStartChars = invalidStartChars;
        b.invalidEndChars = invalidEndChars;
        b.validCharTypes = validCharTypes;
        return b;
    }

    public static Builder builder( final Class<?> type )
    {
        return new Builder( type );
    }

    public static final class Builder
    {

        private final Class<?> type;

        private int maxLength;

        private Pattern regex;

        private int[] invalidChars;

        private int[] validCharTypes;

        private int[] invalidStartChars;

        private int[] invalidEndChars;

        private Builder( final Class<?> type )
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

        public Builder validCharTypes( final int... validCharTypes )
        {
            this.validCharTypes = IntStream.of( validCharTypes ).distinct().sorted().toArray();
            return this;
        }

        public Builder invalidStartChars( final String invalidStartChars )
        {
            this.invalidStartChars = invalidStartChars.chars().sorted().distinct().toArray();
            return this;
        }

        public Builder invalidEndChars( final String invalidEndChars )
        {
            this.invalidEndChars = invalidEndChars.chars().sorted().distinct().toArray();
            return this;
        }

        public NameValidator build()
        {
            return new NameValidator( this );
        }
    }

    @FunctionalInterface
    public interface Validator
    {
        String validate( String value );
    }
}
