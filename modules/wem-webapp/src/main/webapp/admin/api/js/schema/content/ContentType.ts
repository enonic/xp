module api_schema_content{

    export class ContentType extends ContentTypeSummary {

        private form:api_form.Form;

        constructor(json:api_schema_content_json.ContentTypeJson) {
            super(json);
            this.form = api_form.FormItemFactory.createForm(json.form);
        }

        getForm():api_form.Form {
            return this.form;
        }

    }
}