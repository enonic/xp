module api.content.page.json{

    export interface PageComponentTypeWrapperJson {

        ImageComponent?:api.content.page.image.json.ImageComponentJson;

        PartComponent?:api.content.page.part.json.PartComponentJson;

        LayoutComponent?:api.content.page.layout.json.LayoutComponentJson;
    }
}