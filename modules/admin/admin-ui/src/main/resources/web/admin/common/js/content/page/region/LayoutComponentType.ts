module api.content.page.region {

    export class LayoutComponentType extends ComponentType {

        private static INSTANCE: LayoutComponentType = new LayoutComponentType();

        constructor() {
            super('layout');
        }

        newComponentBuilder(): LayoutComponentBuilder {
            return new LayoutComponentBuilder();
        }

        public static get(): LayoutComponentType {
            return LayoutComponentType.INSTANCE;
        }
    }

}
