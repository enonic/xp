module api.content {

    export class CountContentsWithDescendantsRequest extends ContentResourceRequest<number, number> {

        private contentPaths: ContentPath[] = [];

        constructor(contentPath?: ContentPath) {
            super();
            super.setMethod("POST");
            if (contentPath) {
                this.addContentPath(contentPath);
            }
        }

        setContentPaths(contentPaths: ContentPath[]): CountContentsWithDescendantsRequest {
            this.contentPaths = contentPaths;
            return this;
        }

        addContentPath(contentPath: ContentPath): CountContentsWithDescendantsRequest {
            this.contentPaths.push(contentPath);
            return this;
        }

        getParams(): Object {
            var fn = (contentPath: ContentPath) => {
                return contentPath.toString();
            };
            return {
                contentPaths: this.contentPaths.map(fn)
            };
        }

        getRequestPath(): api.rest.Path {
            return api.rest.Path.fromParent(super.getResourcePath(), "countContentsWithDescendants");
        }

        sendAndParse(): wemQ.Promise<number> {

            return this.send().
                then((response: api.rest.JsonResponse<number>) => {

                    return response.getResult();

                });
        }
    }
}