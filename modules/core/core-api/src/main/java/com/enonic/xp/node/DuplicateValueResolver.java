package com.enonic.xp.node;

import com.google.common.base.Strings;

public class DuplicateValueResolver
{
    final static String COPY_TOKEN = "copy";

    private final static String NAME_SEPARATOR = "-";

    private final static String DISPLAY_NAME_SEPARATOR = " ";

    public String name( final NodeName nodeName )
    {
        return doResolve( nodeName.toString(), NAME_SEPARATOR );
    }

    public String name( final String existingName )
    {
        return doResolve( existingName, NAME_SEPARATOR );
    }

    public String displayName( final NodeName nodeName )
    {
        return doResolve( nodeName.toString(), DISPLAY_NAME_SEPARATOR );
    }

    public String displayName( final String existingName )
    {
        return doResolve( existingName, DISPLAY_NAME_SEPARATOR );
    }

    public String fileName( final String existingName )
    {
        int dotIndex = existingName.lastIndexOf( "." );
        String name = existingName;
        String extension = null;
        if ( dotIndex > -1 )
        {
            name = existingName.substring( 0, dotIndex );
            extension = existingName.substring( dotIndex + 1 );
        }
        return doResolve( name, NAME_SEPARATOR, extension );
    }

    public String getPostfix()
    {
        return COPY_TOKEN;
    }

    private String doResolve( final String existingName, final String separator )
    {
        return doResolve( existingName, separator, null );
    }


    private String doResolve( final String existingName, final String separator, final String extension )
    {
        final DuplicateName duplicateName = DuplicateName.from( existingName, separator, extension, getPostfix() );

        return duplicateName.getNextValue();
    }

    static final class DuplicateName
    {
        Integer copyCounter;

        String baseName;

        String extension;

        private String separator;

        private String postfix;

        private DuplicateName()
        {
        }

        private static String normalizeName( final String name )
        {
            return name.trim();
        }

        private static DuplicateName from( final String name, final String separator, final String extension, final String postfix )
        {
            DuplicateName duplicateName = new DuplicateName();
            duplicateName.separator = separator;
            duplicateName.extension = extension;
            duplicateName.postfix = postfix;

            final String normalizedName = normalizeName( name );

            final int copyTokenIndex = normalizedName.toLowerCase().lastIndexOf( separator + postfix );

            if ( copyTokenIndex > 0 )
            {
                duplicateName.baseName = normalizedName.substring( 0, copyTokenIndex );

                final String counterToken = normalizedName.substring( copyTokenIndex + postfix.length() + 1 );

                if ( isFirstCopy( counterToken ) )
                {
                    duplicateName.copyCounter = 1;
                }
                else if ( hasCopyCountNumber( counterToken, separator ) )
                {
                    duplicateName.copyCounter = new Integer( counterToken.substring( 1 ) );
                }
                else
                {
                    // Has count-token, but not as last element before counter
                    duplicateName.baseName = normalizedName;
                }
            }
            else
            {
                duplicateName.baseName = normalizedName;
            }

            return duplicateName;
        }

        static boolean hasCopyCountNumber( final String counterToken, final String separator )
        {
            return counterToken.matches( separator + "\\d+" );
        }

        static boolean isFirstCopy( final String counterToken )
        {
            return Strings.isNullOrEmpty( counterToken );
        }

        private boolean isCopy()
        {
            return copyCounter != null;
        }

        private boolean hasExtension()
        {
            return extension != null;
        }

        private String getNextValue()
        {
            if ( this.copyCounter == null )
            {
                this.copyCounter = 1;
            }
            else
            {
                this.copyCounter++;
            }

            return this.toString();
        }

        @Override
        public String toString()
        {
            StringBuilder builder = new StringBuilder().append( baseName );
            if ( isCopy() )
            {
                builder.append( this.separator ).append( postfix );
                if ( copyCounter > 1 )
                {
                    builder.append( this.separator ).append( copyCounter );
                }
            }
            if ( hasExtension() )
            {
                builder.append( "." ).append( extension );
            }
            return builder.toString();
        }
    }
}
