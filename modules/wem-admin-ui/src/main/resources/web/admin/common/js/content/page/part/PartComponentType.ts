module api.content.page.part {

    import PageComponentType = api.content.page.PageComponentType;

    export class PartComponentType extends PageComponentType {

        constructor() {
            super("part");
        }

        newComponentBuilder(): PartComponentBuilder {
            return new PartComponentBuilder();
        }
    }

    new PartComponentType();

}