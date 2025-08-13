package com.enonic.xp.lib.schema;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.NamedType;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import com.enonic.xp.form.FieldSet;
import com.enonic.xp.form.Form;
import com.enonic.xp.form.FormItem;
import com.enonic.xp.form.FormItemSet;
import com.enonic.xp.form.FormOptionSet;
import com.enonic.xp.form.FormOptionSetOption;
import com.enonic.xp.form.InlineMixin;
import com.enonic.xp.form.Input;
import com.enonic.xp.form.Occurrences;
import com.enonic.xp.inputtype.InputTypeDefault;
import com.enonic.xp.inputtype.InputTypeName;
import com.enonic.xp.inputtype.InputTypeProperty;
import com.enonic.xp.lib.schema.mixin.ContentTypeBuilderMixin;
import com.enonic.xp.lib.schema.mixin.ContentTypeMixin;
import com.enonic.xp.lib.schema.mixin.ContentTypeNameMixin;
import com.enonic.xp.lib.schema.mixin.FieldSetBuilderMixin;
import com.enonic.xp.lib.schema.mixin.FieldSetMixin;
import com.enonic.xp.lib.schema.mixin.FormBuilderMixin;
import com.enonic.xp.lib.schema.mixin.FormItemMixin;
import com.enonic.xp.lib.schema.mixin.FormItemSetBuilderMixin;
import com.enonic.xp.lib.schema.mixin.FormItemSetMixin;
import com.enonic.xp.lib.schema.mixin.FormMixin;
import com.enonic.xp.lib.schema.mixin.FormOptionSetBuilderMixin;
import com.enonic.xp.lib.schema.mixin.FormOptionSetMixin;
import com.enonic.xp.lib.schema.mixin.FormOptionSetOptionBuilderMixin;
import com.enonic.xp.lib.schema.mixin.FormOptionSetOptionMixin;
import com.enonic.xp.lib.schema.mixin.InlineMixinBuilderMixin;
import com.enonic.xp.lib.schema.mixin.InlineMixinMixin;
import com.enonic.xp.lib.schema.mixin.InputBuilderMixin;
import com.enonic.xp.lib.schema.mixin.InputMixin;
import com.enonic.xp.lib.schema.mixin.InputTypeDefaultBuilderMixin;
import com.enonic.xp.lib.schema.mixin.InputTypeDefaultMixin;
import com.enonic.xp.lib.schema.mixin.InputTypeNameMixin;
import com.enonic.xp.lib.schema.mixin.InputTypePropertyBuilderMixin;
import com.enonic.xp.lib.schema.mixin.InputTypePropertyMixin;
import com.enonic.xp.lib.schema.mixin.MixinNameMixin;
import com.enonic.xp.lib.schema.mixin.OccurrencesMixin;
import com.enonic.xp.schema.content.ContentType;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.schema.mixin.MixinName;

public final class ContentTypeParser
{
    private static final ObjectMapper MAPPER = new ObjectMapper( new YAMLFactory() );

    static
    {
        MAPPER.addMixIn( ContentType.class, ContentTypeMixin.class );
        MAPPER.addMixIn( ContentType.Builder.class, ContentTypeBuilderMixin.class );
        MAPPER.addMixIn( ContentTypeName.class, ContentTypeNameMixin.class );

        MAPPER.addMixIn( Occurrences.class, OccurrencesMixin.class );

        MAPPER.addMixIn( Form.class, FormMixin.class );
        MAPPER.addMixIn( Form.Builder.class, FormBuilderMixin.class );

        // FormItem and implementations

        MAPPER.addMixIn( FormItem.class, FormItemMixin.class );

        MAPPER.addMixIn( Input.class, InputMixin.class );
        MAPPER.addMixIn( Input.Builder.class, InputBuilderMixin.class );
        MAPPER.addMixIn( InputTypeName.class, InputTypeNameMixin.class );

        MAPPER.addMixIn( FieldSet.class, FieldSetMixin.class );
        MAPPER.addMixIn( FieldSet.Builder.class, FieldSetBuilderMixin.class );

        MAPPER.addMixIn( InlineMixin.class, InlineMixinMixin.class );
        MAPPER.addMixIn( InlineMixin.Builder.class, InlineMixinBuilderMixin.class );
        MAPPER.addMixIn( MixinName.class, MixinNameMixin.class );

        MAPPER.addMixIn( FormItemSet.class, FormItemSetMixin.class );
        MAPPER.addMixIn( FormItemSet.Builder.class, FormItemSetBuilderMixin.class );

        MAPPER.addMixIn( FormOptionSet.class, FormOptionSetMixin.class );
        MAPPER.addMixIn( FormOptionSet.Builder.class, FormOptionSetBuilderMixin.class );

        MAPPER.addMixIn( FormOptionSetOption.class, FormOptionSetOptionMixin.class );
        MAPPER.addMixIn( FormOptionSetOption.Builder.class, FormOptionSetOptionBuilderMixin.class );

        MAPPER.addMixIn( InputTypeProperty.class, InputTypePropertyMixin.class );
        MAPPER.addMixIn( InputTypeProperty.Builder.class, InputTypePropertyBuilderMixin.class );


        MAPPER.addMixIn( InputTypeDefault.class, InputTypeDefaultMixin.class );
        MAPPER.addMixIn( InputTypeDefault.Builder.class, InputTypeDefaultBuilderMixin.class );

        MAPPER.registerSubtypes(
            new NamedType( Input.class, "Input" ),
            new NamedType( FieldSet.class, "FieldSet" ),
            new NamedType( InlineMixin.class, "InlineMixin" ),
            new NamedType( FormItemSet.class, "ItemSet" ),
            new NamedType( FormOptionSet.class, "OptionSet" ),
            new NamedType( FormOptionSetOption.class, "OptionSetOption" )
        );
    }

    public ContentType parse( final String contentTypeAsYml )
        throws Exception
    {
        return MAPPER.readValue( contentTypeAsYml, ContentType.class );
    }

}
