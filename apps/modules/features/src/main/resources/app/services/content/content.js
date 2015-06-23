var portal = require('/lib/xp/portal');
var stk = require('stk/stk');

exports.post = handlePost;

function handlePost(req) {
    var contentData = req.formParams;
    var contentCreated = null;
    var contentItem = stk.content.get(contentData.content_ID);
    var contentFolder;
    var saveLocation;

    if(stk.content.exists(contentItem._path + '/content')) {
        saveLocation = contentItem._path + '/content';
    } else {
        contentFolder = execute('content.create', {
            name: 'content',
            parentPath: contentItem._path,
            displayName: 'Content',
            draft: true,
            requireValid: true,
            contentType: 'base:folder',
            data: {}
        });

        saveLocation = contentFolder._path;
    }

    var contentName = 'Content-' + Math.floor((Math.random() * 1000000000) + 1);

    var newContent = execute('content.create', {
        name: contentName,
        parentPath: saveLocation,
        displayName: contentName,
        draft: true,
        requireValid: true,
        contentType: module.name + ':all-input-types',
        data: {
            myDateTime: contentData.datetime,
            myCheckbox: contentData.checkbox
        }
    });

    if (newContent._id) {
        contentCreated = true;
        stk.log('New content created with id ' + newContent._id);
    } else {
        stk.log('Something went wrong creating content for ' + contentItem.displayName);
    }

    return {

        redirect: portal.pageUrl({
            path: contentItem._path,
            params: {
                submitted: contentCreated ? 'ok' : null,
                contentId: contentCreated ? newContent._id : null
            }
        })
    }
}
