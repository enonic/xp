module api.content.page {

    export class PageComponentFactory {

        public static createFromJson(json: api.content.page.json.PageComponentTypeWrapperJson): api.content.page.PageComponent {

            if (json.PartComponent) {
                return new part.PartComponentBuilder().fromJson(<part.json.PartComponentJson>json.PartComponent).build();
            }
            else if (json.ImageComponent) {
                return new image.ImageComponentBuilder().fromJson(<image.json.ImageComponentJson>json.ImageComponent).build();
            }
            else if (json.LayoutComponent) {
                return new layout.LayoutComponentBuilder().fromJson(<layout.json.LayoutComponentJson>json.LayoutComponent).build();
            }
            else {
                throw new Error("Not a component that can be placed in a Region: " + json);
            }
        }
    }
}