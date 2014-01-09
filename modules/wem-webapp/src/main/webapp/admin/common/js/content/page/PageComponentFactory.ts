module api.content.page {

    export class PageComponentFactory {

        private static PART_COMPONENT_CLASS_NAME = "PartComponent";

        private static IMAGE_COMPONENT_CLASS_NAME = "ImageComponent";

        private static LAYOUT_COMPONENT_CLASS_NAME = "LayoutComponent";

        public static createFromJson(json: api.content.page.json.PageComponentJson): api.content.page.PageComponent<api.content.page.TemplateKey> {

            if (json.type == PageComponentFactory.PART_COMPONENT_CLASS_NAME) {
                return new part.PartComponentBuilder().fromJson(<part.json.PartComponentJson>json).build();
            }
            else if (json.type == PageComponentFactory.IMAGE_COMPONENT_CLASS_NAME) {
                return new image.ImageComponentBuilder().fromJson(<image.json.ImageComponentJson>json).build();
            }
            else if (json.type == PageComponentFactory.LAYOUT_COMPONENT_CLASS_NAME) {
                return new layout.LayoutComponentBuilder().fromJson(<layout.json.LayoutComponentJson>json).build();
            }
            else {
                throw new Error("Not a component that can be placed in a Region: " + json.type);
            }
        }
    }
}