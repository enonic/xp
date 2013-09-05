package com.enonic.wem.admin.json.country;

import com.enonic.wem.admin.rest.resource.model.Item;
import com.enonic.wem.core.country.Country;

public class CallingCodeJson
    extends Item
{
    private final Country model;

    public CallingCodeJson( final Country model )
    {
        this.model = model;
    }

    public String getCallingCodeId()
    {
        return this.model.getCallingCode() + "_" + model.getCode();
    }

    public String getCallingCode()
    {
        return "+" + this.model.getCallingCode();
    }

    public String getEnglishName()
    {
        return this.model.getEnglishName();
    }

    public String getLocalName()
    {
        return this.model.getLocalName();
    }

    @Override
    public boolean getEditable()
    {
        return false;
    }

    @Override
    public boolean getDeletable()
    {
        return false;
    }
}
