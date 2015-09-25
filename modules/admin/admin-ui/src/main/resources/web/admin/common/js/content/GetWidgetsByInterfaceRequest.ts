module api.content {

    export class GetWidgetsByInterfaceRequest extends WidgetDescriptorResourceRequest<api.content.json.GetWidgetsByInterfaceResultJson, any> {

        private widgetInterface: string;

        constructor(widgetInterface: string) {
            super();
            super.setMethod("GET");
            this.widgetInterface = widgetInterface;
        }

        getParams(): Object {
            return {
                "interface": this.widgetInterface
            };
        }

        getRequestPath(): api.rest.Path {
            return api.rest.Path.fromParent(super.getResourcePath(), "byInterface");
        }

        sendAndParse(): wemQ.Promise<Widget[]> {

            return this.send().
                then((response: api.rest.JsonResponse<api.content.json.GetWidgetsByInterfaceResultJson>) => {
                    return WidgetDescriptorResourceRequest.fromJson(response.getResult());
                });
        }
    }
}