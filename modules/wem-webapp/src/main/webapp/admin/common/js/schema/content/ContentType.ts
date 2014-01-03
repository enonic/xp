module api.schema.content{

    export class ContentType extends ContentTypeSummary {

        private form:api.form.Form;

        constructor(json:api.schema.content.json.ContentTypeJson) {
            super(json);
            console.log(json.form);
            this.form = api.form.FormItemFactory.createForm(json.form);
        }

        getForm():api.form.Form {
            return this.form;
        }

    }
}