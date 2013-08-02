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
            var newContentTypeArray:api_remote_contenttype.ContentType[] = [];

            api_remote.RemoteContentTypeService.contentType_get({
                    qualifiedNames: recentArray,
                    format: "json"}
                , (result:api_remote_contenttype.GetResult) => {

                    result.contentTypes.forEach((contentType:api_remote_contenttype.ContentType) => {
                        newContentTypeArray.push(contentType);
                    });

                    this.contentTypesList.setContentTypes(newContentTypeArray);
                });
        }
    }

}