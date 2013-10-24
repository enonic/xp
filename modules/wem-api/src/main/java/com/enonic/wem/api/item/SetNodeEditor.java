package com.enonic.wem.api.item;


import com.enonic.wem.api.Icon;
import com.enonic.wem.api.data.RootDataSet;

public class SetNodeEditor
    implements NodeEditor
{
    private final String name;

    private final Icon icon;

    private final RootDataSet data;

    private SetNodeEditor( final Builder builder )
    {
        this.name = builder.name;
        this.icon = builder.icon;
        this.data = builder.data;
    }


    @Override
    public Node edit( final Node toBeEdited )
    {
        Node.Builder builder = Node.newNode( toBeEdited );

        boolean changed = false;
        if ( !toBeEdited.name().equals( this.name ) )
        {
            builder.name( this.name );
            changed = true;
        }
        if ( !toBeEdited.icon().equals( this.icon ) )
        {
            builder.icon( this.icon );
            changed = true;
        }
        builder.rootDataSet( this.data );
        changed = true;

        if ( changed )
        {
            return builder.build();
        }
        else
        {
            return null;
        }
    }

    public static Builder newSetItemEditor()
    {
        return new Builder();
    }

    public static class Builder
    {
        private String name;

        private Icon icon;

        private RootDataSet data;

        public Builder name( String value )
        {
            this.name = value;
            return this;
        }

        public Builder icon( Icon value )
        {
            this.icon = value;
            return this;
        }

        public Builder data( RootDataSet value )
        {
            this.data = value;
            return this;
        }

        public SetNodeEditor build()
        {
            return new SetNodeEditor( this );
        }
    }
}
