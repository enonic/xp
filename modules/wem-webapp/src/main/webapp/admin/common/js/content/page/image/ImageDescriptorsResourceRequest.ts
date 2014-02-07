module api.content.page.image {

    export class ImageDescriptorsResourceRequest extends ImageDescriptorResourceRequest<json.ImageDescriptorsJson> {

        fromJsonToImageDescriptors(json: json.ImageDescriptorsJson): ImageDescriptor[] {

            var array: api.content.page.image.ImageDescriptor[] = [];
            json.descriptors.forEach((descriptorJson: json.ImageDescriptorJson)=> {
                array.push(this.fromJsonToImageDescriptor(descriptorJson));
            });
            return array;
        }

        sendAndParse(): JQueryPromise<ImageDescriptor[]> {

            var deferred = jQuery.Deferred<ImageDescriptor[]>();

            this.send().done((response: api.rest.JsonResponse<json.ImageDescriptorsJson>) => {
                deferred.resolve(this.fromJsonToImageDescriptors(response.getResult()));
            }).fail((response: api.rest.RequestError) => {
                deferred.reject(null);
            });

            return deferred;
        }
    }
}