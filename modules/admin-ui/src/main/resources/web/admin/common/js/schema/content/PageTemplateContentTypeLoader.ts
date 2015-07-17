module api.schema.content {

    import ApplicationKey = api.module.ApplicationKey;

    export class PageTemplateContentTypeLoader extends api.util.loader.BaseLoader<api.schema.content.ContentTypeSummaryListJson, ContentTypeSummary> {

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
                return new api.content.GetNearestSiteRequest(this.contentId).sendAndParse().then((parentSite: api.content.site.Site) => {
                    var typesAllowedEverywhere: {[key:string]: ContentTypeName} = {};
                    [ContentTypeName.UNSTRUCTURED, ContentTypeName.FOLDER, ContentTypeName.SITE,
                        ContentTypeName.SHORTCUT].forEach((contentTypeName: ContentTypeName) => {
                            typesAllowedEverywhere[contentTypeName.toString()] = contentTypeName;
                        });
                    var siteModules: {[key:string]: ApplicationKey} = {};
                    parentSite.getApplicationKeys().forEach((applicationKey: ApplicationKey) => {
                        siteModules[applicationKey.toString()] = applicationKey;
                    });

                    var results = contentTypeArray.filter((item: ContentTypeSummary) => {
                        var contentTypeName = item.getContentTypeName();
                        if (item.isAbstract()) {
                            return false;
                        }
                        else if (contentTypeName.isDescendantOfMedia()) {
                            return true;
                        }
                        else if (typesAllowedEverywhere[contentTypeName.toString()]) {
                            return true;
                        }
                        else if (siteModules[contentTypeName.getApplicationKey().toString()]) {
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