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

        addSelectedListener(listener:(selectedContentType:api_remote_contenttype.ContentType) => void) {
            this.contentTypesList.addSelectedListener(listener);
        }

        refresh() {
            var recommendedArray:string[] = RecentContentTypes.get().getRecommendedContentTypes();

            // service returns error if empty array is passed
            if (recommendedArray.length > 0) {
                api_remote.RemoteContentTypeService.contentType_get(
                    {
                        qualifiedNames: recommendedArray,
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