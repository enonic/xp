package com.enonic.wem.repo.internal.entity;

import com.google.common.base.Strings;

import com.enonic.xp.core.node.NodeName;

public abstract class DuplicateValueResolver
{
    public final static String COPY_TOKEN = "copy";

    private final static String NAME_SEPARATOR = "-";

    private final static String DISPLAY_NAME_SEPARATOR = " ";

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


    private static String doResolve( final String existingName, final String separator )
    {
        final DuplicateName duplicateName = DuplicateName.from( existingName, separator );

        return duplicateName.getNextValue();
    }

    static final class DuplicateName
    {
        Integer copyCounter;

        String baseName;

        private String separator;

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

        private DuplicateName()
        {
        }

        private static String normalizeName( final String name )
        {
            return name.trim();
        }

        private static DuplicateName from( final String name, final String separator )
        {
            DuplicateName duplicateName = new DuplicateName();
            duplicateName.separator = separator;

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

        @Override
        public String toString()
        {
            return baseName + ( isCopy() ? this.separator + COPY_TOKEN + ( copyCounter > 1 ? this.separator + copyCounter : "" ) : "" );
        }
    }
}
