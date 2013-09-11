module api_content {

    export class UpdateContentRequest extends CreateContentRequest {

        private id:string;

        constructor(id:string) {
            super();
            this.id = id;
            this.updateParams();
        }

        getId():string {
            return this.id;
        }

        setId(id:string) {
            this.id = id;
            this.updateParams();
            return this;
        }

        getUrl() {
            return super.getResourceUrl() + "/update";
        }

        updateParams() {
            var params = super.updateParams();
            params['contentId'] = this.id;
            super.setParams(params);
            return params;
        }
    }
}