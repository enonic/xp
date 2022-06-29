const path = require('path');
const fs = require('fs');
const glob = require('glob');

const PACKAGE_TEMPLATE = fs.readFileSync(path.resolve(__dirname, 'templates/package.template.json')).toString();

glob.sync('lib-*').forEach(async (libPath) => {
    const libName = libPath.substr(4);

    const hasTs = fs.existsSync(path.join(libPath, `src/main/resources/lib/xp/${libName}.ts`));
    if (!hasTs) {
        return;
    }

    const packageData = PACKAGE_TEMPLATE.replaceAll(/%NAME%/g, libName);

    fs.writeFileSync(path.join(libPath, 'package.json'), packageData);
});