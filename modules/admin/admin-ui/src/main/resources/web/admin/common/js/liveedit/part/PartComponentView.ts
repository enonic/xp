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

        constructor(builder: PartComponentViewBuilder) {
            super(builder.setViewer(new PartComponentViewer()).setInspectActionRequired(true));

            this.createPlaceholder();

            this.contentViews = [];
            this.liveEditModel = builder.parentRegionView.getLiveEditModel();

            this.resetHrefForRootLink(builder);

            this.parseContentViews(this);
            this.disableLinks();
        }

        private createPlaceholder() {
            let placeholder = new PartPlaceholder(this);
            placeholder.setDisplayName(this.getComponent().getName().toString());

            this.setPlaceholder(placeholder);

        }

        private resetHrefForRootLink(builder: PartComponentViewBuilder) {
            if (builder.element && builder.element.getEl().hasAttribute('href')) {
                builder.element.getEl().setAttribute('href', '#');
            }
        }

        /*
        setComponent(partComponent: PartComponent) {
            super.setComponent(partComponent);

            // name can be null when emptying component
            if (partComponent && partComponent.getName()) {
                this.partPlaceholder.setDisplayName(partComponent.getName().toString());
            }
        }
         */
        addContent(view: ContentView) {
            this.contentViews.push(view);
        }

        getContentViews(): ContentView[] {
            return this.contentViews;
        }

        isEmpty(): boolean {
            return !this.getComponent() || this.getComponent().isEmpty();
        }

        toItemViewArray(): ItemView[] {

            let array: ItemView[] = [];
            array.push(this);
            this.contentViews.forEach((contentView: ContentView) => {
                array = array.concat(contentView.toItemViewArray());
            });
            return array;
        }

        private parseContentViews(parentElement?: api.dom.Element) {

            let children = parentElement ? parentElement.getChildren() : this.getChildren();

            children.forEach((childElement: api.dom.Element) => {
                let itemType = ItemType.fromElement(childElement);
                if (itemType) {
                    if (ContentItemType.get().equals(itemType)) {
                        let contentView = new ContentView(new ContentViewBuilder().
                            setParentPartComponentView(this).
                            setParentElement(parentElement ? parentElement : this).
                            setElement(childElement));
                        this.addContent(contentView);
                    } else {
                        this.parseContentViews(childElement);
                    }
                }
            });
        }
    }
}
