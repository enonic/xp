package com.enonic.wem.admin.json.schema.mixin;

import com.enonic.wem.admin.json.ItemJson;
import com.enonic.wem.admin.json.schema.content.form.FieldSetJson;
import com.enonic.wem.admin.json.schema.content.form.FormItemSetJson;
import com.enonic.wem.admin.json.schema.content.form.MixinReferenceJson;
import com.enonic.wem.admin.json.schema.content.form.inputtype.InputJson;
import com.enonic.wem.admin.rest.resource.schema.SchemaImageUriResolver;
import com.enonic.wem.api.schema.content.form.FieldSet;
import com.enonic.wem.api.schema.content.form.FormItemSet;
import com.enonic.wem.api.schema.content.form.Input;
import com.enonic.wem.api.schema.content.form.MixinReference;
import com.enonic.wem.api.schema.mixin.Mixin;

public class MixinJson
    extends ItemJson
{
    private final Mixin model;

    private final String iconUrl;

    public MixinJson( final Mixin model )
    {
        this.model = model;
        this.iconUrl = SchemaImageUriResolver.resolve( model.getSchemaKey() );
    }

    public String getName()
    {
        return model.getName();
    }

    public String getDisplayName()
    {
        return model.getDisplayName();
    }

    public String getModule()
    {
        return model.getModuleName().toString();
    }

    public FormItemSetJson getFormItemSet()
    {
        if ( model.getFormItem() instanceof FormItemSet )
        {
            return new FormItemSetJson( (FormItemSet) model.getFormItem() );
        }
        return null;
    }

    public FieldSetJson getLayout()
    {
        if ( model.getFormItem() instanceof FieldSet )
        {
            return new FieldSetJson( (FieldSet) model.getFormItem() );
        }
        return null;
    }

    public InputJson getInput()
    {
        if ( model.getFormItem() instanceof Input )
        {
            return new InputJson( (Input) model.getFormItem() );
        }
        return null;
    }

    public MixinReferenceJson getMixinReferenceJson()
    {
        if ( model.getFormItem() instanceof MixinReference )
        {
            return new MixinReferenceJson( (MixinReference) model.getFormItem() );
        }
        return null;
    }

    public String getIconUrl()
    {
        return this.iconUrl;
    }

    @Override
    public boolean getDeletable()
    {
        return false;
    }

    @Override
    public boolean getEditable()
    {
        return false;
    }
}
