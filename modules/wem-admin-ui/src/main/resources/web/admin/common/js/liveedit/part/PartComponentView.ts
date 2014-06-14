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

        private contentViews: ContentView[] = [];

        private placeholder: PartPlaceholder;

        private partComponent: PartComponent;

        constructor(builder: PartComponentViewBuilder) {
            super(builder);
            this.partComponent = builder.pageComponent;
            this.placeholder = new PartPlaceholder(this);
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

        select() {
            super.select();
            if (this.isEmpty()) {
                this.placeholder.select();
            }
        }

        deselect() {
            super.deselect();
            if (this.isEmpty()) {
                this.placeholder.deselect();
            }
        }

        conditionedForEmpty(): boolean {
            if (!this.partComponent) {
                return super.isEmpty();
            }
            return this.isEmpty() || !this.partComponent.getDescriptor();
        }

        displayPlaceholder() {
            super.markAsEmpty();

            this.removeChildren();
            this.appendChild(this.placeholder);
        }

        duplicate(duplicate: PartComponent): PartComponentView {

            var duplicatedView = new PartComponentView(new PartComponentViewBuilder().
                setParentRegionView(this.getParentItemView()).
                setPageComponent(duplicate));
            this.getEl().insertAfterThisEl(duplicatedView.getEl());
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