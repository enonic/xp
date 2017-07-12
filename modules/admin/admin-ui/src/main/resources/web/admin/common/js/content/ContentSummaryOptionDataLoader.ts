module api.content {

    import OptionDataLoader = api.ui.selector.OptionDataLoader;
    import TreeNode = api.ui.treegrid.TreeNode;
    import ContentSummaryFetcher = api.content.resource.ContentSummaryFetcher;
    import OptionDataLoaderData = api.ui.selector.OptionDataLoaderData;
    import ContentResponse = api.content.resource.result.ContentResponse;
    import Option = api.ui.selector.Option;
    import ContentQueryRequest = api.content.resource.ContentQueryRequest;
    import ContentTreeSelectorQueryRequest = api.content.resource.ContentTreeSelectorQueryRequest;
    import ContentTreeSelectorItem = api.content.resource.ContentTreeSelectorItem;
    import CompareContentRequest = api.content.resource.CompareContentRequest;
    import CompareContentResults = api.content.resource.result.CompareContentResults;
    import ContentSummaryAndCompareStatusFetcher = api.content.resource.ContentSummaryAndCompareStatusFetcher;
    import ContentAndStatusTreeSelectorItem = api.content.resource.ContentAndStatusTreeSelectorItem;
    import CompareContentResult = api.content.resource.result.CompareContentResult;

    export class ContentSummaryOptionDataLoader implements OptionDataLoader<ContentTreeSelectorItem> {

        protected request: ContentTreeSelectorQueryRequest = new ContentTreeSelectorQueryRequest();

        private loadStatus: boolean;

        constructor(builder?: ContentSummaryOptionDataLoaderBuilder) {
            if (builder) {
                this.loadStatus = builder.loadStatus;

                this.initRequest(builder);
            }
        }

        private initRequest(builder: ContentSummaryOptionDataLoaderBuilder) {
            this.request.setContentTypeNames(builder.contentTypeNames);
            this.request.setAllowedContentPaths(builder.allowedContentPaths);
            this.request.setRelationshipType(builder.relationshipType);
            this.request.setContent(builder.content);
        }

        fetch(node: TreeNode<Option<ContentTreeSelectorItem>>): wemQ.Promise<ContentTreeSelectorItem> {
            this.request.setParentPath(node.getDataId() ? node.getData().displayValue.getPath() : null);
            if (this.request.getContent()) {
                return this.load().then(items => items[0]);
            } else {
                if (this.loadStatus) {
                    return ContentSummaryAndCompareStatusFetcher.fetch(node.getData().displayValue.getContentId()).then(
                        content => new ContentAndStatusTreeSelectorItem(content, false));
                } else {
                    return ContentSummaryFetcher.fetch(node.getData().displayValue.getContentId()).then(
                        content => new ContentAndStatusTreeSelectorItem(ContentSummaryAndCompareStatus.fromContentSummary(content), false));
                }
            }
        }

        fetchChildren(parentNode: TreeNode<Option<ContentTreeSelectorItem>>, from: number = 0,
                      size: number = -1): wemQ.Promise<OptionDataLoaderData<ContentTreeSelectorItem>> {

            if (this.request.getContent()) {
                this.request.setFrom(from);
                this.request.setSize(size);

                this.request.setParentPath(parentNode.getDataId() ? parentNode.getData().displayValue.getPath() : null);

                return this.load().then((result: ContentAndStatusTreeSelectorItem[]) => {
                    return this.createOptionData(result, 0, 0);
                });
            } else {

                if (this.loadStatus) {
                    return ContentSummaryAndCompareStatusFetcher.fetchChildren(
                        parentNode.getData() ? parentNode.getData().displayValue.getContentId() : null, from, size).then(
                        (response: ContentResponse<ContentSummaryAndCompareStatus>) => {

                            return this.createOptionData(response.getContents().map(
                                content => new ContentAndStatusTreeSelectorItem(content, false)),
                                response.getMetadata().getHits(),
                                response.getMetadata().getTotalHits());
                        });
                } else {
                    return ContentSummaryFetcher.fetchChildren(
                        parentNode.getData() ? parentNode.getData().displayValue.getContentId() : null, from, size).then(
                        (response: ContentResponse<ContentSummary>) => {

                            return this.createOptionData(response.getContents().map(
                                content => new ContentAndStatusTreeSelectorItem(ContentSummaryAndCompareStatus.fromContentSummary(
                                    content), false)), response.getMetadata().getHits(), response.getMetadata().getTotalHits());
                        });
                }
            }
        }

        protected createOptionData(data: ContentAndStatusTreeSelectorItem[], hits: number,
                                   totalHits: number): OptionDataLoaderData<ContentTreeSelectorItem> {
            return new OptionDataLoaderData(data, hits, totalHits);
        }

        checkReadonly(items: ContentAndStatusTreeSelectorItem[]): wemQ.Promise<string[]> {
            return ContentSummaryFetcher.getReadOnly(items.map(item => item.getContent()));
        }

        private load(): wemQ.Promise<ContentAndStatusTreeSelectorItem[]> {
            if (this.request.getContent()) {
                return this.request.sendAndParse().then(items => {
                    if (this.loadStatus) {
                        return this.loadStatuses(items);
                    } else {

                        const deferred = wemQ.defer<ContentAndStatusTreeSelectorItem[]>();

                        deferred.resolve(items.map((item: ContentTreeSelectorItem) => {
                            return new ContentAndStatusTreeSelectorItem(ContentSummaryAndCompareStatus.fromContentSummary(
                                item.getContent()), item.getExpand());
                        }));

                        return deferred.promise;
                    }
                });
            }
        }

        private loadStatuses(contents: ContentTreeSelectorItem[]): wemQ.Promise<ContentAndStatusTreeSelectorItem[]> {
            return CompareContentRequest.fromContentSummaries(contents.map(item => item.getContent())).sendAndParse().then(
                (compareResults: CompareContentResults) => {

                    return contents.map(item => {

                        const compareResult: CompareContentResult = compareResults.get(item.getId());
                        const contentAndCompareStatus = ContentSummaryAndCompareStatus.fromContentAndCompareAndPublishStatus(
                            item.getContent(), compareResult.getCompareStatus(), compareResult.getPublishStatus());

                        return new ContentAndStatusTreeSelectorItem(contentAndCompareStatus, item.getExpand());
                    });
                });
        }

        static create(): ContentSummaryOptionDataLoaderBuilder {
            return new ContentSummaryOptionDataLoaderBuilder();
        }
    }

    export class ContentSummaryOptionDataLoaderBuilder {

        content: ContentSummary;

        contentTypeNames: string[] = [];

        allowedContentPaths: string[] = [];

        relationshipType: string;

        loadStatus: boolean;

        public setContentTypeNames(contentTypeNames: string[]): ContentSummaryOptionDataLoaderBuilder {
            this.contentTypeNames = contentTypeNames;
            return this;
        }

        public setAllowedContentPaths(allowedContentPaths: string[]): ContentSummaryOptionDataLoaderBuilder {
            this.allowedContentPaths = allowedContentPaths;
            return this;
        }

        public setRelationshipType(relationshipType: string): ContentSummaryOptionDataLoaderBuilder {
            this.relationshipType = relationshipType;
            return this;
        }

        public setContent(content: ContentSummary): ContentSummaryOptionDataLoaderBuilder {
            this.content = content;
            return this;
        }

        public setLoadStatus(value: boolean): ContentSummaryOptionDataLoaderBuilder {
            this.loadStatus = value;
            return this;
        }

        build(): ContentSummaryOptionDataLoader {
            return new ContentSummaryOptionDataLoader(this);
        }
    }
}
