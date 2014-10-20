module api.content.page.part {

    export class PartDescriptorsResourceRequest extends PartDescriptorResourceRequest<PartDescriptorsJson, PartDescriptor[]> {

        fromJsonToPartDescriptors(json: PartDescriptorsJson): PartDescriptor[] {

            var array: api.content.page.part.PartDescriptor[] = [];
            json.descriptors.forEach((descriptorJson: PartDescriptorJson)=> {
                array.push(this.fromJsonToPartDescriptor(descriptorJson));
            });
            return array;
        }
    }
}