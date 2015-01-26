module api.liveedit.part {

    import ComponentView = api.liveedit.ComponentView;
    import ContentView = api.liveedit.ContentView;
    import RegionView = api.liveedit.RegionView;
    import PartComponent = api.content.page.region.PartComponent;

    export class PartComponentViewBuilder extends ComponentViewBuilder<PartComponent> {

        constructor() {
            super();
            this.setType(PartItemType.get());
        }
    }

    export class PartComponentView extends ComponentView<PartComponent> {

        private contentViews: ContentView[];

        private partComponent: PartComponent;

        constructor(builder: PartComponentViewBuilder) {
            this.contentViews = [];
            this.liveEditModel = builder.parentRegionView.liveEditModel;
            this.partComponent = builder.component;

            super(builder.
                setTooltipViewer(new PartComponentViewer()).
                setPlaceholder(new PartPlaceholder(this)));
            this.addClass('part-view');

            this.parseContentViews(this);
        }

        addContent(view: ContentView) {
            this.contentViews.push(view);
        }

        getContentViews(): ContentView[] {
            return this.contentViews;
        }

        isEmpty(): boolean {
            return !this.partComponent || this.partComponent.isEmpty();
        }

        duplicate(duplicate: PartComponent): PartComponentView {

            var duplicatedView = new PartComponentView(new PartComponentViewBuilder().
                setParentRegionView(this.getParentItemView()).
                setParentElement(this.getParentElement()).
                setComponent(duplicate));
            duplicatedView.insertAfterEl(this);
            return duplicatedView;
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