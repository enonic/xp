package com.enonic.xp.repo.impl.node;

import com.enonic.xp.node.NodeName;

import static com.google.common.base.Strings.isNullOrEmpty;

abstract class DuplicateValueResolver
{
    static final String COPY_TOKEN = "copy";

    private static final String NAME_SEPARATOR = "-";

    private static final String DISPLAY_NAME_SEPARATOR = " ";

    public static String name( final NodeName nodeName )
    {
        return doResolve( nodeName.toString(), NAME_SEPARATOR );
    }

    public static String name( final String existingName )
    {
        return doResolve( existingName, NAME_SEPARATOR );
    }

    public static String displayName( final NodeName nodeName )
    {
        return doResolve( nodeName.toString(), DISPLAY_NAME_SEPARATOR );
    }

    public static String displayName( final String existingName )
    {
        return doResolve( existingName, DISPLAY_NAME_SEPARATOR );
    }

    public static String fileName( final String existingName )
    {
        int dotIndex = existingName.lastIndexOf( '.' );
        String name = existingName;
        String extension = null;
        if ( dotIndex > -1 )
        {
            name = existingName.substring( 0, dotIndex );
            extension = existingName.substring( dotIndex + 1 );
        }
        return doResolve( name, NAME_SEPARATOR, extension );
    }

    private static String doResolve( final String existingName, final String separator )
    {
        return doResolve( existingName, separator, null );
    }


    private static String doResolve( final String existingName, final String separator, final String extension )
    {
        final DuplicateName duplicateName = DuplicateName.from( existingName, separator, extension );

        return duplicateName.getNextValue();
    }

    static final class DuplicateName
    {
        Integer copyCounter;

        String baseName;

        String extension;

        private String separator;

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

        private DuplicateName()
        {
        }

        private static String normalizeName( final String name )
        {
            return name.trim();
        }

        private static DuplicateName from( final String name, final String separator, final String extension )
        {
            DuplicateName duplicateName = new DuplicateName();
            duplicateName.separator = separator;
            duplicateName.extension = extension;

            final String normalizedName = normalizeName( name );

            final int copyTokenIndex = normalizedName.toLowerCase().lastIndexOf( separator + COPY_TOKEN );

            if ( copyTokenIndex > 0 )
            {
                duplicateName.baseName = normalizedName.substring( 0, copyTokenIndex );

                final String counterToken = normalizedName.substring( copyTokenIndex + COPY_TOKEN.length() + 1 );

                if ( isFirstCopy( counterToken ) )
                {
                    duplicateName.copyCounter = 1;
                }
                else if ( hasCopyCountNumber( counterToken, separator ) )
                {
                    duplicateName.copyCounter = Integer.valueOf( counterToken.substring( 1 ) );
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
            return isNullOrEmpty( counterToken );
        }

        @Override
        public String toString()
        {
            StringBuilder builder = new StringBuilder().append( baseName );
            if ( isCopy() )
            {
                builder.append( this.separator ).append( COPY_TOKEN );
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
