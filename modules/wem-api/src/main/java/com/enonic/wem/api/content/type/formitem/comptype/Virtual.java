package com.enonic.wem.api.content.type.formitem.comptype;


import com.enonic.wem.api.content.data.Data;
import com.enonic.wem.api.content.datatype.DataType;
import com.enonic.wem.api.content.datatype.DataTypes;
import com.enonic.wem.api.content.type.formitem.BreaksRequiredContractException;

public class Virtual
    extends BaseComponentType
{
    private String className;

    public Virtual()
    {
        super( "virtual", DataTypes.COMPUTED );
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
        // never - the referred component are checked instead
    }

    @Override
    public void ensureType( final Data data )
    {
        // TODO:
    }
}
