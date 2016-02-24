module api.content {

    export class GetWidgetsByInterfaceRequest extends WidgetDescriptorResourceRequest<api.content.json.WidgetDescriptorJson[], any> {

        private widgetInterfaces: string[];

        constructor(widgetInterfaces: string[]) {
            super();
            super.setMethod("POST");
            this.widgetInterfaces = widgetInterfaces;
        }

        getParams(): Object {
            return this.widgetInterfaces;
        }

        getRequestPath(): api.rest.Path {
            return api.rest.Path.fromParent(super.getResourcePath(), "list/byinterfaces");
        }

        sendAndParse(): wemQ.Promise<Widget[]> {

            return this.send().
                then((response: api.rest.JsonResponse<api.content.json.WidgetDescriptorJson[]>) => {
                    return WidgetDescriptorResourceRequest.fromJson(response.getResult());
                });
        }
    }
}