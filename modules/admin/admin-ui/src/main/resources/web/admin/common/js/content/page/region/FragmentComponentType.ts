module api.content.page.region {

    export class FragmentComponentType extends ComponentType {

        private static INSTANCE = new FragmentComponentType();

        constructor() {
            super("fragment");
        }

        newComponentBuilder(): FragmentComponentBuilder {
            return new FragmentComponentBuilder();
        }

        public static get(): FragmentComponentType {
            return FragmentComponentType.INSTANCE;
        }
    }

}