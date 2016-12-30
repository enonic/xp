module api.util.htmlarea.editor {

    import AnchorModalDialog = api.util.htmlarea.dialog.AnchorModalDialog;
    import ImageModalDialog = api.util.htmlarea.dialog.ImageModalDialog;
    import LinkModalDialog = api.util.htmlarea.dialog.LinkModalDialog;
    import HtmlAreaAnchor = api.util.htmlarea.dialog.HtmlAreaAnchor;
    import HtmlAreaImage = api.util.htmlarea.dialog.HtmlAreaImage;
    import StringHelper = api.util.StringHelper;

    export class HTMLAreaHelper {

        private static getConvertedImageSrc(imgSrc:string):string {
            let contentId = imgSrc.replace(ImageModalDialog.imagePrefix, StringHelper.EMPTY_STRING),
                imageUrl = new api.content.util.ContentImageUrlResolver().
                    setContentId(new api.content.ContentId(contentId)).
                    setScaleWidth(true).
                    setSize(ImageModalDialog.maxImageWidth).
                    resolve();

            return " src=\"" + imageUrl + "\" data-src=\"" + imgSrc + "\"";
        }

        public static prepareImgSrcsInValueForEdit(value:string):string {
            let processedContent = value,
                regex = /<img.*?src="(.*?)"/g,
                imgSrcs;

            if (!processedContent) {
                return "";
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
            const content = editor.getContent();
            const regex = /<img.*?data-src="(.*?)".*?>/g;
            let processedContent = editor.getContent();

            AppHelper.whileTruthy(() => regex.exec(content), (imgTags) => {
                const imgTag = imgTags[0];

                if (imgTag.indexOf("<img ") === 0 && imgTag.indexOf(ImageModalDialog.imagePrefix) > 0) {
                    const dataSrc = /<img.*?data-src="(.*?)".*?>/.exec(imgTag)[1];
                    const src = /<img.*?src="(.*?)".*?>/.exec(imgTags[0])[1];

                    const convertedImg = imgTag.replace(src, dataSrc).replace(` data-src="${dataSrc}"`, StringHelper.EMPTY_STRING);
                    processedContent = processedContent.replace(imgTag, convertedImg);
                }
            });

            return processedContent;
        }

        public static updateImageAlignmentBehaviour(editor: HtmlAreaEditor) {
            let imgs = editor.getBody().querySelectorAll('img');

            for (let i = 0; i < imgs.length; i++) {
                this.changeImageParentAlignmentOnImageAlignmentChange(imgs[i]);
                this.updateImageParentAlignment(imgs[i]);
            }
        }

        public static changeImageParentAlignmentOnImageAlignmentChange(img: HTMLImageElement) {
            let observer = new MutationObserver((mutations) => {
                mutations.forEach((mutation) => {
                    let alignment = (<HTMLElement>mutation.target).style.textAlign;
                    HTMLAreaHelper.updateImageParentAlignment(img, alignment);
                });
            });

            let config = {attributes: true, childList: false, characterData: false, attributeFilter: ["style"]};

            observer.observe(img, config);
        }

        public static updateImageParentAlignment(image: HTMLElement, alignment?: string) {
            if (!alignment) {
                alignment = image.style.textAlign;
            }

            let styleFormat = "float: {0}; margin: {1};" +
                              (HTMLAreaHelper.isImageInOriginalSize(image) ? "" : "width: {2}%;"),
                styleAttr = "";

            image.parentElement.className = "";

            switch (alignment) {
                case 'left':
                case 'right':
                    styleAttr = StringHelper.format(styleFormat, alignment, "15px", "40");
                    break;
                case 'center':
                    styleAttr = StringHelper.format(styleFormat, "none", "auto", "60");
                    image.parentElement.classList.add(alignment);
                    break;
            case 'justify':
                image.parentElement.classList.add(alignment);
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