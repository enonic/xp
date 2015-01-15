module api.content.page.region {

    import PropertyIdProvider = api.data.PropertyIdProvider;

    export class ComponentFactory {

        public static createFromJson(json: ComponentTypeWrapperJson, componentIndex: number, region: Region,
                                     propertyIdProvider: PropertyIdProvider): Component {

            if (json.PartComponent) {
                return new PartComponentBuilder().fromJson(<PartComponentJson>json.PartComponent, region,
                    propertyIdProvider).build();
            }
            else if (json.ImageComponent) {
                return new ImageComponentBuilder().fromJson(<ImageComponentJson>json.ImageComponent, region,
                    propertyIdProvider).build();
            }
            else if (json.LayoutComponent) {
                var layoutComponentBuilder = new LayoutComponentBuilder();
                layoutComponentBuilder.setIndex(componentIndex);
                return layoutComponentBuilder.fromJson(<LayoutComponentJson>json.LayoutComponent, region,
                    propertyIdProvider);
            }
            else if (json.TextComponent) {
                return new TextComponentBuilder().fromJson(<TextComponentJson>json.TextComponent, region).setIndex(componentIndex).build();
            }
            else {
                throw new Error("Not a component that can be placed in a Region: " + json);
            }
        }
    }
}