module app.create {

    export class RecentContentTypesList extends ContentTypesList {

        constructor(className?: string, markRoots?: boolean) {
            super(className, "Recently used", markRoots);
        }

        setContentTypes(contentTypes: ContentTypes, siteRootContentTypes: SiteRootContentTypes) {

            var filtered = contentTypes.filter(RecentContentTypes.get().getRecentContentTypes());
            super.setContentTypes(filtered, siteRootContentTypes);
        }
    }
}