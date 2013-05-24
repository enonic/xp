module admin.ui {

    export class FormComponent {

        ext;

        constructor() {

            var panel = new Ext.form.Panel();
            // TODO: fill out panel

            this.ext = panel;
        }

        getContentData():API_content_data.ContentData {
            var contentData = new API_content_data.ContentData();
            return contentData;
        }
    }
}