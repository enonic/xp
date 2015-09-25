module api.content {

    export class WidgetDescriptorResourceRequest<JSON_TYPE, PARSED_TYPE> extends api.rest.ResourceRequest<JSON_TYPE, PARSED_TYPE> {

        private resourcePath: api.rest.Path;


        constructor() {
            super();
            this.resourcePath = api.rest.Path.fromParent(super.getRestPath(), "widget/descriptor");
        }

        getResourcePath(): api.rest.Path {
            return this.resourcePath;
        }

        static fromJson(json: api.content.json.GetWidgetsByInterfaceResultJson): Widget[] {
            var result: Widget[] = [];
            json.widgetDescriptors.forEach((widgetDescriptor: api.content.json.WidgetDescriptorJson) => {
                result.push(new Widget(widgetDescriptor.name,
                    widgetDescriptor.displayName,
                    widgetDescriptor.interfaces,
                    widgetDescriptor.key.applicationKey.name,
                    widgetDescriptor.key.name));
            });
            return result;
        }
    }
}