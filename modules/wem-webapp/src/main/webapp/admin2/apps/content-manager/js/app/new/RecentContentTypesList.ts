module app_new {

    export class RecentContentTypesList extends api_dom.DivEl {

        private contentTypesList:ContentTypesList;

        constructor(className?:string) {
            super("RecentContentTypesList", className);

            var h4 = new api_dom.H4El();
            h4.getEl().setInnerHtml("Recent");
            this.appendChild(h4);

            this.contentTypesList = new ContentTypesList();
            this.appendChild(this.contentTypesList);
        }

        refresh() {
            var recentArray:string[] = RecentContentTypes.get().getRecentContentTypes();

            // service returns error if empty array is passed
            if (recentArray.length > 0) {
                api_remote.RemoteContentTypeService.contentType_get({
                        qualifiedNames: recentArray,
                        format: "json"}
                    , (result:api_remote_contenttype.GetResult) => {

                        if (result && result.success) {
                            var newContentTypeArray:api_remote_contenttype.ContentType[] = [];

                            result.contentTypes.forEach((contentType:api_remote_contenttype.ContentType) => {
                                newContentTypeArray.push(contentType);
                            });

                            this.contentTypesList.setContentTypes(newContentTypeArray);
                        } else {
                            console.log('Error getting recent content types');
                        }

                    });
            }

        }
    }

}