package com.enonic.xp.content;

public interface DuplicateContentListener
{
    void contentDuplicated( int count );

    void contentReferencesUpdated( int count );
}
