module api.content.page.region {

    export class LayoutComponentType extends ComponentType {

        constructor() {
            super("layout");
        }

        newComponentBuilder(): LayoutComponentBuilder {
            return new LayoutComponentBuilder();
        }
    }

    new LayoutComponentType();

}