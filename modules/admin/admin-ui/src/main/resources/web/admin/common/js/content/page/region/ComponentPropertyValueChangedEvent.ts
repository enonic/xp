module api.content.page.region {

    export class ComponentPropertyValueChangedEvent extends ComponentChangedEvent {

        private propertyName: string;

        constructor(path: ComponentPath, propertyName: string) {
            super(path);
            this.propertyName = propertyName;
        }

        public getPropertyName(): string {
            return this.propertyName;
        }

    }
}