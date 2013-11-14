module app_new {

    export class RecommendedContentTypesList extends BaseContentTypesListView implements api_event.Observable {

        constructor(className?:string) {
            super("RecommendedContentTypesList", "Recommended", className);
        }

        refresh() {
            var recommendedArray:api_schema_content.ContentTypeName[] = RecentContentTypes.get().getRecommendedContentTypes();

            this.refreshContentTypes(recommendedArray);
        }
    }
}