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

    export class ContentSummaryOptionDataLoader implements OptionDataLoader<ContentTreeSelectorItem> {

        private request: ContentTreeSelectorQueryRequest = new ContentTreeSelectorQueryRequest();

        private contentTypeNames: string[] = [];

        private allowedContentPaths: string[] = [];

        private relationshipType: string;

        constructor(builder?: Builder) {
            if (builder) {
                this.contentTypeNames = builder.contentTypeNames;
                this.allowedContentPaths = builder.allowedContentPaths;
                this.relationshipType = builder.relationshipType;

                this.initRequest(builder);
            }
        }

        private initRequest(builder: Builder) {
            let request = this.request;
            request.setContentTypeNames(builder.contentTypeNames);
            request.setAllowedContentPaths(builder.allowedContentPaths);
            request.setRelationshipType(builder.relationshipType);
            request.setContent(builder.content);
        }

        fetch(node: TreeNode<Option<ContentTreeSelectorItem>>): wemQ.Promise<ContentTreeSelectorItem> {

            this.request.setParentPath(node.getDataId() ? node.getData().displayValue.getPath() : null);
            if (this.request.getContent()) {
                return this.request.sendAndParse().then((contents: ContentTreeSelectorItem[]) => {
                    return !!contents && contents.length > 0 ? contents[0] : null;
                });
            } else {
                return ContentSummaryFetcher.fetch(node.getData().displayValue.getContentId()).then(
                    content => new ContentTreeSelectorItem(content, false));
            }
        }

        fetchChildren(parentNode: TreeNode<Option<ContentTreeSelectorItem>>, from: number = 0,
                      size: number = -1): wemQ.Promise<OptionDataLoaderData<ContentTreeSelectorItem>> {

            if (this.request.getContent()) {
                this.request.setFrom(from);
                this.request.setSize(size);

                this.request.setParentPath(parentNode.getDataId() ? parentNode.getData().displayValue.getPath() : null);

                return this.request.sendAndParse().then(items => {
                    return new OptionDataLoaderData(items,
                        0,
                        0);
                });
            } else {
                return ContentSummaryFetcher.fetchChildren(parentNode.getData() ? parentNode.getData().displayValue.getContentId() : null,
                    from,
                    size).then((response: ContentResponse<ContentSummary>) => {
                    return new OptionDataLoaderData(response.getContents().map(content => new ContentTreeSelectorItem(content, false)),
                        response.getMetadata().getHits(),
                        response.getMetadata().getTotalHits());
                });
            }

        }

        checkReadonly(items: ContentTreeSelectorItem[]): wemQ.Promise<string[]> {
            return ContentSummaryFetcher.getReadOnly(items.map(item => item.getContent()));
        }

        static create(): Builder {
            return new Builder();
        }
    }

    export class Builder {

        content: ContentSummary;

        contentTypeNames: string[] = [];

        allowedContentPaths: string[] = [];

        relationshipType: string;

        public setContentTypeNames(contentTypeNames: string[]): Builder {
            this.contentTypeNames = contentTypeNames;
            return this;
        }

        public setAllowedContentPaths(allowedContentPaths: string[]): Builder {
            this.allowedContentPaths = allowedContentPaths;
            return this;
        }

        public setRelationshipType(relationshipType: string): Builder {
            this.relationshipType = relationshipType;
            return this;
        }

        public setContent(content: ContentSummary): Builder {
            this.content = content;
            return this;
        }

        build(): ContentSummaryOptionDataLoader {
            return new ContentSummaryOptionDataLoader(this);
        }
    }
}
