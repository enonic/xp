module api.liveedit {

    import Component = api.content.page.region.Component;

    export class RegionViewDropZoneBuilder {

        itemType: ItemType;

        dropAllowed: boolean = true;

        text: string = "Drop {0} here";

        regionView: RegionView;

        componentView: ComponentView<Component>;

        setItemType(value: ItemType): RegionViewDropZoneBuilder {
            this.itemType = value;
            return this;
        }

        setDropAllowed(value: boolean): RegionViewDropZoneBuilder {
            this.dropAllowed = value;
            return this;
        }

        setText(value: string): RegionViewDropZoneBuilder {
            this.text = value;
            return this;
        }

        setRegionView(value: RegionView): RegionViewDropZoneBuilder {
            this.regionView = value;
            return this;
        }

        setComponentView(value: ComponentView<Component>): RegionViewDropZoneBuilder {
            this.componentView = value;
            this.itemType = value.getType();
            return this;
        }

        build(): RegionViewDropZone {
            return new RegionViewDropZone(this);
        }
    }

    export class RegionViewDropZone extends api.dom.DivEl {

        private itemType: ItemType;

        private dropAllowed: boolean;

        private text: string;

        private regionView: RegionView;

        private componentView: ComponentView<Component>;

        constructor(builder: RegionViewDropZoneBuilder) {
            super("region-view-drop-zone");
            this.itemType = builder.itemType;
            this.dropAllowed = builder.dropAllowed;
            this.text = api.util.StringHelper.format(builder.text, api.util.StringHelper.capitalize(this.itemType.getShortName()));
            this.regionView = builder.regionView;
            this.componentView = builder.componentView;

            if (this.regionView.countNonMovingComponentViews() == 0) {
                this.getEl().setPaddingTop("28px");
                this.getEl().setPaddingBottom("28px");
                this.getEl().setPaddingLeft("10px");
                this.getEl().setPaddingRight("10px");
            }

            var innerHtml = "<span>";

            if (this.dropAllowed) {
                innerHtml += this.text + ' ';
            }
            else {
                innerHtml += '<span style="color: red">' + this.text + '</span> ';
            }

            this.getEl().addClass(this.itemType.getShortName().toLowerCase());

            innerHtml += "</span>";
            this.getEl().setInnerHtml(innerHtml);
        }
    }
}