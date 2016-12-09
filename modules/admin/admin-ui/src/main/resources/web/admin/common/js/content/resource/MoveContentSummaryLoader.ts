module api.content.resource {

    import LoadedDataEvent = api.util.loader.event.LoadedDataEvent;
    import LoadingDataEvent = api.util.loader.event.LoadingDataEvent;
    import QueryField = api.query.QueryField;
    import GetContentTypeByNameRequest = api.schema.content.GetContentTypeByNameRequest;
    import ContentType = api.schema.content.ContentType;
    import ContentTypeName = api.schema.content.ContentTypeName;

    export class MoveContentSummaryLoader extends ContentSummaryPreLoader {

        protected request: MoveAllowedTargetsRequest;

        private filterContentPaths: ContentPath[];

        private filterContentTypes: ContentType[];

        protected createRequest(): MoveAllowedTargetsRequest {
            return new MoveAllowedTargetsRequest();
        }

        protected getRequest(): MoveAllowedTargetsRequest {
            return this.request;
        }

        setSize(size: number) {
            this.getRequest().setSize(size);
        }

        setFilterContentPaths(contentPaths: ContentPath[]) {
            this.filterContentPaths = contentPaths;
            this.getRequest().setFilterContentPaths(contentPaths);
            const path = contentPaths.length === 1 ? contentPaths[0].getParentPath() : null;
            this.getRequest().setContentPath(path);
        }

        setFilterContentTypes(contentTypes: ContentType[]) {
            this.filterContentTypes = contentTypes;
        }

        search(searchString: string): wemQ.Promise<ContentSummary[]> {
            this.getRequest().setSearchString(searchString);
            return this.load();
        }

        resetSearchString() {
            this.getRequest().setSearchString("");
        }

        load(postLoad: boolean = false): wemQ.Promise<ContentSummary[]> {

            this.notifyLoadingData(postLoad);

            return this.sendRequest().then((contents: ContentSummary[]) => {
                var deferred = wemQ.defer<ContentSummary[]>();

                const allContentTypes = contents.map((content)=> content.getType());
                const contentTypes = api.util.ArrayHelper.removeDuplicates(allContentTypes, (ct) => ct.toString());
                const contentTypeRequests = contentTypes.map((contentType)=> new GetContentTypeByNameRequest(contentType).sendAndParse());

                wemQ.all(contentTypeRequests).spread((...contentTypes: ContentType[]) => {
                    if (this.filterContentPaths) {
                        contents = this.filterContent(contents, contentTypes);
                    }
                    if (contents && contents.length > 0) {
                        /*
                         We don't need sorting on the client - items must be sorted on the server before they are fed to the pager
                         if (!this.contentSummaryRequest.getSearchString() || this.contentSummaryRequest.getSearchString().length == 0) {
                         contents.sort(new api.content.util.ContentByPathComparator().compare);
                         }
                         */
                        this.notifyLoadedData(contents);
                    } else {
                        this.notifyLoadedData([]);
                    }

                    deferred.resolve(contents);
                }).catch((reason: any) => deferred.reject(reason)).done();

                return deferred.promise;
            });
        }

        isPartiallyLoaded(): boolean {
            return this.getRequest().isPartiallyLoaded();
        }

        resetParams() {
            this.getRequest().resetParams()
        }

        private filterContent(contents: ContentSummary[], contentTypes: ContentType[]): ContentSummary[] {
            var contentTypeAllowsChild: { [s: string]: boolean; } = {};
            contentTypes.forEach((contentType)=> contentTypeAllowsChild[contentType.getName()] = contentType.isAllowChildContent());

            var createContentFilter = new api.content.util.CreateContentFilter();

            var filtered = contents.slice(0);

            if (this.filterContentPaths.length === 1) {
                const contentPath = this.filterContentPaths[0];
                return filtered.filter((content: ContentSummary) => {
                    return !content.getPath().isDescendantOf(contentPath) && !contentPath.isChildOf(content.getPath()) &&
                           !contentPath.equals(content.getPath()) &&
                           contentTypeAllowsChild[content.getType().toString()] &&
                           createContentFilter.isCreateContentAllowed(content, this.filterContentTypes[0]);
                });
            }

            // Optimize filter for multiple paths
            filtered = filtered.filter((content: ContentSummary) => {
                return contentTypeAllowsChild[content.getType().toString()] &&
                       this.filterContentTypes.every(type => createContentFilter.isCreateContentAllowed(content, type));
            });

            this.filterContentPaths.forEach((contentPath) => {
                filtered = filtered.filter((content: ContentSummary) => {
                    return !content.getPath().isDescendantOf(contentPath) && !contentPath.isChildOf(content.getPath()) &&
                           !contentPath.equals(content.getPath());
                });
            });

            return filtered;
        }

    }

}
