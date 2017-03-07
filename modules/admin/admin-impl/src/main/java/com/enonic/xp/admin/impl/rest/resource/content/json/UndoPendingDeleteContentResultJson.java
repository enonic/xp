package com.enonic.xp.admin.impl.rest.resource.content.json;

public class UndoPendingDeleteContentResultJson
{
    private Integer success = 0;

    @SuppressWarnings("unused")
    public Integer getSuccess()
    {
        return success;
    }

    public UndoPendingDeleteContentResultJson setSuccess( final Integer success )
    {
        if ( success != null )
        {
            this.success = success;
        }

        return this;
    }
}
