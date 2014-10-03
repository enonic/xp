module api.content.page {

    import ContentJson = api.content.json.ContentJson;
    import ListContentResult = api.content.ListContentResult;

    export class GetPageTemplatesByCanRenderRequest extends PageTemplateResourceRequest<ListContentResult<ContentJson>, PageTemplate[]> {

        private site: api.content.ContentId;

        private contentTypeName: api.schema.content.ContentTypeName;

        constructor(site: api.content.ContentId, contentTypeName: api.schema.content.ContentTypeName) {
            super();
            this.setMethod("GET");
            this.site = site;
            this.contentTypeName = contentTypeName;
        }

        getParams(): Object {
            return {
                siteId: this.site.toString(),
                contentTypeName: this.contentTypeName.toString()
            }
        }

        getRequestPath(): api.rest.Path {
            return api.rest.Path.fromParent(super.getResourcePath(), "listByCanRender");
        }

        sendAndParse(): wemQ.Promise<PageTemplate[]> {

            return this.send().then((response: api.rest.JsonResponse<ListContentResult<ContentJson>>) => {
                return response.getResult().contents.map((contentJson: ContentJson) => {
                    return this.fromJsonToContent(contentJson);
                });
            });
        }
    }
}