module api_template {

    export class Template extends api_item.BaseItem {

        static fromExtModel(model:Ext_data_Model):Template {
            return new api_template.Template(model.raw);
        }

        constructor(json:any) {
            super(json);

        }

    }

}