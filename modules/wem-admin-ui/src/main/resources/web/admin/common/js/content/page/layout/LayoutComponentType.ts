module api.content.page.layout {

    import ComponentType = api.content.page.ComponentType;

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