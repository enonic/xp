module api.content.page.region {

    export class ComponentResetEvent extends ComponentChangedEvent {

        constructor(componentPath: ComponentPath) {
            super(componentPath);

        }

    }
}