module api.content.page.region {

    export class PartComponentType extends ComponentType {

        private static INSTANCE: PartComponentType = new PartComponentType();

        constructor() {
            super("part");
        }

        newComponentBuilder(): PartComponentBuilder {
            return new PartComponentBuilder();
        }

        public static get(): PartComponentType {
            return PartComponentType.INSTANCE;
        }
    }

}
