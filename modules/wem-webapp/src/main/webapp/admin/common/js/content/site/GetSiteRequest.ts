module api_content_site {

    export class GetSiteRequest extends SiteResourceRequest<api_content_site_json.SiteJson> {

        private siteId:string;

        constructor(siteId:string) {
            super();
            super.setMethod("GET");
            this.siteId = siteId;
        }

        getParams():Object {
            return {
                siteId: this.siteId
            };
        }

        getRequestPath():api_rest.Path {
            return api_rest.Path.fromParent(super.getResourcePath(), "create");
        }
    }
}