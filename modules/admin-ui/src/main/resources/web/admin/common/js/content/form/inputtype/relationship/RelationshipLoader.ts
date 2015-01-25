module api.content.form.inputtype.relationship {

    /**
     * Extends ContentSummaryLoader to restrict requests before allowed content types are set.
     * If search() method was called before allowed content types are set
     * then search string is preserved and request postponed.
     * After content types are set, search request is made with latest preserved search string.
     */
    export class RelationshipLoader extends api.content.ContentSummaryLoader {

        private allowedContentTypes:string[];

        private postponedSearchString:string = null;

        setAllowedContentTypes(contentTypes:string[]) {
            this.allowedContentTypes = contentTypes;
            super.setAllowedContentTypes(contentTypes);

            if (this.postponedSearchString != null) {
                this.search(this.postponedSearchString);
            }
        }

        search(searchString: string): wemQ.Promise<ContentSummary[]> {

            return super.search(searchString);
        }

    }
}