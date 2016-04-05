module api.content.page.region {

    export class ComponentFactory {

        public static createFromJson(json: ComponentTypeWrapperJson, componentIndex: number, region: Region): Component {

            if (json.PartComponent) {
                return new PartComponentBuilder().fromJson(json.PartComponent, region).build();
            }
            else if (json.ImageComponent) {
                return new ImageComponentBuilder().fromJson(json.ImageComponent, region).build();
            }
            else if (json.LayoutComponent) {
                var layoutComponentBuilder = new LayoutComponentBuilder();
                layoutComponentBuilder.setIndex(componentIndex);
                return layoutComponentBuilder.fromJson(json.LayoutComponent, region);
            }
            else if (json.TextComponent) {
                return new TextComponentBuilder().fromJson(json.TextComponent, region).setIndex(componentIndex).build();
            }
            else if (json.FragmentComponent) {
                return new FragmentComponentBuilder().fromJson(json.FragmentComponent, region).setIndex(componentIndex).build();
            }
            else {
                throw new Error("Not a component that can be placed in a Region: " + json);
            }
        }
    }
}