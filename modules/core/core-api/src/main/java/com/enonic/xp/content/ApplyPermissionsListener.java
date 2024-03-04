package com.enonic.xp.content;

public interface ApplyPermissionsListener
{
    default void setTotal( int count )
    {
    }

    default void permissionsApplied( int count )
    {
    }

    default void notEnoughRights( int count )
    {
    }
}
