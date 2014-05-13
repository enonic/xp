module api.content.page {

    export class PageComponentType {

        private static shortNameToInstance: {[shortName: string]: PageComponentType} = {};

        private shortName: string;

        constructor(shortName: string) {
            PageComponentType.shortNameToInstance[shortName] = this;
            this.shortName = shortName;
        }

        getShortName(): string {
            return this.shortName;
        }

        newComponentBuilder(): PageComponentBuilder<PageComponent> {
            throw new Error("Must be implemented by inheritors");
        }

        static byShortName(shortName: string): PageComponentType {
            return PageComponentType.shortNameToInstance[shortName];
        }
    }

}