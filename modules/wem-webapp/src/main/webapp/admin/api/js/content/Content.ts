module api_content{

    export class Content extends ContentSummary {

        private data:api_content.ContentData;

        constructor(json:api_content_json.ContentJson) {
            super(json);
            this.data = ContentDataFactory.createContentData(json.data);
        }

        getContentData():ContentData {
            return this.data;
        }
    }
}