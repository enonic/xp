module api.schema.content {

    export class GetContentTypeByNameRequest extends ContentTypeResourceRequest<json.ContentTypeJson, ContentType> {

        private name: ContentTypeName;

        private mixinReferencesToFormItems: boolean = true;

        constructor(name: ContentTypeName) {
            super();
            super.setMethod("GET");
            this.name = name;
        }

        getParams(): Object {
            return {
                name: this.name.toString(),
                mixinReferencesToFormItems: this.mixinReferencesToFormItems
            };
        }

        getRequestPath(): api.rest.Path {
            return super.getResourcePath();
        }

        sendAndParse(): wemQ.Promise<ContentType> {

            var contentTypeCache = ContentTypeCache.get();
            var contentType = contentTypeCache.getByKey(this.name);
            if (contentType) {
                return Q(contentType);
            }
            else {
                return this.send().then((response: api.rest.JsonResponse<json.ContentTypeJson>) => {
                    contentType = this.fromJsonToContentType(response.getResult());
                    contentTypeCache.put(contentType);
                    return  contentType;
                });
            }
        }
    }
}