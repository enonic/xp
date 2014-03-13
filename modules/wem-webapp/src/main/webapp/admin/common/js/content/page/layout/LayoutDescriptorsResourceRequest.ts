module api.content.page.layout {

    export class LayoutDescriptorsResourceRequest extends LayoutDescriptorResourceRequest<LayoutDescriptorsJson> {

        fromJsonToLayoutDescriptors(json: LayoutDescriptorsJson): LayoutDescriptor[] {

            var array: api.content.page.layout.LayoutDescriptor[] = [];
            json.descriptors.forEach((descriptorJson: LayoutDescriptorJson)=> {
                array.push(this.fromJsonToLayoutDescriptor(descriptorJson));
            });
            return array;
        }

        sendAndParse(): Q.Promise<LayoutDescriptor[]> {

            var deferred = Q.defer<LayoutDescriptor[]>();

            this.send().then((response: api.rest.JsonResponse<LayoutDescriptorsJson>) => {
                deferred.resolve(this.fromJsonToLayoutDescriptors(response.getResult()));
            }).catch((response: api.rest.RequestError) => {
                deferred.reject(null);
            }).done();

            return deferred.promise;
        }
    }
}