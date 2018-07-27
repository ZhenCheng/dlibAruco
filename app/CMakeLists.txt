cmake_minimum_required(VERSION 3.4.1)

# OpenCV stuff
find_package(OpenCV REQUIRED)
include_directories( ${OpenCV_INCLUDE_DIRS} )

# 3rdparty stuff
include_directories( ${CMAKE_SOURCE_DIR}/libs/3rdparty/)
add_subdirectory( ${CMAKE_SOURCE_DIR}/libs/3rdparty/aruco)

find_library( log-lib log )

add_library( native-lib  SHARED  src/main/cpp/native-lib.cpp )
target_link_libraries( native-lib  ${log-lib}  ${OpenCV_LIBS}  aruco)
