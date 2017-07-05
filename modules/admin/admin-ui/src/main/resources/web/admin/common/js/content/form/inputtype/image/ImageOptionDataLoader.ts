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
    import ImageSelectorDisplayValue = api.content.form.inputtype.image.ImageSelectorDisplayValue;

    export class ImageOptionDataLoader extends ContentSummaryOptionDataLoader {

        protected createOptionData(data: ContentTreeSelectorItem[]) {
            return new OptionDataLoaderData(data.map((cur => new ImageTreeSelectorItem(cur.getContent(), cur.getExpand()))),
                0,
                0);
        }

        static create(): ImageOptionDataLoaderBuilder {
            return new ImageOptionDataLoaderBuilder();
        }
    }

    export class ImageOptionDataLoaderBuilder extends ContentSummaryOptionDataLoaderBuilder {

        inputName: string;

        public setInputName(value: string): ImageOptionDataLoaderBuilder {
            this.inputName = value;
            return this;
        }

        setContentTypeNames(value: string[]): ImageOptionDataLoaderBuilder {
            super.setContentTypeNames(value);
            return this;
        }

        public setAllowedContentPaths(value: string[]): ImageOptionDataLoaderBuilder {
            super.setAllowedContentPaths(value);
            return this;
        }

        public setRelationshipType(value: string): ImageOptionDataLoaderBuilder {
            super.setRelationshipType(value);
            return this;
        }

        public setContent(value: ContentSummary): ImageOptionDataLoaderBuilder {
            super.setContent(value);
            return this;
        }

        build(): ImageOptionDataLoader {
            return new ImageOptionDataLoader(this);
        }
    }

    export class ImageTreeSelectorItem extends ContentTreeSelectorItem {

        private imageSelectorDisplayValue: ImageSelectorDisplayValue;

        constructor(content: ContentSummary, expand: boolean) {
            super(content, expand);
            this.imageSelectorDisplayValue = ImageSelectorDisplayValue.fromContentSummary(content);
        }

        getImageUrl(): string {
            return this.imageSelectorDisplayValue.getImageUrl();
        }

        isEmptyContent(): boolean {
            return this.imageSelectorDisplayValue.isEmptyContent();
        }

        getContentSummary(): ContentSummary {
            return this.imageSelectorDisplayValue.getContentSummary();
        }
    }
}
