module api.content.page {

    export interface PageComponentTypeWrapperJson {

        ImageComponent?:api.content.page.image.ImageComponentJson;

        PartComponent?:api.content.page.part.PartComponentJson;

        TextComponent?:api.content.page.text.TextComponentJson;

        LayoutComponent?:api.content.page.layout.LayoutComponentJson;
    }
}