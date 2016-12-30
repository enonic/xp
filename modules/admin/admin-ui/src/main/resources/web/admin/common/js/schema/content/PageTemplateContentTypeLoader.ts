module api.schema.content {

    import ApplicationKey = api.application.ApplicationKey;

    export class PageTemplateContentTypeLoader extends api.util.loader.BaseLoader<ContentTypeSummaryListJson, ContentTypeSummary> {

        private contentId: api.content.ContentId;
        constructor(contentId: api.content.ContentId) {
            super(new GetAllContentTypesRequest());
            this.contentId = contentId;
        }

        filterFn(contentType: ContentTypeSummary) {
            return contentType.getContentTypeName().toString().indexOf(this.getSearchString().toLowerCase()) != -1;
        }

        sendRequest(): wemQ.Promise<ContentTypeSummary[]> {
            return new GetAllContentTypesRequest().sendAndParse().then((contentTypeArray: ContentTypeSummary[]) => {
                return new api.content.resource.GetNearestSiteRequest(this.contentId).sendAndParse().then(
                    (parentSite: api.content.site.Site) => {
                    let typesAllowedEverywhere: {[key:string]: ContentTypeName} = {};
                    [ContentTypeName.UNSTRUCTURED, ContentTypeName.FOLDER, ContentTypeName.SITE,
                        ContentTypeName.SHORTCUT].forEach((contentTypeName: ContentTypeName) => {
                            typesAllowedEverywhere[contentTypeName.toString()] = contentTypeName;
                        });
                    let siteApplications: {[key:string]: ApplicationKey} = {};
                    parentSite.getApplicationKeys().forEach((applicationKey: ApplicationKey) => {
                        siteApplications[applicationKey.toString()] = applicationKey;
                    });

                    let results = contentTypeArray.filter((item: ContentTypeSummary) => {
                        let contentTypeName = item.getContentTypeName();
                        if (item.isAbstract()) {
                            return false;
                        }
                        else if (contentTypeName.isDescendantOfMedia()) {
                            return true;
                        }
                        else if (typesAllowedEverywhere[contentTypeName.toString()]) {
                            return true;
                        }
                        else if (siteApplications[contentTypeName.getApplicationKey().toString()]) {
                            return true;
                        }
                        else {
                            return false;
                        }

                    });

                    return results;
                });
            });
        }
    }

}