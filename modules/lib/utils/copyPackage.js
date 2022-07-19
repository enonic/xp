const path = require('path');
const fs = require('fs');
const glob = require('glob');

// TODO: Replace with `xpVersion()` before the first actual release
const XP_VERSION = '0.1.0';

const PACKAGE_TEMPLATE = fs.readFileSync(path.resolve(__dirname, 'templates/package.template.json')).toString();

// eslint-disable-next-line no-unused-vars
function xpVersion() {
    const properties = fs.readFileSync(path.resolve('../../gradle.properties')).toString();
    const [, version] = /^version\s*=\s*(.+)$/m.exec(properties) ?? [];
    return version;
}

void function copyPackages() {
    glob.sync('lib-*').forEach(async (libPath) => {
        const libName = libPath.substr(4);

        const hasTs = fs.existsSync(path.join(libPath, `src/main/resources/lib/xp/${libName}.ts`));
        if (!hasTs) {
            return;
        }

        const packageData = PACKAGE_TEMPLATE
            .replaceAll(/%VERSION%/g, XP_VERSION)
            .replaceAll(/%NAME%/g, libName);

        fs.writeFileSync(path.join(libPath, 'package.json'), packageData);
    });
}();
