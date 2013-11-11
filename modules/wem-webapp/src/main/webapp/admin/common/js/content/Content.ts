module api_content{

    export class Content extends ContentSummary {

        private data:api_content.ContentData;

        private form:api_form.Form;

        constructor(json:api_content_json.ContentJson) {
            super(json);
            this.data = ContentDataFactory.createContentData(json.data);
            this.form = json.form != null ? new api_form.Form(json.form) : null;
        }

        getContentData():ContentData {
            return this.data;
        }

        getForm():api_form.Form {
            return this.form;
        }
    }
}