module api.content.page {

    export interface ComponentTypeWrapperJson {

        ImageComponent?:api.content.page.image.ImageComponentJson;

        PartComponent?:api.content.page.part.PartComponentJson;

        TextComponent?:api.content.page.text.TextComponentJson;

        LayoutComponent?:api.content.page.layout.LayoutComponentJson;
    }
}