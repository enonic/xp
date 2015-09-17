module api.content.page.region {

    export class PartDescriptorsResourceRequest extends PartDescriptorResourceRequest<PartDescriptorsJson, PartDescriptor[]> {

        fromJsonToPartDescriptors(json: PartDescriptorsJson): PartDescriptor[] {

            var array: PartDescriptor[] = [];
            json.descriptors.forEach((descriptorJson: PartDescriptorJson)=> {
                array.push(this.fromJsonToPartDescriptor(descriptorJson));
            });
            return array;
        }
    }
}