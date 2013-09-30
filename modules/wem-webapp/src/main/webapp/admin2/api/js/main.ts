/**
 * Main file for all admin API classes and methods.
 */

// require ExtJs as long as it is used for implementation
///<reference path='ExtJs.d.ts' />

///<reference path='plugin/PersistentGridSelectionPlugin.ts' />
///<reference path='plugin/GridToolbarPlugin.ts' />
///<reference path='plugin/fileupload/FileUploadGrid.ts' />
///<reference path='plugin/fileupload/PhotoUploadButton.ts' />
///<reference path='plugin/fileupload/PhotoUploadWindow.ts' />

///<reference path='Mousetrap.d.ts' />
///<reference path='jquery.d.ts' />
///<reference path='jqueryui.d.ts' />
///<reference path='codemirror.d.ts' />
///<reference path='slickgrid.d.ts' />

///<reference path='util/ImageLoader.ts' />
///<reference path='util/UriHelper.ts' />
///<reference path='util/Animation.ts'/>
///<reference path='util/CookieHelper.ts'/>

///<reference path='model/Model.ts' />
///<reference path='model/SpaceModel.ts' />
///<reference path='model/ContentModel.ts' />
///<reference path='model/SchemaModel.ts' />
///<reference path='model/ContentTypeModel.ts' />

///<reference path='handler/DeleteSpaceParam.ts' />
///<reference path='handler/DeleteSpaceParamFactory.ts' />
///<reference path='handler/DeleteSpacesHandler.ts' />
///<reference path='handler/DeleteContentParam.ts' />
///<reference path='handler/DeleteContentParamFactory.ts' />
///<reference path='handler/DeleteContentHandler.ts' />
///<reference path='handler/DeleteSchemaParam.ts' />
///<reference path='handler/DeleteSchemaParamFactory.ts' />
///<reference path='handler/DeleteSchemaHandler.ts' />

///<reference path='remote/JsonRpcProvider.ts' />
///<reference path='remote/BaseRemoteModel.ts' />
///<reference path='remote/BaseRemoteService.ts' />
///<reference path='remote/content/RemoteContentModel.ts' />
///<reference path='remote/content/RemoteContentService.ts' />
///<reference path='remote/account/RemoteAccountModel.ts' />
///<reference path='remote/account/RemoteAccountService.ts' />
///<reference path='remote/contenttype/RemoteContentTypeModel.ts' />
///<reference path='remote/contenttype/RemoteContentTypeService.ts' />
///<reference path='remote/mixin/RemoteMixinModel.ts' />
///<reference path='remote/mixin/RemoteMixinService.ts' />
///<reference path='remote/relationshiptype/RemoteRelationshipTypeModel.ts' />
///<reference path='remote/relationshiptype/RemoteRelationshipTypeService.ts' />
///<reference path='remote/schema/RemoteSchemaModel.ts' />
///<reference path='remote/schema/RemoteSchemaService.ts' />
///<reference path='remote/space/RemoteSpaceModel.ts' />
///<reference path='remote/space/RemoteSpaceService.ts' />
///<reference path='remote/userstore/RemoteUserStoreModel.ts' />
///<reference path='remote/userstore/RemoteUserStoreService.ts' />
///<reference path='remote/util/RemoteUtilsModel.ts' />
///<reference path='remote/util/RemoteSystemService.ts' />

///<reference path='notify/module.ts' />

///<reference path='event/Listener.ts' />
///<reference path='event/Observable.ts' />
///<reference path='event/Event.ts' />
///<reference path='event/EventBus.ts' />

///<reference path='rest/module.ts' />

///<reference path='dom/TextNodeHelper.ts' />
///<reference path='dom/TextNode.ts' />
///<reference path='dom/ElementHelper.ts' />
///<reference path='dom/ImgElHelper.ts' />
///<reference path='dom/Element.ts' />
///<reference path='dom/AEl.ts' />
///<reference path='dom/Body.ts' />
///<reference path='dom/DivEl.ts' />
///<reference path='dom/H1El.ts' />
///<reference path='dom/H2El.ts' />
///<reference path='dom/H3El.ts' />
///<reference path='dom/H4El.ts' />
///<reference path='dom/H5El.ts' />
///<reference path='dom/H6El.ts' />
///<reference path='dom/UlEl.ts' />
///<reference path='dom/LiEl.ts' />
///<reference path='dom/EmEl.ts' />
///<reference path='dom/ImgEl.ts' />
///<reference path='dom/SpanEl.ts' />
///<reference path='dom/ButtonEl.ts' />
///<reference path='dom/PEl.ts' />
///<reference path='dom/FormInputEl.ts' />
///<reference path='dom/InputEl.ts' />
///<reference path='dom/LabelEl.ts' />
///<reference path='dom/SelectEl.ts' />
///<reference path='dom/OptionEl.ts' />
///<reference path='dom/IFrameEl.ts' />
///<reference path='dom/FieldsetEl.ts' />
///<reference path='dom/LegendEl.ts' />
///<reference path='dom/FormEl.ts' />

///<reference path='ui/KeyBindings.ts'/>
///<reference path='ui/Mnemonic.ts' />
///<reference path='ui/Action.ts' />
///<reference path='ui/ActionContainer.ts' />
///<reference path='ui/Closeable.ts' />
///<reference path='ui/Panel.ts' />
///<reference path='ui/DeckPanelListener.ts' />
///<reference path='ui/DeckPanel.ts' />
///<reference path='ui/SplitPanel.ts' />
///<reference path='ui/BodyMask.ts' />
///<reference path='ui/Tooltip.ts' />
///<reference path='ui/ProgressBar.ts' />
///<reference path='ui/Button.ts' />
///<reference path='ui/ActionButton.ts' />
///<reference path='ui/ToggleSlide.ts' />
///<reference path='ui/toolbar/Toolbar.ts' />
///<reference path='ui/menu/MenuItem.ts' />
///<reference path='ui/menu/ContextMenu.ts' />
///<reference path='ui/menu/ActionMenu.ts' />
///<reference path='ui/PanelNavigationItem.ts' />
///<reference path='ui/DeckPanelNavigator.ts' />
///<reference path='ui/DeckPanelNavigatorListener.ts' />
///<reference path='ui/tab/TabBar.ts' />
///<reference path='ui/tab/TabBarItem.ts' />
///<reference path='ui/tab/TabBarItemListener.ts' />
///<reference path='ui/tab/TabMenu.ts' />
///<reference path='ui/tab/TabMenuButton.ts' />
///<reference path='ui/tab/TabMenuItem.ts' />
///<reference path='ui/tab/TabMenuItemListener.ts' />
///<reference path='ui/NavigatedDeckPanel.ts' />
///<reference path='ui/FloatingWindow.ts' />
///<reference path='ui/NavigableFloatingWindow.ts' />


///<reference path='ui/Dropdown.ts' />
///<reference path='ui/RadioGroup.ts' />
///<reference path='ui/TextInputListener.ts' />
///<reference path='ui/TextInput.ts' />
///<reference path='ui/AutosizeTextInput.ts' />
///<reference path='ui/ComboBox.ts' />
///<reference path='ui/ComboBoxListener.ts' />
///<reference path='ui/CheckboxInputListener.ts' />
///<reference path='ui/CheckboxInput.ts' />
///<reference path='ui/PasswordInput.ts' />
///<reference path='ui/TextArea.ts' />
///<reference path='ui/CodeArea.ts' />
///<reference path='ui/form/Fieldset.ts' />
///<reference path='ui/form/Form.ts' />
///<reference path='ui/form/FormItem.ts' />

///<reference path='grid/Grid.ts' />
///<reference path='grid/DataView.ts' />

///<reference path='ui/dialog/DialogButton.ts' />
///<reference path='ui/dialog/ModalDialog.ts' />


///<reference path='facet/Facet.ts' />
///<reference path='facet/FacetEntry.ts' />
///<reference path='facet/TermsFacetEntry.ts' />
///<reference path='facet/TermsFacet.ts' />
///<reference path='facet/QueryFacet.ts' />
///<reference path='facet/FacetFactory.ts' />
///<reference path='facet/FacetEntryViewSelectionChangedEvent.ts' />
///<reference path='facet/FacetEntryView.ts' />
///<reference path='facet/FacetView.ts' />
///<reference path='facet/TermsFacetEntryView.ts' />
///<reference path='facet/TermsFacetView.ts' />
///<reference path='facet/QueryFacetEntryView.ts' />
///<reference path='facet/QueryFacetView.ts' />
///<reference path='facet/FacetGroupView.ts' />
///<reference path='facet/FacetContainer.ts' />

///<reference path='data/json/_module.ts' />
///<reference path='data/_module.ts' />

///<reference path='item/ItemJson.ts' />

///<reference path='content/json/module.ts' />
///<reference path='content/module.ts' />

///<reference path='schema/content/form/json/module.ts' />
///<reference path='schema/content/form/module.ts' />
///<reference path='schema/content/json/module.ts' />
///<reference path='schema/content/module.ts' />
///<reference path="schema/relationshiptype/json/RelationshipTypeJson.ts" />

///<reference path='page/json/module.ts' />
///<reference path='page/module.ts' />

///<reference path='app/wizard/SaveAction.ts' />
///<reference path='app/wizard/CloseAction.ts' />
///<reference path='app/wizard/FormIcon.ts' />
///<reference path='app/wizard/WizardEvents.ts' />
///<reference path='app/wizard/WizardHeaderListener.ts' />
///<reference path='app/wizard/WizardHeader.ts' />
///<reference path='app/wizard/WizardHeaderWithDisplayNameAndName.ts' />
///<reference path='app/wizard/WizardHeaderWithName.ts' />
///<reference path='app/wizard/WizardStepDeckPanel.ts' />
///<reference path='app/wizard/WizardStepNavigator.ts' />
///<reference path='app/wizard/WizardStepNavigationArrow.ts' />
///<reference path='app/wizard/WizardStep.ts' />
///<reference path='app/wizard/WizardPanelListener.ts' />
///<reference path='app/wizard/WizardPanel.ts' />

///<reference path='app/AppBar.ts' />
///<reference path='app/UserInfoPopup.ts' />
///<reference path='app/AppBarActions.ts' />
///<reference path='app/AppBarEvents.ts' />
///<reference path='app/AppBarTabMenu.ts' />
///<reference path='app/AppBarTabMenuButton.ts' />
///<reference path='app/AppBarTabMenuItem.ts' />
///<reference path='app/AppPanel.ts' />
///<reference path='app/BrowseAndWizardBasedAppPanel.ts' />
///<reference path='app/AppManager.ts'/>
///<reference path='app/AppManagerListener.ts'/>

///<reference path='app/browse/grid/GridPanelListener.ts' />
///<reference path='app/browse/grid/GridPanel.ts' />
///<reference path='app/browse/grid/TreePanelListener.ts' />
///<reference path='app/browse/grid/TreePanel.ts' />
///<reference path='app/browse/grid/TreeGridPanelListener.ts' />
///<reference path='app/browse/grid/TreeGridPanel.ts' />

///<reference path='app/browse/BrowsePanel.ts' />
///<reference path='app/browse/BrowseItem.ts' />
///<reference path='app/browse/ItemsSelectionPanelListener.ts' />
///<reference path='app/browse/ItemsSelectionPanel.ts' />
///<reference path='app/browse/BrowseItemPanelListener.ts' />
///<reference path='app/browse/BrowseItemPanel.ts' />
///<reference path='app/browse/GridContainer.ts'/>

///<reference path='app/browse/filter/BrowseFilterPanelListener.ts' />
///<reference path='app/browse/filter/BrowseFilterPanel.ts' />
///<reference path='app/browse/filter/TextSearchField.ts'/>
///<reference path='app/browse/filter/ClearFilterButton.ts'/>


///<reference path='app/view/ViewItem.ts' />
///<reference path='app/view/ItemStatisticsPanel.ts' />
///<reference path='app/view/ItemViewPanel.ts' />
///<reference path='app/view/ItemViewPanelListener.ts' />

///<reference path='app/delete/DeleteItem.ts' />
///<reference path='app/delete/DeleteDialog.ts' />
///<reference path='app/wizard/SaveBeforeCloseDialog.ts' />


declare var Mousetrap;
declare var Ext;
declare var Admin;

Ext.Loader.setConfig({
    enabled: false,
    disableCaching: false
});

Ext.override(Ext.LoadMask, {
    floating: {
        shadow: false
    },
    msg: undefined,
    cls: 'admin-load-mask',
    msgCls: 'admin-load-text',
    maskCls: 'admin-mask-white'
});

