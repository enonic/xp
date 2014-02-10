module api.content.page.image {

    export class ImageDescriptorsResourceRequest extends ImageDescriptorResourceRequest<json.ImageDescriptorsJson> {

        fromJsonToImageDescriptors(json: json.ImageDescriptorsJson): ImageDescriptor[] {

            var array: api.content.page.image.ImageDescriptor[] = [];
            json.descriptors.forEach((descriptorJson: json.ImageDescriptorJson)=> {
                array.push(this.fromJsonToImageDescriptor(descriptorJson));
            });
            return array;
        }

        sendAndParse(): Q.Promise<ImageDescriptor[]> {

            var deferred = Q.defer<ImageDescriptor[]>();

            this.send().then((response: api.rest.JsonResponse<json.ImageDescriptorsJson>) => {
                deferred.resolve(this.fromJsonToImageDescriptors(response.getResult()));
            }).catch((response: api.rest.RequestError) => {
                deferred.reject(null);
            }).done();

            return deferred.promise;
        }
    }
}