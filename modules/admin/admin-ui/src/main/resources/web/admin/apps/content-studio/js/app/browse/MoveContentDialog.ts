import "../../api.ts";
import {OpenMoveDialogEvent} from "./OpenMoveDialogEvent";
import {ContentMoveComboBox} from "./ContentMoveComboBox";

import ContentPath = api.content.ContentPath;
import ContentType = api.schema.content.ContentType;
import GetContentTypeByNameRequest = api.schema.content.GetContentTypeByNameRequest;
import ContentSummary = api.content.ContentSummary;
import ContentResponse = api.content.resource.result.ContentResponse;
import ContentIds = api.content.ContentIds;
import MoveContentResult = api.content.resource.result.MoveContentResult;
import MoveContentResultFailure = api.content.resource.result.MoveContentResultFailure;

export class MoveContentDialog extends api.ui.dialog.ModalDialog {

    private destinationSearchInput: ContentMoveComboBox;

    private movedContentSummaries: api.content.ContentSummary[];

    private contentPathSubHeader: api.dom.H6El;

    private moveMask: api.ui.mask.LoadMask;

    constructor() {
        super("Move item with children");

        this.addClass("move-content-dialog");

        this.contentPathSubHeader = new api.dom.H6El().addClass("content-path");
        var descMessage = new api.dom.H6El().addClass("desc-message").setHtml(
            "Moves selected items with all children and current permissions to selected destination");
        this.moveMask = new api.ui.mask.LoadMask(this);
        this.initSearchInput();
        this.initMoveAction();

        this.listenOpenMoveDialogEvent();

        this.appendChildToContentPanel(this.contentPathSubHeader);
        this.appendChildToContentPanel(descMessage);
        this.appendChildToContentPanel(this.destinationSearchInput);
        this.appendChildToContentPanel(this.moveMask);
        this.addCancelButtonToBottom();
    }

    private listenOpenMoveDialogEvent() {
        OpenMoveDialogEvent.on((event) => {

            this.movedContentSummaries = event.getContentSummaries();
            this.destinationSearchInput.clearCombobox();

            const contents = event.getContentSummaries();

            const allContentTypes = contents.map((content)=> content.getType());
            const contentTypes = api.util.ArrayHelper.removeDuplicates(allContentTypes, (ct) => ct.toString());
            const contentTypeRequests = contentTypes.map((contentType)=> new GetContentTypeByNameRequest(contentType).sendAndParse());

            const deferred = wemQ.defer<ContentType[]>();
            wemQ.all(contentTypeRequests).spread((...filterContentTypes: ContentType[]) => {
                const filterContentPaths = contents.map((content)=> content.getPath());
                this.destinationSearchInput.setFilterContentPaths(filterContentPaths);
                this.destinationSearchInput.setFilterContentTypes(filterContentTypes);
                this.contentPathSubHeader.setHtml(contents.length === 1 ? contents[0].getPath().toString() : "");
                this.open();
                deferred.resolve(filterContentTypes);
            }).catch((reason: any) => deferred.reject(reason)).done();
        });
    }

    private initSearchInput() {
        this.destinationSearchInput = new ContentMoveComboBox();
        this.destinationSearchInput.addClass("content-selector");
        this.destinationSearchInput.onKeyUp((event: KeyboardEvent) => {
            if (event.keyCode === 27) {
                this.getCancelAction().execute();
            }
        });
        this.destinationSearchInput.onOptionSelected(() => {
            this.getButtonRow().focusDefaultAction();
        });
    }

    private initMoveAction() {

        this.addAction(new api.ui.Action("Move", "").onExecuted(() => {

            this.moveMask.show();

            var parentContent = this.getParentContent();
            this.moveContent(parentContent);
        }), true);
    }

    private moveContent(parentContent: api.content.ContentSummary) {
        var parentRoot = (!!parentContent) ? parentContent.getPath() : ContentPath.ROOT;

        var contentIds = ContentIds.create().fromContentIds(this.movedContentSummaries.map(summary => summary.getContentId())).build();

        new api.content.resource.MoveContentRequest(contentIds, parentRoot).sendAndParse().then((response: MoveContentResult) => {
            if (parentContent) {
                this.destinationSearchInput.deselect(parentContent);
            }
            this.moveMask.hide();

            if (response.getMoved().length > 0) {
                if (response.getMoved().length > 1) {
                    api.notify.showFeedback(response.getMoved().length + ' items moved');
                } else {
                    api.notify.showFeedback("\"" + response.getMoved()[0] + '\" moved');
                }
            }

            response.getMoveFailures().forEach((failure: MoveContentResultFailure) => {
                api.notify.showWarning(failure.getReason());
            });
            this.close();
        }).catch((reason)=> {
            api.notify.showWarning(reason.getMessage());
            this.close();
            this.destinationSearchInput.deselect(this.getParentContent());
        }).done();
    }

    private getParentContent(): api.content.ContentSummary {
        return <api.content.ContentSummary> this.destinationSearchInput.getSelectedDisplayValues()[0];
    }

    show() {
        api.dom.Body.get().appendChild(this);
        super.show();
        this.destinationSearchInput.giveFocus();
    }

}
