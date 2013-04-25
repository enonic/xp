package com.enonic.wem.api.content.data;

import com.google.common.base.Preconditions;


public class DataSetArray
    extends DataArray
{
    private DataSetArray( final Builder builder )
    {
        super( builder.parent, builder.name );
    }

    @Override
    public DataSet getData( final int i )
    {
        return (DataSet) super.getData( i );
    }

    void checkType( Data data )
    {
        Preconditions.checkArgument( data instanceof DataSet,
                                     "Unexpected type of Data for DataSet array at path [%s]: " + data.getClass().getSimpleName(),
                                     getPath() );
    }

    public static Builder newDataSetArray()
    {
        return new Builder();
    }

    public static class Builder
    {
        private String name;

        private DataSet parent;

        public Builder name( String value )
        {
            this.name = value;
            return this;
        }

        public Builder parent( DataSet value )
        {
            this.parent = value;
            return this;
        }

        public DataSetArray build()
        {
            return new DataSetArray( this );
        }
    }
}
