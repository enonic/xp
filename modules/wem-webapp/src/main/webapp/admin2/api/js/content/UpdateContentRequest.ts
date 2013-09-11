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
        }

        getUrl() {
            return super.getResourceUrl() + "/update";
        }

        updateParams():UpdateContentRequest {
            super.updateParams();
            var params = super.getParams();
            params['contentId'] = this.id;
            super.setParams(params);
            return this;
        }
    }
}