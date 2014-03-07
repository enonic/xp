module api.content.page {

    export class IsRenderableRequest extends PageTemplateResourceRequest<boolean> {

        private contentId:api.content.ContentId;

        constructor(contentId:api.content.ContentId) {
            super();
            this.setMethod("GET");
            this.contentId = contentId;
        }

        setContentId(value:api.content.ContentId): IsRenderableRequest {
            this.contentId = value;
            return this;
        }

        getParams():Object {
            return {
                contentId: this.contentId.toString(),
            }
        }

        getRequestPath(): api.rest.Path {
            return api.rest.Path.fromParent(super.getResourcePath(), "isRenderable");
        }
    }
}