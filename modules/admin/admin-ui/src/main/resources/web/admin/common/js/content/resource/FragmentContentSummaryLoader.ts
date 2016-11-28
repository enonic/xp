module api.content.resource {

    import ContentTypeName = api.schema.content.ContentTypeName;
    
    export class FragmentContentSummaryLoader extends ContentSummaryLoader {

        protected request: FragmentContentSummaryRequest;

        constructor() {
            super();
            super.setAllowedContentTypeNames([ContentTypeName.FRAGMENT]);
        }

        protected createRequest(): FragmentContentSummaryRequest {
            return new FragmentContentSummaryRequest();
        }

        setParentSitePath(parentSitePath: string): FragmentContentSummaryLoader {
            (<FragmentContentSummaryRequest>this.getRequest()).setParentSitePath(parentSitePath);
            return this;
        }

        setAllowedContentTypes(contentTypes: string[]) {
            throw new Error("Only fragments allowed");
        }

        setAllowedContentTypeNames(contentTypeNames: api.schema.content.ContentTypeName[]) {
            throw new Error("Only fragments allowed");
        }

    }

}