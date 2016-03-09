module api.content {

    import SvgContentSource = api.content.json.SvgContentSource;

    export class GetSvgContentSourceRequest extends api.rest.ResourceRequest<SvgContentSource, string> {

        private id: ContentId;

        constructor(id: ContentId) {
            super();
            this.id = id;
        }

        getParams(): Object {
            return {
                id: this.id.toString()
            };
        }

        getRequestPath(): api.rest.Path {
            return api.rest.Path.fromParent(super.getRestPath(), "content/getSvgContentSource");
        }

        sendAndParse(): wemQ.Promise<string> {

            return this.send().then((response: api.rest.JsonResponse<SvgContentSource>) => {
                return response.getResult().svgSource;
            });
        }
    }
}
