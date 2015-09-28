module api.content.page.region {

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