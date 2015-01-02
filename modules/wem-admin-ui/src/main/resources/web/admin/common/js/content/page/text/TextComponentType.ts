module api.content.page.text {

    import ComponentType = api.content.page.ComponentType;

    export class TextComponentType extends ComponentType {

        constructor() {
            super("text");
        }

        newComponentBuilder(): TextComponentBuilder {
            return new TextComponentBuilder();
        }
    }

    new TextComponentType();

}