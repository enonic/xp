module api_schema_content_form{

    export class Layout extends FormItem {

        public static fromRemote(remoteLayout:api_remote_contenttype.Layout):Layout {

            // TODO
            return null;
        }

        constructor(name:string) {
            super(name);
        }
    }
}