package com.enonic.xp.content;

public interface DeleteContentListener
{
    void contentDeleted( int count );

    void setTotal( int count );
}
