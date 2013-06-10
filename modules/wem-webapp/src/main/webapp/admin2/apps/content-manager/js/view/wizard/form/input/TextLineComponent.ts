module admin.ui {

    export class TextLine extends admin.ui.BaseInputComponent {

        ext;

        constructor(input:api_schema_content_form.Input) {
            super(input);

            var fieldContainer = <any> Ext.create('Ext.form.FieldContainer');
            fieldContainer.setFieldLabel('');
            fieldContainer.labelWidth = 110;
            fieldContainer.labelPad = 0;
            // more base stuff...

            var textField = <any> Ext.create('Ext.form.Text');
            textField.enableKeyEvents = true;
            textField.displayNameSource = true;
            //textField.name = this.name;
            //textField.value = this.value;
            fieldContainer.add(textField);

            this.ext = fieldContainer;
        }

        setValue(value:string, arrayIndex:number) {

            this.ext.down('textfield').setValue(value);

            super.setValue(value, arrayIndex)
        }
    }
}