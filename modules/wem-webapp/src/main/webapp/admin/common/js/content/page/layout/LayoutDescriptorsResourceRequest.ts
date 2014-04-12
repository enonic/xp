module api.content.page.layout {

    export class LayoutDescriptorsResourceRequest extends LayoutDescriptorResourceRequest<LayoutDescriptorsJson> {

        fromJsonToLayoutDescriptors(json: LayoutDescriptorsJson): LayoutDescriptor[] {

            var array: api.content.page.layout.LayoutDescriptor[] = [];
            json.descriptors.forEach((descriptorJson: LayoutDescriptorJson)=> {
                array.push(this.fromJsonToLayoutDescriptor(descriptorJson));
            });
            return array;
        }

        sendAndParse(): Q.Promise<LayoutDescriptor[]> {

            return this.send().then((response: api.rest.JsonResponse<LayoutDescriptorsJson>) => {
                return this.fromJsonToLayoutDescriptors(response.getResult());
            });
        }
    }
}