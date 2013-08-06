/**
 * NOT IN USE */
module app_ui_wizard {
    export class SpaceStepPanel {

        ext:Ext_form_FieldSet;

        constructor(public data?:Object) {

            var templates = new Ext.data.Store({
                fields: ['code', 'name'],
                data: [
                    {"code": "1", "name": "Tpl1"},
                    {"code": "2", "name": "Tpl2"},
                    {"code": "3", "name": "Tpl3"}
                ]
            });

            var fs = this.ext = new Ext.form.FieldSet({
                stepTitle: 'Space',
                title: 'Template',
                padding: '10px 15px',
                defaults: {
                    width: 600
                },
            });


            var combo = new Ext.form.field.ComboBox({
                fieldLabel: 'Space Template',
                displayField: 'name',
                valueField: 'code',
                store: templates
            });

            fs.add(combo);

        }

    }
}