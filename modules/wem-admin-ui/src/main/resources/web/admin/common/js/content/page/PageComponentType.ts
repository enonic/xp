module api.content.page {

    export class PageComponentType {

        private static shortNameToClass: {[shortName: string]: Function} = {};

        static register(shortName: string, clazz: Function) {
            PageComponentType.shortNameToClass[shortName] = clazz;
        }

        static byShortName(shortName: string): Function {
            return PageComponentType.shortNameToClass[shortName];
        }
    }

}