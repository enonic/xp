module app_new {

    export class RecommendedContentTypesList extends ContentTypesList implements api_event.Observable {

        constructor(className?: string, markRoots?: boolean) {
            super(className, "Recommended", markRoots);
        }

        setContentTypes(contentTypes: ContentTypes, siteRootContentTypes: SiteRootContentTypes) {

            var filtered = contentTypes.filter(RecentContentTypes.get().getRecommendedContentTypes());
            super.setContentTypes(filtered, siteRootContentTypes);
        }
    }
}