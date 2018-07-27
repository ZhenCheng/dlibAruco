#include <jni.h>
#include <opencv2/core.hpp>
#include <opencv2/imgproc.hpp>
#include <opencv2/calib3d.hpp>
#include <aruco/aruco.h>

#include <fstream>
#include <iostream>
#include <opencv2/highgui.hpp>
#include <sstream>
#include <string>

namespace Java_com_tzutalin_dlibtest_OnGetImageListener {

    static std::vector<aruco::Marker> lastDetectedMarkers;
    static aruco::MarkerDetector MDetector;
    static aruco::CameraParameters cameraParameters;
    static float markerSize=1;

    static std::vector<std::vector<aruco::Marker> > CalibrationMarkers;

    std::string ConvertJString(JNIEnv* env, jstring str);
    std::stringstream LogStream;

};


using namespace std;
using namespace cv;
using namespace aruco;
using namespace Java_com_tzutalin_dlibtest_OnGetImageListener;

extern "C" {

JNIEXPORT void JNICALL Java_com_tzutalin_dlibtest_OnGetImageListener_arucoSimple
        (JNIEnv *env, jobject,
         jlong ImageGray, jlong ImageColor) {

    cv::Mat &matgray = *(cv::Mat *) ImageGray;
    cv::Mat &matcolor = *(cv::Mat *) ImageColor;

    // just detect if is matgray with 1 layer
    if (matgray.type() != CV_8UC1) return;

    lastDetectedMarkers = MDetector.detect(matgray, cameraParameters, markerSize);

    for (auto m:lastDetectedMarkers)
        m.draw(matcolor, cv::Scalar(0, 0, 255), 2, true);

    //LogStream << "Called Teste-ALAN" << endl;
}

JNIEXPORT jstring JNICALL
Java_com_tzutalin_dlibtest_OnGetImageListener_jniGetLog
        (JNIEnv *env, jobject) {

    string str = LogStream.str();
    LogStream.str(std::string());
    return env->NewStringUTF(str.c_str());
}

JNIEXPORT void JNICALL Java_com_tzutalin_dlibtest_OnGetImageListener_convertGray
        (JNIEnv *env, jobject,
         jlong ImageColor, jlong ImageGray) {

    cv::Mat &matcolor = *(cv::Mat *) ImageColor;
    cv::Mat &matgray = *(cv::Mat *) ImageGray;

    //LogStream << "convertGrayCalled" << endl;
    cvtColor(matcolor, matgray, CV_RGBA2GRAY);
}

}

namespace Java_com_tzutalin_dlibtest_OnGetImageListener{

    std::string ConvertJString(JNIEnv* env, jstring str)
    {
        const jsize len = env->GetStringUTFLength(str);
        const char* strChars = env->GetStringUTFChars(str, (jboolean *)0);

        std::string Result(strChars, len);
        env->ReleaseStringUTFChars(str, strChars);

        return Result;
    };

}

