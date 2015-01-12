module api.content.page.region {

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