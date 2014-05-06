module api.content.page {

    export class PageComponentFactory {

        public static createFromJson(json: api.content.page.PageComponentTypeWrapperJson, regionPath:RegionPath): api.content.page.PageComponent {

            if (json.PartComponent) {
                return new part.PartComponentBuilder().fromJson(<part.PartComponentJson>json.PartComponent, regionPath).build();
            }
            else if (json.ImageComponent) {
                return new image.ImageComponentBuilder().fromJson(<image.ImageComponentJson>json.ImageComponent, regionPath).build();
            }
            else if (json.LayoutComponent) {
                return new layout.LayoutComponentBuilder().fromJson(<layout.LayoutComponentJson>json.LayoutComponent, regionPath).build();
            }
            else {
                throw new Error("Not a component that can be placed in a Region: " + json);
            }
        }
    }
}