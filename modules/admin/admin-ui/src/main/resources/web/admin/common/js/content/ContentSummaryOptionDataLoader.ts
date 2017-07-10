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

    export class ContentSummaryOptionDataLoader implements OptionDataLoader<ContentAndStatusTreeSelectorItem> {

        private request: ContentTreeSelectorQueryRequest = new ContentTreeSelectorQueryRequest();

        private contentTypeNames: string[] = [];

        private allowedContentPaths: string[] = [];

        private relationshipType: string;

        constructor(builder?: ContentSummaryOptionDataLoaderBuilder) {
            if (builder) {
                this.contentTypeNames = builder.contentTypeNames;
                this.allowedContentPaths = builder.allowedContentPaths;
                this.relationshipType = builder.relationshipType;

                this.initRequest(builder);
            }
        }

        private initRequest(builder: ContentSummaryOptionDataLoaderBuilder) {
            let request = this.request;
            request.setContentTypeNames(builder.contentTypeNames);
            request.setAllowedContentPaths(builder.allowedContentPaths);
            request.setRelationshipType(builder.relationshipType);
            request.setContent(builder.content);
        }

        fetch(node: TreeNode<Option<ContentAndStatusTreeSelectorItem>>): wemQ.Promise<ContentAndStatusTreeSelectorItem> {
            this.request.setParentPath(node.getDataId() ? node.getData().displayValue.getPath() : null);
            if (this.request.getContent()) {
                this.request.sendAndParse().then((items: ContentTreeSelectorItem[]) => {

                    const contentSummaries: ContentSummary[] = items.map(item => item.getContent());

                    return CompareContentRequest.fromContentSummaries(contentSummaries).sendAndParse().then(
                        (compareResults: CompareContentResults) => {
                            const contents: ContentSummaryAndCompareStatus[] = ContentSummaryAndCompareStatusFetcher.updateCompareStatus(
                                contentSummaries, compareResults);

                            return !!contents && contents.length > 0 ? contents[0] : null;
                        });
                });
            } else {
                return ContentSummaryFetcher.fetch(node.getData().displayValue.getContentId()).then(
                    content => {
                        return CompareContentRequest.fromContentSummaries([content]).sendAndParse().then(
                            (compareResults: CompareContentResults) => {

                                const compareResult = compareResults.get(content.getContentId().toString());

                                return new ContentAndStatusTreeSelectorItem(
                                    ContentSummaryAndCompareStatus.fromContentAndCompareAndPublishStatus(content,
                                        compareResult.getCompareStatus(), compareResult.getPublishStatus()), false);
                            });
                    });
            }
        }

        fetchChildren(parentNode: TreeNode<Option<ContentAndStatusTreeSelectorItem>>, from: number = 0,
                      size: number = -1): wemQ.Promise<OptionDataLoaderData<ContentAndStatusTreeSelectorItem>> {
            if (this.request.getContent()) {
                this.request.setFrom(from);
                this.request.setSize(size);

                this.request.setParentPath(parentNode.getDataId() ? parentNode.getData().displayValue.getPath() : null);

                return this.request.sendAndParse().then(items => {
                    return CompareContentRequest.fromContentSummaries(items.map(item => item.getContent())).sendAndParse().then(
                        (compareResults: CompareContentResults) => {
                            const result = items.map(item => {
                                const compareResult: CompareContentResult = compareResults.get(item.getId());
                                const contentAndCompareStatus = ContentSummaryAndCompareStatus.fromContentAndCompareAndPublishStatus(
                                    item.getContent(), compareResult.getCompareStatus(), compareResult.getPublishStatus());
                                return new ContentAndStatusTreeSelectorItem(contentAndCompareStatus, item.getExpand());
                            });

                            return new OptionDataLoaderData(result,
                                0,
                                0);
                        });
                });
            } else {
                return ContentSummaryAndCompareStatusFetcher.fetchChildren(
                    parentNode.getData() ? parentNode.getData().displayValue.getContentId() : null,
                    from,
                    size).then((response: ContentResponse<ContentSummaryAndCompareStatus>) => {
                    return new OptionDataLoaderData(response.getContents().map(
                        content => new ContentAndStatusTreeSelectorItem(content, false)),
                        response.getMetadata().getHits(),
                        response.getMetadata().getTotalHits());
                });
            }
        }

        checkReadonly(items: ContentAndStatusTreeSelectorItem[]): wemQ.Promise<string[]> {
            return ContentSummaryFetcher.getReadOnly(items.map(item => item.getContent().getContentSummary()));
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

        build(): ContentSummaryOptionDataLoader {
            return new ContentSummaryOptionDataLoader(this);
        }
    }
}
