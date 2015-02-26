module api.schema.content {

    import ModuleKey = api.module.ModuleKey;

    export class PageTemplateContentTypeLoader extends api.util.loader.BaseLoader<api.schema.content.ContentTypeSummaryListJson, ContentTypeSummary> {

        private contentId: api.content.ContentId;
        constructor(contentId: api.content.ContentId) {
            super(new GetAllContentTypesRequest());
            this.contentId = contentId;
        }

        filterFn(contentType: ContentTypeSummary) {
            return contentType.getContentTypeName().toString().indexOf(this.getSearchString().toLowerCase()) != -1;
        }

        load(): wemQ.Promise<ContentTypeSummary[]> {

            this.notifyLoadingData();
            return this.sendRequest().then((contentTypeArray: ContentTypeSummary[]) => {
                return new api.content.GetNearestSiteRequest(this.contentId).sendAndParse().then((parentSite: api.content.site.Site) => {
                    var typesAllowedEverywhere: {[key:string]: ContentTypeName} = {};
                    [ContentTypeName.UNSTRUCTURED, ContentTypeName.FOLDER, ContentTypeName.SITE,
                        ContentTypeName.SHORTCUT].forEach((contentTypeName: ContentTypeName) => {
                            typesAllowedEverywhere[contentTypeName.toString()] = contentTypeName;
                        });
                    var siteModules: {[key:string]: ModuleKey} = {};
                    parentSite.getModuleKeys().forEach((moduleKey: ModuleKey) => {
                        siteModules[moduleKey.toString()] = moduleKey;
                    });

                    this.results = contentTypeArray.filter((item: ContentTypeSummary) => {
                        var contentTypeName = item.getContentTypeName();
                        if (item.isAbstract()) {
                            return false;
                        }
                        else if (contentTypeName.isDescendantOfMedia()) {
                            return true;
                        }
                        else if (contentTypeName.isTemplateFolder()) {
                            return true; // template-folder is allowed under site
                        }
                        else if (typesAllowedEverywhere[contentTypeName.toString()]) {
                            return true;
                        }
                        else if (siteModules[contentTypeName.getModuleKey().toString()]) {
                            return true;
                        }
                        else {
                            return false;
                        }

                    });

                    if (this.comparator) {
                        this.results.sort(this.comparator.compare);
                    }
                    this.notifyLoadedData(this.results);
                    return this.results;
                });
            });
        }

    }

}