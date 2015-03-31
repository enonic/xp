module api.content {

    export class CountItemsWithChildrenRequest extends ContentResourceRequest<any, any> {

        private contentPaths:ContentPath[] = [];

        constructor(contentPath?:ContentPath) {
            super();
            super.setMethod("POST");
            if (contentPath) {
                this.addContentPath(contentPath);
            }
        }

        setContentPaths(contentPaths:ContentPath[]):CountItemsWithChildrenRequest {
            this.contentPaths = contentPaths;
            return this;
        }

        addContentPath(contentPath:ContentPath):CountItemsWithChildrenRequest {
            this.contentPaths.push(contentPath);
            return this;
        }

        getParams():Object {
            var fn = (contentPath:ContentPath) => {
                return contentPath.toString();
            };
            return {
                contentPaths: this.contentPaths.map(fn)
            };
        }

        getRequestPath():api.rest.Path {
            return api.rest.Path.fromParent(super.getResourcePath(), "countItemsWithChildren");
        }

        sendAndParse(): wemQ.Promise<any> {

            return this.send().
                then((response: api.rest.JsonResponse<any>) => {

                    return response.getResult();

                });
        }
    }
}