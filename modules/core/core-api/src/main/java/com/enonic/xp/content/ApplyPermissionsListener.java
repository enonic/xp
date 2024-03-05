package com.enonic.xp.content;

public interface ApplyPermissionsListener
{
    ApplyPermissionsListener EMPTY = new ApplyPermissionsListener()
    {
        @Override
        public void setTotal( final int count )
        {
        }

        @Override
        public void permissionsApplied( final int count )
        {
        }

        @Override
        public void notEnoughRights( final int count )
        {
        }
    };

    void setTotal( int count );

    void permissionsApplied( int count );

    void notEnoughRights( int count );
}
