module api.liveedit.part {

    import ComponentView = api.liveedit.ComponentView;
    import ContentView = api.liveedit.ContentView;
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

        private partPlaceholder: PartPlaceholder;

        constructor(builder: PartComponentViewBuilder) {
            this.contentViews = [];
            this.liveEditModel = builder.parentRegionView.getLiveEditModel();
            this.partComponent = builder.component;

            this.partPlaceholder = new PartPlaceholder(this);

            this.resetHrefForRootLink(builder);

            super(builder.
                setViewer(new PartComponentViewer()).
                setPlaceholder(this.partPlaceholder).
                setInspectActionRequired(true));

            this.parseContentViews(this);
        }

        private resetHrefForRootLink(builder: PartComponentViewBuilder) {
            if (builder.element && builder.element.getEl().hasAttribute("href")) {
                builder.element.getEl().setAttribute("href", "#");
            }
        }

        setComponent(partComponent: PartComponent) {
            super.setComponent(partComponent);

            // name can be null when emptying component
            if (partComponent && partComponent.getName()) {
                this.partPlaceholder.setDisplayName(partComponent.getName().toString());
            }
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