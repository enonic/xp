package com.enonic.wem.xml;

public interface XmlObject<I, O>
{
    public void from( final I input );

    public void to( final O output );
}
