module api_schema_content{

    export class ContentType extends ContentTypeSummary {

        private form:api_schema_content_form.Form;

        constructor(json:api_schema_content_json.ContentTypeJson) {
            super(json);
            this.form = api_schema_content_form.FormItemFactory.createForm(json.form);
        }

        getForm():api_schema_content_form.Form {
            return this.form;
        }

    }
}