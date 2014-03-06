module api.content.page.text {

    export class TextDescriptorsResourceRequest extends TextDescriptorResourceRequest<json.TextDescriptorsJson> {

        fromJsonToTextDescriptors(json: json.TextDescriptorsJson): TextDescriptor[] {

            var array: api.content.page.text.TextDescriptor[] = [];
            json.descriptors.forEach((descriptorJson: json.TextDescriptorJson)=> {
                array.push(this.fromJsonToTextDescriptor(descriptorJson));
            });
            return array;
        }

        sendAndParse(): Q.Promise<TextDescriptor[]> {

            var deferred = Q.defer<TextDescriptor[]>();

            this.send().then((response: api.rest.JsonResponse<json.TextDescriptorsJson>) => {
                deferred.resolve(this.fromJsonToTextDescriptors(response.getResult()));
            }).catch((response: api.rest.RequestError) => {
                deferred.reject(null);
            }).done();

            return deferred.promise;
        }
    }
}