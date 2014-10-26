package com.enonic.wem.api.xml.serializer;

import com.enonic.wem.api.xml.model.ObjectFactory;
import com.enonic.wem.api.xml.model.XmlForm;

final class XmlFormSerializer
    extends XmlSerializer<XmlForm>
{
    public XmlFormSerializer()
    {
        super( XmlForm.class );
    }

    @Override
    protected Object wrapXml( final XmlForm xml )
    {
        return new ObjectFactory().createForm( xml );
    }
}
