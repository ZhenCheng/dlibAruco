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
    static float minmarkersize=0;

    static std::vector<std::vector<aruco::Marker> > CalibrationMarkers;

    std::string ConvertJString(JNIEnv* env, jstring str);
    std::stringstream LogStream;

};


using namespace std;
using namespace cv;
using namespace aruco;
using namespace Java_com_tzutalin_dlibtest_OnGetImageListener;

extern "C" {

JNIEXPORT float JNICALL Java_com_tzutalin_dlibtest_OnGetImageListener_arucoSimple
        (JNIEnv *env, jobject,
         jlong ImageGray, jlong ImageColor) {

    cv::Mat &matgray = *(cv::Mat *) ImageGray;
    cv::Mat &matcolor = *(cv::Mat *) ImageColor;

    cvtColor(matcolor, matgray, CV_RGBA2GRAY);

    // just detect if is matgray with 1 layer
    if (matgray.type() != CV_8UC1) return 1;

    // set my config
    //MDetector.loadParamsFromFile("/storage/emulated/0/data/arucoConfig.yml");

    // add my dictionary aruco
    std::string mydictionary = "/storage/emulated/0/data/myconnect.dict";
    MDetector.setDictionary(mydictionary);

    // set data file camera calibration
    //cameraParameters.readFromXMLFile("/storage/emulated/0/data/calibration_smartphone_1280x720.yml");
    cameraParameters.readFromXMLFile("/storage/emulated/0/data/calibration_smartphone_640x480.yml");
    // set the detection mode
    MDetector.setDetectionMode(DM_NORMAL, minmarkersize);

    // set the corner refinement Method
    MDetector.getParameters().setCornerRefinementMethod(CORNER_SUBPIX);

    // run detection marker
    lastDetectedMarkers = MDetector.detect(matgray, cameraParameters, markerSize);

    if (cameraParameters.isValid()) {
        for (auto m:lastDetectedMarkers){
            if(m.id == 1){
                //azul
                m.draw(matcolor, Scalar(0, 0, 255, 0), 2, CV_AA);
                //red
                //m.draw(matcolor, Scalar(255, 0, 0, 0), 2, CV_AA);
                //aruco::CvDrawingUtils::draw3dAxis(matcolor, m, cameraParameters, 2);
                //aruco::CvDrawingUtils::draw3dCube(matcolor, m, cameraParameters, 2);
                LogStream << " LandMarker [" << m.id << "]: " <<
                          "  Tx: " << m.Tvec.ptr<float>(0)[0] << " m "<<
                          "\tTy: " << m.Tvec.ptr<float>(1)[0] << " m "<<
                          "\tTz: " << m.Tvec.ptr<float>(2)[0] << " m "<<
                          "\tRx: " << m.Rvec.ptr<float>(0)[0] << " rad "<<
                          "\tRy: " << m.Rvec.ptr<float>(1)[0] << " rad "<<
                          "\tRz: " << m.Rvec.ptr<float>(2)[0] << " rad "<< endl;
                putText(matcolor, "X", Point(matcolor.cols / 2, matcolor.rows / 2), 1.2, 1.2, Scalar(0, 0, 255), 2);
                //putText(matcolor, "Tz: " + to_string(m.Tvec.ptr<float>(2)[0]) + " m ", Point(10, 480 / 2), 1.2, 1.2, Scalar(0, 0, 255), 2);
//                float results[2];
//                results[0] = m.Tvec.ptr<float>(0)[0];
//                results[1] = m.Tvec.ptr<float>(1)[0];
//                results[2] = m.Tvec.ptr<float>(2)[0];
                float results = m.Tvec.ptr<float>(2)[0];
                //LogStream << " Tz " << results << endl;

                return results;
            }
        }

    }

    return 0;
    //LogStream << "Called Teste-ALAN" << endl;
}

JNIEXPORT jstring JNICALL
Java_com_tzutalin_dlibtest_OnGetImageListener_jniGetLog
        (JNIEnv *env, jobject) {

    string str = LogStream.str();
    LogStream.str(std::string());
    return env->NewStringUTF(str.c_str());
}

//JNIEXPORT void JNICALL Java_com_tzutalin_dlibtest_OnGetImageListener_convertGray
//        (JNIEnv *env, jobject,
//         jlong ImageColor, jlong ImageGray) {
//
//    cv::Mat &matcolor = *(cv::Mat *) ImageColor;
//    cv::Mat &matgray = *(cv::Mat *) ImageGray;
//
//    //LogStream << "convertGrayCalled" << endl;
//    cvtColor(matcolor, matgray, CV_RGBA2GRAY);
//}

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

