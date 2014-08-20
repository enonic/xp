module api.content.page.image {

    export class ImageDescriptorsResourceRequest extends ImageDescriptorResourceRequest<ImageDescriptorsJson, ImageDescriptor[]> {

        fromJsonToImageDescriptors(json: ImageDescriptorsJson): ImageDescriptor[] {

            var array: api.content.page.image.ImageDescriptor[] = [];
            json.descriptors.forEach((descriptorJson: ImageDescriptorJson)=> {
                array.push(this.fromJsonToImageDescriptor(descriptorJson));
            });
            return array;
        }

        sendAndParse(): wemQ.Promise<ImageDescriptor[]> {

            return this.send().then((response: api.rest.JsonResponse<ImageDescriptorsJson>) => {
                return this.fromJsonToImageDescriptors(response.getResult());
            });
        }
    }
}