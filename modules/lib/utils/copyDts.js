const glob = require('glob');
const cpFile = require('cp-file');
const path = require('path');

const SRC_RESOURCES = 'src/main/resources';

glob.sync(`lib-*/${SRC_RESOURCES}/lib/xp/*.d.ts`).forEach(async (srcResourcePath) => {
  const [libPath] = srcResourcePath.split('/');

  const resourceName = path.basename(srcResourcePath);
  const indexPath = path.join(libPath, '', resourceName).replaceAll(path.sep, '/');

  await cpFile(srcResourcePath, indexPath);
});
