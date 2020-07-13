//
// Created by kaushal on 10/7/20.
//

#include <jni.h>
#include <string>
#include <opencv2/core.hpp>
#include <opencv2/imgproc.hpp>

#include "include/filters.hpp"

using namespace cv;

extern "C"
JNIEXPORT void JNICALL
Java_com_example_scanin_ImageDataModule_ImageEditUtil_getTestGray(JNIEnv *env, jclass clazz, jlong img_addr,
                                                  jlong gray_img_addr) {
    Mat& mGr  = *(Mat*)img_addr;
    Mat& cvtImg = *(Mat*)gray_img_addr;

    //magic_filter(mGr, cvtImg);
    dark_magic_filter(mGr, cvtImg);

//    cvtColor(mGr, cvtImg, COLOR_BGR2GRAY);
}