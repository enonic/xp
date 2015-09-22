module api.content.page {

    import ContentJson = api.content.json.ContentJson;
    import ListContentResult = api.content.ListContentResult;

    export class GetPageTemplatesRequest extends PageTemplateResourceRequest<ListContentResult<ContentJson>, PageTemplate[]> {

        constructor() {
            super();
            super.setMethod("GET");
        }

        getRequestPath(): api.rest.Path {
            return api.rest.Path.fromParent(super.getResourcePath(), "list");
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
