module api.content.site {

    export class GetSiteRequest extends SiteResourceRequest<api.content.site.SiteJson, any> {

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

        getRequestPath():api.rest.Path {
            return api.rest.Path.fromParent(super.getResourcePath(), "create");
        }
    }
}