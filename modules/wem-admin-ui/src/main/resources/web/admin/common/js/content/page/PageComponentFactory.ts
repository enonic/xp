module api.content.page {

    import Region = api.content.page.region.Region;
    import PropertyIdProvider = api.data2.PropertyIdProvider;

    export class PageComponentFactory {

        public static createFromJson(json: api.content.page.PageComponentTypeWrapperJson, region: Region,
                                     propertyIdProvider: PropertyIdProvider): api.content.page.PageComponent {

            if (json.PartComponent) {
                return new part.PartComponentBuilder().fromJson(<part.PartComponentJson>json.PartComponent, region,
                    propertyIdProvider).build();
            }
            else if (json.ImageComponent) {
                return new image.ImageComponentBuilder().fromJson(<image.ImageComponentJson>json.ImageComponent, region,
                    propertyIdProvider).build();
            }
            else if (json.LayoutComponent) {
                return new layout.LayoutComponentBuilder().fromJson(<layout.LayoutComponentJson>json.LayoutComponent, region,
                    propertyIdProvider);
            }
            else if (json.TextComponent) {
                return new text.TextComponentBuilder().fromJson(<text.TextComponentJson>json.TextComponent, region).build();
            }
            else {
                throw new Error("Not a component that can be placed in a Region: " + json);
            }
        }
    }
}