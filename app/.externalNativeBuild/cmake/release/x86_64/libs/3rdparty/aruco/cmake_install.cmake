# Install script for directory: /home/alantavares/AndroidStudioProjects/ProjetosConnectRobotic/dlibAruco/app/libs/3rdparty/aruco

# Set the install prefix
if(NOT DEFINED CMAKE_INSTALL_PREFIX)
  set(CMAKE_INSTALL_PREFIX "/usr/local")
endif()
string(REGEX REPLACE "/$" "" CMAKE_INSTALL_PREFIX "${CMAKE_INSTALL_PREFIX}")

# Set the install configuration name.
if(NOT DEFINED CMAKE_INSTALL_CONFIG_NAME)
  if(BUILD_TYPE)
    string(REGEX REPLACE "^[^A-Za-z0-9_]+" ""
           CMAKE_INSTALL_CONFIG_NAME "${BUILD_TYPE}")
  else()
    set(CMAKE_INSTALL_CONFIG_NAME "Release")
  endif()
  message(STATUS "Install configuration: \"${CMAKE_INSTALL_CONFIG_NAME}\"")
endif()

# Set the component getting installed.
if(NOT CMAKE_INSTALL_COMPONENT)
  if(COMPONENT)
    message(STATUS "Install component: \"${COMPONENT}\"")
    set(CMAKE_INSTALL_COMPONENT "${COMPONENT}")
  else()
    set(CMAKE_INSTALL_COMPONENT)
  endif()
endif()

# Install shared libraries without execute permission?
if(NOT DEFINED CMAKE_INSTALL_SO_NO_EXE)
  set(CMAKE_INSTALL_SO_NO_EXE "1")
endif()

if("${CMAKE_INSTALL_COMPONENT}" STREQUAL "main" OR NOT CMAKE_INSTALL_COMPONENT)
  file(INSTALL DESTINATION "${CMAKE_INSTALL_PREFIX}/lib" TYPE STATIC_LIBRARY FILES "/home/alantavares/AndroidStudioProjects/ProjetosConnectRobotic/dlibAruco/app/.externalNativeBuild/cmake/release/x86_64/libs/3rdparty/aruco/libaruco.a")
endif()

if("${CMAKE_INSTALL_COMPONENT}" STREQUAL "Unspecified" OR NOT CMAKE_INSTALL_COMPONENT)
  file(INSTALL DESTINATION "${CMAKE_INSTALL_PREFIX}/include/aruco" TYPE FILE FILES
    "/home/alantavares/AndroidStudioProjects/ProjetosConnectRobotic/dlibAruco/app/libs/3rdparty/aruco/aruco_export.h"
    "/home/alantavares/AndroidStudioProjects/ProjetosConnectRobotic/dlibAruco/app/libs/3rdparty/aruco/cameraparameters.h"
    "/home/alantavares/AndroidStudioProjects/ProjetosConnectRobotic/dlibAruco/app/libs/3rdparty/aruco/cvdrawingutils.h"
    "/home/alantavares/AndroidStudioProjects/ProjetosConnectRobotic/dlibAruco/app/libs/3rdparty/aruco/dictionary.h"
    "/home/alantavares/AndroidStudioProjects/ProjetosConnectRobotic/dlibAruco/app/libs/3rdparty/aruco/ippe.h"
    "/home/alantavares/AndroidStudioProjects/ProjetosConnectRobotic/dlibAruco/app/libs/3rdparty/aruco/marker.h"
    "/home/alantavares/AndroidStudioProjects/ProjetosConnectRobotic/dlibAruco/app/libs/3rdparty/aruco/markerdetector.h"
    "/home/alantavares/AndroidStudioProjects/ProjetosConnectRobotic/dlibAruco/app/libs/3rdparty/aruco/markerlabeler.h"
    "/home/alantavares/AndroidStudioProjects/ProjetosConnectRobotic/dlibAruco/app/libs/3rdparty/aruco/markermap.h"
    "/home/alantavares/AndroidStudioProjects/ProjetosConnectRobotic/dlibAruco/app/libs/3rdparty/aruco/posetracker.h"
    "/home/alantavares/AndroidStudioProjects/ProjetosConnectRobotic/dlibAruco/app/libs/3rdparty/aruco/markerlabelers/dictionary_based.h"
    "/home/alantavares/AndroidStudioProjects/ProjetosConnectRobotic/dlibAruco/app/libs/3rdparty/aruco/timers.h"
    "/home/alantavares/AndroidStudioProjects/ProjetosConnectRobotic/dlibAruco/app/libs/3rdparty/aruco/debug.h"
    "/home/alantavares/AndroidStudioProjects/ProjetosConnectRobotic/dlibAruco/app/libs/3rdparty/aruco/aruco.h"
    "/home/alantavares/AndroidStudioProjects/ProjetosConnectRobotic/dlibAruco/app/libs/3rdparty/aruco/markerlabelers/svmmarkers.h"
    )
endif()

