module app_new {

    export class RecentContentTypesList extends api_dom.DivEl implements api_event.Observable {

        private contentTypesList:ContentTypesList;

        constructor(className?:string) {
            super("RecentContentTypesList", className);

            var h4 = new api_dom.H4El();
            h4.getEl().setInnerHtml("Recent");
            this.appendChild(h4);

            this.contentTypesList = new ContentTypesList();
            this.appendChild(this.contentTypesList);
        }

        addListener(listener:ContentTypesListListener) {
            this.contentTypesList.addListener(listener);
        }

        removeListener(listener:ContentTypesListListener) {
            this.contentTypesList.removeListener(listener);
        }

        refresh() {
            var recentArray:string[] = RecentContentTypes.get().getRecentContentTypes();

            // service returns error if empty array is passed
            if (recentArray.length > 0) {
                api_remote_contenttype.RemoteContentTypeService.contentType_get(
                    {
                        qualifiedNames: recentArray,
                        format: "json"
                    },
                    (result:api_remote_contenttype.GetResult) => {

                        var newContentTypeArray:api_remote_contenttype.ContentType[] = [];
                        result.contentTypes.forEach((contentType:api_remote_contenttype.ContentType) => {
                            newContentTypeArray.push(contentType);
                        });

                        this.contentTypesList.setContentTypes(newContentTypeArray);
                    });
            }

        }
    }

}