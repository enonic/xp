package com.enonic.wem.api.data.data;

public class DataSetArray
    extends DataArray<DataSet>
{
    private DataSetArray( final Builder builder )
    {
        super( builder.parent, builder.name );
    }

    void checkType( final DataSet dataSet )
    {
        // nothing to check for DataSet
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
