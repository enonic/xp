module api.content.form.inputtype.upload {

    export class VectorUploader extends MediaUploader {

        private svgWrapper: api.dom.DivEl;

        constructor(config: api.content.form.inputtype.ContentInputTypeViewContext) {
            super(config, "vector-uploader");
        }

        layoutProperty(input: api.form.Input, property: api.data.Property): wemQ.Promise<void> {
            var result: wemQ.Promise<void> = super.layoutProperty(input, property);

            this.svgWrapper = new api.dom.DivEl("svg-preview-wrapper");

            this.appendChild(this.svgWrapper);

            this.getSvgSourceAndShow(this.getConfig().contentId);

            this.getMediaUploaderEl().onFileUploaded((event: api.ui.uploader.FileUploadedEvent<api.content.Content>) => {

                var content = event.getUploadItem().getModel();

                this.getSvgSourceAndShow(content.getContentId());
            });

            return result;
        }

        private getSvgSourceAndShow(contentId: api.content.ContentId) {
            new api.content.GetSvgContentSourceRequest(contentId).sendAndParse().
                then((svgSource: string) => {
                    if (!!svgSource) {
                        this.svgWrapper.setHtml(svgSource, false);
                        this.getMediaUploaderEl().setResultVisible(true); // need to call it manually as svg images are uploaded too quickly
                    }
                });
        }

    }

    api.form.inputtype.InputTypeManager.register(new api.Class("VectorUploader", VectorUploader));
}