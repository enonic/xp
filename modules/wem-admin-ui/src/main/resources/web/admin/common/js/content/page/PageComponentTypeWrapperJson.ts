module api.content.page {

    export interface PageComponentTypeWrapperJson {

        ImageComponent?:api.content.page.image.ImageComponentJson;

        PartComponent?:api.content.page.part.PartComponentJson;

        LayoutComponent?:api.content.page.layout.LayoutComponentJson;
    }
}