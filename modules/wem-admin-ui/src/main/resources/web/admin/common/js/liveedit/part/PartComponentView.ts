module api.liveedit.part {

    import PageComponentView = api.liveedit.PageComponentView;
    import ContentView = api.liveedit.ContentView;
    import RegionView = api.liveedit.RegionView;
    import PartComponent = api.content.page.part.PartComponent;

    export class PartComponentViewBuilder extends PageComponentViewBuilder<PartComponent> {

        constructor() {
            super();
            this.setType(PartItemType.get());
        }
    }

    export class PartComponentView extends PageComponentView<PartComponent> {

        private contentViews: ContentView[];

        private partComponent: PartComponent;

        constructor(builder: PartComponentViewBuilder) {
            this.contentViews = [];
            this.liveEditModel = builder.parentRegionView.liveEditModel;
            super(builder.setPlaceholder(new PartPlaceholder(this)));
            this.partComponent = builder.pageComponent;

            if (this.conditionedForEmpty()) {
                this.displayPlaceholder();
            }
            this.parseContentViews(this);
        }

        addContent(view: ContentView) {
            this.contentViews.push(view);
        }

        getContentViews(): ContentView[] {
            return this.contentViews;
        }

        conditionedForEmpty(): boolean {
            if (!this.partComponent) {
                return super.isEmpty();
            }
            return this.isEmpty() || !this.partComponent.getDescriptor();
        }

        duplicate(duplicate: PartComponent): PartComponentView {

            var duplicatedView = new PartComponentView(new PartComponentViewBuilder().
                setParentRegionView(this.getParentItemView()).
                setParentElement(this.getParentElement()).
                setPageComponent(duplicate));
            duplicatedView.insertAfterEl(this);
            return duplicatedView;
        }

        getTooltipViewer(): PartComponentViewer {
            return new PartComponentViewer();
        }

        toItemViewArray(): ItemView[] {

            var array: ItemView[] = [];
            array.push(this);
            this.contentViews.forEach((contentView: ContentView) => {
                array = array.concat(contentView.toItemViewArray());
            });
            return array;
        }

        private parseContentViews(parentElement?: api.dom.Element) {

            var children = parentElement ? parentElement.getChildren() : this.getChildren();

            children.forEach((childElement: api.dom.Element) => {
                var itemType = ItemType.fromElement(childElement);
                if (itemType) {
                    if (ContentItemType.get().equals(itemType)) {
                        var contentView = new ContentView(new ContentViewBuilder().
                            setParentPartComponentView(this).
                            setParentElement(parentElement ? parentElement : this).
                            setElement(childElement));
                        this.addContent(contentView);
                    }
                    else {
                        this.parseContentViews(childElement);
                    }
                }
            });
        }
    }
}