package com.enonic.wem.api.xml.mapper;

import com.enonic.wem.api.form.Form;
import com.enonic.wem.api.xml.model.XmlForm;

public final class XmlMappers
{
    private final static XmlFormMapper FORM = new XmlFormMapper();

    public static XmlMapper<Form, XmlForm> form()
    {
        return FORM;
    }
}
