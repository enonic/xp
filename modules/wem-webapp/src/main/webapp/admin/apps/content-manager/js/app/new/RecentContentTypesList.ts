module app_new {

    export class RecentContentTypesList extends BaseContentTypesListView implements api_event.Observable {

        constructor(className?:string) {
            super("RecentContentTypesList", "Recent", className);
        }

        refresh() {
            var recentArray:api_schema_content.ContentTypeName[] = RecentContentTypes.get().getRecentContentTypes();

            this.refreshContentTypes(recentArray);
        }
    }

}