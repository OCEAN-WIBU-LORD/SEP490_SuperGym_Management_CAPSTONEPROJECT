package com.supergym.sep490_supergymmanagement.ImageToBitmapConverter;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.Image;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicYuvToRGB;
import android.renderscript.Type;

public class YuvToRgbConverter {
    private final RenderScript rs;
    private ScriptIntrinsicYuvToRGB yuvToRgbIntrinsic;
    private Allocation inputAllocation;
    private Allocation outputAllocation;

    public YuvToRgbConverter(Context context) {
        rs = RenderScript.create(context);
        yuvToRgbIntrinsic = ScriptIntrinsicYuvToRGB.create(rs, Element.U8_4(rs));
    }

    public void yuvToRgb(Image image, Bitmap outputBitmap) {
        if (inputAllocation == null || inputAllocation.getType().getX() != image.getWidth() ||
                inputAllocation.getType().getY() != image.getHeight()) {
            Type.Builder yuvType = new Type.Builder(rs, Element.U8(rs))
                    .setX(image.getPlanes()[0].getBuffer().capacity());
            inputAllocation = Allocation.createTyped(rs, yuvType.create(), Allocation.USAGE_SCRIPT);

            Type.Builder rgbType = new Type.Builder(rs, Element.RGBA_8888(rs))
                    .setX(image.getWidth())
                    .setY(image.getHeight());
            outputAllocation = Allocation.createTyped(rs, rgbType.create(), Allocation.USAGE_SCRIPT);
        }

        inputAllocation.copyFrom(image.getPlanes()[0].getBuffer().array());
        yuvToRgbIntrinsic.setInput(inputAllocation);
        yuvToRgbIntrinsic.forEach(outputAllocation);
        outputAllocation.copyTo(outputBitmap);
    }
}
