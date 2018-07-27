cmake_minimum_required(VERSION 3.4.1)

# OpenCV stuff
find_package(OpenCV REQUIRED)
include_directories( ${OpenCV_INCLUDE_DIRS} )

add_library( lib_opencv SHARED IMPORTED )
set_target_properties(lib_opencv PROPERTIES IMPORTED_LOCATION ${CMAKE_CURRENT_SOURCE_DIR}/src/main/jniLibs/${ANDROID_ABI}/libopencv_java3.so)

# 3rdparty stuff
include_directories( ${CMAKE_SOURCE_DIR}/libs/3rdparty/)
add_subdirectory( ${CMAKE_SOURCE_DIR}/libs/3rdparty/aruco)

find_library( log-lib log )

add_library( native-lib  SHARED  src/main/cpp/native-lib.cpp )
target_link_libraries( native-lib  ${log-lib}  lib_opencv ${OpenCV_LIBS}  aruco)
