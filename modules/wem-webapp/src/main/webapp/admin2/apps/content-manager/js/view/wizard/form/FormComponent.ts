module admin.ui {

    export class FormComponent {

        ext;

        constructor() {

            var panel = new Ext.form.Panel();
            // TODO: fill out panel

            this.ext = panel;
        }

        getContentData():api_content_data.ContentData {
            var contentData = new api_content_data.ContentData();
            return contentData;
        }
    }
}