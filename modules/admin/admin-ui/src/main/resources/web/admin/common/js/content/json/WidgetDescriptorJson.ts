module api.content.json {

    export interface WidgetDescriptorJson {

        name: string;
        displayName: string;
        interfaces: string[];
        key: WidgetDescriptorKeyJson;
    }

    export interface WidgetDescriptorKeyJson {

        name: string;
        applicationKey: WidgetDescriptorApplicationKeyJson;
    }

    export interface WidgetDescriptorApplicationKeyJson {

        name: string;
    }
}