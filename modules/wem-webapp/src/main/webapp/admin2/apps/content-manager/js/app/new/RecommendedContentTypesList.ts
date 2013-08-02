module app_new {

    export class RecommendedContentTypesList extends api_dom.DivEl {

        private contentTypesList:ContentTypesList;

        constructor(className?:string) {
            super("RecommendedContentTypesList", className);

            var h4 = new api_dom.H4El();
            h4.getEl().setInnerHtml("Recommended");
            this.appendChild(h4);

            this.contentTypesList = new ContentTypesList();
            this.appendChild(this.contentTypesList);
        }

        refresh() {
            var recommendedArray:string[] = RecentContentTypes.get().recommendContentTypes();
            var newContentTypeArray:api_remote_contenttype.ContentType[] = [];

            recommendedArray.forEach((qualifiedContentTypeName:string, index:number) => {
                api_remote.RemoteContentTypeService.contentType_get({
                        contentType: qualifiedContentTypeName,
                        format: "json"}
                    , (result:api_remote_contenttype.GetResult) => {
                        newContentTypeArray.push(result.contentType);
                        if (index == recommendedArray.length - 1) {
                            this.contentTypesList.setContentTypes(newContentTypeArray);
                        }
                    });
            });

        }
    }
}