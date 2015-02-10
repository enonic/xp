package com.enonic.wem.api.xml.mapper;

import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.google.common.collect.Lists;

import com.enonic.wem.api.form.FieldSet;
import com.enonic.wem.api.form.Form;
import com.enonic.wem.api.form.FormItem;
import com.enonic.wem.api.form.FormItemSet;
import com.enonic.wem.api.form.FormItems;
import com.enonic.wem.api.form.InlineMixin;
import com.enonic.wem.api.form.Input;
import com.enonic.wem.api.form.Occurrences;
import com.enonic.wem.api.form.ValidationRegex;
import com.enonic.wem.api.form.inputtype.InputType;
import com.enonic.wem.api.form.inputtype.InputTypeConfig;
import com.enonic.wem.api.form.inputtype.InputTypes;
import com.enonic.wem.api.xml.XmlException;
import com.enonic.wem.api.xml.model.XmlFieldSet;
import com.enonic.wem.api.xml.model.XmlForm;
import com.enonic.wem.api.xml.model.XmlFormItem;
import com.enonic.wem.api.xml.model.XmlFormItemSet;
import com.enonic.wem.api.xml.model.XmlFormItems;
import com.enonic.wem.api.xml.model.XmlInline;
import com.enonic.wem.api.xml.model.XmlInput;
import com.enonic.wem.api.xml.model.XmlOccurrence;

public final class XmlFormMapper
{
    public static XmlForm toXml( final Form object )
    {
        final XmlForm result = new XmlForm();
        result.getList().addAll( toItemsXml( object.getFormItems() ).getList() );
        return result;
    }

    public static Form fromXml( final XmlForm xml )
    {
        final Form.Builder builder = Form.newForm();

        if ( xml != null )
        {
            fromXml( xml, builder );
        }

        return builder.build();
    }

    public static void fromXml( final XmlForm xml, final Form.Builder builder )
    {
        builder.addFormItems( fromItemsXml( xml ) );
    }

    public static List<FormItem> fromItemsXml( final XmlFormItems xml )
    {
        final List<FormItem> result = Lists.newArrayList();
        for ( final XmlFormItem item : xml.getList() )
        {
            final FormItem converted = fromItemXml( item );
            result.add( converted );
        }

        return result;
    }

    private static FormItem fromItemXml( final XmlFormItem xml )
    {
        if ( xml instanceof XmlInput )
        {
            return fromItemXml( (XmlInput) xml );
        }

        if ( xml instanceof XmlFormItemSet )
        {
            return fromItemXml( (XmlFormItemSet) xml );
        }

        if ( xml instanceof XmlInline )
        {
            return fromItemXml( (XmlInline) xml );
        }

        if ( xml instanceof XmlFieldSet )
        {
            return fromItemXml( (XmlFieldSet) xml );
        }

        throw new XmlException( "Unknown item type [{0}]", xml.getClass() );
    }

    private static Input fromItemXml( final XmlInput xml )
    {
        final InputType type = InputTypes.parse( xml.getType() );

        final Input.Builder builder = Input.newInput();
        builder.name( xml.getName() );
        builder.label( xml.getLabel() );
        builder.customText( xml.getCustomText() );
        builder.helpText( xml.getHelpText() );
        builder.occurrences( fromOccurenceXml( xml.getOccurrences() ) );
        builder.immutable( xml.isImmutable() != null && xml.isImmutable() );
        builder.indexed( xml.isIndexed() != null && xml.isIndexed() );
        builder.inputType( type );
        builder.validationRegexp( xml.getValidationRegexp() );
        builder.inputTypeConfig( fromConfigXml( type, xml.getConfig() ) );

        return builder.build();
    }

    private static FormItemSet fromItemXml( final XmlFormItemSet xml )
    {
        final FormItemSet.Builder builder = FormItemSet.newFormItemSet();
        builder.name( xml.getName() );
        builder.label( xml.getLabel() );
        builder.customText( xml.getCustomText() );
        builder.helpText( xml.getHelpText() );
        builder.occurrences( fromOccurenceXml( xml.getOccurrences() ) );
        builder.immutable( xml.isImmutable() != null && xml.isImmutable() );
        builder.addFormItems( fromItemsXml( xml.getItems() ) );
        return builder.build();
    }

    private static InlineMixin fromItemXml( final XmlInline xml )
    {
        final InlineMixin.Builder builder = InlineMixin.newInlineMixin();
        builder.mixin( xml.getMixin() );
        return builder.build();
    }

    private static FieldSet fromItemXml( final XmlFieldSet xml )
    {
        final FieldSet.Builder builder = FieldSet.newFieldSet();
        builder.name( xml.getName() );
        builder.label( xml.getLabel() );
        builder.addFormItems( fromItemsXml( xml.getItems() ) );
        return builder.build();
    }

    private static Occurrences fromOccurenceXml( final XmlOccurrence xml )
    {
        final Occurrences.Builder builder = Occurrences.newOccurrences();
        builder.minimum( xml.getMinimum() );
        builder.maximum( xml.getMaximum() );
        return builder.build();
    }

    private static InputTypeConfig fromConfigXml( final InputType type, final Object value )
    {
        if ( !( value instanceof Element ) )
        {
            return null;
        }

        final Element element = (Element) value;
        return type.getInputTypeConfigXmlSerializer().parseConfig( element );
    }

    public static XmlFormItems toItemsXml( final FormItems object )
    {
        final XmlFormItems result = new XmlFormItems();
        for ( final FormItem item : object )
        {
            final XmlFormItem converted = toItemXml( item );
            result.getList().add( converted );
        }

        return result;
    }

    private static XmlFormItem toItemXml( final FormItem object )
    {
        if ( object instanceof Input )
        {
            return toItemXml( (Input) object );
        }

        if ( object instanceof FormItemSet )
        {
            return toItemXml( (FormItemSet) object );
        }

        if ( object instanceof InlineMixin )
        {
            return toItemXml( (InlineMixin) object );
        }

        if ( object instanceof FieldSet )
        {
            return toItemXml( (FieldSet) object );
        }

        throw new XmlException( "Unknown item type [{0}]", object.getClass() );
    }

    private static XmlInput toItemXml( final Input object )
    {
        final InputType type = object.getInputType();

        final XmlInput result = new XmlInput();
        result.setName( object.getName() );
        result.setLabel( object.getLabel() );
        result.setCustomText( object.getCustomText() );
        result.setHelpText( object.getHelpText() );
        result.setOccurrences( toOccurenceXml( object.getOccurrences() ) );
        result.setImmutable( object.isImmutable() );
        result.setIndexed( object.isIndexed() );
        result.setType( type.getName() );

        final ValidationRegex regex = object.getValidationRegexp();
        result.setValidationRegexp( regex != null ? regex.toString() : null );
        result.setConfig( toConfigXml( type, object.getInputTypeConfig() ) );
        return result;
    }

    private static XmlFormItemSet toItemXml( final FormItemSet object )
    {
        final XmlFormItemSet result = new XmlFormItemSet();
        result.setName( object.getName() );
        result.setLabel( object.getLabel() );
        result.setCustomText( object.getCustomText() );
        result.setHelpText( object.getHelpText() );
        result.setOccurrences( toOccurenceXml( object.getOccurrences() ) );
        result.setImmutable( object.isImmutable() );
        result.setItems( toItemsXml( object.getFormItems() ) );
        return result;
    }

    private static XmlInline toItemXml( final InlineMixin object )
    {
        final XmlInline result = new XmlInline();
        result.setMixin( object.getMixinName().toString() );
        return result;
    }

    private static XmlFieldSet toItemXml( final FieldSet object )
    {
        final XmlFieldSet result = new XmlFieldSet();
        result.setName( object.getName() );
        result.setLabel( object.getLabel() );
        result.setItems( toItemsXml( object.getFormItems() ) );
        return result;
    }

    private static XmlOccurrence toOccurenceXml( final Occurrences object )
    {
        final XmlOccurrence result = new XmlOccurrence();
        result.setMinimum( object.getMinimum() );
        result.setMaximum( object.getMaximum() );
        return result;
    }

    @SuppressWarnings("unchecked")
    private static Object toConfigXml( final InputType type, final InputTypeConfig value )
    {
        if ( value == null )
        {
            return null;
        }

        final Document doc = type.getInputTypeConfigXmlSerializer().generate( value );
        return doc.getDocumentElement().getChildNodes();
    }
}
