module api.content.page.part {

    export class PartDescriptorsResourceRequest extends PartDescriptorResourceRequest<PartDescriptorsJson> {

        fromJsonToPartDescriptors(json: PartDescriptorsJson): PartDescriptor[] {

            var array: api.content.page.part.PartDescriptor[] = [];
            json.descriptors.forEach((descriptorJson: PartDescriptorJson)=> {
                array.push(this.fromJsonToPartDescriptor(descriptorJson));
            });
            return array;
        }

        sendAndParse(): Q.Promise<PartDescriptor[]> {

            return this.send().then((response: api.rest.JsonResponse<PartDescriptorsJson>) => {
                return this.fromJsonToPartDescriptors(response.getResult());
            });
        }
    }
}