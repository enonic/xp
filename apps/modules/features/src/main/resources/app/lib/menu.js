function isInteger(x) {
    return Math.round(x) === x;
}

function isMenuItem(content) {
    var extraData = content.x;
    if (!extraData) {
        return false;
    }
    var extraDataModule = extraData['com-enonic-xp-modules-features'];
    if (!extraDataModule || !extraDataModule['menu-item']) {
        return false;
    }
    var menuItemMetadata = extraDataModule['menu-item'] || {};
    var menuItemValue = menuItemMetadata.menuItem;
    return menuItemValue;
}

function getChildMenuItems(parentContent, levels) {
    var childrenResult = execute('content.getChildren', {
        key: parentContent._id,
        count: 100
    });

    levels--;

    var childMenuItems = [];
    
    childrenResult.contents.forEach(function (child) {
        if (isMenuItem(child)) {
            childMenuItems.push(menuItemToJson(child, levels));
        }
    });

    log.info('getChildMenuItems: \r\n %s', JSON.stringify(childMenuItems, null, 4));
    
    return childMenuItems;
}

function menuItemToJson(content, levels) {
    var subMenus = [];
    if (levels > 0) {
        subMenus = getChildMenuItems(content, levels);
    }
    
    return {
        displayName: content.displayName,
        menuName: content.x['com-enonic-xp-modules-features']['menu-item'].menuName,
        path: content._path,
        name: content._name,
        id: content._id,
        hasChildren: subMenus.length > 0,
        children: subMenus
    };
}

exports.getSiteMenu = function (levels) {
    levels = (isInteger(levels) ? levels : 1);
    var site = execute('portal.getSite');
    if (!site) {
        return [];
    }
    var menuItems = getChildMenuItems(site, levels);

    log.info('Site Menu: \r\n %s', JSON.stringify(menuItems, null, 4));

    return menuItems;
};
