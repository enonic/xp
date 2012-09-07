package com.enonic.wem.core.content.type.formitem.fieldtype;


import com.enonic.wem.core.content.data.Data;
import com.enonic.wem.core.content.datatype.DataType;
import com.enonic.wem.core.content.datatype.DataTypes;
import com.enonic.wem.core.content.type.formitem.BreaksRequiredContractException;

public class Virtual
    implements ComponentType
{
    private String className;

    Virtual()
    {
        this.className = this.getClass().getName();
    }

    public String getName()
    {
        return "virtual";
    }

    public String getClassName()
    {
        return className;
    }

    public DataType getDataType()
    {
        return DataTypes.COMPUTED;
    }

    public boolean requiresConfig()
    {
        return false;
    }

    public Class requiredConfigClass()
    {
        return null;
    }

    public AbstractComponentTypeConfigSerializerJson getComponentTypeConfigJsonGenerator()
    {
        return null;
    }

    @Override
    public void checkBreaksRequiredContract( final Data data )
        throws BreaksRequiredContractException
    {
        // never - the referred fields are checked instead
    }

    @Override
    public void ensureType( final Data data )
    {
        // TODO:
    }
}
