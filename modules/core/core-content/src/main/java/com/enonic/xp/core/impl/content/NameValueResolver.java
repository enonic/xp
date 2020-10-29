package com.enonic.xp.core.impl.content;

import static com.google.common.base.Strings.isNullOrEmpty;

abstract class NameValueResolver
{
    private static final String NAME_SEPARATOR = "-";

    public static String name( final String existingName )
    {
        return doResolve( existingName, NAME_SEPARATOR );
    }

    private static String doResolve( final String existingName, final String separator )
    {
        final NewName newName = NewName.from( existingName, separator );

        return newName.getNextValue();
    }

    private static final class NewName
    {
        Integer copyCounter;

        String baseName;

        private String separator;

        private static String normalizeName( final String name )
        {
            return name.trim();
        }

        private static NewName from( final String name, final String separator )
        {
            final NewName newName = new NewName();
            newName.separator = separator;

            final String normalizedName = normalizeName( name );

            final int copyTokenIndex = normalizedName.toLowerCase().lastIndexOf( separator );

            if ( copyTokenIndex > 0 )
            {
                newName.baseName = normalizedName.substring( 0, copyTokenIndex );

                final String counterToken = normalizedName.substring( copyTokenIndex + 1 );

                if ( isFirstCopy( counterToken ) )
                {
                    newName.copyCounter = 1;
                }
                else if ( hasCopyCountNumber( counterToken, separator ) )
                {
                    newName.copyCounter = Integer.valueOf( counterToken.substring( 1 ) );
                }
                else
                {
                    newName.baseName = normalizedName;
                }
            }
            else
            {
                newName.baseName = normalizedName;
            }

            return newName;
        }

        static boolean hasCopyCountNumber( final String counterToken, final String separator )
        {
            return counterToken.matches( separator + "\\d+" );
        }

        static boolean isFirstCopy( final String counterToken )
        {
            return isNullOrEmpty( counterToken );
        }

        private boolean isCopy()
        {
            return copyCounter != null;
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
                builder.append( this.separator );
                builder.append( copyCounter );
            }
            return builder.toString();
        }
    }
}
