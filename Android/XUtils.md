# Android 开发中一些有用的工具方法

## 解决输入框被输入法覆盖的冲突
> 解决方法是将输入框所在的view 滑动到 scrollToView 所在位置，即将scrollToView以上的view 滑动到输入法之上

```java
/**
 * @param root 最外层布局，需要调整的布局
 * @param scrollToView 被键盘遮挡的scrollToView，滚动root,使scrollToView在root可视区域的底部
 */
private void controlKeyboardLayout(final View root, final View scrollToView)
{
    root.getViewTreeObserver().addOnGlobalLayoutListener( new ViewTreeObserver.OnGlobalLayoutListener()
    {
        int mLastHeight = 0;
        int mLastBottom = -1;

        ValueAnimator scrollAnimator = null;
        @Override
        public void onGlobalLayout()
        {
            Rect rect = new Rect();
            // 获取到布局的位置
            root.getWindowVisibleDisplayFrame(rect);

            if (mLastBottom == -1) {
                mLastBottom = rect.bottom;
                return;
            }

            int nb = rect.bottom;
            int ob = mLastBottom;

            if (nb < ob) {
                // 键盘显示了， 滑上去
                int[] location = new int[2];
                scrollToView.getLocationInWindow(location);
                int scrollHeight = (location[1] + scrollToView.getHeight()) - nb;

                scrollAnimator = ValueAnimator.ofInt(0, scrollHeight);
                scrollAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
                scrollAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
                {
                    @Override
                    public void onAnimationUpdate(ValueAnimator valueAnimator)
                    {
                        int value = (int) valueAnimator.getAnimatedValue();
                        root.scrollTo(0, value);
                        mLastHeight = value;
                    }
                });
                scrollAnimator.start();
            }
            else if (nb > ob) {
                // 键盘隐藏了, 滑下来
                if (scrollAnimator != null) {
                    scrollAnimator.cancel();
                }
                scrollAnimator = ValueAnimator.ofInt(mLastHeight, 0);
                scrollAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
                scrollAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
                {
                    @Override
                    public void onAnimationUpdate(ValueAnimator valueAnimator)
                    {
                        int value = (int) valueAnimator.getAnimatedValue();
                        root.scrollTo(0, value);
                        mLastHeight = value;
                    }
                });
                scrollAnimator.addListener(new Animator.AnimatorListener()
                {
                    @Override
                    public void onAnimationStart(Animator animator)
                    {
                    }

                    @Override
                    public void onAnimationEnd(Animator animator)
                    {
                        root.scrollTo(0, 0);
                        mLastHeight = 0;
                    }

                    @Override
                    public void onAnimationCancel(Animator animator)
                    {
                        root.scrollTo(0, 0);
                        mLastHeight = 0;
                    }

                    @Override
                    public void onAnimationRepeat(Animator animator)
                    {
                    }
                });

                scrollAnimator.start();
            }

            if (nb != ob) {
                mLastBottom = nb;
            }
        }
    });
}
```

## pd, px 的相互转换
```java
public static float DISPLAY_DENSITY = 1;
public static DisplayMetrics DISPLAY_METRICS = null;

public void initializeCommon(Context context)
{
    DISPLAY_METRICS = context.getResources().getDisplayMetrics();
    DISPLAY_DENSITY = DISPLAY_METRICS.density;
}

public static int dpToPx(float dp)
{
    return (int) (dp * DISPLAY_DENSITY + 0.5f);
}

public static int pxToDp(float px)
{
    return (int) (px / DISPLAY_DENSITY + 0.5f);
}
```

## 根据 Uri 获取文件的实际位置
```java
public static String getRealFilePath(Context context, final Uri uri)
{
    if (null == uri) {
        return null;
    }

    final String scheme = uri.getScheme();
    String data = null;
    if (scheme == null) {
        data = uri.getPath();
    }
    else if (ContentResolver.SCHEME_FILE.equals(scheme)) {
        data = uri.getPath();
    }
    else if (ContentResolver.SCHEME_CONTENT.equals(scheme)) {
        Cursor cursor = context.getContentResolver().query(
                            uri, new String[]{MediaStore.Images.ImageColumns.DATA}, null, null, null);
        if (null != cursor) {
            if (cursor.moveToFirst()) {
                int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                if (index > -1) {
                    data = cursor.getString(index);
                }
            }
            cursor.close();
        }
    }

    return data;
}
```

## 保存图片到系统相册
```java
public static void saveImageToGallery(Context context, File file)
{
    if (context == null || file == null || !file.exists()) {
        return;
    }

    try {
        MediaStore.Images.Media.insertImage(
                context.getContentResolver(), file.getAbsolutePath(), file.getName(), null);
    }
    catch (FileNotFoundException e) {
        e.printStackTrace();
    }

    Uri uri = Uri.fromFile(file);
    context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri));
    // 此处的扫描，这样系统相册里面才会立即显示出来
    MediaScannerConnection.scanFile(context, new String[]{ file.getAbsolutePath() }, null, null);
}
```

## 主动隐藏或者显示输入法
```java
/**
 * 隐藏输入法
 */
public static void dismissSoftInputMethod(Context context, IBinder windowToken)
{
    InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);

    imm.hideSoftInputFromWindow(windowToken, 0);
}

/**
 * 显示
 */
public static void showSoftInputMethod(Context context)
{
    InputMethodManager imm = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
    imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
}
```

## 获取图片的旋转角度
> 由于一些机型或者系统 比如 ios, 拍出的照片可能带有旋转角度

```java
/**
 * 读取图片的旋转的角度
 *
 * @param path 图片绝对路径
 * @return 图片的旋转角度
 */
public static int getImageDegree(String path)
{
    int degree = 0;
    try {
        // 从指定路径下读取图片，并获取其EXIF信息
        ExifInterface exifInterface = new ExifInterface(path);
        // 获取图片的旋转信息
        int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                degree = 90;
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                degree = 180;
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                degree = 270;
                break;
        }
    }
    catch (IOException e) {
        e.printStackTrace();
    }

    return degree;
}
```

## 将图片按照某个角度进行旋转
```java
/**
 * 将图片按照某个角度进行旋转
 *
 * @param bm     需要旋转的图片
 * @param degree 旋转角度
 * @return 旋转后的图片
 */
public static Bitmap rotateBitmapByDegree(Bitmap bm, int degree)
{
    Bitmap returnBm = null;

    // 根据旋转角度，生成旋转矩阵
    Matrix matrix = new Matrix();
    matrix.postRotate(degree);
    try {
        // 将原始图片按照旋转矩阵进行旋转，并得到新的图片
        returnBm = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), matrix, true);
    }
    catch (OutOfMemoryError e) {
        returnBm = null;
    }

    if (returnBm == null) {
        returnBm = bm;
    }
    if (bm != returnBm) {
        bm.recycle();
    }
    return returnBm;
}
```

## 获取当前进程的名字
```java
public static String getCurProcessName(Context context)
{
    int pid = android.os.Process.myPid();
    ActivityManager mActivityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
    for (ActivityManager.RunningAppProcessInfo appProcess : mActivityManager.getRunningAppProcesses()) {
        if (appProcess.pid == pid) {
            return appProcess.processName;
        }
    }
    return null;
}
```
