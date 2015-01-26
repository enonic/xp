module api.liveedit {

    import Component = api.content.page.region.Component;

    export class RegionDropzoneBuilder {

        itemType: ItemType;

        dropAllowed: boolean = true;

        text: string = "Drop {0} here";

        regionView: RegionView;

        componentView: ComponentView<Component>;

        setItemType(value: ItemType): RegionDropzoneBuilder {
            this.itemType = value;
            return this;
        }

        setDropAllowed(value: boolean): RegionDropzoneBuilder {
            this.dropAllowed = value;
            return this;
        }

        setText(value: string): RegionDropzoneBuilder {
            this.text = value;
            return this;
        }

        setRegionView(value: RegionView): RegionDropzoneBuilder {
            this.regionView = value;
            return this;
        }

        setComponentView(value: ComponentView<Component>): RegionDropzoneBuilder {
            this.componentView = value;
            this.itemType = value.getType();
            return this;
        }

        build(): RegionDropzone {
            return new RegionDropzone(this);
        }
    }

    export class RegionDropzone extends api.dom.DivEl {

        private itemType: ItemType;

        private dropAllowed: boolean;

        private text: string;

        private regionView: RegionView;

        private message: api.dom.DivEl;

        private componentView: ComponentView<Component>;

        constructor(builder: RegionDropzoneBuilder) {
            super("region-dropzone");
            this.itemType = builder.itemType;
            this.dropAllowed = builder.dropAllowed;
            this.text = api.util.StringHelper.format(builder.text, api.util.StringHelper.capitalize(this.itemType.getShortName()));
            this.regionView = builder.regionView;
            this.componentView = builder.componentView;

            this.message = new api.dom.DivEl("message");
            this.message.setHtml(this.text);

            this.addClass(this.itemType.getShortName().toLowerCase());

            if (!this.dropAllowed) {
                this.addClass("forbidden");
            }

            this.appendChild(this.message);
        }
    }
}