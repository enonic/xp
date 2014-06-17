module api.content.page {

    import Region = api.content.page.region.Region;

    export class PageComponentFactory {

        public static createFromJson(json: api.content.page.PageComponentTypeWrapperJson, region: Region): api.content.page.PageComponent {

            if (json.PartComponent) {
                return new part.PartComponentBuilder().fromJson(<part.PartComponentJson>json.PartComponent, region).build();
            }
            else if (json.ImageComponent) {
                return new image.ImageComponentBuilder().fromJson(<image.ImageComponentJson>json.ImageComponent, region).build();
            }
            else if (json.LayoutComponent) {
                return new layout.LayoutComponentBuilder().fromJson(<layout.LayoutComponentJson>json.LayoutComponent, region);
            }
            else {
                throw new Error("Not a component that can be placed in a Region: " + json);
            }
        }
    }
}