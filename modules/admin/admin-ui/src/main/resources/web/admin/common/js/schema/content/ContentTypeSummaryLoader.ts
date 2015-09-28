module api.schema.content {


    export class ContentTypeSummaryLoader extends api.util.loader.BaseLoader<api.schema.content.ContentTypeSummaryListJson, ContentTypeSummary> {

        constructor() {
            super(new GetAllContentTypesRequest())
        }

        filterFn(contentType: ContentTypeSummary) {
            return contentType.getContentTypeName().toString().indexOf(this.getSearchString().toLowerCase()) != -1;
        }

    }

}