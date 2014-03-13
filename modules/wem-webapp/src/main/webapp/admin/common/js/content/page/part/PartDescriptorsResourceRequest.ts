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

            var deferred = Q.defer<PartDescriptor[]>();

            this.send().then((response: api.rest.JsonResponse<PartDescriptorsJson>) => {
                deferred.resolve(this.fromJsonToPartDescriptors(response.getResult()));
            }).catch((response: api.rest.RequestError) => {
                deferred.reject(null);
            }).done();

            return deferred.promise;
        }
    }
}