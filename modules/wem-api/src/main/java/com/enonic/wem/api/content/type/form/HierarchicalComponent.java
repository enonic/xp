package com.enonic.wem.api.content.type.form;


import com.google.common.base.Preconditions;

public abstract class HierarchicalComponent
    extends Component
{
    private ComponentPath path;

    protected HierarchicalComponent()
    {
    }

    void setPath( final ComponentPath path )
    {
        Preconditions.checkNotNull( path, "Given path is null" );
        Preconditions.checkArgument( getName().equals( path.getLastElement() ),
                                     "Last element of path must be equal to name [%s]: " + path.getLastElement(), getName() );
        this.path = path;
    }

    void setParentPath( final ComponentPath parentPath )
    {
        Preconditions.checkNotNull( parentPath, "parentPath cannot be null" );

        if ( this.path == null || this.path.elementCount() == 0 )
        {
            throw new IllegalStateException( "Cannot set parent path unless there is already an existing path" );
        }

        this.path = new ComponentPath( parentPath, this.path.getLastElement() );
    }

    public final ComponentPath getPath()
    {
        return path;
    }

    public HierarchicalComponent copy()
    {
        final HierarchicalComponent copy = (HierarchicalComponent) super.copy();
        copy.path = path;
        return copy;
    }

    @Override
    public String toString()
    {
        final ComponentPath componentPath = getPath();
        if ( componentPath != null )
        {
            return componentPath.toString();
        }
        else
        {
            return getName() + "?";
        }
    }
}
