module api_content_site {

    export class GetNearestSiteRequest extends SiteResourceRequest<api_content_json.ContentJson> {

        private contentId: api_content.ContentId;

        constructor(contentId: api_content.ContentId) {
            super();
            super.setMethod("POST");
            this.contentId = contentId;
        }

        getParams(): Object {
            return {
                contentId: this.contentId.toString()
            };
        }

        getRequestPath(): api_rest.Path {
            return api_rest.Path.fromParent(super.getResourcePath(), "nearest");
        }

        sendAndParse(): JQueryPromise<api_content.Content> {

            var deferred = jQuery.Deferred<api_content.Content>();

            this.send().done((response: api_rest.JsonResponse<api_content_json.ContentJson>) => {
                var siteContent = null;
                if( !response.isBlank() ) {
                    siteContent = this.fromJsonToContent(response.getResult());
                }
                deferred.resolve(siteContent);
            }).fail((response: api_rest.RequestError) => {
                    deferred.reject(null);
                });

            return deferred;
        }
    }
}