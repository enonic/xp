package com.enonic.xp.core.internal;

import java.text.Normalizer;
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

    /**
     * Maximum number of non-spacing marks (Mn) attached to a single base character.
     * Spacing marks (Mc) in the same mark sequence do not reset the count.
     */
    private static final int MAX_NON_SPACING_MARKS_PER_BASE = 4;

    public static final NameValidator NAME = builder( NameValidator.class ).invalidChars( NAME_ILLEGAL_CHARACTERS )
        .invalidStartChars( " " )
        .invalidEndChars( " " )
        .validCharTypes( Character.LOWERCASE_LETTER, Character.MODIFIER_LETTER, Character.UPPERCASE_LETTER, Character.TITLECASE_LETTER,
                         Character.OTHER_LETTER, Character.NON_SPACING_MARK, Character.COMBINING_SPACING_MARK,
                         Character.DECIMAL_DIGIT_NUMBER, Character.START_PUNCTUATION, Character.END_PUNCTUATION,
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

    public String validate( final String name, final String subject )
    {
        requireNonNull( name, () -> subject + " must not be null" );

        if ( name.isEmpty() )
        {
            throw new IllegalArgumentException( subject + " must not be empty" );
        }

        if ( reservedNames.contains( name ) )
        {
            throw new IllegalArgumentException( subject + " must not be " + name );
        }

        if ( invalidStartChars != null )
        {
            final int firstCp = name.codePointAt( 0 );
            if ( Arrays.binarySearch( invalidStartChars, firstCp ) >= 0 )
            {
                throw new IllegalArgumentException( subject + " must not start with '" + toUCode( firstCp ) + "'" );
            }
        }

        if ( invalidEndChars != null )
        {
            final int lastCp = name.codePointBefore( name.length() );
            if ( Arrays.binarySearch( invalidEndChars, lastCp ) >= 0 )
            {
                throw new IllegalArgumentException( subject + " must not end with '" + toUCode( lastCp ) + "'" );
            }
        }

        if ( maxLength > 0 && name.length() > maxLength )
        {
            throw new IllegalArgumentException( subject + " must not be longer than " + maxLength + " characters" );
        }

        if ( !Normalizer.isNormalized( name, Normalizer.Form.NFC ) )
        {
            throw new IllegalArgumentException( subject + " must be in Unicode NFC form" );
        }

        name.codePoints().filter( this::isInvalidChar ).findFirst().ifPresent( cp -> {
            throw new IllegalArgumentException( subject + " must not contain '" + toUCode( cp ) + "'" );
        } );

        validateMarks( name, subject );

        if ( regex != null && !regex.matcher( name ).matches() )
        {
            throw new IllegalArgumentException( subject + " is invalid: " + name );
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

    /**
     * Validates combining marks (Mn, Mc). Expects an NFC string.
     * <p>
     * A mark is attached if the nearest preceding non-mark code point is a letter (L*) or a decimal digit (Nd).
     * A mark sequence inherits the attachment of its first mark. Each check reports its own error:
     * <ul>
     *     <li>invisible mark: a mark that is Default_Ignorable_Code_Point, regardless of position;</li>
     *     <li>unattached mark: a mark at the start of the string or after a non-base character;</li>
     *     <li>too many non-spacing marks: more than {@value #MAX_NON_SPACING_MARKS_PER_BASE} Mn after one base.</li>
     * </ul>
     */
    private static void validateMarks( final String name, final String subject )
    {
        boolean attached = false;
        int nonSpacingMarks = 0;
        for ( int i = 0; i < name.length(); )
        {
            final int cp = name.codePointAt( i );
            final int type = Character.getType( cp );
            if ( isMark( type ) )
            {
                if ( isInvisibleMark( cp ) )
                {
                    throw new IllegalArgumentException( subject + " must not contain invisible mark '" + toUCode( cp ) + "'" );
                }
                if ( !attached )
                {
                    throw new IllegalArgumentException( subject + " must not contain unattached mark '" + toUCode( cp ) + "'" );
                }
                if ( type == Character.NON_SPACING_MARK && ++nonSpacingMarks > MAX_NON_SPACING_MARKS_PER_BASE )
                {
                    throw new IllegalArgumentException(
                        subject + " must not contain more than " + MAX_NON_SPACING_MARKS_PER_BASE + " non-spacing marks per character" );
                }
            }
            else
            {
                attached = Character.isLetter( cp ) || type == Character.DECIMAL_DIGIT_NUMBER;
                nonSpacingMarks = 0;
            }
            i += Character.charCount( cp );
        }
    }

    private static boolean isMark( final int type )
    {
        return type == Character.NON_SPACING_MARK || type == Character.COMBINING_SPACING_MARK;
    }

    /**
     * Marks (Mn) that are also Default_Ignorable_Code_Point:
     * combining grapheme joiner, Khmer inherent vowels, Mongolian free variation selectors, variation selectors.
     */
    private static boolean isInvisibleMark( final int cp )
    {
        return cp == 0x034F || ( cp >= 0x17B4 && cp <= 0x17B5 ) || ( cp >= 0x180B && cp <= 0x180D ) || cp == 0x180F ||
            ( cp >= 0xFE00 && cp <= 0xFE0F ) || ( cp >= 0xE0100 && cp <= 0xE01EF );
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

        return validCharTypes != null && Arrays.binarySearch( validCharTypes, Character.getType( cp ) ) < 0;
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
            this.invalidChars = invalidChars.codePoints().sorted().distinct().toArray();
            return this;
        }

        public Builder validCharTypes( final int... validCharTypes )
        {
            this.validCharTypes = IntStream.of( validCharTypes ).distinct().sorted().toArray();
            return this;
        }

        public Builder invalidStartChars( final String invalidStartChars )
        {
            this.invalidStartChars = invalidStartChars.codePoints().sorted().distinct().toArray();
            return this;
        }

        public Builder invalidEndChars( final String invalidEndChars )
        {
            this.invalidEndChars = invalidEndChars.codePoints().sorted().distinct().toArray();
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