module api.content.page.layout {

    export class LayoutDescriptorsResourceRequest extends LayoutDescriptorResourceRequest<LayoutDescriptorsJson, LayoutDescriptor[]> {

        fromJsonToLayoutDescriptors(json: LayoutDescriptorsJson): LayoutDescriptor[] {

            var array: api.content.page.layout.LayoutDescriptor[] = [];
            json.descriptors.forEach((descriptorJson: LayoutDescriptorJson)=> {
                array.push(this.fromJsonToLayoutDescriptor(descriptorJson));
            });
            return array;
        }


    }
}