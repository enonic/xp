package com.enonic.xp.node;

public interface ApplyNodePermissionsListener
{
    void setTotal( int count );

    void permissionsApplied( int count );

    void notEnoughRights( int count );
}
