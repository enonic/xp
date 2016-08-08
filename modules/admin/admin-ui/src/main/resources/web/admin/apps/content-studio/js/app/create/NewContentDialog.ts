import "../../api.ts";
import {MostPopularItemsBlock} from "./MostPopularItemsBlock";
import {RecentItemsBlock} from "./RecentItemsBlock";
import {NewContentDialogItemSelectedEvent} from "./NewContentDialogItemSelectedEvent";
import {NewMediaUploadEvent} from "./NewMediaUploadEvent";
import {NewContentEvent} from "./NewContentEvent";
import {FilterableItemsList} from "./FilterableItemsList";

import GetAllContentTypesRequest = api.schema.content.GetAllContentTypesRequest;
import GetContentTypeByNameRequest = api.schema.content.GetContentTypeByNameRequest;
import GetNearestSiteRequest = api.content.resource.GetNearestSiteRequest;
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
import ListContentByPathRequest = api.content.resource.ListContentByPathRequest;
import LoadMask = api.ui.mask.LoadMask;
import ContentResponse = api.content.resource.result.ContentResponse;

export class NewContentDialog extends api.ui.dialog.ModalDialog {

    private contentDialogTitle: NewContentDialogTitle;

    private parentContent: api.content.Content;

    private fileInput: api.ui.text.FileInput;

    private dropzoneContainer: api.ui.uploader.DropzoneContainer;

    private allContentTypes: FilterableItemsList;

    private mostPopularContentTypes: MostPopularItemsBlock;

    private recentContentTypes: RecentItemsBlock;

    protected loadMask: LoadMask;

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
        this.initDragAndDropUploaderEvents();
        this.initLoadMask();
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
        this.dropzoneContainer = new api.ui.uploader.DropzoneContainer(true);
        this.dropzoneContainer.hide();
        this.appendChild(this.dropzoneContainer);

        this.fileInput = new api.ui.text.FileInput('large', undefined).
            setPlaceholder("Search for content types").
            setUploaderParams({parent: ContentPath.ROOT.toString()});

        this.fileInput.getUploader().addDropzone(this.dropzoneContainer.getDropzone().getId());

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
    }

    private initLoadMask() {
        this.loadMask = new LoadMask(this);
    }

    // in order to toggle appropriate handlers during drag event
    // we catch drag enter on this element and trigger uploader to appear,
    // then catch drag leave on uploader's dropzone to get back to previous state
    private initDragAndDropUploaderEvents() {
        var dragOverEl;
        this.onDragEnter((event: DragEvent) => {
            if (this.fileInput.getUploader().isEnabled()) {
                var target = <HTMLElement> event.target;

                if (!!dragOverEl || dragOverEl == this.getHTMLElement()) {
                    this.dropzoneContainer.show();
                }
                dragOverEl = target;
            }
        });

        this.fileInput.getUploader().onDropzoneDragLeave(() => this.dropzoneContainer.hide());
        this.fileInput.getUploader().onDropzoneDrop(() => this.dropzoneContainer.hide());
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

        this.getContentPanel().getParentElement().appendChild(this.loadMask);
    }

    setParentContent(parent: api.content.Content) {
        this.parentContent = parent;
        this.allContentTypes.setParentContent(parent);

        var params: {[key: string]: any} = {
            parent: parent ? parent.getPath().toString() : api.content.ContentPath.ROOT.toString()
        };

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

        this.fileInput.disable();
        //this.uploader.setEnabled(false);
        this.resetFileInputWithUploader();

        super.show();

        // CMS-3711: reload content types each time when dialog is show.
        // It is slow but newly create content types are displayed.
        this.loadContentTypes();
    }

    hide() {
        super.hide();
        this.mostPopularContentTypes.hide();
        this.clearAllItems();
    }

    close() {
        this.fileInput.reset();
        super.close();
    }

    private loadContentTypes() {

        this.loadMask.show();

        wemQ.all(this.sendRequestsToFetchContentData())
            .spread((contentTypes: ContentTypeSummary[], directChilds: ContentResponse<api.content.ContentSummary>,
                     parentSite: Site) => {

                this.allContentTypes.createItems(contentTypes, parentSite);
                this.mostPopularContentTypes.getItemsList().createItems(this.allContentTypes.getItems(), directChilds.getContents());
                this.recentContentTypes.getItemsList().createItems(this.allContentTypes.getItems());

            }).catch((reason: any) => {

            api.DefaultErrorHandler.handle(reason);

        }).finally(() => {
            this.fileInput.enable();
            this.fileInput.giveFocus();
            this.toggleUploadersEnabled();
            this.loadMask.hide();
            this.mostPopularContentTypes.showIfNotEmpty();
            this.centerMyself();
        }).done();
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

    private toggleUploadersEnabled() {
        var uploaderEnabled = !this.parentContent || !this.parentContent.getType().isTemplateFolder();
        this.toggleClass("no-uploader-el", !uploaderEnabled);
        this.fileInput.getUploader().setEnabled(uploaderEnabled);
    }

    private resetFileInputWithUploader() {
        this.fileInput.reset();
        this.fileInput.getUploader().setEnabled(false);
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
