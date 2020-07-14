//
// Created by kaushal on 10/7/20.
//

#include <iostream>
#include <jni.h>
#include <string>
#include <vector>
#include <opencv2/core.hpp>
#include <opencv2/imgproc.hpp>

#include "filters.hpp"
#include "corners.hpp"
using namespace cv;

extern "C"
JNIEXPORT void JNICALL
Java_com_example_scanin_ImageDataModule_ImageEditUtil_cropImage(JNIEnv *env, jclass clazz,
                                                                jlong img_addr, jlong crop_img_addr,
                                                                jlong pts) {
    Mat& src = *(Mat*) img_addr;
    Mat& dst = *(Mat*) crop_img_addr;
    Mat& points = *(Mat*) pts;
    std::vector <cv::Point> vec_pts (points);

    // changing cv::INTER_NEAREST to cv::INTER_LANCZOS4 should improve results
    // but degrade performance.
    // This can raise errors due to order problem.
    int ret = four_point_transform(src, dst, vec_pts, cv::INTER_NEAREST);
    if (ret) {
        std::cerr << "transform failed due to order issue." << std::endl;
    }
}

extern "C"
JNIEXPORT void JNICALL
Java_com_example_scanin_ImageDataModule_ImageEditUtil_getTestGray(JNIEnv *env, jclass clazz,
                                                                  jlong img_addr,
                                                                  jlong gray_img_addr) {
    Mat &src = *(Mat*) img_addr;
    Mat &dst = *(Mat*) gray_img_addr;

    dark_magic_filter(src, dst);
}

extern "C"
JNIEXPORT void JNICALL
Java_com_example_scanin_ImageDataModule_ImageEditUtil_filterImage(JNIEnv *env, jclass clazz,
                                                                  jlong img_addr,
                                                                  jlong filter_img_addr,
                                                                  jint filter_id) {
    Mat &src = *(Mat*) img_addr;
    Mat &dst = *(Mat*) filter_img_addr;

    // filterList = {"magic_filter", "gray_filter", "dark_magic_filter", "sharpen_filter"};
    switch (filter_id) {
        case 0: magic_filter(src, dst); break;
        case 1: gray_filter(src, dst); break;
        case 2: dark_magic_filter(src, dst); break;
        case 3: sharpen_filter(src, dst); break;
        default: std::cerr << "We should never reach here." << std::endl;
    }
}

extern "C"
JNIEXPORT void JNICALL
Java_com_example_scanin_ImageDataModule_ImageEditUtil_getBestPoints(JNIEnv *env, jclass clazz,
                                                                    jlong img_addr, jlong pts) {
    Mat &src = *(Mat*) img_addr;
    Mat &res = *(Mat*) pts;
    std::vector <Point> vec_pts;

    find_best_corners(src, vec_pts);
    res = Mat(vec_pts);
}