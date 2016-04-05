module api.util.htmlarea.editor {

    import AnchorModalDialog = api.util.htmlarea.dialog.AnchorModalDialog;
    import ImageModalDialog = api.util.htmlarea.dialog.ImageModalDialog;
    import LinkModalDialog = api.util.htmlarea.dialog.LinkModalDialog;
    import HtmlAreaAnchor = api.util.htmlarea.dialog.HtmlAreaAnchor;
    import HtmlAreaImage = api.util.htmlarea.dialog.HtmlAreaImage;

    export class HTMLAreaHelper {

        private static getConvertedImageSrc(imgSrc:string):string {
            var contentId = imgSrc.replace(ImageModalDialog.imagePrefix, api.util.StringHelper.EMPTY_STRING),
                imageUrl = new api.content.ContentImageUrlResolver().
                    setContentId(new api.content.ContentId(contentId)).
                    setScaleWidth(true).
                    setSize(ImageModalDialog.maxImageWidth).
                    resolve();

            return "src=\"" + imageUrl + "\" data-src=\"" + imgSrc + "\"";
        }

        public static prepareImgSrcsInValueForEdit(value:string):string {
            var processedContent = value,
                regex = /<img.*?src="(.*?)"/g,
                imgSrcs;

            if (!processedContent) {
                return value;
            }

            while (processedContent.search(" src=\"" + ImageModalDialog.imagePrefix) > -1) {
                imgSrcs = regex.exec(processedContent);
                if (imgSrcs) {
                    imgSrcs.forEach((imgSrc:string) => {
                        if (imgSrc.indexOf(ImageModalDialog.imagePrefix) === 0) {
                            processedContent =
                                processedContent.replace(" src=\"" + imgSrc + "\"", HTMLAreaHelper.getConvertedImageSrc(imgSrc));
                        }
                    });
                }
            }
            return processedContent;
        }

        public static prepareEditorImageSrcsBeforeSave(editor:HtmlAreaEditor):string {
            var content = editor.getContent(),
                processedContent = editor.getContent(),
                regex = /<img.*?data-src="(.*?)".*?>/g,
                imgTags, imgTag;

            while ((imgTags = regex.exec(content)) != null) {
                imgTag = imgTags[0];
                if (imgTag.indexOf("<img ") === 0 && imgTag.indexOf(ImageModalDialog.imagePrefix) > 0) {
                    var dataSrc = /<img.*?data-src="(.*?)".*?>/.exec(imgTag)[1],
                        src = /<img.*?src="(.*?)".*?>/.exec(imgTags[0])[1];

                    var convertedImg = imgTag.replace(src, dataSrc).replace(" data-src=\"" + dataSrc + "\"",
                        api.util.StringHelper.EMPTY_STRING);
                    processedContent = processedContent.replace(imgTag, convertedImg);
                }
            }

            return processedContent;
        }

        public static updateImageAlignmentBehaviour(editor) {
            var imgs = editor.getBody().querySelectorAll('img');

            for (let i = 0; i < imgs.length; i++) {
                this.changeImageParentAlignmentOnImageAlignmentChange(imgs[i]);
            }
        }

        public static changeImageParentAlignmentOnImageAlignmentChange(img: HTMLImageElement) {
            var observer = new MutationObserver((mutations) => {
                mutations.forEach((mutation) => {
                    var alignment = (<HTMLElement>mutation.target).style.textAlign;
                    HTMLAreaHelper.updateImageParentAlignment(img, alignment);
                });
            });

            var config = {attributes: true, childList: false, characterData: false, attributeFilter: ["style"]};

            observer.observe(img, config);
        }

        public static updateImageParentAlignment(image: HTMLElement, alignment?: string) {
            if (!alignment) {
                alignment = image.style.textAlign;
            }

            var styleFormat = "float: {0}; margin: {1};" +
                (HTMLAreaHelper.isImageInOriginalSize(image) ? "" : "width: {2}%;");
            var styleAttr = "text-align: " + alignment + ";";

            switch (alignment) {
                case 'left':
                case 'right':
                    styleAttr = api.util.StringHelper.format(styleFormat, alignment, "15px", "40");
                    break;
                case 'center':
                    styleAttr = styleAttr + api.util.StringHelper.format(styleFormat, "none", "auto", "60");
                    break;
            }

            image.parentElement.setAttribute("style", styleAttr);
            image.parentElement.setAttribute("data-mce-style", styleAttr);
        }

        private static isImageInOriginalSize(image: HTMLElement) {
            return image.getAttribute("data-src").indexOf("keepSize=true") > 0;
        }
    }
}