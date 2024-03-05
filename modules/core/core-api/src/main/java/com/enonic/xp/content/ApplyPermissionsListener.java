package com.enonic.xp.content;

public interface ApplyPermissionsListener
{
    void setTotal( int count );

    void permissionsApplied( int count );

    void notEnoughRights( int count );
}
