package com.enonic.xp.core.impl.content;

import com.enonic.xp.data.Property;
import com.enonic.xp.data.PropertySet;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.data.PropertyVisitor;
import com.enonic.xp.form.Form;
import com.enonic.xp.form.FormItemPath;
import com.enonic.xp.form.FormItems;
import com.enonic.xp.form.Input;
import com.enonic.xp.form.inputtype.InputType;

public class PropertyTreeFormTranslator
{
    public static PropertyTree transform( final PropertyTree propertyTree, final Form form )
    {
        final TreeTransformerVisitor treeTransformerVisitor = new TreeTransformerVisitor( form.getFormItems() );

        treeTransformerVisitor.traverse( propertyTree );

        return treeTransformerVisitor.getTransformedPropertyTree();
    }

    private static class TreeTransformerVisitor
        extends PropertyVisitor
    {
        private final FormItems formItems;

        final PropertyTree transformedPropertyTree = new PropertyTree();

        public TreeTransformerVisitor( final FormItems formItems )
        {
            this.formItems = formItems;
        }

        @Override
        public void visit( final Property property )
        {
            final PropertySet parentSet = resolveOrAddParent( property );

            final Input input = this.formItems.getInput( FormItemPath.from( property.getPath().toString() ) );

            if ( input == null )
            {
                // No schema for this, should this be allowed?
                parentSet.addProperty( property.getName(), property.getValue() );
            }
            else
            {
                final InputType inputType = input.getInputType();
                inputType.checkBreaksRequiredContract( property );

                parentSet.addProperty( property.getName(), inputType.newValue( property.getString() ) );
            }
        }

        private PropertySet resolveOrAddParent( final Property property )
        {
            final PropertySet parentSet = property.getParent();

            if ( parentSet.getProperty() == null )
            {
                return transformedPropertyTree.getRoot();
            }

            final Property parentSetProperty = parentSet.getProperty();

            PropertySet transformedParent = transformedPropertyTree.getSet( parentSetProperty.getPath() );

            if ( transformedParent == null )
            {
                final PropertySet parent = resolveOrAddParent( parentSetProperty );
                transformedParent = parent.addSet( parentSetProperty.getName() );
            }

            return transformedParent;
        }

        public PropertyTree getTransformedPropertyTree()
        {
            return transformedPropertyTree;
        }
    }
}
