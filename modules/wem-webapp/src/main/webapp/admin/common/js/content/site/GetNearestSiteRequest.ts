module api.content.site {

    export class GetNearestSiteRequest extends SiteResourceRequest<api.content.json.ContentJson> {

        private contentId: api.content.ContentId;

        constructor(contentId: api.content.ContentId) {
            super();
            super.setMethod("POST");
            this.contentId = contentId;
        }

        getParams(): Object {
            return {
                contentId: this.contentId.toString()
            };
        }

        getRequestPath(): api.rest.Path {
            return api.rest.Path.fromParent(super.getResourcePath(), "nearest");
        }

        sendAndParse(): Q.Promise<api.content.Content> {

            var deferred = Q.defer<api.content.Content>();

            this.send().done((response: api.rest.JsonResponse<api.content.json.ContentJson>) => {
                var siteContent = null;
                if( !response.isBlank() ) {
                    siteContent = this.fromJsonToContent(response.getResult());
                }
                deferred.resolve(siteContent);
            }).fail((response: api.rest.RequestError) => {
                    deferred.reject(null);
                });

            return deferred.promise;
        }
    }
}