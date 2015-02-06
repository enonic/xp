module api.liveedit {

    import Component = api.content.page.region.Component;

    export class DragPlaceholderBuilder {

        itemType: ItemType;

        dropAllowed: boolean = true;

        text: string = "Drop {0} here";

        regionView: RegionView;

        componentView: ComponentView<Component>;

        setItemType(value: ItemType): DragPlaceholderBuilder {
            this.itemType = value;
            return this;
        }

        setDropAllowed(value: boolean): DragPlaceholderBuilder {
            this.dropAllowed = value;
            return this;
        }

        setText(value: string): DragPlaceholderBuilder {
            this.text = value;
            return this;
        }

        setRegionView(value: RegionView): DragPlaceholderBuilder {
            this.regionView = value;
            return this;
        }

        setComponentView(value: ComponentView<Component>): DragPlaceholderBuilder {
            this.componentView = value;
            this.itemType = value.getType();
            return this;
        }

        build(): DragPlaceholder {
            return new DragPlaceholder(this);
        }
    }

    export class DragPlaceholder extends ItemViewPlaceholder {

        private itemType: ItemType;

        private dropAllowed: boolean;

        private text: string;

        private regionView: RegionView;

        private message: api.dom.DivEl;

        private componentView: ComponentView<Component>;

        constructor(builder: DragPlaceholderBuilder) {
            super();
            this.itemType = builder.itemType;
            this.dropAllowed = builder.dropAllowed;
            this.text = api.util.StringHelper.format(builder.text, api.util.StringHelper.capitalize(this.itemType.getShortName()));
            this.regionView = builder.regionView;
            this.componentView = builder.componentView;

            this.message = new api.dom.DivEl("message");
            if (builder.text) {
                this.message.setHtml(this.text);
            }
            this.appendChild(this.message);

            this.addClass(this.itemType.getShortName().toLowerCase() + "-placeholder drag-placeholder");

            if (!this.dropAllowed) {
                this.addClass("forbidden");
            }
        }
    }
}