module api.content.json {

    export interface WidgetDescriptorJson {

        url: string;
        displayName: string;
        interfaces: string[];
        key: string;
        config: { [key: string]: string };
    }
}
