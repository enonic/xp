module api.content.page.part {

    import ComponentType = api.content.page.ComponentType;

    export class PartComponentType extends ComponentType {

        constructor() {
            super("part");
        }

        newComponentBuilder(): PartComponentBuilder {
            return new PartComponentBuilder();
        }
    }

    new PartComponentType();

}