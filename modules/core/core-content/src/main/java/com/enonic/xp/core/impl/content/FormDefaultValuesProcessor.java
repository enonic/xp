package com.enonic.xp.core.impl.content;

import java.util.stream.StreamSupport;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.xp.data.PropertyPath;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.data.Value;
import com.enonic.xp.form.FieldSet;
import com.enonic.xp.form.Form;
import com.enonic.xp.form.FormItem;
import com.enonic.xp.form.Input;
import com.enonic.xp.inputtype.InputTypes;

import static com.enonic.xp.form.FormItemType.FORM_ITEM_SET;
import static com.enonic.xp.form.FormItemType.INPUT;
import static com.enonic.xp.form.FormItemType.LAYOUT;

public final class FormDefaultValuesProcessor
{
    private final static Logger LOG = LoggerFactory.getLogger( CreateContentCommand.class );

    private FormDefaultValuesProcessor()
    {
    }

    public static void process( final Form form, final PropertyTree data )
    {
        processFormItems( form.getFormItems(), data, PropertyPath.from( "" ) );
    }

    private static void processFormItems( final Iterable<FormItem> formItems, final PropertyTree data, final PropertyPath parentPath )
    {
        StreamSupport.stream( formItems.spliterator(), false ).forEach( formItem -> {
            if ( formItem.getType() == INPUT )
            {
                Input input = formItem.toInput();
                if ( input.getDefaultValue() != null )
                {
                    try
                    {
                        final Value defaultValue = InputTypes.BUILTIN.resolve( input.getInputType() ).
                            createDefaultValue( input.getDefaultValue() );
                        if ( defaultValue != null )
                        {
                            data.setProperty( PropertyPath.from( parentPath, input.getName() ), defaultValue );
                        }
                    }
                    catch ( IllegalArgumentException ex )
                    {
                        LOG.warn(
                            "Invalid default value for [" + input.getInputType() + "] input type with name '" + input.getName() + "' : " +
                                ex.toString(), ex );
                    }
                }
            }
            else if ( formItem.getType() == FORM_ITEM_SET )
            {
                processFormItems( formItem.toFormItemSet().getFormItems(), data, PropertyPath.from( parentPath, formItem.getName() ) );
            }
            else if ( formItem.getType() == LAYOUT && formItem.toLayout() instanceof FieldSet )
            {
                processFormItems( (FieldSet) formItem.toLayout(), data, parentPath );
            }
        } );
    }
}
