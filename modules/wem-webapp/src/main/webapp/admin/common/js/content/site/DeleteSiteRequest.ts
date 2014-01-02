module api.content.site {

    export class DeleteSiteRequest extends SiteResourceRequest<api.content.json.ContentJson> {

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

        getRequestPath(): api.rest.Path {
            return api.rest.Path.fromParent(super.getResourcePath(), "delete");
        }
    }
}