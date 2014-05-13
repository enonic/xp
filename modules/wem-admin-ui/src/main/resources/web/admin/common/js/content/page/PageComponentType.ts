module api.content.page {

    export class PageComponentType {

        private static shortNameToInstance: {[shortName: string]: PageComponentType} = {};

        constructor(shortName: string) {
            PageComponentType.shortNameToInstance[shortName] = this;
        }

        newComponentBuilder(): PageComponentBuilder<PageComponent> {
            throw new Error("Must be implemented by inheritors");
        }

        static byShortName(shortName: string): PageComponentType {
            return PageComponentType.shortNameToInstance[shortName];
        }
    }

}