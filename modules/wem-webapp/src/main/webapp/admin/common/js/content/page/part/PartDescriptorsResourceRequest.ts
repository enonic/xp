module api.content.page.part {

    export class PartDescriptorsResourceRequest extends PartDescriptorResourceRequest<json.PartDescriptorsJson> {

        fromJsonToPartDescriptors(json: json.PartDescriptorsJson): PartDescriptor[] {

            var array: api.content.page.part.PartDescriptor[] = [];
            json.descriptors.forEach((descriptorJson: json.PartDescriptorJson)=> {
                array.push(this.fromJsonToPartDescriptor(descriptorJson));
            });
            return array;
        }

        sendAndParse(): Q.Promise<PartDescriptor[]> {

            var deferred = Q.defer<PartDescriptor[]>();

            this.send().then((response: api.rest.JsonResponse<json.PartDescriptorsJson>) => {
                deferred.resolve(this.fromJsonToPartDescriptors(response.getResult()));
            }).catch((response: api.rest.RequestError) => {
                deferred.reject(null);
            }).done();

            return deferred.promise;
        }
    }
}