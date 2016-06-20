import "../../api.ts";
import {MostPopularItemsBlock} from "./MostPopularItemsBlock";
import {RecentItemsBlock} from "./RecentItemsBlock";
import {NewContentDialogItemSelectedEvent} from "./NewContentDialogItemSelectedEvent";
import {NewMediaUploadEvent} from "./NewMediaUploadEvent";
import {NewContentEvent} from "./NewContentEvent";
import {FilterableItemsList} from "./FilterableItemsList";
import {NewContentDialogMediaUploader} from "./NewContentDialogMediaUploader";

import GetAllContentTypesRequest = api.schema.content.GetAllContentTypesRequest;
import GetContentTypeByNameRequest = api.schema.content.GetContentTypeByNameRequest;
import GetNearestSiteRequest = api.content.GetNearestSiteRequest;
import ContentName = api.content.ContentName;
import Content = api.content.Content;
import ContentPath = api.content.ContentPath;
import ContentTypeName = api.schema.content.ContentTypeName;
import ContentTypeSummary = api.schema.content.ContentTypeSummary;
import ContentType = api.schema.content.ContentType;
import Site = api.content.site.Site;
import ApplicationKey = api.application.ApplicationKey;
import FileUploadStartedEvent = api.ui.uploader.FileUploadStartedEvent;
import UploadItem = api.ui.uploader.UploadItem;
import ListContentByPathRequest = api.content.ListContentByPathRequest;

export class NewContentDialog extends api.ui.dialog.ModalDialog {

    private contentDialogTitle: NewContentDialogTitle;

    private parentContent: api.content.Content;

    private fileInput: api.ui.text.FileInput;

    private uploader: NewContentDialogMediaUploader;

    private allContentTypes: FilterableItemsList;

    private mostPopularContentTypes: MostPopularItemsBlock;

    private recentContentTypes: RecentItemsBlock;

    constructor() {
        this.contentDialogTitle = new NewContentDialogTitle("Create Content", "");

        super({
            title: this.contentDialogTitle
        });

        this.addClass("new-content-dialog");

        this.initElements();

        this.appendElementsToDialog();

        api.dom.Body.get().appendChild(this);
    }

    private initElements() {
        this.initContentTypesLists();
        this.initFileInput();
        this.initMediaUploader();
    }

    private initContentTypesLists() {
        this.allContentTypes = new FilterableItemsList();
        this.mostPopularContentTypes = new MostPopularItemsBlock();
        this.recentContentTypes = new RecentItemsBlock();

        this.allContentTypes.onSelected(this.closeAndFireEventFromContentType.bind(this));
        this.mostPopularContentTypes.getItemsList().onSelected(this.closeAndFireEventFromContentType.bind(this));
        this.recentContentTypes.getItemsList().onSelected(this.closeAndFireEventFromContentType.bind(this));
    }


    private initFileInput() {
        this.fileInput = new api.ui.text.FileInput('large').setPlaceholder("Search for content types").setUploaderParams({
            parent: ContentPath.ROOT.toString()
        });

        this.initFileInputEvents();
    }

    private initFileInputEvents() {
        this.fileInput.onUploadStarted(this.closeAndFireEventFromMediaUpload.bind(this));

        this.fileInput.onInput((event: Event) => {
            if (api.util.StringHelper.isEmpty(this.fileInput.getValue())) {
                this.mostPopularContentTypes.showIfNotEmpty();
            } else {
                this.mostPopularContentTypes.hide();
            }

            this.allContentTypes.filter(this.fileInput.getValue());
        });

        this.fileInput.onKeyUp((event: KeyboardEvent) => {
            if (event.keyCode === 27) {
                this.getCancelAction().execute();
            }
        });

        this.fileInput.onShown(() => {
            this.fileInput.giveFocus();
        });
    }

    private initMediaUploader() {
        this.uploader = new NewContentDialogMediaUploader();
        this.uploader.onUploadStarted(this.closeAndFireEventFromMediaUpload.bind(this));

        this.initDragAndDropUploaderEvents();
    }

    private initDragAndDropUploaderEvents() {
        var dragOverEl;
        // make use of the fact that when dragging
        // first drag enter occurs on the child element and after that
        // drag leave occurs on the parent element that we came from
        // meaning that to know when we left some element
        // we need to compare it to the one currently dragged over
        this.onDragEnter((event: DragEvent) => {
            if (this.uploader.isEnabled()) {
                var target = <HTMLElement> event.target;

                if (!!dragOverEl || dragOverEl == this.getHTMLElement()) {
                    this.uploader.show();
                }
                dragOverEl = target;
            }
        });

        this.onDragLeave((event: DragEvent) => {
            if (this.uploader.isEnabled()) {
                var targetEl = <HTMLElement> event.target;

                if (dragOverEl == targetEl) {
                    this.uploader.hide();
                }
            }
        });

        this.onDrop((event: DragEvent) => {
            if (this.uploader.isEnabled()) {
                this.uploader.hide();
            }
        });
    }

    private closeAndFireEventFromMediaUpload(event: FileUploadStartedEvent<Content>) {
        this.close();
        new NewMediaUploadEvent(event.getUploadItems(), this.parentContent).fire();
    }

    private closeAndFireEventFromContentType(event: NewContentDialogItemSelectedEvent) {
        this.close();
        new NewContentEvent(event.getItem().getContentType(), this.parentContent).fire();
    }

    private appendElementsToDialog() {
        var section = new api.dom.SectionEl().setClass("column");
        this.appendChildToContentPanel(section);

        this.mostPopularContentTypes.hide();

        var contentTypesListDiv = new api.dom.DivEl("content-types-content");
        contentTypesListDiv.appendChildren(<api.dom.Element>this.mostPopularContentTypes,
            <api.dom.Element>this.allContentTypes);

        section.appendChildren(<api.dom.Element>this.fileInput, <api.dom.Element>contentTypesListDiv);

        this.appendChildToContentPanel(this.recentContentTypes);

        this.appendChild(this.uploader);
    }

    setParentContent(parent: api.content.Content) {
        this.parentContent = parent;
        this.allContentTypes.setParentContent(parent);

        var params: {[key: string]: any} = {
            parent: parent ? parent.getPath().toString() : api.content.ContentPath.ROOT.toString()
        };

        this.uploader.setParams(params);
        this.fileInput.setUploaderParams(params)
    }

    open() {
        super.open();
        var keyBindings = [
            new api.ui.KeyBinding('up', () => {
                api.dom.FormEl.moveFocusToPrevFocusable(api.dom.Element.fromHtmlElement(<HTMLElement>document.activeElement),
                    "input,li");
            }).setGlobal(true),
            new api.ui.KeyBinding('down', () => {
                api.dom.FormEl.moveFocusToNextFocusable(api.dom.Element.fromHtmlElement(<HTMLElement>document.activeElement),
                    "input,li");
            }).setGlobal(true)];

        api.ui.KeyBindings.get().bindKeys(keyBindings);
    }

    show() {
        this.updateDialogTitlePath();

        this.toggleUploaderEnabled();
        this.resetFileInputWithUploader();

        super.show();

        // CMS-3711: reload content types each time when dialog is show.
        // It is slow but newly create content types are displayed.
        this.loadContentTypes();
    }

    hide() {
        super.hide();
        this.uploader.stop();
        this.mostPopularContentTypes.hide();
        this.clearAllItems();
    }

    close() {
        this.fileInput.reset();
        super.close();
    }

    private loadContentTypes() {

        this.showLoadingMasks();

        wemQ.all(this.sendRequestsToFetchContentData())
            .spread((contentTypes: ContentTypeSummary[], directChilds: api.content.ContentResponse<api.content.ContentSummary>,
                     parentSite: Site) => {

                this.allContentTypes.createItems(contentTypes, parentSite);
                this.mostPopularContentTypes.getItemsList().createItems(this.allContentTypes.getItems(), directChilds.getContents());
                this.recentContentTypes.getItemsList().createItems(this.allContentTypes.getItems());

            }).catch((reason: any) => {

            api.DefaultErrorHandler.handle(reason);

        }).finally(() => {
            this.hideLoadingMasks();
            this.mostPopularContentTypes.showIfNotEmpty();
            this.centerMyself();
        }).done();
    }

    private showLoadingMasks() {
        this.allContentTypes.showLoadingMask();
        this.recentContentTypes.getItemsList().showLoadingMask();
    }

    private hideLoadingMasks() {
        this.allContentTypes.hideLoadingMask();
        this.recentContentTypes.getItemsList().hideLoadingMask();
    }

    private sendRequestsToFetchContentData(): wemQ.Promise<any>[] {
        var requests: wemQ.Promise<any>[] = [];
        requests.push(new GetAllContentTypesRequest().sendAndParse());
        if (this.parentContent) {
            requests.push(new ListContentByPathRequest(this.parentContent.getPath()).sendAndParse());
            requests.push(new GetNearestSiteRequest(this.parentContent.getContentId()).sendAndParse());
        } else {
            requests.push(new ListContentByPathRequest(ContentPath.ROOT).sendAndParse());
        }

        return requests;
    }

    private updateDialogTitlePath() {
        if (this.parentContent) {
            this.contentDialogTitle.setPath(this.parentContent.getPath().toString());
        } else {
            this.contentDialogTitle.setPath('');
        }
    }

    private clearAllItems() {
        this.mostPopularContentTypes.getItemsList().clearItems();
        this.allContentTypes.clearItems();
        this.recentContentTypes.getItemsList().clearItems();
    }

    private toggleUploaderEnabled() {
        var uploaderEnabled = !this.parentContent || !this.parentContent.getType().isTemplateFolder();
        this.uploader.setEnabled(uploaderEnabled);
        this.toggleClass("no-uploader-el", !uploaderEnabled);
    }

    private resetFileInputWithUploader() {
        this.uploader.reset();
        this.fileInput.reset();
        this.fileInput.getUploader().setEnabled(this.uploader.isEnabled());
    }
}

export class NewContentDialogTitle extends api.ui.dialog.ModalDialogHeader {

    private pathEl: api.dom.PEl;

    constructor(title: string, path: string) {
        super(title);

        this.pathEl = new api.dom.PEl('path');
        this.pathEl.setHtml(path);
        this.appendChild(this.pathEl);
    }

    setPath(path: string) {
        this.pathEl.setHtml(path).setVisible(!api.util.StringHelper.isBlank(path));
    }
}
