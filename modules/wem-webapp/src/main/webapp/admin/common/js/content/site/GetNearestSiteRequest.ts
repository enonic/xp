module api_content_site {

    export class GetNearestSiteRequest extends SiteResourceRequest<api_content_json.ContentJson> {

        private contentId: string;

        constructor(contentId: string) {
            super();
            super.setMethod("POST");
            this.contentId = contentId;
        }

        getParams(): Object {
            return {
                contentId: this.contentId
            };
        }

        getRequestPath(): api_rest.Path {
            return api_rest.Path.fromParent(super.getResourcePath(), "nearest");
        }
    }
}