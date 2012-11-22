// This file was auto-generated on 2012-11-21 08:57:04 VET

if (!Templates) {
    var Templates = {};
}

Templates.account = {

    deleteManyUsers: '<div class="admin-delete-user-confirmation-message">' +
                     '<div class="icon-question-mark-32 admin-left" style="width:32px; height:32px; margin-right: 10px"><!-- --></div>' +
                     '<div class="admin-left" style="margin-top:5px">Are you sure you want to delete the selected {selectionLength} items?</div>' +
                     '</div>',

    gridPanelNameRenderer: '<div style="float:left;padding-top: 3px"><img src="{0}" alt="" class="admin-grid-thumbnail"></div>' +
                           '<div style="float:left; padding: 3px 0 0 5px">' +
                           '<div class="admin-grid-title">{1}</div>' +
                           '<div class="admin-grid-description">{3}\\{2}</div>' +
                           '</div>',

    groupPreviewCommonInfo: '<tpl if="type===\'role\'">' +
                            '<div class="container">' +
                            '<table>' +
                            '<thead>' +
                            '<tr>' +
                            '<th>Description</th>' +
                            '</tr>' +
                            '</thead>' +
                            '<tbody>' +
                            '<tr>' +
                            '<td>{staticDesc}</td>' +
                            '</tr>' +
                            '</tbody>' +
                            '</table>' +
                            '</div>' +
                            '</tpl>' +
                            '<tpl if="type===\'group\'">' +
                            '<div class="container">' +
                            '<table>' +
                            '<thead>' +
                            '<tr>' +
                            '<th colspan="2">Properties</th>' +
                            '</tr>' +
                            '</thead>' +
                            '<tbody>' +
                            '<tr>' +
                            '<td class="label">Public:</td>' +
                            '<td>{[values.public ? "yes" : "no"]}</td>' +
                            '</tr>' +
                            '<tr>' +
                            '<td class="label">Description:</td>' +
                            '<td>{description}</td>' +
                            '</tr>' +
                            '</tbody>' +
                            '</table>' +
                            '</div>' +
                            '</tpl>' +
                            '<div class="container">' +
                            '<table>' +
                            '<thead>' +
                            '<tr>' +
                            '<th colspan="2">Statistics</th>' +
                            '</tr>' +
                            '</thead>' +
                            '<tbody>' +
                            '<tr>' +
                            '<td class="label">Member count:</td>' +
                            '<td>{membersCount}</td>' +
                            '</tr>' +
                            '<tr>' +
                            '<td class="label">Last updated:</td>' +
                            '<td>{lastModified}</td>' +
                            '</tr>' +
                            '</tbody>' +
                            '</table>' +
                            '</div>',

    groupPreviewMemberships: '<tpl for="members">' +
                             '<div class="clearfix admin-member-preview-el x-boxselect-item">' +
                             '<div class="admin-left">' +
                             '<span class="{[values.type==="user" && !values.builtIn ? "icon-user" : values.type==="role" || values.builtIn ? "icon-role" : "icon-group"]} admin-list-item"></span>' +
                             '</div>' +
                             '<div class="admin-left">' +
                             '<span>' +
                             '<tpl if="type==\'user\'"> {displayName} ({qualifiedName})</tpl>' +
                             '<tpl if="type!=\'user\'">{name} ({userStore})</tpl>' +
                             '</span>' +
                             '</div>' +
                             '</div>' +
                             '<br>' +
                             '</tpl>',

    groupWizardHeader: '<div class="admin-wizard-header">' +
                       '<h1 class="admin-display-name admin-edited-field">{displayName}</h1>' +
                       '<span>{qualifiedName}</span>' +
                       '</div>',

    noUserSelected: '<div>No account selected</div>',

    selectedUserLarge: '<tpl for="users">' +
                       '<div id="selected-item-box-{key}" class="admin-selected-item-box large x-btn-default-large clearfix">' +
                       '<div class="left">' +
                       '<img src="{image_url}" alt="{displayName}"/>' +
                       '</div>' +
                       '<div class="center"><h2>{displayName}</h2>' +
                       '<p>{userStore}\\\\{name}</p></div>' +
                       '<div class="right">' +
                       '<a id="remove-from-selection-button-{key}" class="remove-selection" href="javascript:;"></a>' +
                       '</div>' +
                       '</div>' +
                       '</tpl>',

    selectedUserSmall: '<tpl for="users">' +
                       '<div id="selected-item-box-{key}" class="admin-selected-item-box small x-btn-default-small clearfix">' +
                       '<div class="admin-selected-item-box left">' +
                       '<img src="{image_url}" alt="{displayName}"/>' +
                       '</div>' +
                       '<div class="admin-selected-item-box center"><h2>{displayName}</h2></div>' +
                       '<div class="admin-selected-item-box right">' +
                       '<a id="remove-from-selection-button-{key}" class="remove-selection" href="javascript:;"></a>' +
                       '</div>' +
                       '</div>' +
                       '</tpl>',

    shortValidationResult: '<tpl if="(valid)"><img src="resources/images/icons/16x16/check.png"/></tpl>',

    userPreviewCommonInfo: '<div class="container">' +
                           '<table>' +
                           '<thead>' +
                           '<tr>' +
                           '<th>Login Info</th>' +
                           '</tr>' +
                           '</thead>' +
                           '<tbody>' +
                           '<tr>' +
                           '<td class="label">User Name:</td>' +
                           '<td>{name}</td>' +
                           '</tr>' +
                           '<tr>' +
                           '<td class="label">E-mail:</td>' +
                           '<td>{email}</td>' +
                           '</tr>' +
                           '</tbody>' +
                           '</table>' +
                           '</div>' +
                           '<div class="container admin-groups-boxselect">' +
                           '<table>' +
                           '<thead>' +
                           '<tr>' +
                           '<th>Roles</th>' +
                           '</tr>' +
                           '</thead>' +
                           '<tbody>' +
                           '<tpl for="groups">' +
                           '<tpl if="type == \'role\'">' +
                           '<tr>' +
                           '<td>' +
                           '<li class="x-boxselect-item admin-{type}-item">' +
                           '<div class="x-boxselect-item-text">{qualifiedName}</div>' +
                           '</li>' +
                           '</td>' +
                           '</tr>' +
                           '</tpl>' +
                           '</tpl>' +
                           '</tbody>' +
                           '</table>' +
                           '</div>' +
                           '<div class="container">' +
                           '<table>' +
                           '<thead>' +
                           '<tr>' +
                           '<th colspan="2">Settings</th>' +
                           '</tr>' +
                           '</thead>' +
                           '<tbody>' +
                           '<tr>' +
                           '<td class="label">Locale:</td>' +
                           '<td>{locale}</td>' +
                           '</tr>' +
                           '<tr>' +
                           '<td class="label">Country:</td>' +
                           '<td>{country}</td>' +
                           '</tr>' +
                           '<tr>' +
                           '<td class="label">TimeZone:</td>' +
                           '<td>{timezone}</td>' +
                           '</tr>' +
                           '</tbody>' +
                           '</table>' +
                           '</div>' +
                           '<div class="container">' +
                           '<table>' +
                           '<thead>' +
                           '<tr>' +
                           '<th colspan="2">Statistics</th>' +
                           '</tr>' +
                           '</thead>' +
                           '<tbody>' +
                           '<tr>' +
                           '<td class="label">Last login:</td>' +
                           '<td>{lastLogged}</td>' +
                           '</tr>' +
                           '<tr>' +
                           '<td class="label">Created:</td>' +
                           '<td>{created}</td>' +
                           '</tr>' +
                           '<tr>' +
                           '<td class="label">Owner of:</td>' +
                           '<td>394</td>' +
                           '</tr>' +
                           '</tbody>' +
                           '</table>' +
                           '</div>',

    userPreviewHeader: '<div class="container">' +
                       '<h1>{displayName}</h1>' +
                       '<div>' +
                       '<span>{userStore}\\\\{name}</span><!--<span class="email">&nbsp;{email}</span>-->' +
                       '</div>' +
                       '</div>',

    userPreviewMemberships: '<fieldset class="x-fieldset x-fieldset-default admin-memberships-container">' +
                            '<legend class="x-fieldset-header x-fieldset-header-default">' +
                            '<div class="x-component x-fieldset-header-text x-component-default">Graph</div>' +
                            '</legend>' +
                            '</fieldset>',

    userPreviewPhoto: '<div class="admin-user-photo west admin-left">' +
                      '<div class="photo-placeholder">' +
                      '<img src="{[values.image_url]}" alt="{name}"/>' +
                      '</div>' +
                      '</div>',

    userPreviewPlaces: '<tpl if="userInfo == null || userInfo.addresses == null || userInfo.addresses.length == 0">' +
                       '<h2 class="message">No data</h2>' +
                       '</tpl>' +
                       '<tpl if="profile != null && profile.addresses != null && profile.addresses.length &gt; 0">' +
                       '<fieldset class="x-fieldset x-fieldset-default admin-addresses-container">' +
                       '<legend class="x-fieldset-header x-fieldset-header-default">' +
                       '<div class="x-component x-fieldset-header-text x-component-default">Addresses</div>' +
                       '</legend>' +
                       '<tpl for="profile.addresses">' +
                       '<div class="address">' +
                       '<tpl if="label != null">' +
                       '<h3 class="x-fieldset-header-text">{label}</h3>' +
                       '</tpl>' +
                       '<div class="body">' +
                       '<table>' +
                       '<tbody>' +
                       '<tr>' +
                       '<td class="label">Street:</td>' +
                       '<td>{street}</td>' +
                       '</tr>' +
                       '<tr>' +
                       '<td class="label">Postal Code:</td>' +
                       '<td>{postalCode}</td>' +
                       '</tr>' +
                       '<tr>' +
                       '<td class="label">Postal Address:</td>' +
                       '<td>{postalAddress}</td>' +
                       '</tr>' +
                       '<tr>' +
                       '<td class="label">Country:</td>' +
                       '<td>{country}</td>' +
                       '</tr>' +
                       '<tr>' +
                       '<td class="label">Region:</td>' +
                       '<td>{region}</td>' +
                       '</tr>' +
                       '</tbody>' +
                       '</table>' +
                       '</div>' +
                       '</div>' +
                       '</tpl>' +
                       '</fieldset>' +
                       '</tpl>',

    userPreviewProfile: '<div>' +
                        '<tpl for=".">' +
                        '<fieldset class="x-fieldset x-fieldset-default">' +
                        '<legend class="x-fieldset-header x-fieldset-header-default">' +
                        '<div class="x-component x-fieldset-header-text x-component-default">{title}</div>' +
                        '</legend>' +
                        '<table>' +
                        '<tbody>' +
                        '<tpl for="fields">' +
                        '<tr>' +
                        '<td class="label">{title}</td>' +
                        '<td>{value}</td>' +
                        '</tr>' +
                        '</tpl>' +
                        '</tbody>' +
                        '</table>' +
                        '</fieldset>' +
                        '</tpl>' +
                        '</div>',

    userstoreRadioButton: '<tpl for=".">' +
                          '<div class="admin-userstore clearfix">' +
                          '<div class="admin-left" style="margin: 24px 5px 0 0">' +
                          '<input type="radio" name="userstore" value="{key}">' +
                          '</div>' +
                          '<div class="admin-userstore-block admin-left">' +
                          '<div class="admin-left" style="padding-right: 15px;">' +
                          '<img width="48" height="48" src="resources/images/icons/48x48/userstore.png"/>' +
                          '</div>' +
                          '<div class="admin-left">' +
                          '<h2>{name}</h2>' +
                          '<p>(usersstores\\\\{name})</p>' +
                          '</div>' +
                          '</div>' +
                          '</div>' +
                          '</tpl>',

    userWizardHeader: '<div class="admin-wizard-userstore">' +
                      '<label>{[ values.isNewUser ? "New User" : "User" ]}: </label>' +
                      '<span>{[ values.userstoreName ? (values.userstoreName + "&#92;") : "" ]}</span>' +
                      '<span>{qUserName}</span>' +
                      '</div>'

};

Templates.cache = {

    detailPanelHeader: '<div class="admin-cache-info">' +
                       '<h1>{name} <span><tpl if="implementationName != null">({implementationName})</tpl></span></h1>' +
                       '</div>'

};

Templates.common = {

    groupList: '<div class="clearfix">' +
               '<div class="admin-left">' +
               '<span class="{[values.type==="user" && !values.builtIn ? "icon-user" : values.type==="role" || values.builtIn ? "icon-role" : "icon-group"]} admin-list-item"></span>' +
               '</div>' +
               '<div class="admin-left">' +
               '<span>' +
               '<tpl if="type==\'user\'"> {name} ({qualifiedName})</tpl>' +
               '<tpl if="type!=\'user\'">{name} ({userStore})</tpl>' +
               '</span>' +
               '</div>' +
               '</div>',

    notifyUserMessage: 'Hi {0}! Your username is {1}.\n If required, please choose userstore: {2} when logging in. \nRegards, {3}.',

    photoUploadButton: '<div id="{id}" title="{title}" class="admin-image-upload-button-container" style="width:{width - 9}px;height:{height - 9}px; margin: 0">' +
                       '<img src="{photoUrl}" class="admin-image-upload-button-image" style="width:{width - 11}px;height:{height - 11}px"/>' +
                       '<div class="admin-image-upload-button-progress-bar-container" style="width:{width - 12}px">' +
                       '<div class="admin-image-upload-button-progress-bar" style="height:{progressBarHeight}px"><!-- --></div>' +
                       '</div>' +
                       '</div>',

    summaryToolbar: '<div class="admin-left">' +
                    '<span>Modified fields are displayed below</span>' +
                    '- <a href="javascript:;" class="admin-summary-show-all-fields-button">Show all fields</a>' +
                    '- <a href="javascript:;" class="admin-summary-show-comparison-button">Show comparison</a>' +
                    '</div>' +
                    '<div class="admin-right">' +
                    '<div class="key added">Added</div>' +
                    '<div class="key modified">Modified</div>' +
                    '<div class="key removed">Removed</div>' +
                    '<br/>' +
                    '</div>',

    userInfo: '<div>' +
              '<div class="admin-user-info clearfix">' +
              '<div class="admin-user-photo west admin-left">' +
              '<div class="photo-placeholder">' +
              '<img src="{[values.image_url]}" alt="{name}"/>' +
              '</div>' +
              '</div>' +
              '<div class="admin-left">' +
              '<h2>{displayName}</h2>({qualifiedName})<br/>' +
              '<a href="mailto:{email}:">{email}</a>' +
              '</div>' +
              '</div>' +
              '</div>',

    wizardPanelSteps: '<div class="admin-wizard-navigation-container">' +
                      '<ul class="admin-wizard-navigation clearfix">' +
                      '<tpl for=".">' +
                      '<li class="{[ this.resolveClsName( xindex, xcount ) ]}" wizardStep="{[xindex]}">' +
                      '<a href="javascript:;" class="step {[ this.resolveClsName( xindex, xcount ) ]}">{[xindex]}. {[' +
                      '(values.stepTitle || values.title) ]}</a></li>' +
                      '</tpl>' +
                      '</ul>' +
                      '</div>'

};

Templates.contentManager = {

    contentWizardHeader: '<div class="admin-wizard-header">' +
                         '<input type="text" value="{displayName}" readonly="true" class="admin-display-name"/>' +
                         '</div>' +
                         '<div class="admin-wizard-userstore">' +
                         '<label>{[ values.isNewContent ? "New Content" : "Content" ]}: </label>' +
                         '<span>/some/content/path</span>' +
                         '</div>',

    deleteMultiple: '<div class="admin-delete-user-confirmation-message">' +
                    '<div class="icon-question-mark-32 admin-left" style="width:32px; height:32px; margin-right: 10px"><!-- --></div>' +
                    '<div class="admin-left" style="margin-top:5px">Are you sure you want to delete {selectionLength} item(s)?</div>' +
                    '</div>',

    deleteSingle: '<div>' +
                  '<div class="admin-content-info clearfix">' +
                  '<div class="admin-content-photo west admin-left">' +
                  '<div class="photo-placeholder">' +
                  '<tpl if="type == \'site\'">' +
                  '<img src="resources/images/icons/128x128/earth2.png" alt="{name}"/>' +
                  '</tpl>' +
                  '<tpl if="type == \'contentType\'">' +
                  '<img src="resources/images/icons/128x128/cubes_blue.png" alt="{name}"/>' +
                  '</tpl>' +
                  '</div>' +
                  '</div>' +
                  '<div class="admin-left">' +
                  '<h2>{name}</h2>' +
                  '<p>{type}</p>' +
                  '</div>' +
                  '</div>' +
                  '</div>',

    gridPanelNameRenderer: '<div style="float:left;">' +
                           '<img src="data:image/gif;base64,R0lGODlhAQABAID/AMDAwAAAACH5BAEAAAAALAAAAAABAAEAAAICRAEAOw==" class="admin-grid-thumbnail {0}">' +
                           '</div>' +
                           '<div style="float:left; padding: 0 0 0 5px;">' +
                           '<div class="admin-grid-title" style="line-height: 32px">{1}</div>' +
                           '</div>',

    previewCommonInfo: '<div class="container">' +
                       '<table>' +
                       '<thead>' +
                       '<tr>' +
                       '<th colspan="2">General</th>' +
                       '</tr>' +
                       '</thead>' +
                       '<tbody>' +
                       '<tr>' +
                       '<td class="label">Owner:</td>' +
                       '<td>{owner}</td>' +
                       '</tr>' +
                       '<tr>' +
                       '<td class="label">Modified:</td>' +
                       '<td>{modifiedTime}</td>' +
                       '</tr>' +
                       '</tbody>' +
                       '</table>' +
                       '</div>',

    previewHeader: '<h1>{name}</h1><span>{type}</span>',

    previewPhoto: '<img src="data:image/gif;base64,R0lGODlhAQABAID/AMDAwAAAACH5BAEAAAAALAAAAAABAAEAAAICRAEAOw==" alt="{name}" class="{iconCls}"/>',

    previewSelectionLarge: '<tpl for=".">' +
                           '<div id="selected-item-box-{data.key}" class="admin-selected-item-box large x-btn-default-large clearfix">' +
                           '<div class="left">' +
                           '<img src="data:image/gif;base64,R0lGODlhAQABAID/AMDAwAAAACH5BAEAAAAALAAAAAABAAEAAAICRAEAOw==" class="{data.iconCls}"' +
                           'alt="{data.name}"/>' +
                           '</div>' +
                           '<div class="center">' +
                           '<h2>{data.name}</h2>' +
                           '<p>{data.type}</p>' +
                           '</div>' +
                           '<div class="right">' +
                           '<a id="remove-from-selection-button-{data.key}" class="remove-selection" href="javascript:;"></a>' +
                           '</div>' +
                           '</div>' +
                           '</tpl>',

    previewSelectionSmall: '<tpl for=".">' +
                           '<div id="selected-item-box-{data.key}" class="admin-selected-item-box small x-btn-default-small clearfix">' +
                           '<div class="admin-selected-item-box left">' +
                           '<img src="data:image/gif;base64,R0lGODlhAQABAID/AMDAwAAAACH5BAEAAAAALAAAAAABAAEAAAICRAEAOw==" alt="{data.name}"' +
                           'class="{data.iconCls}"/>' +
                           '</div>' +
                           '<div class="admin-selected-item-box center">' +
                           '<h2>{data.name}</h2>' +
                           '</div>' +
                           '<div class="admin-selected-item-box right">' +
                           '<a id="remove-from-selection-button-{data.key}" class="remove-selection" href="javascript:;"></a>' +
                           '</div>' +
                           '</div>' +
                           '</tpl>'

};

Templates.contenttype = {

    detailPanelInfo: '<div class="detail-info">' +
                     '<h3>{name}</h3>' +
                     '<dl>' +
                     '<dt>Key</dt>' +
                     '<dd>{key}</dd>' +
                     '<dt>Last Modified</dt>' +
                     '<dd>{timestamp:this.formatDate}</dd>' +
                     '</dl>' +
                     '</div>'

};

Templates.datadesigner = {

    gridPanelRenderer: '<div style="float:left;padding-top: 3px"><img src="{0}" alt="" class="admin-grid-thumbnail"/></div>' +
                       '<div style="float:left; padding: 5px 0 0 5px">' +
                       '<div class="admin-grid-title">{1}</div>' +
                       '<div class="admin-grid-description">{2}</div>' +
                       '</div>',

    noContentTypeSelected: '<div>No content type selected</div>',

    previewCommonInfo: '<div class="container">' +
                       '<table>' +
                       '<thead>' +
                       '<tr>' +
                       '<th colspan="2">General</th>' +
                       '</tr>' +
                       '</thead>' +
                       '<tbody>' +
                       '<tr>' +
                       '<td class="label">Created:</td>' +
                       '<td>{createdTime}</td>' +
                       '</tr>' +
                       '<tr>' +
                       '<td class="label">Modified:</td>' +
                       '<td>{modifiedTime}</td>' +
                       '</tr>' +
                       '</tbody>' +
                       '</table>' +
                       '</div>' +
                       '<div class="container">' +
                       '<table>' +
                       '<thead>' +
                       '<tr>' +
                       '<th colspan="2">Statistics</th>' +
                       '</tr>' +
                       '</thead>' +
                       '<tbody>' +
                       '<tr>' +
                       '<td class="label">Usage Count:</td>' +
                       '<td>{usageCount}</td>' +
                       '</tr>' +
                       '</tbody>' +
                       '</table>' +
                       '</div>',

    previewHeader: '<h1>{name}</h1><div>{qualifiedName}</div>',

    previewIcon: '<img src="{[values.icon !=="" ? values.icon : "resources/images/icons/128x128/cubes.png"]}" alt="{name}" title="{name}"/>',

    wizardHeader: '<div class="admin-wizard-header">' +
                  '<input type="text" value="{displayName}" readonly="true" class="admin-display-name"/>' +
                  '</div>'

};

Templates.main = {

    activityStream: '<tpl for=".">' +
                    '<div class="admin-activity-stream-message">' +
                    '<table border="0" cellspacing="0" cellpadding="0">' +
                    '<tr>' +
                    '<td valign="top" class="photo-container">' +
                    '<img class="photo" src="{photo}"/>' +
                    '</td>' +
                    '<td valign="top">' +
                    '<div class="display-name-location"><a href="javascript:;">' +
                    '<tpl if="birthday"><img src="_app/main/images/activity-stream/cake.png" style="width:11px; height:8px" title="{displayName} has Birthday today"/></tpl>' +
                    '{displayName}</a> via {location}</div>' +
                    '<div>{action}:' +
                    '<tpl if="action == \'Said\'">{description}</tpl>' +
                    '<tpl if="action != \'Said\'"><a href="javascript:;">{description}</a></tpl>' +
                    '</div>' +
                    '</td>' +
                    '</tr>' +
                    '</table>' +
                    '<div class="actions clearfix" style="clear:both">' +
                    '<table border="0" cellspacing="0" cellpadding="0">' +
                    '<tr>' +
                    '<td>' +
                    '<span class="pretty-date">{prettyDate}</span>' +
                    '</td>' +
                    '<td>' +
                    '<span class="link favorite" style="visibility:hidden">Favorite</span>' +
                    '</td>' +
                    '<td>' +
                    '<span class="link comment" style="visibility:hidden">Comment</span>' +
                    '</td>' +
                    '<td style="text-align:right">' +
                    '<span class="link more" style="visibility:hidden"><!-- --></span>' +
                    '</td>' +
                    '</tr>' +
                    '</table>' +
                    '</div>' +
                    '</div>' +
                    '</tpl>',

    loggedInUserButtonPopup: '<div class="admin-logged-in-user-popup-left">' +
                             '<img src="resources/images/x-user.png"/>' +
                             '</div>' +
                             '<div class="admin-logged-in-user-popup-right">' +
                             '<h1>{displayName}</h1>' +
                             '<p>{qualifiedName}</p>' +
                             '<p>{email}</p>' +
                             '<p>&nbsp;</p>' +
                             '<p><form action="main.html"> Change user: <input id="main-change-user-input" name="qname" type="text"/><input type="submit" style="display:none" /></form></p>' +
                             '<p>&nbsp;</p>' +
                             '<p><span class="link">Edit Account</span></p>' +
                             '<p><span class="link">Change Password</span></p>' +
                             '<p class="admin-logged-in-user-popup-log-out" style="float:right"><a href="index.html">Log Out</a></p>' +
                             '</div>',

    notificationWindow: '<div class="admin-notification-window clearfix">' +
                        '<table border="0">' +
                        '<tr>' +
                        '<td style="width: 42px" valign="top">' +
                        '<img src="app/main/images/feedback-ok.png" style="width:32px; height:32px"/>' +
                        '</td>' +
                        '<td valign="top">' +
                        '<h1>{messageTitle}</h1>' +
                        '<div class="message-text">{messageText}</div>' +
                        '</td>' +
                        '</tr>' +
                        '<tpl if="notifyUser">' +
                        '<tr>' +
                        '<td colspan="2" style="text-align: right">' +
                        '<p><span class="link notify-user" href="javascript:;">Notify User</span></p>' +
                        '</td>' +
                        '</tr>' +
                        '</tpl>' +
                        '</table>' +
                        '</div>',

    speakOutPanel: '<div>' +
                   '<h1>What\'s happening?</h1>' +
                   '<div id="activity-stream-speak-out-text-input"><!-- --></div>' +
                   '<div class="clearfix">' +
                   '<div class="clearfix">' +
                   '<div class="admin-left">' +
                   '<div id="activity-stream-speak-out-url-shortener-button-container"><!-- --></div>' +
                   '</div>' +
                   '<div class="admin-right">' +
                   '<div id="activity-stream-speak-out-letters-left-container" class="admin-left">140</div>' +
                   '<div id="activity-stream-speak-out-send-button-container" class="admin-left"><!-- --></div>' +
                   '</div>' +
                   '</div>' +
                   '</div>' +
                   '</div>'

};

Templates.userstore = {

    editFormHeader: '<div class="admin-userstore-info">' +
                    '<h1>{name}</h1>' +
                    '<em>{connectorName}</em>' +
                    '</div>',

    gridPanelNameRenderer: '<div style="float:left"><img src="{0}" class="admin-grid-thumbnail"></div>' +
                           '<div style="float:left; padding: 0 0 0 5px">' +
                           '<div class="admin-grid-title">{1}</div>' +
                           '<div class="admin-grid-description">{2}</div>' +
                           '</div>',

    noUserstoreSelected: '<div>No userstore selected</div>',

    previewCommonInfo: '<div class="container">' +
                       '<table>' +
                       '<thead>' +
                       '<tr>' +
                       '<th colspan="2">General</th>' +
                       '</tr>' +
                       '</thead>' +
                       '<tbody>' +
                       '<tr>' +
                       '<td class="label">Created:</td>' +
                       '<td>{created}</td>' +
                       '</tr>' +
                       '<tr>' +
                       '<td class="label">Modified:</td>' +
                       '<td>{lastModified}</td>' +
                       '</tr>' +
                       '</tbody>' +
                       '</table>' +
                       '</div>' +
                       '<div class="container">' +
                       '<table>' +
                       '<thead>' +
                       '<tr>' +
                       '<th colspan="2">Statistics</th>' +
                       '</tr>' +
                       '</thead>' +
                       '<tbody>' +
                       '<tr>' +
                       '<td class="label">User count:</td>' +
                       '<td>{userCount}</td>' +
                       '</tr>' +
                       '<tr>' +
                       '<td class="label">Group count:</td>' +
                       '<td>{groupCount}</td>' +
                       '</tr>' +
                       '</tbody>' +
                       '</table>' +
                       '</div>' +
                       '<div class="container">' +
                       '<table>' +
                       '<thead>' +
                       '<tr>' +
                       '<th colspan="2">Connector</th>' +
                       '</tr>' +
                       '</thead>' +
                       '<tbody>' +
                       '<tr>' +
                       '<td class="label">Name:</td>' +
                       '<td>{connectorName}<tpl if="connectorName == null">Local</tpl></td>' +
                       '</tr>' +
                       '<tr>' +
                       '<td class="label">Plugin:</td>' +
                       '<td>{plugin}</td>' +
                       '</tr>' +
                       '<tr>' +
                       '<td class="label">User Policy:</td>' +
                       '<td>{userPolicy}</td>' +
                       '</tr>' +
                       '<tr>' +
                       '<td class="label">Group Policy:</td>' +
                       '<td>{groupPolicy}</td>' +
                       '</tr>' +
                       '</tbody>' +
                       '</table>' +
                       '</div>',

    previewHeader: '<h1>{name}' +
                   '<tpl if="defaultStore">(default)</tpl>' +
                   '</h1><span>{[values.connectorName==null ? "Local" : values.connectorName ]}</span>',

    previewPhoto: '<img src="resources/images/icons/128x128/userstore.png" alt="{name}"/>',

    userstoreInfo: '<div>' +
                   '<div class="admin-user-info clearfix">' +
                   '<div class="admin-user-photo west admin-left">' +
                   '<div class="photo-placeholder">' +
                   '<img src="resources/images/icons/128x128/userstore.png" alt="{name}"/>' +
                   '</div>' +
                   '</div>' +
                   '<div class="admin-left" style="line-height: 32px">' +
                   '<h2>{name}</h2>' +
                   '</div>' +
                   '</div>' +
                   '</div>'

};

