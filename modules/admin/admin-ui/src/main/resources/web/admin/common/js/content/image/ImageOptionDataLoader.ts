module api.content.image {

    import TreeNode = api.ui.treegrid.TreeNode;
    import ContentAndStatusTreeSelectorItem = api.content.resource.ContentAndStatusTreeSelectorItem;
    import OptionDataLoaderData = api.ui.selector.OptionDataLoaderData;
    import ContentTreeSelectorItem = api.content.resource.ContentTreeSelectorItem;
    import Option = api.ui.selector.Option;

    export class ImageOptionDataLoader extends ContentSummaryOptionDataLoader {

        protected createOptionData(data: ContentAndStatusTreeSelectorItem[], hits: number, totalHits: number) {
            return new OptionDataLoaderData(data.map((cur => new ImageTreeSelectorItem(cur.getContent(), cur.getExpand()))),
                hits,
                totalHits);
        }

        search(value: string): wemQ.Promise<ImageTreeSelectorItem[]> {
            return super.search(value).then((items: ContentTreeSelectorItem[]) => {
                return items.map(item =>
                    new ImageTreeSelectorItem(item.getContent(), item.getExpand())
                );
            });
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

        getTypeLocaleName(): string {
            return this.imageSelectorDisplayValue.getTypeLocaleName();
        }

        equals(o: api.Equitable): boolean {

            if (!api.ObjectHelper.iFrameSafeInstanceOf(o, api.ClassHelper.getClass(this))) {
                return false;
            }

            if (!super.equals(o)) {
                return false;
            }

            let other = <ImageTreeSelectorItem>o;

            if (!ObjectHelper.equals(this.imageSelectorDisplayValue, other.imageSelectorDisplayValue)) {
                return false;
            }

            return true;
        }
    }
}
