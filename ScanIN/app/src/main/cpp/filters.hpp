#ifndef __FILTERS_HPP__
#define __FILTERS_HPP__
#include <opencv2/core/core.hpp>
#include <opencv2/imgproc.hpp>

void resize_image_if_bigger (cv::Mat &input, const int dim=1536, const int interpolation=cv::INTER_AREA);

void magic_filter (cv::Mat &src, cv::Mat &dst, const double alpha=1.5, const int beta=0, const int threshold=0);

void sepia_filter (cv::Mat &src, cv::Mat &dst);

void lighten_filter (cv::Mat &src, cv::Mat &dst);

void gray_filter (cv::Mat &src, cv::Mat &dst);

void sharpen_filter (cv::Mat &src, cv::Mat &dst, cv::Size kernel_size=cv::Size(5, 5), const double sigma=1.0, const double amount=1.0, const int threshold=0);

#endif