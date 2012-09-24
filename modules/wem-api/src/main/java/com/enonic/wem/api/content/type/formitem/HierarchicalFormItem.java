package com.enonic.wem.api.content.type.formitem;


import com.google.common.base.Preconditions;

public abstract class HierarchicalFormItem
    extends FormItem
{
    private FormItemPath path;

    protected HierarchicalFormItem()
    {
    }

    void setPath( final FormItemPath path )
    {
        Preconditions.checkNotNull( path, "Given path is null" );
        Preconditions.checkArgument( getName().equals( path.getLastElement() ),
                                     "Last element of path must be equal to name [%s]: " + path.getLastElement(), getName() );
        this.path = path;
    }

    void setParentPath( final FormItemPath parentPath )
    {
        Preconditions.checkNotNull( parentPath, "parentPath cannot be null" );

        if ( this.path == null || this.path.elementCount() == 0 )
        {
            throw new IllegalStateException( "Cannot set parent path unless there is already an existing path" );
        }

        this.path = new FormItemPath( parentPath, this.path.getLastElement() );
    }

    public final FormItemPath getPath()
    {
        return path;
    }

    public HierarchicalFormItem copy()
    {
        final HierarchicalFormItem formItem = (HierarchicalFormItem) super.copy();
        formItem.path = path;
        return formItem;
    }

    @Override
    public String toString()
    {
        FormItemPath formItemPath = getPath();
        if ( formItemPath != null )
        {
            return formItemPath.toString();
        }
        else
        {
            return getName() + "?";
        }
    }
}
