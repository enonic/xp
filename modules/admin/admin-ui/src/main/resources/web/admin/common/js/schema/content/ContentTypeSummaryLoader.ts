module api.schema.content {

    import BaseLoader = api.util.loader.BaseLoader;
    import ContentTypeSummaryListJson = api.schema.content.ContentTypeSummaryListJson;

    export class ContentTypeSummaryLoader
        extends BaseLoader<ContentTypeSummaryListJson, ContentTypeSummary> {

        constructor(contextDependent: boolean = false, contentId: ContentId) {
            let req = new GetAllContentTypesRequest();
            req.setContextDependent(contextDependent);
            req.setContentContext(contentId);
            super(req);
        }

        filterFn(contentType: ContentTypeSummary) {
            return contentType.getContentTypeName().toString().indexOf(this.getSearchString().toLowerCase()) !== -1;
        }

    }

}
