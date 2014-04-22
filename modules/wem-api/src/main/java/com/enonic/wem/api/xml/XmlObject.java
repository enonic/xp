package com.enonic.wem.api.xml;

public interface XmlObject<I, O>
{
    void from( final I input );

    void to( final O output );
}
