
# NOTE: You must set these variables to their respective source paths before
# compiling. For example, set LEPTONICA_PATH to the directory containing
# the Leptonica configure file and source folders. Directories must be
# root-relative, e.g. TESSERACT_PATH := /home/username/tesseract-3.00
#
# To set the variables, you can run the following shell commands:
# export TESSERACT_PATH=path-to-tesseract
# export LEPTONICA_PATH=path-to-leptonica
# export LIBJPEG_PATH=path-to-libjpeg
#
# Or you can fill out and uncomment the following definitions:
# TESSERACT_PATH := path-to-tesseract
# LEPTONICA_PATH := path-to-leptonica
# LIBJPEG_PATH := path-to-libjpeg

TESSERACT_PATH := $(call my-dir)/../external/tesseract-3.01
LEPTONICA_PATH := $(call my-dir)/../external/leptonica-1.68
LIBJPEG_PATH := $(call my-dir)/../external/libjpeg



LOCAL_PATH := $(call my-dir)
TESSERACT_PATH := $(LOCAL_PATH)/com_googlecode_tesseract_android/src
LEPTONICA_PATH := $(LOCAL_PATH)/com_googlecode_leptonica_android/src

# Just build the Android.mk files in the subdirs
include $(call all-subdir-makefiles)
