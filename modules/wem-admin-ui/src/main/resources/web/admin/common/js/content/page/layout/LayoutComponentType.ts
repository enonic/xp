module api.content.page.layout {

    import PageComponentType = api.content.page.PageComponentType;

    export class LayoutComponentType extends PageComponentType {

        constructor() {
            super("layout");
        }

        newComponentBuilder(): LayoutComponentBuilder {
            return new LayoutComponentBuilder();
        }
    }

    new LayoutComponentType();

}