/*
 * Copyright (c) 2011 J. Lewis Muir <jlmuir@imca-cat.org>
 *
 * Permission to use, copy, modify, and distribute this software for any
 * purpose with or without fee is hereby granted, provided that the above
 * copyright notice and this permission notice appear in all copies.
 *
 * THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES
 * WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR
 * ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES
 * WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
 * ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF
 * OR IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
 */

package com.vava33.d2dplot.auxi;

import ij.IJ;
import ij.ImagePlus;
import ij.process.FloatProcessor;
import ij.process.ImageProcessor;
import imcacat.jcbf.CbfImageReader;
import imcacat.jcbf.CbfImageReaderSpi;

import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferFloat;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.event.IIOReadWarningListener;
import javax.imageio.stream.ImageInputStream;

/**
 * I am an ImageJ plug-in for reading a CBF image.
 */
public class ImageJCbfReader {
  private static final Pattern WHITESPACE_PATTERN = Pattern.compile(" ");

  public ImagePlus read(File cbfFile) {
    CbfImageReaderSpi spi = new CbfImageReaderSpi();
    ImageInputStream stream = null;
    try {
      stream = ImageIO.createImageInputStream(cbfFile);
    } catch (IOException e) {
      showError("Error creating input stream from specified file", e);
      return null;
    }
    try {
      if (!spi.canDecodeInput(stream)) {
        showMessage("The specified file is not a CBF image.");
        return null;
      }
    } catch (IOException e) {
      showError("Error checking whether specified file is a CBF image", e);
      return null;
    }
    CbfImageReader reader = new CbfImageReader(spi);
    reader.setInput(stream);
    ImageReadParam param = reader.getDefaultReadParam();
    Iterator<ImageTypeSpecifier> types = null;
    try {
      types = reader.getImageTypes(0);
    } catch (IOException e) {
      showError("Error reading CBF image metadata", e);
      reader.dispose();
      return null;
    }
    while (types.hasNext()) {
      ImageTypeSpecifier each = types.next();
      if (each.getSampleModel().getDataType() == DataBuffer.TYPE_FLOAT) {
        param.setDestinationType(each);
        break;
      }
    }

    final String cbfFilePath = cbfFile.getPath();
    reader.addIIOReadWarningListener(new IIOReadWarningListener() {
      @Override
      public void warningOccurred(ImageReader source, String warning) {
        IJ.log("Warning: " + warning + " (" + cbfFilePath + ")");
      }
    });

    int width = 0;
    int height = 0;
    try {
      width = reader.getWidth(0);
      height = reader.getHeight(0);
    } catch (IOException e) {
      showError("Error determining width and height of CBF image", e);
      reader.dispose();
      return null;
    }
    BufferedImage image = null;
    try {
      image = reader.read(0, param);
    } catch (IOException e) {
      showError("Error reading CBF image data", e);
      reader.dispose();
      return null;
    }
    DataBuffer buffer = image.getData().getDataBuffer();
    int bufferType = buffer.getDataType();
    float[] pixels;
    ImageProcessor ip;
    if (buffer instanceof DataBufferFloat) {
      pixels = ((DataBufferFloat)buffer).getData();
      ip = new FloatProcessor(width, height, pixels, null);
      image.flush();
      reader.dispose();
      return new ImagePlus(cbfFile.getName(), ip);
    } else if (bufferType == DataBuffer.TYPE_INT || bufferType == DataBuffer.TYPE_FLOAT) {
      pixels = new float[width * height];
      for (int i = 0; i < pixels.length; i++)
        pixels[i] = buffer.getElemFloat(i);
      ip = new FloatProcessor(width, height, pixels, null);
      image.flush();
      reader.dispose();
      return new ImagePlus(cbfFile.getName(), ip);
    } else if (bufferType == DataBuffer.TYPE_DOUBLE) {
      pixels = new float[width * height];
      boolean clipped = false;
      for (int i = 0; i < pixels.length; i++) {
        double doubleValue = buffer.getElemDouble(i);
        float floatValue;
        if (doubleValue < -Float.MAX_VALUE) {
          floatValue = -Float.MAX_VALUE;
          clipped = true;
        } else if (doubleValue > Float.MAX_VALUE) {
          floatValue = Float.MAX_VALUE;
          clipped = true;
        } else {
          floatValue = (float)doubleValue;
        }
        pixels[i] = floatValue;
      }
      if (clipped) {
        IJ.log("Warning: pixel value(s) clipped to fit in type float (" + cbfFilePath + ")");
      }
      ip = new FloatProcessor(width, height, pixels, null);
      image.flush();
      reader.dispose();
      return new ImagePlus(cbfFile.getName(), ip);
    } else {
      reader.dispose();
      return new ImagePlus(cbfFile.getName(), image);
    }
  }

  private static void showError(String msg, Throwable t) {
    StringBuilder buffer = new StringBuilder();
    buffer.append(msg);
    if (t != null) {
      buffer.append(": ");
      buffer.append(t.getMessage());
    }
    if (buffer.length() != 0) {
      char last = buffer.charAt(buffer.length() - 1);
      if (last != '.' && last != '!' && last != '?') buffer.append('.');
    }
    IJ.error("CBF Reader", wordWrap(buffer.toString(), 78));
  }

  private static void showMessage(String msg) {
    IJ.showMessage("CBF Reader", wordWrap(msg, 78));
  }

  private static String wordWrap(String text, int maxColumnWidth) {
    return wordWrap(text, maxColumnWidth, System.getProperty("line.separator"));
  }

  /*
   * Performs a very basic type of word wrapping on the specified text.
   * Splits words on the space character and expects the text to not contain
   * any whitespace characters other than the space character.
   */
  private static String wordWrap(String text, int maxColumnWidth, String lineSeparator) {
    if (maxColumnWidth <= 0) throw new IllegalArgumentException("maxColumnWidth must be > 0");

    String buffer = "";
    String lineBuffer = "";
    String[] words = WHITESPACE_PATTERN.split(text);
    for (int i = 0; i < words.length; i++) {
      if (words[i].length() + 1 + lineBuffer.length() <= maxColumnWidth) {
        if (lineBuffer.length() != 0) lineBuffer += " ";
        lineBuffer += words[i];
      } else if (words[i].length() <= maxColumnWidth) {
        buffer += lineBuffer.trim() + lineSeparator;
        lineBuffer = "";
        i--;
      } else {
        buffer += lineBuffer.trim() + lineSeparator;
        lineBuffer = "";
        for (int j = 0; j < words[i].length(); j += maxColumnWidth) {
          lineBuffer = words[i].substring(j, Math.min(j + maxColumnWidth,
            words[i].length()));
          if (lineBuffer.length() == maxColumnWidth) {
            buffer += lineBuffer.trim() + lineSeparator;
            lineBuffer = "";
          }
        }
      }
    }
    if (lineBuffer.length() != 0) buffer += lineBuffer.trim();
    return buffer.trim();
  }
}
