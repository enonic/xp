module app.create {

    export class RecommendedContentTypesList extends ContentTypesList implements api.event.Observable {

        constructor(className?: string, markRoots?: boolean) {
            super(className, "Recommended", markRoots);
        }

        setContentTypes(contentTypes: ContentTypes, siteRootContentTypes: SiteRootContentTypes) {

            var filtered = contentTypes.filter(RecentContentTypes.get().getRecommendedContentTypes());
            super.setContentTypes(filtered, siteRootContentTypes);
        }
    }
}