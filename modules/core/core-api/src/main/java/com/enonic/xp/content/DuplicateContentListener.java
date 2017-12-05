package com.enonic.xp.content;

public interface DuplicateContentListener
{
    void setTotal( int count );

    void contentDuplicated( int count );
}
