cmake_minimum_required(VERSION 3.1.0 FATAL_ERROR)

set(CMAKE_CXX_FLAGS "-g -Wall")
set(CMAKE_CXX_STANDARD 11)

add_library(native-poker-eval-jni SHARED
        PokerEvaluator.cpp)

target_link_libraries(native-poker-eval-jni
        android
        log)