module api.content.page.region {

    export class ComponentType {

        private static shortNameToInstance: {[shortName: string]: ComponentType} = {};

        private shortName: string;

        constructor(shortName: string) {
            ComponentType.shortNameToInstance[shortName] = this;
            this.shortName = shortName;
        }

        getShortName(): string {
            return this.shortName;
        }

        newComponentBuilder(): ComponentBuilder<Component> {
            throw new Error('Must be implemented by inheritors');
        }

        static byShortName(shortName: string): ComponentType {
            return ComponentType.shortNameToInstance[shortName];
        }

        getDefaultName(): ComponentName {
            return new ComponentName(api.util.StringHelper.capitalize(api.util.StringHelper.removeWhitespaces(this.shortName)));
        }
    }

}
