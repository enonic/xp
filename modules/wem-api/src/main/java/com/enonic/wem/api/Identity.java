package com.enonic.wem.api;

public interface Identity<KeyType, NameType>
{
    KeyType getKey();

    NameType getName();
}
