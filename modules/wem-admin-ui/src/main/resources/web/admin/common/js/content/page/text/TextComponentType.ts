module api.content.page.text {

    import PageComponentType = api.content.page.PageComponentType;

    export class TextComponentType extends PageComponentType {

        constructor() {
            super("text");
        }

        newComponentBuilder(): TextComponentBuilder {
            return new TextComponentBuilder();
        }
    }

    new TextComponentType();

}