package com.enonic.xp.schema.content.validator;

import java.util.ArrayList;
import java.util.List;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

import com.enonic.xp.data.Property;
import com.enonic.xp.data.PropertySet;
import com.enonic.xp.form.FieldSet;
import com.enonic.xp.form.Form;
import com.enonic.xp.form.FormItem;
import com.enonic.xp.form.FormItemSet;
import com.enonic.xp.form.Input;


public final class InputValidator
{
    private final Form form;

    public InputValidator( final Form form )
    {
        Preconditions.checkNotNull( form, "No form given" );
        this.form = form;
    }

    public final void validate( final PropertySet dataSet )
    {
        final List<PropertySet> parentDataSets = Lists.newArrayList();
        parentDataSets.add( dataSet );
        validate( form, parentDataSets );
    }

    private void validate( final Iterable<FormItem> formItems, final List<PropertySet> parentDataSets )
    {
        for ( FormItem formItem : formItems )
        {
            if ( formItem instanceof Input )
            {
                validateInput( (Input) formItem, parentDataSets );
            }
            else if ( formItem instanceof FormItemSet )
            {
                validateFormItemSet( (FormItemSet) formItem, parentDataSets );
            }
            else if ( formItem instanceof FieldSet )
            {
                validate( ( (FieldSet) formItem ).formItemIterable(), parentDataSets );
            }
        }
    }

    private void validateInput( final Input input, final List<PropertySet> parentDataSets )
    {
        for ( PropertySet parentDataSet : parentDataSets )
        {
            final int entryCount = parentDataSet.countProperties( input.getName() );
            for ( int i = 0; i < entryCount; i++ )
            {
                final Property property = parentDataSet.getProperty( input.getName(), i );
                input.checkValidity( property );
            }
        }
    }

    private void validateFormItemSet( final FormItemSet formItemSet, final List<PropertySet> parentDataSets )
    {
        final List<PropertySet> dataSets = getDataSets( formItemSet.getName(), parentDataSets );
        validate( formItemSet.getFormItems(), dataSets );
    }

    private List<PropertySet> getDataSets( final String name, final List<PropertySet> parentDataSets )
    {
        final List<PropertySet> dataSets = new ArrayList<>();
        for ( final PropertySet parentDataSet : parentDataSets )
        {
            for ( final PropertySet set : parentDataSet.getSets( name ) )
            {
                dataSets.add( set );
            }
        }
        return dataSets;
    }
}

