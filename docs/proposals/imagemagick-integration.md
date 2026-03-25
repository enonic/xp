# Replace Java-based Image Service with ImageMagick Native Libraries

## Summary

Replace the current pure-Java image processing pipeline (`javax.imageio` + JHLabs filters) in `core-image` with ImageMagick native libraries to improve performance and add WebP/AVIF output format support. The existing Image Service API (`ImageService.readImage(ReadImageParams)`) must remain fully backward-compatible.

## Motivation

The current image service has two key limitations:

1. **Performance**: Java's `javax.imageio` with `BufferedImage` is slow for large images. All pixel data is loaded into JVM heap memory, scaled via `AffineTransformOp`/`Graphics2D`, and filtered through JHLabs `BufferedImageOp` chains. This is CPU-intensive and memory-heavy compared to native image processing libraries.

2. **No WebP/AVIF support**: The current implementation only supports PNG, JPEG, and GIF output formats (see `NormalizedImageParams.normalizeFormat()`). WebP is explicitly unsupported with a TODO comment: *"WEBP is not supported by ImageService implementation yet"*. Modern web performance requires WebP and increasingly AVIF support.

## Current Architecture

### Processing Pipeline

```
HTTP Request → ImageMediaHandler/ImageHandler
  → ImageHandlerWorker.transform()
    → ReadImageParams (builder)
      → ImageServiceImpl.readImage()
        → NormalizedImageParams (validation/normalization)
        → Cache lookup (SHA256-based file cache in $XP_HOME/work/cache/img/)
        → If cache miss:
          → ContentService.getBinary() → InputStream
          → ImageIO.createImageInputStream() → ImageReader.read() → BufferedImage
          → applyRotation() via AffineTransform (8 EXIF orientations)
          → applyCropping() via BufferedImage.getSubimage()
          → imageScaleFunction.apply() (6 scale modes: block, square, max, wide, height, width)
          → imageFilterBuilder.build().apply() (21 filters via JHLabs + custom)
          → ImageHelper.removeAlphaChannel() (for non-PNG output)
          → ImageHelper.writeImage() (javax.imageio writer with quality/progressive settings)
          → Cache result to disk
        → Return ByteSource
```

### Key Files

| File | Purpose |
|------|---------|
| `modules/core/core-api/.../image/ImageService.java` | Public API interface (single method: `readImage(ReadImageParams)`) |
| `modules/core/core-api/.../image/ReadImageParams.java` | Input parameters (contentId, mimeType, scaleParams, cropping, focalPoint, orientation, quality, backgroundColor, filterParam) |
| `modules/core/core-api/.../image/ImageHelper.java` | Utility: image writing, alpha removal, scaling helpers |
| `modules/core/core-image/.../ImageServiceImpl.java` | Main service implementation (~390 lines) |
| `modules/core/core-image/.../NormalizedImageParams.java` | Parameter normalization and format validation |
| `modules/core/core-image/.../ImageFilterBuilderImpl.java` | Filter registry and expression parsing |
| `modules/core/core-image/.../ImageScaleFunctionBuilderImpl.java` | Scale function registry |
| `modules/core/core-image/.../effect/ImageFilters.java` | 21 filter implementations (JHLabs-based + custom) |
| `modules/core/core-image/.../effect/ImageScales.java` | 6 scale mode implementations |
| `modules/core/core-image/.../effect/ScaledFunction.java` | Scale application with memory estimation |
| `modules/core/core-image/.../MemoryCircuitBreaker.java` | JVM memory protection (semaphore-based) |
| `modules/core/core-image/.../ImmutableFilesHelper.java` | Disk cache management |
| `modules/core/core-image/.../ImageConfig.java` | OSGi configuration (scale.maxDimension, filters.maxTotal, memoryLimit, progressive) |
| `modules/core/core-image/build.gradle` | Dependencies: core-api, core-internal, jhlabs.filters |

### Supported Operations

**Scale modes** (6): `block(w,h)`, `square(s)`, `max(s)`, `wide(w,h)`, `height(s)`, `width(s)` — all with focal point support

**Filters** (21): `block`, `blur`, `border`, `bump`, `colorize`, `edge`, `emboss`, `fliph`, `flipv`, `gamma`, `grayscale`, `hsbadjust`, `hsbcolorize`, `invert`, `rgbadjust`, `rotate90`, `rotate180`, `rotate270`, `rounded`, `sepia`, `sharpen`

**Output formats** (3): PNG, JPEG, GIF

**EXIF orientations** (8): All standard EXIF orientation values handled

### Dependencies

- `javax.imageio` (Java standard library) — image reading/writing
- `java.awt` (Java standard library) — `BufferedImage`, `AffineTransform`, `Graphics2D`, rendering
- `com.jhlabs:filters:2.0.235-1` — blur, sharpen, emboss, edge, gamma, grayscale, flip, etc.
- Custom filters: `ColorizeFilter`, `HSBColorizeFilter`, `SepiaFilter` (extend `java.awt.image.RGBImageFilter`)

## Proposed Solution

### Approach: Use `im4java` to invoke ImageMagick CLI

Use the [`im4java`](https://github.com/Waxolunist/im4java) library as a Java bridge to ImageMagick's command-line tools (`magick`/`convert`). This approach:

- **Avoids JNI complexity**: No native JNI bindings to maintain per platform. `im4java` invokes ImageMagick as a subprocess via `ProcessBuilder`.
- **Leverages ImageMagick's full format support**: WebP, AVIF, HEIF, and all standard formats.
- **Uses ImageMagick's optimized native code**: Significantly faster than Java AWT for scaling, filtering, and format conversion.
- **Platform-independent Java code**: The same Java code works on all platforms; only the ImageMagick binary differs per distro.

### High-Level Architecture

```
ImageServiceImpl.readImage(ReadImageParams)
  → NormalizedImageParams (updated to accept webp/avif output formats)
  → Cache lookup (unchanged)
  → If cache miss:
    → ContentService.getBinary() → write to temp file or pipe
    → Build ImageMagick command via im4java IMOperation:
      → Auto-orient (replaces manual EXIF rotation)
      → Crop (replaces BufferedImage.getSubimage)
      → Scale/Resize (replaces custom ScaleCalculator + AffineTransform)
      → Filter chain (replaces JHLabs BufferedImageOp)
      → Format conversion + quality settings
    → Execute magick command → output to cache file
  → Return ByteSource from cache
```

### Implementation Plan

#### Phase 1: Core Integration

**1.1 Add `im4java` dependency**

Add to `gradle/libs.versions.toml`:
```toml
im4java = { module = "org.im4java:im4java", version = "1.4.0" }
```

Add to `modules/core/core-image/build.gradle`:
```gradle
dependencies {
    implementation project(':core:core-api')
    implementation project(':core:core-internal')
    implementation libs.im4java
    // Remove: implementation libs.jhlabs.filters
}
```

**1.2 Create `ImageMagickCommandBuilder`**

New class that translates `NormalizedImageParams` into an ImageMagick command pipeline:

```java
// modules/core/core-image/src/main/java/com/enonic/xp/core/impl/image/ImageMagickCommandBuilder.java
class ImageMagickCommandBuilder {
    IMOperation buildOperation(NormalizedImageParams params, int sourceWidth, int sourceHeight) {
        IMOperation op = new IMOperation();
        op.addImage("-");              // Read from stdin
        op.autoOrient();               // Handle EXIF orientation
        applyCropping(op, params);     // -crop geometry
        applyScaling(op, params, sourceWidth, sourceHeight);  // -resize/-crop+resize
        applyFilters(op, params);      // -blur, -sharpen, -modulate, etc.
        applyQuality(op, params);      // -quality N
        applyBackground(op, params);   // -background color -flatten
        op.addImage(params.getFormat() + ":-"); // Write to stdout
        return op;
    }
}
```

**1.3 Map all 21 filters to ImageMagick equivalents**

| XP Filter | ImageMagick Equivalent |
|-----------|----------------------|
| `block(size)` | `-scale {1/size}% -scale {size*100}%` (pixelate) |
| `blur(radius)` | `-blur 0x{radius}` |
| `border(size,color)` | `-bordercolor {color} -border {size}` |
| `bump` | `-shade 135x45` |
| `colorize(r,g,b)` | `-colorspace Gray -channel R -evaluate multiply {r} ...` or `-colorize` |
| `edge` | `-edge 1` |
| `emboss` | `-emboss 1` |
| `fliph` | `-flop` |
| `flipv` | `-flip` |
| `gamma(value)` | `-gamma {value}` |
| `grayscale` | `-colorspace Gray` |
| `hsbadjust(h,s,b)` | `-modulate {100+b*100},{100+s*100},{100+h*360/255}` |
| `hsbcolorize(color)` | `-fill {color} -colorize 50%` (approximate) |
| `invert` | `-negate` |
| `rgbadjust(r,g,b)` | `-channel R -evaluate add {r*255} -channel G -evaluate add {g*255} -channel B -evaluate add {b*255}` |
| `rotate90` | `-rotate 90` |
| `rotate180` | `-rotate 180` |
| `rotate270` | `-rotate 270` |
| `rounded(radius,border,color)` | Compose with rounded rectangle mask |
| `sepia(depth)` | `-sepia-tone {depth}%` |
| `sharpen` | `-sharpen 0x1` |

**1.4 Map all 6 scale modes**

| Scale Mode | ImageMagick Equivalent |
|------------|----------------------|
| `block(w,h)` | `-resize {w}x{h}^ -gravity Center -extent {w}x{h}` (with focal point offset via `-gravity` + `-extent` geometry) |
| `square(s)` | `-resize {s}x{s}^ -gravity Center -extent {s}x{s}` |
| `max(s)` | `-resize {s}x{s}` (default fit-within behavior) |
| `wide(w,h)` | `-resize {w}x -gravity Center -extent {w}x{h}` |
| `height(s)` | `-resize x{s}` |
| `width(s)` | `-resize {s}x` |

Focal point support for `block`, `square`, and `wide` modes requires calculating the `-extent` offset geometry based on the focal point coordinates instead of using `-gravity Center`.

**1.5 Rewrite `ImageServiceImpl.createImage()`**

Replace the Java AWT pipeline with im4java command execution:

```java
private void createImage(ByteSource blob, NormalizedImageParams params, ByteSink sink) throws IOException {
    IMOperation op = commandBuilder.buildOperation(params, ...);
    
    ConvertCmd cmd = new ConvertCmd(true); // true = use "magick" instead of "convert"
    cmd.setSearchPath(imageConfig.imageMagickPath()); // configurable path
    
    // Use Pipe for stdin/stdout to avoid temp files
    Pipe stdin = new Pipe(blob.openStream(), null);
    Pipe stdout = new Pipe(null, sink.openBufferedStream());
    cmd.setInputProvider(stdin);
    cmd.setOutputConsumer(stdout);
    
    cmd.run(op);
}
```

**1.6 Update `NormalizedImageParams.normalizeFormat()`**

Add WebP and AVIF support:

```java
private static String normalizeFormat(ReadImageParams readImageParams) {
    return switch (readImageParams.getMimeType()) {
        case "image/png" -> "png";
        case "image/jpeg" -> "jpeg";
        case "image/gif" -> "gif";
        case "image/webp" -> "webp";
        case "image/avif" -> "avif";
        default -> throw new IllegalArgumentException("Unsupported type " + mimeType);
    };
}
```

#### Phase 2: Configuration & Platform Support

**2.1 Update `ImageConfig`**

Add ImageMagick-specific configuration:

```java
@interface ImageConfig {
    int scale_maxDimension() default 8000;
    int filters_maxTotal() default 25;
    String memoryLimit() default "10%";    // Still used for process concurrency control
    String progressive() default "jpeg";
    String imageMagick_path() default "";  // Auto-detect if empty
    int imageMagick_timeout() default 30;  // Seconds, per operation
}
```

**2.2 Bundle ImageMagick binaries per platform**

Create platform-specific distribution modules or leverage the existing distro packaging:

```
modules/runtime/src/
  imagemagick/
    linux-x64/
      magick (ImageMagick binary + delegates)
    linux-arm64/
      magick
    macos-x64/
      magick
    macos-arm64/
      magick
    windows-x64/
      magick.exe
```

The `runtime/build.gradle` needs to be updated to:
- Download pre-built static ImageMagick binaries (AppImage or portable builds) during build
- Include the correct platform binary in the distribution zip
- Set the `imageMagick_path` config default to the bundled binary location

An alternative: use ImageMagick's **portable/static builds** which bundle all delegates (libwebp, libjpeg-turbo, libpng, etc.) into a single binary with no system dependencies.

**2.3 Auto-detect ImageMagick on startup**

Add validation in `ImageServiceImpl.activate()`:
```java
@Activate
public void activate(ImageConfig config) {
    String magickPath = config.imageMagick_path();
    if (magickPath.isEmpty()) {
        magickPath = findBundledMagick(); // Check $XP_HOME/imagemagick/{platform}/
    }
    validateMagickBinary(magickPath); // Run "magick -version" and log
}
```

#### Phase 3: Backward Compatibility & Cleanup

**3.1 Remove JHLabs dependency**

Remove from `build.gradle`:
```gradle
// Remove: implementation libs.jhlabs.filters
```

Remove from `jar.bundle.bnd`:
```gradle
// Remove: 'Private-Package': 'com.jhlabs.*'
```

Delete custom filter classes that directly extend AWT classes:
- `ColorizeFilter.java`
- `HSBColorizeFilter.java`
- `SepiaFilter.java`

**3.2 Simplify memory management**

The `MemoryCircuitBreaker` semaphore approach can be simplified since ImageMagick processes use OS-level memory, not JVM heap. Replace with a simple concurrency limiter (max concurrent ImageMagick processes) to prevent system overload:

```java
// Instead of megabyte-based semaphore, use process count semaphore
private final Semaphore processSemaphore = new Semaphore(config.imageMagick_maxConcurrent()); // default: 4
```

ImageMagick itself has built-in resource limits (`-limit memory`, `-limit map`, `-limit disk`) that can be set via the command line.

**3.3 Update `ImageHelper.java` in core-api**

The `ImageHelper` utility class in `core-api` has public static methods (`writeImage`, `removeAlphaChannel`, `getScaledInstance`, `scaleSquare`) that may be used by external code. These should be preserved but deprecated:

```java
@Deprecated(since = "8.x", forRemoval = true)
public static void writeImage(OutputStream out, BufferedImage image, String format, int quality) { ... }
```

**3.4 Preserve the public API contract**

The `ImageService` interface and `ReadImageParams` class in `core-api` remain unchanged. The only public API addition is accepting `image/webp` and `image/avif` MIME types in `ReadImageParams.mimeType`.

**3.5 Visual regression testing**

Create regression tests that:
1. Process a set of reference images through all 21 filters + 6 scale modes
2. Compare ImageMagick output with the current Java output
3. Allow a configurable pixel-difference tolerance (ImageMagick may produce slightly different results)
4. Verify EXIF orientation handling for all 8 orientations
5. Test WebP and AVIF output format support

### Files to Modify

| File | Change |
|------|--------|
| `gradle/libs.versions.toml` | Add `im4java` dependency, remove `jhlabs-filters` |
| `modules/core/core-image/build.gradle` | Update dependencies |
| `modules/core/core-image/.../ImageServiceImpl.java` | Replace AWT pipeline with im4java commands |
| `modules/core/core-image/.../NormalizedImageParams.java` | Add webp/avif format support |
| `modules/core/core-image/.../ImageConfig.java` | Add ImageMagick config properties |
| `modules/core/core-image/.../MemoryCircuitBreaker.java` | Simplify to concurrency limiter |
| `modules/core/core-image/.../effect/ImageFilters.java` | Rewrite as ImageMagick command builders |
| `modules/core/core-image/.../effect/ImageScales.java` | Rewrite as ImageMagick resize commands |
| `modules/core/core-image/.../effect/ScaledFunction.java` | Remove (logic moves to command builder) |
| `modules/core/core-image/.../effect/ScaleCalculator.java` | Simplify or remove |
| `modules/core/core-image/.../effect/ColorizeFilter.java` | Remove |
| `modules/core/core-image/.../effect/HSBColorizeFilter.java` | Remove |
| `modules/core/core-image/.../effect/SepiaFilter.java` | Remove |
| `modules/core/core-api/.../image/ImageHelper.java` | Deprecate AWT-based methods |
| `modules/runtime/build.gradle` | Add ImageMagick binary packaging |
| `modules/runtime/src/home/config/com.enonic.xp.image.cfg` | Add ImageMagick config defaults |

### Files to Create

| File | Purpose |
|------|---------|
| `modules/core/core-image/.../ImageMagickCommandBuilder.java` | Translates params to IM commands |
| `modules/core/core-image/.../ImageMagickProcessRunner.java` | Executes IM with timeout/error handling |
| `modules/core/core-image/src/test/.../ImageMagickCommandBuilderTest.java` | Unit tests for command generation |
| `modules/core/core-image/src/test/.../VisualRegressionTest.java` | Pixel-comparison regression tests |

## Risks & Considerations

### 1. Platform binary management
Bundling ImageMagick binaries (~30-50MB per platform) increases distribution size. Consider:
- Downloading at first run (like Playwright does for browsers)
- Providing a "headless" distro without ImageMagick
- Using ImageMagick's static/AppImage builds to avoid system library dependencies

### 2. Visual differences
ImageMagick's algorithms differ from Java AWT's. Filters like `blur`, `sharpen`, and `sepia` may produce slightly different visual output. This is acceptable but should be documented as a breaking change for pixel-exact comparisons.

### 3. Security
ImageMagick has had CVEs related to processing malicious images. Mitigations:
- Use ImageMagick's **policy.xml** to disable risky coders (SVG, MVG, ephemeral, URL)
- Set resource limits (`-limit memory`, `-limit time`, `-limit disk`)
- Run with minimal file system permissions
- Keep ImageMagick version updated

### 4. `rounded` filter complexity
The `rounded` filter (rounded corners with optional border) creates a composited image with a mask. This requires a multi-step ImageMagick command using `-compose`, mask generation, and alpha compositing. It's the most complex filter to port.

### 5. Fallback strategy
Consider keeping the Java implementation as a fallback if ImageMagick is not available (e.g., in development environments without ImageMagick installed), controlled by configuration.

### 6. Docker/container deployments
Container images need to include ImageMagick. Either:
- Bundle it in the XP base Docker image
- Provide installation instructions
- Use the static binary approach

## Acceptance Criteria

- [ ] All 6 scale modes produce equivalent output to the current Java implementation
- [ ] All 21 filters produce visually equivalent output (within tolerance)
- [ ] All 8 EXIF orientations are handled correctly
- [ ] WebP output format is supported with configurable quality
- [ ] AVIF output format is supported with configurable quality
- [ ] PNG, JPEG, GIF output continues to work identically
- [ ] Focal point-aware scaling works correctly for block, square, and wide modes
- [ ] Cropping with zoom works correctly
- [ ] Progressive JPEG output is supported
- [ ] Image cache (SHA256-based) continues to work
- [ ] Memory/concurrency protection is in place
- [ ] `ImageService` interface is unchanged
- [ ] `ReadImageParams` API is unchanged (only new `mimeType` values accepted)
- [ ] Configuration is backward-compatible (existing `com.enonic.xp.image.cfg` works without changes)
- [ ] ImageMagick binary is bundled for Linux x64, Linux ARM64, macOS x64, macOS ARM64, Windows x64
- [ ] Security policy restricts dangerous ImageMagick coders
- [ ] Processing timeout prevents hung processes
- [ ] All existing image service tests pass
- [ ] Visual regression tests verify output quality
- [ ] Performance benchmarks show improvement over Java AWT
