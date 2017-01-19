import '../../api.ts';

import Button = api.ui.button.Button;
import CloseButton = api.ui.button.CloseButton;

export interface ThumbnailUploaderElConfig extends api.ui.uploader.UploaderElConfig {

}

export class ThumbnailUploaderEl extends api.ui.uploader.UploaderEl<api.content.Content> {

    private iconUrlResolver: api.content.util.ContentIconUrlResolver;

    constructor(config?: ThumbnailUploaderElConfig) {

        if (config.url == null) {
            config.url = api.util.UriHelper.getRestUri('content/updateThumbnail');
        }
        if (config.showCancel == null) {
            config.showCancel = false;
        }
        if (config.resultAlwaysVisisble == null) {
            config.resultAlwaysVisisble = true;
        }
        if (config.allowTypes == null) {
            config.allowTypes = [
                {title: 'Image files', extensions: 'jpg,gif,png,svg'}
            ];
        }
        if (config.allowMultiSelection == null) {
            config.allowMultiSelection = false;
        }
        if (config.hasUploadButton == null) {
            config.hasUploadButton = false;
        }
        if (config.hideDefaultDropZone == null) {
            config.hideDefaultDropZone = false;
        }

        super(config);

        this.addClass('thumbnail-uploader-el');
        this.iconUrlResolver = new api.content.util.ContentIconUrlResolver();
    }

    createModel(serverResponse: api.content.json.ContentJson): api.content.Content {
        if (serverResponse) {
            return new api.content.ContentBuilder().fromContentJson(<api.content.json.ContentJson> serverResponse).build();
        } else {
            return null;
        }
    }

    getModelValue(item: api.content.Content): string {
        return this.iconUrlResolver.setContent(item).resolve();
    }

    createResultItem(value: string): api.dom.Element {
        return new api.dom.ImgEl(value);
    }

}
