module api_content_page_region {

    export class RegionPlaceableComponentFactory {

        private static PART_COMPONENT_CLASS_NAME = "PartComponent";

        private static IMAGE_COMPONENT_CLASS_NAME = "ImageComponent";

        private static LAYOUT_COMPONENT_CLASS_NAME = "LayoutComponent";

        public static create(componentAsDataSet: api_data.DataSet): api_content_page.BasePageComponent<api_content_page.TemplateKey> {

            if (componentAsDataSet.getName() == RegionPlaceableComponentFactory.PART_COMPONENT_CLASS_NAME) {
                return new api_content_page_part.PartComponentBuilder().fromDataSet(componentAsDataSet).build();
            }
            else if (componentAsDataSet.getName() == RegionPlaceableComponentFactory.IMAGE_COMPONENT_CLASS_NAME) {
                return new api_content_page_image.ImageComponentBuilder().fromDataSet(componentAsDataSet).build();
            }
            else if (componentAsDataSet.getName() == RegionPlaceableComponentFactory.LAYOUT_COMPONENT_CLASS_NAME) {
                return new api_content_page_layout.LayoutComponentBuilder().fromDataSet(componentAsDataSet).build();
            }
            else {
                throw new Error("Not a component that can be placed in a Region: " + componentAsDataSet.getName());
            }
        }
    }

}