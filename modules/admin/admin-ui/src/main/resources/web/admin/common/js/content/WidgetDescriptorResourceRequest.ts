module api.content {

    export class WidgetDescriptorResourceRequest<JSON_TYPE, PARSED_TYPE> extends api.rest.ResourceRequest<JSON_TYPE, PARSED_TYPE> {

        private resourcePath: api.rest.Path;


        constructor() {
            super();
            this.resourcePath = api.rest.Path.fromParent(super.getRestPath(), "widget");
        }

        getResourcePath(): api.rest.Path {
            return this.resourcePath;
        }

        static fromJson(json: api.content.json.GetWidgetsByInterfaceResultJson): Widget[] {
            var result: Widget[] = [];
            json.widgetDescriptors.forEach((widgetDescriptor: api.content.json.WidgetDescriptorJson) => {
                result.push(new Widget(widgetDescriptor.url,
                    widgetDescriptor.displayName,
                    widgetDescriptor.interfaces,
                    widgetDescriptor.key));
            });
            return result;
        }
    }
}