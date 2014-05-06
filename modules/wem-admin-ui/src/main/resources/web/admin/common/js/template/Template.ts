module api.template {

    export class Template extends api.item.BaseItem {

        static fromExtModel(model:Ext.data_Model):Template {
            return new api.template.Template(model.raw);
        }

        constructor(json:any) {
            super(json);

        }

    }

}