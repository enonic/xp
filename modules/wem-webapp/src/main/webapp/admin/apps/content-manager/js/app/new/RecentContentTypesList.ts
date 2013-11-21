module app_new {

    export class RecentContentTypesList extends ContentTypesList implements api_event.Observable {

        constructor(className?:string) {
            super("RecentContentTypesList", "Recent", className);
        }

        setContentTypes(contentTypes:ContentTypes, siteRootContentTypes:SiteRootContentTypes) {

            var filtered = contentTypes.filter(RecentContentTypes.get().getRecentContentTypes());
            super.setContentTypes(filtered, siteRootContentTypes);
        }
    }
}