// This file was auto-generated on 2012-04-17 14:38:20 CEST

if ( !Templates ) {
    var Templates = {};
}

Templates.main = {

    loggedInUserButtonPopup:
		'<div class="admin-logged-in-user-popup-left">' +
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

    activityStream:
		'<tpl for=".">' + 
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

    speakOutPanel:
		'<div>' + 
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
		'</div>',

    notificationWindow:
		'<div class="admin-notification-window clearfix">' +
		    '<table border="0">' + 
		        '<tr>' + 
		            '<td style="width: 42px" valign="top">' + 
		                '<img src="_app/main/images/feedback-ok.png" style="width:32px; height:32px"/>' +
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
		'</div>'

};