package com.enonic.wem.xml;

public interface XmlObject<I, O>
{
    void from( final I input );

    void to( final O output );
}
